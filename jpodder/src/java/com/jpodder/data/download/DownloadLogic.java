package com.jpodder.data.download;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.jpodder.FileHandler;
import com.jpodder.JPodderException;
import com.jpodder.data.cache.Cache;
import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.DataEvent;
import com.jpodder.data.configuration.IDataListener;
import com.jpodder.data.content.ContentLogic;
import com.jpodder.data.feeds.IXPersonalEnclosure;
import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.XEnclosureException;
import com.jpodder.data.id3.ID3Logic;
import com.jpodder.data.player.PlayerLogic;
import com.jpodder.net.INetTaskListener;
import com.jpodder.net.NetTask;
import com.jpodder.net.NetTaskEvent;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class DownloadLogic implements INetTaskListener, ActionListener,
		IDataListener {

	private Logger mLog = Logger.getLogger(getClass().getName());

	public static final int CONNECTING = 0; // Active state

	public static final int DOWNLOADING = 1; // Active state

	public static final int RETRYING = 2; // Active state

	public static final int ERROR = 3; // Passive state

	public static final int CANCELLED = 4; // Passive state

	public static final int COMPLETED = 5; // Passive state

	public static final int QUEUED = 6; // Active state

	public static final int IDLE = 7; // Passive state

	public static final int PAUZED = 8; // Passive state

	public static final int RELEASING = 9; // Activate state

	public static final String[] STATE_DESCRIPTION = { "CONNECTING",
			"DOWNLOADING", "RETRYING", "ERROR", "CANCELLED", "COMPLETED",
			"QUEUED", "IDLE", "PAUZED", "RELEASING" };

	private List<Download> mDownloadList = new CopyOnWriteArrayList<Download>();

	private List<IDownloadListener> mDownloadListeners = new CopyOnWriteArrayList<IDownloadListener>();

	private static DownloadLogic sSelf;

	private boolean mPauzed = false;

	public static DownloadLogic getInstance() {
		if (sSelf == null) {
			sSelf = new DownloadLogic();
		}
		return sSelf;
	}

	protected void fireDownloadEvent(DownloadEvent pEvent) {
		Iterator<IDownloadListener> lIt = mDownloadListeners.iterator();
		for (; lIt.hasNext();) {
			IDownloadListener lListener = lIt.next();
			if (mDownloadListeners.contains(lListener)) {
				lListener.modelChanged(pEvent);
			}
		}
	}

	/**
	 * @param pListener
	 */
	public void addListener(IDownloadListener pListener) {
		if (!mDownloadListeners.contains(pListener)) {
			mDownloadListeners.add(pListener);
		}
	}

	/**
	 * @param pListener
	 */
	public void removeListener(IDownloadListener pListener) {
		if (mDownloadListeners.contains(pListener)) {
			mDownloadListeners.remove(pListener);
		}
	}

	/**
	 * Add a download to the download list.
	 * 
	 * @param t
	 *            Thread
	 * @param encl
	 *            Enclosure
	 * @return Download
	 */
	public Download addDownload(IXPersonalEnclosure encl) {
		Download lDownload = new Download(encl);
		lDownload.setState(QUEUED);
		synchronized (mDownloadList) {
			mDownloadList.add(mDownloadList.size(), lDownload);
		}

		fireDownloadEvent(new DownloadEvent(this));
		download(lDownload);

		return lDownload;
	}

	public void abort(IXPersonalFeed lFeed) {
		synchronized (mDownloadList) {
			for (int i = 0; i < mDownloadList.size(); i++) {
				Download lDownload = (Download) mDownloadList.get(i);
				if (lDownload.getEnclosure().getFeed().equals(lFeed)) {
					abort(lDownload);
				}
			}
		}
	}

	public void abort(Download pDownload) {
		if (pDownload != null) {
			int lState = pDownload.getState();
			if (lState != COMPLETED || lState != ERROR) {
				pDownload.setState(CANCELLED);
				NetTask.getInstance().fireNetActionPerformed(
						new NetTaskEvent(pDownload,
								NetTaskEvent.DOWNLOAD_STATUS_CHANGED));
			}
		}
	}

	/**
	 * This method will create the folder and file to store the download. it
	 * will also handle possible errors in preparing the download file.
	 * Subsequently the download object is handed over to the network function.
	 * 
	 * @param pDownload
	 *            Downloads.Download
	 */
	public void download(Download pDownload) {

		if (mPauzed) {
			return; // Downloads are not added in status pauzed.
		}

		IXPersonalEnclosure encl = pDownload.getEnclosure();
		IXPersonalFeed lFeed = encl.getFeed();
		// File lFile = null;
		String lFolder = lFeed.getFolder();
		File lFolderFile = new File(lFolder);

		if (!lFolderFile.exists()) {
			// Create the subdirectory & file
			// This is repeated, even if done also in inspection.
			encl.getFeed().setFolder(
					FileHandler.getFeedFolder(encl.getFeed().getTitle()));
			try {
				FileHandler.createFeedFolder(encl.getFeed().getFolder());
			} catch (JPodderException e) {
				// We can't create the folder. set the default
				// folder for this feed.
				lFeed.setFolder(FileHandler.getPodcastFolder());
			}
		}
		try {
			int lState = pDownload.getState();
			if (lState == CANCELLED) {
				return;
			} else {
				pDownload.setState(QUEUED);
				NetTask.download(pDownload);
			}
		} catch (Exception ie) {
			mLog.warn(ie.getMessage());
		}
	}

	/**
	 * Change the status of all downloads to pauze.
	 */
	public void pauzeAll() {
		mPauzed = true;
		for (int i = 0; i < mDownloadList.size(); i++) {
			Download lDownload = (Download) mDownloadList.get(i);
			int lState = lDownload.getState();
			if (lState == DOWNLOADING || lState == QUEUED
					|| lState == CONNECTING || lState == RETRYING) {
				lDownload.setState(PAUZED);
				NetTask.getInstance().fireNetActionPerformed(
						new NetTaskEvent(lDownload,
								NetTaskEvent.DOWNLOAD_STATUS_CHANGED));
			}
		}
	}

	/**
	 * Change the status of all downloads to pauze.
	 */
	public void resumeAll() {
		this.mPauzed = false;
		for (int i = 0; i < mDownloadList.size(); i++) {
			Download lDownload = (Download) mDownloadList.get(i);
			if (lDownload.getState() == PAUZED) {
				resetDownloadRate(lDownload);
				download(lDownload);
			}
		}
	}

	/**
	 * Clean all completed, errorneous and cancelled downloads.
	 */
	public void cleanAllCompleted() {
		int lState;
		Iterator lIt = mDownloadList.iterator();
		for (; lIt.hasNext();) {
			Download download = (Download) lIt.next();
			lState = download.getState();
			if (lState == COMPLETED || lState == ERROR || lState == CANCELLED) {
				mDownloadList.remove(download);
			}
		}
	}

	/**
	 * Get a download class, based on the index.
	 * 
	 * @param int
	 *            pIndex
	 * @return Download
	 */
	public Download getDownload(int pIndex) {
		synchronized (mDownloadList) {
			return (Download) mDownloadList.get(pIndex);
		}
	}

	public boolean isValidIndex(int pIndex) {
		synchronized (mDownloadList) {
			try {
				mDownloadList.get(pIndex);
				return true;
			} catch (IndexOutOfBoundsException e) {
				return false;
			}
		}
	}

	/**
	 * Get a download class, based on the enclosure.
	 * 
	 * @param encl
	 *            Enclosure
	 * @return Download
	 */
	public Download getDownload(IXPersonalEnclosure encl) {
		synchronized (mDownloadList) {
			Iterator it = mDownloadList.iterator();
			while (it.hasNext()) {
				Download d = (Download) it.next();
				if (d.getEnclosure() == encl) {
					return d;
				}
			}
		}
		return null;
	}

	/**
	 * Get the download list.
	 * 
	 * @return Object[]
	 */
	public Object[] getDownloadArray() {
		synchronized (mDownloadList) {
			return mDownloadList.toArray();
		}
	}

	public int getDownloadIndexOf(Download pDownload) {
		synchronized (mDownloadList) {
			return mDownloadList.indexOf(pDownload);
		}
	}

	/**
	 * Get the download list.
	 * 
	 * @return LinkedList
	 */
	public Iterator getDownloadIterator() {
		return mDownloadList.iterator();
	}

	/**
	 * The number of currently ongoing downloads;
	 * 
	 * @return int
	 */
	public int getNumberOfPauzedDownloads() {
		int count = 0;
		synchronized (mDownloadList) {
			Iterator it = mDownloadList.iterator();
			while (it.hasNext()) {
				count += ((Download) it.next()).getState() == PAUZED ? 1 : 0;
			}
		}
		return count;
	}

	/**
	 * The number of all entries in the download list.
	 * 
	 * @return int
	 */
	public int getNumberOfDownloadItems() {
		synchronized (mDownloadList) {
			return mDownloadList.size();
		}
	}

	/**
	 * The number of currently ongoing downloads; including status
	 * <code>DOWNLOADING</code>, <code>CONNECTING</code>,
	 * <code>QUEUED</code> and <code>RETRYING</code>
	 * 
	 * @return int
	 */
	public int getNumberOfActiveDownloads() {
		int count = 0;
		synchronized (mDownloadList) {
			Iterator it = mDownloadList.iterator();
			while (it.hasNext()) {
				int lState = ((Download) it.next()).getState();
				if (lState == DOWNLOADING || lState == RETRYING
						|| lState == CONNECTING || lState == QUEUED) {
					count++;
				}
			}
		}
		return count;
	}

	public boolean isDownloading() {
		return getNumberOfActiveDownloads() == 0;
	}

	/**
	 * Query if a specific enclosure is in the download list and the status is
	 * downloading. This convenience class of of particular use before
	 * initiating a download to avoid a download conflict.
	 * 
	 * @param encl
	 * @return boolean If the enclosure is being downloaded.
	 */
	public boolean isDownloading(IXPersonalEnclosure encl) {
		synchronized (mDownloadList) {
			Iterator it = mDownloadList.iterator();
			while (it.hasNext()) {
				IXPersonalEnclosure dEncl = ((Download) it.next())
						.getEnclosure();
				String file;
				try {
					file = dEncl.getURL().getFile();
					String cfile = encl.getURL().getFile();
					if (file.equals(cfile)) {
						return true;
					}
				} catch (XEnclosureException e) {
				}
			}
		}
		return false;
	}

	/**
	 * Retry a download.(Not possible in status pauzed).
	 */
	public void retry(Download pDownload) {
		if (!mPauzed) {
			resetDownload(pDownload);
			download(pDownload);
		} else {
			mLog.info("Retry not allowed in status pauzed");
		}
	}

	/**
	 * Check the status of this service.
	 * 
	 * @return
	 */
	public boolean getPauzed() {
		return mPauzed;
	}

	/**
	 * Reset the download object. (Basically same as a new download object).
	 * 
	 * @param d
	 * @return
	 */
	public Download resetDownload(Download d) {

		boolean match = false;
		Iterator it = mDownloadList.iterator();
		while (it.hasNext()) {
			Download d1 = (Download) it.next();
			if (d1.equals(d)) {
				match = true;
				break;
			}
		}
		if (match) {
			d.setState(IDLE);
			d.timeElapsed = 0;
			d.bytespersecond = 0;
			d.setCurrent(0);
			d.mPrevious = 0;
		}
		return d;
	}

	public Download resetDownloadRate(Download d) {

		boolean match = false;
		Iterator it = mDownloadList.iterator();
		while (it.hasNext()) {
			Download d1 = (Download) it.next();
			if (d1.equals(d)) {
				match = true;
				break;
			}
		}
		if (match) {
			d.bytespersecond = 0;
		}
		return d;
	}

	/**
	 * Listen for network events.
	 * 
	 * @see com.jpodder.net.INetTaskListener#netActionPerformed(com.jpodder.net.NetTaskEvent)
	 */
	public void netActionPerformed(NetTaskEvent event) {

		Download lDownload = (Download) event.getSource();
		int lState = lDownload.getState();
		IXPersonalEnclosure lEnclosure = lDownload.getEnclosure();

		if (event.getNetEvent() == NetTaskEvent.DOWNLOAD_SUCCESS) {
			Cache.getInstance().addTrack(lEnclosure.getFile());
			lEnclosure.setDownloadCompleted(true);
			lEnclosure.setCached(true);
			lEnclosure.setLocal(true);

			lEnclosure.setMarked(false);
			lEnclosure.setCandidate(false);
			lEnclosure.getFeed().setCandidatesCount(
					lEnclosure.getFeed().getCandidatesCount() - 1);
			mLog.info("Download of: " + lEnclosure.getFile() + " completed");

			if (ContentLogic.isTorrent(lEnclosure.getFile().getName())) {
				mLog.info("Process torrent file.");
				lEnclosure.setTorrent();
			} else {
				mLog.info("Process file (non-torrent).");

				// CB TODO In Concurrency, we would like to exit this thread and
				// let another thread post-process the download. Using
				// semaphores? Also which tasks are executed should be
				// configurable.

				ID3Logic.getInstance().rewriteTags(lEnclosure);
				PlayerLogic.getInstance().storeInPlayer(lEnclosure);
			}
		}

		if (event.getNetEvent() == NetTaskEvent.DOWNLOAD_STATUS_CHANGED) {
			mLog.info("Download of: " + lEnclosure + " changed state to: "
					+ STATE_DESCRIPTION[lState]);
		}
		if (event.getNetEvent() == NetTaskEvent.DOWNLOAD_FAILED) {
			mLog.error("Error while downloading " + lEnclosure);
			// retry the download.
			if (event.getException() != null) {
				String message = event.getException().getMessage();
				mLog.error(message);
				if (message != null) {
					if (message.startsWith("Read timed")
							|| message.startsWith("Partial content")
							|| message.startsWith("Connection reset")
							|| message.startsWith("Connection timed out")) {
						DownloadsRetry lRetry = new DownloadsRetry();
						lRetry.retryLater(lDownload);
					}
				}
			}
			// Delete the file if the download failed totally (size =0).

			File file = lEnclosure.getFile();
			if (file != null && file.exists() && file.length() == 0) {
				file.delete();
			}
		}

		// Do some state updating on the model.
		switch (lState) {
		case DOWNLOADING:
		case QUEUED:
		case CONNECTING:
		case RELEASING:
			break;
		case RETRYING:
		case PAUZED: {
			lDownload.bytespersecond = 0;
		}
			break;
		case ERROR:
		case CANCELLED: {
			lDownload.bytespersecond = 0;
		}
			break;
		case COMPLETED: {
			lDownload.bytespersecond = 0;
			if (Configuration.getInstance().getSound()) {
				Toolkit.getDefaultToolkit().beep();
			}
		}
			break;

		}
	}

	/**
	 * Update elapsed time (+ 1 second) and bandwith for each download.
	 * 
	 */
	public void heartBeat() {
		synchronized (mDownloadList) {

			Iterator it = mDownloadList.iterator();
			while (it.hasNext()) {
				Download lDownload = (Download) it.next();
				switch (lDownload.getState()) {
				case COMPLETED:
				case QUEUED:
				case CANCELLED:
				case PAUZED:
				case ERROR:
					break;
				case CONNECTING:
				case RELEASING:
				case RETRYING: {
					lDownload.timeElapsed += 1000;
				}
					break;
				case DOWNLOADING: {
					lDownload.timeElapsed += 1000;
					lDownload.calculateSpeed();
					if (lDownload.mPrevious != lDownload.getCurrent()) {
						lDownload.mPrevious = lDownload.getCurrent();
					}
				}
					break;
				}
			}
		}
	}

	/**
	 * Hearbeat action.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		heartBeat();
	}

	public void dataRemoved(DataEvent pEvent) {
		if (pEvent.getSource() instanceof IXPersonalFeed) {
			// walk through the list of downloads and check if we are
			// downloading
			// for this feed.
		}
	}

	public void dataWillBeRemoved(DataEvent pEvent) {
		if (pEvent.getSource() instanceof IXPersonalFeed) {
			abort((IXPersonalFeed)pEvent.getSource());
		}
	}

}