package com.jpodder.data.feeds;

import java.io.File;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.FileHandler;
import com.jpodder.JPodderException;
import com.jpodder.data.cache.Cache;
import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IDataHandler;
import com.jpodder.data.download.DownloadLogic;
import com.jpodder.data.feeds.list.XPersonalFeedList;
import com.jpodder.data.feeds.stats.XFeedEvent;
import com.jpodder.data.feeds.stats.XFeedEventHistory;
import com.jpodder.data.feeds.stats.XFeedInstruction;
import com.jpodder.data.player.PlayerLogic;
import com.jpodder.net.NetHEADInfo;
import com.jpodder.net.NetTask;

// CB TODO, UI references? 
import com.jpodder.schedule.SchedulerLogic;
import com.jpodder.tasks.ITaskListener;
import com.jpodder.tasks.TaskEvent;
import com.jpodder.util.Messages;
import com.jpodder.util.TokenHandler;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class XFeedLogic implements IConfigurationListener, ITaskListener {

	private boolean mStartup = true; // Is true when instantiated and then

	// false, when model is created

	private Logger mLog = Logger.getLogger(getClass().getName());

	private Thread mFeedInfoCollectorThread;

	private Thread mFeedEnclosureInspectorThread;

	private Thread mFeedEnsolureCollectorThread;

	private boolean mThreadsStarted = false;

	public ArrayList<IXFeedListener> mListeners = new ArrayList<IXFeedListener>();

	private List<IXPersonalFeed> mCollectFeedInfoQueue = new ArrayList<IXPersonalFeed>();

	private List<IXPersonalFeed> mInspectEnclosuresQueue = new ArrayList<IXPersonalFeed>();

	private List<IXPersonalFeed> mCollectEnclosuresQueue = new ArrayList<IXPersonalFeed>();

	protected FeedCollector mFeedInfoCollector = new FeedCollector(
			mCollectFeedInfoQueue);

	protected FeedEnclosureInspector mFeedEnclosureInspector = new FeedEnclosureInspector(
			mInspectEnclosuresQueue);

	protected FeedEnclosureCollector mFeedEnsolureCollector = new FeedEnclosureCollector(
			mCollectEnclosuresQueue);

	private static XFeedLogic sSelf;

	private ThreadGroup tasksGrp = new ThreadGroup("tasks");

	public static XFeedLogic getInstance() {
		if (sSelf == null) {
			sSelf = new XFeedLogic();
		}
		ConfigurationLogic.getInstance().addConfigListener(sSelf);
		return sSelf;
	}

	public XFeedLogic() {
		start();
	}

	public void start() {
		mFeedInfoCollectorThread = new Thread(tasksGrp, mFeedInfoCollector,
				"Feed Collection");
		mFeedEnclosureInspectorThread = new Thread(tasksGrp,
				mFeedEnclosureInspector, "Enclosure Inspection");
		mFeedEnsolureCollectorThread = new Thread(tasksGrp,
				mFeedEnsolureCollector, "Enclosure collection");
		mFeedInfoCollectorThread.start();
		mFeedEnclosureInspectorThread.start();
		mFeedEnsolureCollectorThread.start();
		mThreadsStarted = true;
	}

	/**
	 */
	public void stop() {
		mFeedInfoCollectorThread.interrupt();
		mFeedEnclosureInspectorThread.interrupt();
		mFeedEnsolureCollectorThread.interrupt();
		mThreadsStarted = false;
		ConfigurationLogic.getInstance().removeConfigListener(sSelf);
	}

	/**
	 * @param listener
	 */
	public void addListener(IXFeedListener listener) {
		synchronized (mListeners) {
			if (!mListeners.contains(listener)) {
				mListeners.add(listener);
			}
		}
	}

	/**
	 * @param listener
	 */
	public synchronized void removeListener(IXFeedListener listener) {
		synchronized (mListeners) {
			if (mListeners.contains(listener)) {
				mListeners.remove(listener);
			}
		}
	}

	/**
	 * Fire an event, notifying any listener of a succesful task completion.
	 * 
	 * @param event
	 */
	// AS A deadlock occurrs when the FeedDialog hide() method is called because
	// AS it tries to remove itself from the list of listeners and if at the
	// AS same times this method is called it locks on the list of listeners
	// AS which ends up in the FileTableModel that want to send an event which
	// is
	// AS locked by the FeedDialog. Removing the synchronized here and handle
	// AS the listeners one by one
	// AS protected synchronized void fireTaskSucceeded(TaskEvent event) {
	protected void fireTaskSucceeded(XFeedEvent event) {
		// IXFeedListener[] lListeners = (IXFeedListener[]) listeners
		// .toArray(new IXFeedListener[] {});
		Iterator<IXFeedListener> lIt = mListeners.iterator();
		for (; lIt.hasNext();) {
			IXFeedListener lListener = lIt.next();
			lListener.instructionSucceeded(event);
			mLog.debug("Subject:" + event.getSubject() + ", Source" + event.getSource() +" Instruction: " + event.getDescription());
		}
	}

	/**
	 * Fire an event, notifying any listener of a unsuccesful task completion.
	 * 
	 * @param event
	 */
	protected void fireTaskFailed(XFeedEvent event) {
		IXFeedListener[] lListeners = (IXFeedListener[]) mListeners
				.toArray(new IXFeedListener[] {});
		for (int i = 0; i < lListeners.length; i++) {
			IXFeedListener lListener = lListeners[i];
			if (mListeners.contains(lListener)) {
				lListener.instructionFailed(event);
			}
		}
	}

	/**
	 * Fire an event, notifying any listener of task information available.
	 * 
	 * @param event
	 */
	protected synchronized void fireTaskInfo(XFeedEvent event) {
		IXFeedListener[] lListeners = (IXFeedListener[]) mListeners
				.toArray(new IXFeedListener[] {});
		for (int i = 0; i < lListeners.length; i++) {
			IXFeedListener lListener = lListeners[i];
			if (mListeners.contains(lListener)) {
				lListener.instructionInfo(event);
			}
		}
	}

	/**
	 * Add a pFeed to the collection queue.
	 * 
	 * @param pFeed
	 *            PollModel.Feed
	 */
	public void addFeedToCollect(IXPersonalFeed pFeed) {
		if (!mThreadsStarted) {
			start();
		}
		synchronized (mCollectFeedInfoQueue) {
			mLog.info("Add feed: " + pFeed + " to the collection queue");
			mCollectFeedInfoQueue.add(pFeed);
			if (mLog.isDebugEnabled()) {
				mLog.info("Notify the waiting threads on the list");
			}
			mCollectFeedInfoQueue.notify();
		}
	}

	/**
	 * Notify all threads. This is needed as some thread might have gone to
	 * sleep after delegating work.
	 */
	public synchronized void wakeUpAll() {
		notifyAll();
	}

	/**
	 * A pFeed information collection method. It is a synchronized class, and
	 * remains in wait state until notified. When notified it will check if a
	 * pFeed is available in the collection queue. The pFeed instruction will
	 * determine if this action should be executed. it then collects the
	 * following pFeed information: <ul
	 * <li>The HTTP HEAD information for this pFeed.</li>
	 * <li>The enclosures in the RSS pFeed.</li>
	 * <li>The title of the pFeed. (This is the first title element in RSS
	 * pFeed).</li>
	 * </ul>
	 * It also creates a sub-folder for this pFeed in the main podcast folder
	 * and marks the pFeed quality.
	 * <p>
	 * When succesfull it adds the pFeed to the enclosure inspection and
	 * collection queue. It then processes the next pFeed in the queue or goes
	 * back to wait state. Finally the applicable components are updated in the
	 * GUI.
	 * 
	 * @see Feed
	 */
	public class FeedCollector implements Runnable {
		protected boolean mExit;

		private List mTasks;

		public FeedCollector(List pList) {
			mTasks = pList;
		}

		public void run() {
			while (!mExit) {
				IXPersonalFeed lFeed = null;
				try {
					// Lock on the list of tasks to avoid interaction with rest
					// of this class responsibilities
					synchronized (mTasks) {
						if (mTasks.isEmpty()) {
							mTasks.wait();
							continue;
						}
						lFeed = (IXPersonalFeed) mTasks.get(0);
						mTasks.remove(lFeed);
					}
					collectFeed(lFeed);
				} catch (InterruptedException ie) {
					// Thread interrupted so exit here
					if (mLog.isDebugEnabled()) {
						mLog
								.debug("Collecting Feed Info is interrupted -> terminate thread: "
										+ Thread.currentThread());
					}
					mExit = true;
				} catch (Exception e) {
					// Log but ignore the exception to keep on working
					mLog.warn("Collecting Feed Info failed (keep on working)",
							e);
				} catch (Error err) {
					// Log the error and exit
					mLog.fatal(
							"Collecting Feed Info failed with error -> terminate thread: "
									+ Thread.currentThread(), err);
					throw err;
				}
			}
		}

		public void collectFeed(IXPersonalFeed pFeed) {

			String lInfo = "";
			if (mLog.isDebugEnabled()) {
				mLog.info("collectInfo(), instruction: "
						+ pFeed.getInstruction());
			}
			if (pFeed.getInstruction().isCollect()) {
				int newEnclosures = 0;
				try {
					// Perform an HTTP HEAD INFO to determine the last modified
					// date against the modified date.
					URL url = pFeed.getURL();

					if (url != null) {

						NetHEADInfo hi = null;

						// Perform some checks to decide if the feed should
						// be recollected.
						// Check the modified date against the currently stored
						// modified date.
						//
						boolean collect = true;

						// Check for the protocol first.
						String protocol = url.getProtocol();
						if (protocol.equals("http")) {
							try {
								hi = NetTask.getHeadInfo(pFeed, url);
							} catch (JPodderException e) {
								collect = false;
							}
						} else {
							// The HEAD information is irrelevant for non-HTTP
							// protocol.
							mLog.info("Protocol not supported" + protocol);
							return;
						}

						// We do NOT collect if the the modified date hasn't
						// changed.
						if (pFeed.getHEADInfo() != null
								&& pFeed.getHEADInfo().getModifiedString() != null
								&& pFeed.getHEADInfo().getModifiedString()
										.equals(hi.getModifiedString())) {
							collect = false;
						}

						// collect, if the file is not present.
						if (pFeed.getFile() == null
								|| !pFeed.getFile().exists()) {
							collect = true;
						} else {
						}

						if (!collect) {

							// This pFeed has not changed. The RSS file will not
							// be collected again. We do parse it to set the
							// model.

							newEnclosures = pFeed.updateEnclosures(pFeed
									.getFile());

							mLog.info("The feed " + pFeed
									+ " is up to date (Will not parse it)");
							pFeed.getInstruction().setCollectEnclosure(false);
							pFeed.setQuality(XPersonalFeedList.GOOD_QUALITY);
							pFeed.setQualityDescription("");
							// pFeed.getInstruction().setMark(false);
							mLog.info("The feed " + pFeed
									+ " has been marked (Will not Mark)");

							// Rewrite the instruction if was previously
							// inspected.
							XFeedEventHistory.XFeedHistoryEvent event = pFeed
									.getHistory().getLatestEvent();
							if (event != null) {
								Object eventObject = event.getEventObject();
								if (eventObject instanceof XFeedInstruction) {
									if (((XFeedInstruction) eventObject)
											.isInspect())
										// CB FIXME, there is a bug, inspection
										// is overriden.
										// pFeed.getInstruction()
										// .setInspect(false);
										mLog
												.info("The feed "
														+ pFeed
														+ " has been inspected (Will not Inspect)");
								}
							}

							lInfo = (pFeed.getTitle().length() > 35 ? pFeed
									.getTitle().substring(0, 35)
									+ "..." : pFeed.getTitle())
									+ " "
									+ Messages.getString("tasks.upToDate");

							fireTaskSucceeded(new XFeedEvent(pFeed
									.getInstruction().getSource(),
									XFeedEvent.INSTRUCTION_COLLECT, pFeed,
									(JPodderException) null));

						} else {
							// Here It's a totaly new or modified Feed.
							// The Feed is parsed. If it's not totally new, we
							// copy the enclosure settings from the previous
							// settings.

							mLog.info(pFeed + " is modified, parse it");
							Reader stream = NetTask.getReader(pFeed, url, pFeed
									.getHEADInfo());
							// Save the stream in a temporary file first.
							File lFile = FileHandler.saveTempFeedFile(stream);

							newEnclosures = pFeed.updateEnclosures(lFile);

							pFeed.setHEADInfo(hi);
							pFeed.setQuality(XPersonalFeedList.GOOD_QUALITY);
							pFeed.setQualityDescription(Messages
									.getString("feed.quality.good"));

							lInfo = (pFeed.getTitle().length() > 35 ? pFeed
									.getTitle().substring(0, 35)
									+ "..." : pFeed.getTitle())
									+ " "
									+ newEnclosures
									+ " "
									+ (newEnclosures == 1 ? Messages
											.getString("tasks.newEnclosure")
											: Messages
													.getString("tasks.newEnclosures"));

							fireTaskSucceeded(new XFeedEvent(pFeed
									.getInstruction().getSource(),
									XFeedEvent.INSTRUCTION_COLLECT, pFeed,
									(JPodderException) null));

						}

					} else { // The URL off the pFeed is unknown.
						fireTaskFailed(new XFeedEvent(pFeed.getInstruction()
								.getSource(), XFeedEvent.INSTRUCTION_COLLECT,
								pFeed, new JPodderException("Empty URL")));
					}

					// fireTaskInfo(new XFeedEvent(pFeed.getInstruction()
					// .getSource(), XFeedEvent.INSTRUCTION_COLLECT,
					// pFeed, lInfo));
				} catch (Exception ie) {
					// Something wrong in either accessing the pFeed URL or
					// parsing
					// the XML RSS 2.0 pFeed.
					// The pFeed quality is set to bad, but it could be that no
					// networks exists at all. (Need to include check on
					// network.
					mLog.error("Error collecting feed " + pFeed);
					pFeed.setQuality(XPersonalFeedList.BAD_QUALITY);
					pFeed.setQualityDescription(ie.getMessage());
					fireTaskFailed(new XFeedEvent(pFeed.getInstruction()
							.getSource(), XFeedEvent.INSTRUCTION_COLLECT,
							pFeed, ie));

				} finally {
					// We now set the folder after regardless.
					// If no network or bad feed we would set it.
					String lDefaultFolder = FileHandler.getPodcastFolder()
							+ File.separator + TokenHandler.RSS_ITEM_TITLE;

					if (pFeed.getFolder().length() == 0
							|| pFeed.getFolder().equals(lDefaultFolder)) {
						pFeed.setFolder(FileHandler.getFeedFolder(pFeed
								.getTitle()));
					}
				}
			}
			synchronized (mInspectEnclosuresQueue) {
				if (mLog.isDebugEnabled()) {
					mLog
							.info("collectInfo(), add feed to inspect enclosures queue");
				}
				mInspectEnclosuresQueue.add(pFeed);
				// Now wake up any waiting thread on the inspect enclosures
				// tasks
				mInspectEnclosuresQueue.notify();
			}
			synchronized (mCollectEnclosuresQueue) {
				if (mLog.isDebugEnabled()) {
					mLog
							.info("collectInfo(), add feed to collect enclosures queue");
				}
				mCollectEnclosuresQueue.add(pFeed);
				// Now wake up any waiting thread on the collecting
				// enclosures tasks
				mCollectEnclosuresQueue.notify();
			}
		}
	}

	/**
	 * Add a pFeed to the inspection list.
	 * 
	 * @param pFeed
	 *            PollModel.Feed
	 */
	public void addFeedToInspect(IXPersonalFeed pFeed) {
		if (!mThreadsStarted) {
			start();
		}
		synchronized (mInspectEnclosuresQueue) {
			if (!mInspectEnclosuresQueue.contains(pFeed)) {
				if (mLog.isDebugEnabled()) {
					mLog
							.info("Add feed: " + pFeed
									+ " to the inspection queue");
				}
				mInspectEnclosuresQueue.add(mInspectEnclosuresQueue.size(),
						pFeed);
				mInspectEnclosuresQueue.notify();
			}
		}
	}

	/**
	 * A pFeed file inspection method. <Update what happens here>
	 * 
	 * 
	 * This method is synchronized, it remains in wait state until notified.
	 * When notified it processes the next pFeed in the inspection queue. If
	 * this method is executed is subject to the instruction member in the pFeed
	 * object.
	 * <p>
	 * As a final action, it adds the enclosure to the download queue. It
	 * thereby respects the maximum number of downloads for this pFeed. After
	 * this it process the next enclosure or goes back to wait state.
	 * 
	 * @see Feed
	 */
	public class FeedEnclosureInspector implements Runnable {
		protected boolean mExit;

		private List mTasks;

		public FeedEnclosureInspector(List pList) {
			mTasks = pList;
		}

		public void run() {
			while (!mExit) {
				IXPersonalFeed lFeed = null;
				try {
					// Lock on the list of tasks to avoid interaction with rest
					// of this class responsibilities
					synchronized (mTasks) {
						if (mTasks.isEmpty()) {
							mTasks.wait();
							continue;
						}
						lFeed = (IXPersonalFeed) mTasks.get(0);
						mTasks.remove(lFeed);
					}
					if (mLog.isDebugEnabled()) {
						mLog
								.info(".FeedEnclosureInspector.run(), feed to inspect: "
										+ lFeed);
					}
					inspect(lFeed);
				} catch (InterruptedException ie) {
					// Thread interrupted so exit here
					if (mLog.isDebugEnabled()) {
						mLog
								.debug("Inspecting Feed is interrupted -> terminate thread: "
										+ Thread.currentThread());
					}
					mExit = true;
				} catch (Exception e) {
					// Log but ignore the exception to keep on working
					mLog.warn("Inspecting Feed failed (keep on working)", e);
				} catch (Error err) {
					// Log the error and exit
					mLog.fatal(
							"Inspecting Feed failed with error -> terminate thread: "
									+ Thread.currentThread(), err);
					// throw err;
				}
			}
		}
	}

	public void inspect(IXPersonalFeed pFeed) {
		List pFiles = pFeed.getMerged(true);
		if (pFiles != null) {
			// Report the action.
			if (pFeed.getInstruction().isInspect()) {
				String lInfo = new String();
				if (pFiles.size() > 0) {
					lInfo = Messages.getString("tasks.inspecting.content")
							+ pFiles.size() + " "
							+ Messages.getString("tasks.inspecting.file");
					lInfo = lInfo.trim();
				} else {
					lInfo = Messages.getString("tasks.inspecting.noFiles");
				}

				fireTaskInfo(new XFeedEvent(pFeed.getInstruction()
						.getSource(), XFeedEvent.INSTRUCTION_INFO, pFeed,
						lInfo));

			}

			// We have to reset the candidate count, as the count
			// is regenerated here.
			pFeed.setCandidatesCount(0);
			Iterator it = pFiles.iterator();
			while (it.hasNext()) {
				IXFile lFile = (IXFile) it.next();
				if (lFile instanceof IXPersonalEnclosure) {
					IXPersonalEnclosure lEnclosure = (IXPersonalEnclosure) lFile;
					if (!lEnclosure.isInspected()) {
						if (lEnclosure.getFile() == null) {
						}
						if (lEnclosure.getFile() != null) {
							if (pFeed.getInstruction().isInspect()) {
								inspectSingleFile(lEnclosure);
								fireTaskSucceeded(new XFeedEvent(pFeed
										.getInstruction().getSource(),
										XFeedEvent.INSTRUCTION_INSPECT,
										lFile, (Exception) null));
							}
						}
						if (pFeed.getInstruction().isMark()) {
							markSingleEnclosure(pFeed, lEnclosure);
						}
					}

					pFeed.updateSingleCandidate(lEnclosure, Configuration
							.getInstance().getMarkMax());
					fireTaskSucceeded(new XFeedEvent(pFeed.getInstruction()
							.getSource(), XFeedEvent.INSTRUCTION_MARK,
							lEnclosure, (Exception) null));

					if (pFeed.getInstruction().isDownload()) {
						if (lEnclosure.isCandidate()) {
							downloadEnclosure(lEnclosure);
						}
					}
				} else {
					if (lFile instanceof XLocalFile) {
						if (mLog.isDebugEnabled()) {
							mLog.info("inspect(), local file"
									+ lFile.getName());
						}
						if (pFeed.getInstruction().isInspect()) {
							inspectSingleFile(lFile);
						}
						// Always mark delete for local files.
						markDeleteSingleFile(lFile);
						fireTaskSucceeded(new XFeedEvent(pFeed
								.getInstruction().getSource(),
								XFeedEvent.INSTRUCTION_INSPECT, lFile,
								(Exception) null));
					}

				}
			}

			// CB TODO, this is a bit rough to reload completely.
			// fireTaskSucceeded(new XFeedEvent(pFeed.getInstruction()
			// .getSource(), XFeedEvent.INSTRUCTION_COLLECT,
			// pFeed, (Exception) null));

		}
	}

	/**
	 * Inspect a single file object.
	 * 
	 * @param pXFile
	 *            FileWrapper2
	 */
	public void inspectSingleFile(IXFile pXFile) {

		File lFile = pXFile.getFile();
		pXFile.setLocal(false);
		pXFile.setCached(false);
		pXFile.setInPlayer(false);
		boolean cacheLearn = Configuration.getInstance().getCacheLearn();

		// CHECK IF IN DIRECTORY.
		if (lFile.exists()) {
			pXFile.setLocal(true);
			mLog.info(Messages.getString("tasks.inspecting.marked", lFile
					.getName()));

		}
		// CHECK IF IN CACHE.
		if (Cache.getInstance().hasTrack(lFile)) {
			pXFile.setCached(true);
			mLog.info(Messages.getString("tasks.inspecting.cached", lFile
					.getName()));
		}

		if (PlayerLogic.getInstance().hasTrack(
				pXFile.getFeed().getTitle(), pXFile)) {
			pXFile.setInPlayer(true);
			mLog.info("File in player: " + lFile.getName());
		}

		if (cacheLearn && !pXFile.isCached()
				&& (pXFile.getInPlayer() || pXFile.isLocal())) {
			Cache.getInstance().addTrack(pXFile.getFile());
			pXFile.setCached(true);
			mLog.info("Store in cache " + pXFile);
		}
	}

	/**
	 * Mark enclosures in a Feed.
	 * 
	 * @param pEnclosure
	 *            Enclosure
	 */
	public void markSingleEnclosure(IXPersonalFeed pFeed,
			IXPersonalEnclosure pEnclosure) {
		boolean doMark = true;
		boolean mark_max = Configuration.getInstance().getMarkMax();
		if (mark_max) {

			int index = pFeed.indexOf(pEnclosure); // Also check the position
													// of the enclosure.
			int count = pFeed.getCandidatesCount();
			if (count >= pFeed.getMaxDownloads()
					|| index >= pFeed.getMaxDownloads()) {
				doMark = false;
			}
		}
		if (doMark || pEnclosure.isMarked()) {
			markSingleEnclosure(pEnclosure);
		}
	}

	@SuppressWarnings("deprecation")
	public void markSingleEnclosure(IXPersonalEnclosure pEnclosure) {

		mLog.info(Messages.getString("tasks.inspecting.marking") + pEnclosure);

		// ACTION MATRIX HERE.
		// reset markers here.
		pEnclosure.setMarked(false);
		// 1
		if (!pEnclosure.isLocal() && !pEnclosure.isCached()
				&& !pEnclosure.getInPlayer()) {
			pEnclosure.setMarked(true);
			mLog.info(Messages.getString("tasks.marking") + pEnclosure);
		}
		// 2
		if (!pEnclosure.isLocal() && !pEnclosure.isCached()
				&& pEnclosure.getInPlayer()) {
			// File in player, not downloaded with jPodder, no action.
		}
		// 3
		if (!pEnclosure.isLocal() && pEnclosure.isCached()
				&& !pEnclosure.getInPlayer()) {
			// Previously downloaded, but likely removed, no action
		}
		// 4
		if (!pEnclosure.isLocal() && pEnclosure.isCached()
				&& pEnclosure.getInPlayer()) {
			// Previously downloaded, but likely removed, no action
		}

		// Special case for incomplete files.
		// This behaviour could be optional
		try {
			if (pEnclosure.isLocal() && pEnclosure.isIncomplete()) {
				pEnclosure.setMarked(true);
			}
		} catch (Exception e) {
			// Can't determine the state.
		}
		// 5
		if (pEnclosure.isLocal() && !pEnclosure.isCached()
				&& pEnclosure.getInPlayer()) {
			// Previously downloaded & in player no action
		}

		// 6
		if (pEnclosure.isLocal() && pEnclosure.isCached()
				&& !pEnclosure.getInPlayer()) {

		}

		// 7
		if (pEnclosure.isLocal() && pEnclosure.isCached()
				&& pEnclosure.getInPlayer()) {
			// Previously downloaded, in cache & in player no action
		}
		// 8
		if (pEnclosure.isLocal() && !pEnclosure.isCached()
				&& !pEnclosure.getInPlayer()) {
			// Previously downloaded, in cache & in player no action

		}
	}

	/**
	 * Mark files which are candidates to be deleted. A file is a candidate to
	 * be deleted if it's not in the RSS pFeed.
	 * 
	 * @param file
	 */
	public static void markDeleteSingleFile(IXFile file) {
		file.setMarked(false);
		if (file instanceof XLocalFile) {
			file.setMarked(true);
		}
	}

	/**
	 * Collects enclosure HTTP HEAD information. This class is synchronized and
	 * remains in wait state until notified. When notified it retrieves the next
	 * enclosure in the enclosure collection queue. The execution of this method
	 * is subject to the instruction member in the pFeed which owns this
	 * enclosure.
	 * 
	 * @see Feed
	 */
	public class FeedEnclosureCollector implements Runnable {
		protected boolean mExit;

		private List mTasks;

		public FeedEnclosureCollector(List pList) {
			mTasks = pList;
		}

		public void run() {
			while (!mExit) {
				IXPersonalFeed lFeed = null;
				try {
					// Lock on the list of tasks to avoid interaction with rest
					// of this class responsibilities
					synchronized (mTasks) {
						if (mTasks.isEmpty()) {
							mTasks.wait();
							continue;
						}
						lFeed = (IXPersonalFeed) mTasks.get(0);
						mTasks.remove(lFeed);
					}
					collectEnclosures(lFeed);
				} catch (InterruptedException ie) {
					// Thread interrupted so exit here
					if (mLog.isDebugEnabled()) {
						mLog
								.debug("Collecting Feed Enclosures is interrupted -> terminate thread: "
										+ Thread.currentThread());
					}
					mExit = true;
				} catch (Exception e) {
					// Log but ignore the exception to keep on working
					mLog
							.warn(
									"Collecting Feed Enclosures failed (keep on working)",
									e);
				} catch (Error err) {
					// Log the error and exit
					mLog.fatal(
							"Collecting Feed Enclosures failed with error -> terminate thread: "
									+ Thread.currentThread(), err);
					throw err;
				}
			}
		}

		public void collectEnclosures(IXPersonalFeed pFeed) {
			if (pFeed.getEnclosureSize() > 0
					&& pFeed.getInstruction().isCollectEnclosure()) {
				mLog.info("Getting the enclosure URL info for " + pFeed);
				Iterator it = pFeed.getEnclosureIterator();
				while (it.hasNext()) {
					IXPersonalEnclosure lEnclosure = (IXPersonalEnclosure) it
							.next();
					if (lEnclosure.getContentSize() == 0) {
						try {
							URL url = lEnclosure.getURL();
							NetHEADInfo lInfo = NetTask.getHeadInfo(lEnclosure
									.getFeed(), url);

							lEnclosure.setContentSize(lInfo.length);
							lEnclosure.setContentDate(lInfo.getModifiedLong());

							if (lEnclosure.getContentSize() != 0) {
								fireTaskSucceeded(new XFeedEvent(pFeed
										.getInstruction().getSource(),
										XFeedEvent.INSTRUCTION_COLLECT_ENCL,
										lEnclosure, (Exception) null));
							} else {
								mLog
										.info("collectEnclosures(): Enclosure head info not availble for: "
												+ url.toExternalForm());
							}
						} catch (Exception ie) {
							mLog.error(Messages.getString("tasks.enclosure")
									+ pFeed.getFolder() + ": "
									+ lEnclosure.getName());
						}
					} // HTTP HEAD has been set before.
				}
			}
		}
	}

	/**
	 * This method downloads the enclosure file.
	 * 
	 * @see Feed
	 */
	public void downloadEnclosure(IXPersonalEnclosure pEnclosure) {
		if (pEnclosure.isMarked()) {
			if (!pEnclosure.isCandidate()) {
				mLog
						.info("inspect() marked, but not within download limiter, go on");
			}
			if (!DownloadLogic.getInstance().isDownloading(pEnclosure)) {
				mLog.info("Add a download " + pEnclosure);
				DownloadLogic.getInstance().addDownload(pEnclosure);
			}
		}
	}

	/**
	 * Preview a feed. Initiates an instruction containing Collection &
	 * Inspection of a feed.
	 * 
	 * @param src
	 * 
	 * @param feed
	 */
	public void previewFeed(Object pSrc, IXPersonalFeed feed) {
		mLog.info("previewFeed(), feed : " + feed);
		if (feed != null) {
			boolean mark = false;

			if (feed.getEnclosureSize() == 0) {
				mark = true;
			}
			feed.setInstruction(pSrc, true, true, mark, true, false, false);
			addFeedToCollect(feed);
		}
	}

	/**
	 * Poll the feeds, as provided in the polling model. The various tasks are
	 * performed in accordance to the feed's instruction member.
	 * 
	 * @param download
	 *            boolean Determines if the polling should also download marked
	 *            enclosures.
	 * @return int The number of scanned feeds.
	 * @see Feed
	 */
	public int scan(Object pSrc, boolean download) {

		// Scanning is prohibited, while the podcast folder is not set.
		// The user is asked to set it when it doesn't exist. (See FileHandler)
		while (FileHandler.getPodcastFolder() == null) {
			try {
				synchronized (this) {
					this.wait(1000);
				}
			} catch (InterruptedException e) {
				mLog.info("interrupted" + e.getMessage());
			}
		}

		boolean stop = false;
		int lScanned = 0; // Number of polled feeds.
		Iterator it = XPersonalFeedList.getInstance().getFeedIterator();
		mLog.info(Messages.getString("sequenceControl.start"));
		while (it.hasNext()) {
			if (stop == true) {
				break;
			}
			XPersonalFeed feed = (XPersonalFeed) it.next();
			if (feed.getPoll() && feed.getURL() != null) {
				lScanned++;
				mLog.info(Messages.getString("sequenceControl.scanning") + " "
						+ feed.getURL().getHost());

				// boolean mark = false;
				//				
				// // Mark only if enclosures are not existing, or
				// // if the polling was triggered by the scheduler.
				// if (feed.getEnclosureSize() == 0) {
				// mark = true;
				// }

				feed.setInstruction(pSrc, true, true, true, true, download,
						true);
				addFeedToCollect(feed);
			}
		}
		int lSize = XPersonalFeedList.getInstance().size();
		mLog.info(Messages.getString("sequenceControl.final", new Integer(
				lScanned).toString(), new Integer(lSize).toString()));
		return lScanned;
	}

	/**
	 * Removes a feed from the model. Warns that a removal will occure.
	 * 
	 * @param lFeed
	 * @param removePlayer
	 * @param removeDisc
	 */
	public void removeFeed(IXPersonalFeed lFeed, boolean removePlayer,
			boolean removeDisc) {

		// We notify that this feed will be gone soon.
		XPersonalFeedList.getInstance().willRemoveFeed(lFeed);

		if (removePlayer) {
			PlayerLogic.getInstance().removePlayList(lFeed.getTitle());
		}

		if (removeDisc) {

			mLog.info("Deleting remove from disc."); // remove from the
			// disc.
			// Delete all the files first.
			boolean isEmpty = true;
			File lFeedFolder = new File(lFeed.getFolder());
			if (lFeedFolder.exists()) {
				File[] files = lFeedFolder.listFiles();
				if (files != null) {
					for (int index = 0; index < files.length; index++) {
						if (!files[index].delete()) {
							isEmpty = false;
						}
					}
				}
			}
			if (isEmpty) {
				lFeedFolder.delete();
			}
		}
		XPersonalFeedList.getInstance().removeFeed(lFeed);
	}

	/**
	 * Update the file view, not overriding the inspection.
	 * 
	 * @param pFeed
	 */
	public void collectFeed(Object pSrc, IXPersonalFeed pFeed) {
		collectFeed(pSrc, pFeed, false);
	}

	/**
	 * The history of the feed will determine if the Feed should be collected.
	 * 
	 * @param pFeed
	 * @param pInspectOverride
	 */
	public void collectFeed(Object pSrc, IXPersonalFeed pFeed,
			boolean pInspectOverride) {
		boolean lInspect = false;
		boolean lPreview = Configuration.getInstance().getAutoPreview();
		if (!pInspectOverride) {
			if (pFeed.getEnclosureSize() != 0) {
				// Inspection should only occure the first time. This is to
				// avoid player queries. The querystate is a volatile flag
				// in the feed object
				XFeedEventHistory.XFeedHistoryEvent event = pFeed.getHistory()
						.getLatestEvent();
				if (event != null) {
					Object eventObject = event.getEventObject();
					if (eventObject instanceof XFeedInstruction) {
						lInspect = (((XFeedInstruction) eventObject)
								.isInspect() != true);
					}
				} else {
					lInspect = true;
				}
			} else {
				lInspect = lPreview;
			}
		} else {
			lInspect = pInspectOverride;
		}
		if (lInspect) {
			mLog.info("Inspecting feed enclosures for: " + pFeed);
			pFeed.setInstruction(pSrc, lPreview, lPreview, lPreview, true,
					false, false);
			addFeedToCollect(pFeed);
			// Collection is what we invoke here.
			// addFeedToInspect(pFeed);
		}
	}

	public void listFeeds() {
		Iterator it = XPersonalFeedList.getInstance().getFeedIterator();
		while (it.hasNext()) {
			IXPersonalFeed lFeed = (IXPersonalFeed) it.next();

			// mLog.info("->" + lFeed.getURL().toExternalForm());
			System.out.println("->" + lFeed.getURL().toExternalForm());
			System.out
					.println("-> Enclosure Count:" + lFeed.getEnclosureSize());
			try {
				// mLog.info(lFeed.getTitle() + ":" + lFeed.getDescription());
				System.out.println("->" + lFeed.getTitle() + ":"
						+ lFeed.getDescription());
			} catch (XFeedException e) {
			}
			Iterator it2 = lFeed.getEnclosureIterator();
			while (it2.hasNext()) {
				IXPersonalEnclosure lEncl = (IXPersonalEnclosure) it2.next();
				try {
					// mLog.info("--->" + lEncl.getURL().toExternalForm());
					System.out
							.println("--->" + lEncl.getURL().toExternalForm());
					System.out.println("--->" + lEncl.getType());
					IXItem lItem = lEncl.getItem();
					System.out.println("--->" + lItem.getDescription());
				} catch (XEnclosureException e1) {
				} catch (XItemException e2) {
				}
			}
		}
	}

	/**
	 * scan the podcast folder for previous folders which possibly contain
	 * downloaded podcasts.
	 * 
	 * @return A list of feeds.
	 * @throws XFeedException
	 */
	public static List scanLocalFolder() {

		ArrayList<XPersonalFeed> feedList = new ArrayList<XPersonalFeed>();

		String folder = Configuration.getInstance().getFolder();

		if (folder == null) {
			return feedList;
		}

		File f = new File(folder);
		if (f.exists() && f.isDirectory()) {
			File[] children = f.listFiles();
			for (int index = 0; index < children.length; index++) {
				File child = children[index];
				if (child.isDirectory()) {
					XPersonalFeed feed = new XPersonalFeed();
					feed.setFolder(child.getAbsolutePath());
					feedList.add(feed);
				}
			}
		}
		return feedList;
	}

	/**
	 * CB 05-07-2006 BUILD IN PROGRESS. FIXME, doesn't work when folder is null,
	 * download shoudn't start before the folder is set.
	 * 
	 */
	public void updateFolders(String lNewParent) {
		// The feed list is updated to a new podcast folder location.
		// Note the path is also stored in the personal enclosure objects.
		// If we have ongoing downloads, we would need to delay this process.

		for (Iterator iter = XPersonalFeedList.getInstance().getFeedIterator(); iter
				.hasNext();) {
			IXPersonalFeed lFeed = (IXPersonalFeed) iter.next();
			if (lFeed.getFolder() != null) {
				File lPath = new File(lFeed.getFolder());
				String lParent = lPath.getParent();
				if (lParent != null && !lParent.equals(lNewParent)) {
					// Step 1. First go through the enclosures.
					String lName = lPath.getName();
					File lNewPath = new File(lNewParent, lName);
					lFeed.setFolder(lNewPath.getAbsolutePath());
				}
			}
		}
	}

	public void configurationChanged(ConfigurationEvent event) {
		// CB TODO, We can't update the folder during downloads.
		// well we can, but this will create a lot of issues.

		updateFolders(Configuration.getInstance().getFolder());
	}

	public void taskCompleted(TaskEvent e) {
		Object lSrc = e.getSource();
		if (lSrc instanceof IDataHandler) {
			if (((IDataHandler) lSrc).getIndex() == ConfigurationLogic.FEED_INDEX) {
				if (mStartup) {
					mStartup = false;
					Configuration.Scheduling lScheduling = Configuration
							.getInstance().getScheduling();
					if (lScheduling.getExecuteOnStartup()
							&& Configuration.getInstance().getAuto()) {
						XFeedLogic.getInstance().scan(SchedulerLogic.class,
								true);
					}
				}
			}

		}
	}

	public void taskAborted(TaskEvent e) {
	}

	public void taskFailed(TaskEvent e) {
	}

}