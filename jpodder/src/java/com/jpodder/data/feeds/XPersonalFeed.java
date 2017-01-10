package com.jpodder.data.feeds;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.JPodderException;
import com.jpodder.data.configuration.IDataController;
import com.jpodder.data.feeds.stats.XFeedEventHistory;
import com.jpodder.data.feeds.stats.XFeedInstruction;
import com.jpodder.data.feeds.xmlbeans.XFeed;
import com.jpodder.data.id3.ID3TagRewrite;
import com.jpodder.net.NetHEADInfo;
import com.jpodder.util.TokenHandler;
import com.jpodder.util.Util;

/**
 * A feed class
 * 
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.0
 */
public class XPersonalFeed extends XFeed implements IXPersonalFeed {

	private static final boolean MERGE_MIX = true;

	private FeedCredentials mCredentials = null;

	static final private Logger sLog = Logger.getLogger(XPersonalFeed.class
			.getName());

	/** If the feed should be polled * */
	private boolean mSubscribed;

	/** Quality metric for this feed * */
	private int mQuality;

	/** Quality description * */
	private String mQualityDescription = "";

	/** The maximum number of downloads for this feed * */
	private int mMaxDownloads;

	/** A collection of local files * */
	// TODO REMOVE LATER private List files;
	/** A collection of local files & enclosures * */
	// TODO REMOVE LATER// private List mMergedItems;
	/** A collection of enclosures * */
	private List<IXPersonalEnclosure> mEnclosures = new ArrayList<IXPersonalEnclosure>();

	/** The feed HTTP HEAD information * */
	private NetHEADInfo mHeaderInformation;

	/** The folder on the file system for the feed * */
	private String mLocalFolder;

	/** An activity instructions when tasks are invoked * */
	private XFeedInstruction mCurrentInstruction;

	/** The number of candidates for this feed * */
	private int mCandidateCount;

	public XFeedEventHistory mFeedHistory = new XFeedEventHistory();

	/**
	 * The tags which should be re-written for this feed. Comment for
	 * <code>tagsForRewriteList</code>
	 */
	private ArrayList mID3RewriteList = new ArrayList();

	/**
	 * Data Controller to which this Data object belongs to and if null then it
	 * is not managed by it means it is not going to be saved
	 */
	private IDataController mController = new IDataController.NullDataController();

	/**
	 * The URL of the feed. Comment for <code>mFeedURL</code>
	 */
	private URL mFeedURL;

	/**
	 * The redirect URL.
	 */
	private URL mRedirecturl = null;

	/**
	 * The personal title when the feed is not available.
	 */
	private String mPersonalTitle;

	private String mFormatedFolderSize;

	/**
	 * Constructor.
	 */
	public XPersonalFeed() {
		super(null, false);
	}

	public XPersonalFeed(URL url, boolean poll, int quality, int maxDownloads) {
		this(url, poll, quality, maxDownloads, "");
	}

	/**
	 * Constructor.
	 * 
	 * @param pFeedURL
	 *            URL
	 * @param pSubscribed
	 *            boolean
	 * @param pQuality
	 *            int
	 * @param mMaxDownloads
	 *            int
	 * @param pTitle
	 *            String
	 */
	public XPersonalFeed(URL pFeedURL, boolean pSubscribed, int pQuality,
			int pMaxDownloads, String pTitle) {
		super(null, false);
		mFeedURL = pFeedURL;
		mSubscribed = pSubscribed;
		mQuality = pQuality;
		mMaxDownloads = pMaxDownloads;
		setPersonalTitle(pTitle);
	}

	/**
	 * Set the data controller this object belong to
	 * 
	 * @param pController
	 *            Data Controller of this instance
	 */
	public void setDataController(IDataController pController) {
		if (pController == null) {
			mController = new IDataController.NullDataController();
		} else {
			mController = pController;
		}
	}

	/**
	 * Delivers the enclosure with the given index if available
	 * 
	 * @param pIndex
	 *            Index of the Enclosure in the list
	 * 
	 * @return Enclosure if found
	 */
	public IXPersonalEnclosure getEnclosure(int pIndex) {
		return (IXPersonalEnclosure) mEnclosures.get(pIndex);
	}

	/**
	 * Delivers the index with the given enclosure if available
	 * 
	 * @return pIndex Index of the Enclosure in the list
	 */

	public int indexOf(IXPersonalEnclosure pEnclosure) {
		return mEnclosures.indexOf(pEnclosure);
	}

	public int indexOf(IXFile pFile) {
		return getMerged(true).indexOf(pFile);
	}

	/**
	 * Adds the given Enclosure to our list of Enclosures We also attach the
	 * giving enclosure with the RSS model.
	 * 
	 * @param aEnclosure
	 *            A new enclosure to be added at the end of the list
	 */
	public void addEnclosure(IXPersonalEnclosure pEnclosure) {
		// When adding, we also set the RSS Enclosure model.
		// int lIndex = mEnclosures.size();

		URL lUrl = pEnclosure.getPersonalURL();
		IXItem lItem = null;
		try {
			Iterator it = getItemIterator();
			while (it.hasNext()) {
				lItem = (IXItem) it.next();
				try {
					IXEnclosure lEnclosure = lItem.getEnclosure();
					if (lEnclosure != null) {
						try {
							if (lEnclosure.getURL().toExternalForm().equals(
									lUrl.toExternalForm())) {
								// we found the item, let's leave the loop.
								break;
							}
						} catch (XEnclosureException e) {
							sLog.debug("addEnclosure()" + e);
						}
					}
				} catch (XItemException e) {
					sLog.debug("addEnclosure()" + e);
				}
			}
		} catch (XFeedException e1) {
			sLog.debug("addEnclosure()" + e1);
		}
		if (lItem != null) {
			pEnclosure.setItem(lItem);
		}
		mEnclosures.add(pEnclosure);
		mController.setModified();
	}

	/**
	 * Update the enclosures from a newly received RSS file. Keep the Personal
	 * enclosure settings in case of overlap. The enclosure mapping is based on
	 * the URL of the enclosure.
	 * <p>
	 * 
	 * @param pFile
	 * @return int The number of newly found enclosures.
	 */
	public int updateEnclosures(File pFile) throws XFeedException {
		ArrayList<IXPersonalEnclosure> lEnclosures = new ArrayList<IXPersonalEnclosure>();
		int newEnclosures = 0;
		setFile(pFile);
		XFeed lFeed = new XFeed(pFile, false);
		sLog.info("Merging enclosures for " + this);
		Iterator it = lFeed.getItemIterator();
		while (it.hasNext()) {
			IXItem lItem = (IXItem) it.next();
			try {
				IXEnclosure lEnclosure = lItem.getEnclosure();
				URL lURL = lEnclosure.getURL();
				Iterator it1 = getEnclosureIterator();
				boolean match = false;
				IXPersonalEnclosure lEnclosure1 = null;
				while (it1.hasNext()) {
					lEnclosure1 = (IXPersonalEnclosure) it1.next();
					if (lURL.toExternalForm().equals(
							lEnclosure1.getPersonalURL().toExternalForm())) {
						match = true;
						break;
					}
				}
				XPersonalEnclosure lNewEnclosure = new XPersonalEnclosure(this,
						lItem, lEnclosure);

				lNewEnclosure.setPersonalURL(lURL);
				if (match) {
					if (lEnclosure1 != null) {
						lNewEnclosure.setCached(lEnclosure1.isCached());
						lNewEnclosure.setMarked(lEnclosure1.isMarked());
						lNewEnclosure.setInPlayer(lEnclosure1.getInPlayer());
						lNewEnclosure.setContentSize(lEnclosure1
								.getContentSize());
						// CB TODO Also set the date.
						lNewEnclosure.setFileName(lEnclosure1.getFile()
								.getName());
						lNewEnclosure.setLocal(lEnclosure1.isLocal());
					} else {
						sLog.warn("Illegal state: Programmatic error");
					}
				} else {// New Enclosure!
					newEnclosures++;
					sLog.info("New Enclosure found!" + lEnclosure);
					getInstruction().setMark(true);
				}
				lEnclosures.add(lNewEnclosure);
			} catch (XItemException e1) {
				sLog.debug("updateEnclosures() " + e1.getMessage());
			} catch (XEnclosureException e2) {
				// Something wrong with URL.
				sLog.debug("updateEnclosures() " + e2.getMessage());
			}
		}
		setRSSDocument(lFeed);
		lFeed.release();
		mEnclosures = lEnclosures;
		return newEnclosures;
	}

	/**
	 * Removes the given Enclosure from our list of Enclosures
	 * 
	 * @param aEnclosure
	 *            A new enclosure to be removed from the list
	 */

	// public void removeEnclosure(XEnclosureImpl pEnclosure) {
	// super.getItems().indexOf();
	// enclosures.remove(pEnclosure);
	// mController.setModified();
	// }
	/**
	 * Get the number of personal enclosures.
	 * 
	 * @return
	 */
	public int getEnclosureSize() {
		return mEnclosures.size();
	}

	public Iterator getEnclosureIterator() {
		return mEnclosures.iterator();
	}

	public Object[] getEnclosureArray() {
		return mEnclosures.toArray();
	}

	/**
	 * Overrides the super class method. Returns the I18n for an unknown title.
	 * 
	 * @see com.jpodder.data.feeds.IXFeed#getTitle()
	 */
	public String getTitle() {

		if (getPersonalTitle().equals(TokenHandler.RSS_ITEM_TITLE)) {
			try {
				String lTitle = super.getTitle();
				setPersonalTitle(lTitle);
			} catch (XFeedException e) {
			}
		}
		return getPersonalTitle();

	}

	/**
	 * Get the personal title of this feed. If the personal title is unknow,
	 * return untitled.
	 * 
	 * @return
	 */
	public String getPersonalTitle() {
		if (mPersonalTitle == null || mPersonalTitle.equals("")) {
			return TokenHandler.RSS_ITEM_TITLE;
		} else {
			return mPersonalTitle;
		}
	}

	/**
	 * Set the personal title of this feed.
	 * 
	 * @param pTitle
	 */
	public void setPersonalTitle(String pTitle) {
		mPersonalTitle = pTitle;
	}

	/**
	 * Get the local files in the podcast folder. The file folder is parsed and
	 * returned as a collection. The collection take also a temporary state
	 * which can be modified. (Files beeing added and removed). The application
	 * is responsible for synchroning the collection with the folder state.
	 * 
	 * @param refresh
	 *            boolean Rescans the files directory if requested.
	 * @return Vector
	 */
	public List getFiles(boolean refresh) {

		ArrayList _files = new ArrayList();
		if (refresh) {
			if (mLocalFolder != null) {
				File folderFile = new File(mLocalFolder);
				if (folderFile.exists() && folderFile.isDirectory()) {
					File[] children = folderFile.listFiles();
					if (children != null) {
						for (int index = 0; index < children.length; index++) {
							_files.add(new XLocalFile(children[index], this));
						}
					}
				}
			}
		}
		return Collections.synchronizedList(_files);
	}

	/**
	 * Returns the total size of the files in the folder which are not
	 * enclosures of the RSS feed.
	 * 
	 * @return String total size of this feed's folder space excluding
	 *         enclosures.
	 */
	public String getAccumulatedFolderSize(boolean pRefresh) {
		if (mFormatedFolderSize == null || pRefresh) {
			int total = 0;
			Iterator it = getFiles(true).iterator();
			while (it.hasNext()) {
				XLocalFile file = (XLocalFile) it.next();
				total += file.getFileLength();
			}
			mFormatedFolderSize = Util.formatSize(total);
		}
		return mFormatedFolderSize;
	}

	/**
	 * Returns the total size of the folder.
	 * 
	 * @return String total size of this feed's folder space
	 */

	public String getAccumulatedMergedSize() {
		long total = 0;
		Iterator it = getMerged(true).iterator();
		while (it.hasNext()) {
			IXFile file = (IXFile) it.next();
			if (file.getFile() != null)
				total += file.getFile().length();
		}
		return Util.formatSize(total);
	}

	/**
	 * Get all the marked files.
	 * 
	 * @return int Get the number of files which are marked.
	 */
	public int getMarkedFilesCount() {
		int count = 0;
		Iterator it = getFiles(false).iterator();
		while (it.hasNext()) {
			IXFile file = (IXFile) it.next();
			if (file.isMarked())
				count++;
		}
		return count;
	}

	/**
	 * Get the number of files or enclosures for this feed.
	 * 
	 * @param onlyLocal
	 *            Specify if local or all files/enclosures should be counted.
	 *            Locals are files on the local hard disc.
	 * 
	 * @return Get the number of files or enclosures for this feed.
	 */

	public int getMergedCount(boolean onlyLocal) {
		int count = 0;
		Iterator it = getMerged(false).iterator();
		while (it.hasNext()) {
			IXFile file = (IXFile) it.next();
			boolean countRemote = true;
			if (onlyLocal) {
				if (!file.isLocal()) {
					countRemote = false;
				}
			}
			if (countRemote && file.isMarked())
				count++;
		}
		return count;
	}

	/**
	 * Get the number of download candidates. A candidate is marked and fits
	 * within the limit of maximum downloadable enclosures from this feed.
	 * 
	 * @return in Get the number of files which are a candidate to be
	 *         downloaded.
	 */
	public int getCandidatesCount() {
		return mCandidateCount;
	}

	private int countCandidates() {
		int counter = 0;
		Iterator it = getEnclosureIterator();
		while (it.hasNext()) {
			XPersonalEnclosure encl = (XPersonalEnclosure) it.next();
			if (encl.isCandidate()) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Set the number of download candidates.
	 * 
	 * @param pCandidatesCount
	 */
	public void setCandidatesCount(int pCandidatesCount) {
		mCandidateCount = pCandidatesCount;
	}

	/**
	 * Get the number of files which are on the local disc.
	 * 
	 * @return int Get the number of files, which are available on the local
	 *         hard disc.
	 */
	public int getOnDiscFileCount() {
		int count = 0;

		Iterator it = getFiles(true).iterator();
		while (it.hasNext()) {
			IXFile file = (IXFile) it.next();
			if (file.isLocal())
				count++;
		}
		return count;
	}

	/**
	 * Set the header information.
	 * 
	 * @param head
	 */
	public void setHEADInfo(NetHEADInfo pHeaderInformation) {
		mHeaderInformation = pHeaderInformation;
		mController.setModified();
	}

	/**
	 * Merges the enclosures and the local files. Remerging occures when the
	 * refresh argument is <code>true</code>. Merging adds all enclosures and
	 * the the non-duplicate files.
	 * 
	 * @param refresh
	 *            When <code>true<code>, the files and enclosures for this feef
	 * will be re-merged.
	 * 
	 * @return Vector
	 */
	public List getMerged(boolean refresh) {
		List lMerged = new ArrayList();

		if (mEnclosures != null) {
			// CB TODO, We sort enclosures here. Rethink, to
			// perhaps sort when adding enclosures. This
			// method is called frequently.
			Collections.sort(mEnclosures, new Comparator() {
				public int compare(Object pObj1, Object pObj2) {

					if (pObj1 instanceof IXPersonalEnclosure
							&& pObj2 instanceof IXPersonalEnclosure) {
						IXPersonalEnclosure lEncl1 = (IXPersonalEnclosure) pObj1;
						IXPersonalEnclosure lEncl2 = (IXPersonalEnclosure) pObj2;
						try {
							String lD1 = lEncl1.getItem().getPubDate();
							String lD2 = lEncl2.getItem().getPubDate();
							Date lDate1 = Util.resolvedDateRFC822(lD1);
							Date lDate2 = Util.resolvedDateRFC822(lD2);
							return lDate2.compareTo(lDate1);
						} catch (XEnclosureException e) {
							return 0;
						} catch (XItemException e) {
							return 0;
						} catch (ParseException e) {
							return 0;
						} catch (IllegalArgumentException iae) {
							return 0;
						}
					}
					return 0;
				}
			});
			lMerged.addAll(mEnclosures);
		}
		if (refresh) {
			List lFiles = getFiles(refresh);
			if (lFiles != null) {
				Iterator it = lFiles.iterator();
				while (it.hasNext()) {
					IXFile file = (IXFile) it.next();
					Iterator it1 = mEnclosures.iterator();
					boolean match = false;
					while (it1.hasNext()) {
						XPersonalEnclosure encl = (XPersonalEnclosure) it1
								.next();
						if (file.getName().equals(encl.getName())) {
							match = true;
							break;
						}
					}
					if (!match && file != null) {
						lMerged.add(file);
					}
				}
			}
		} else {
			// Files == null, will not happen.
		}
		return lMerged;
	}

	public Object[] getMergedArray(boolean refresh) {
		return getMerged(refresh).toArray();
	}

	/**
	 * Return all enclosures which have status local + all folder files.
	 * 
	 * @return
	 */
	public Object[] getLocals() {
		ArrayList lLocals = new ArrayList();
		List lMerged = getMerged(false);
		Iterator it = lMerged.iterator();
		while (it.hasNext()) {
			IXFile lWrapper = (IXFile) it.next();
			if (lWrapper.isLocal()) {
				lLocals.add(lWrapper);
			}
		}
		return lLocals.toArray();
	}

	/**
	 * Set the maximum number of downloadable enclosures for this feed(index).
	 * 
	 * @param pMaxDownloads
	 *            int
	 */
	public void setMaxDownloads(int pMaxDownloads) {
		mMaxDownloads = pMaxDownloads;
		mController.setModified();
	}

	/**
	 * Set the folder of this feed.
	 * 
	 * @param pLocalFolder
	 */
	public void setFolder(String pLocalFolder) {
		mLocalFolder = pLocalFolder;
		mController.setModified();
	}

	/**
	 * Get the folder of this feed.
	 * 
	 * @return String Get the folder associated with this feed.
	 */
	public String getFolder() {
		return mLocalFolder;
	}

	/**
	 * Get a description of this feed to be used in a ToolTip text.
	 * 
	 * @return String.
	 */
	public String getToolTipDescription() {

		try {
			return getDescription();
		} catch (XFeedException e) {
		}
		return "";
	}

	/**
	 * Get the maximum number of downloadable enclosures from this feed(index).
	 * This is a wrapper for a tablemodel.
	 * 
	 * @return int
	 */
	public int getMaxDownloads() {
		return mMaxDownloads;
	}

	/**
	 * Get the URL Head information.
	 * 
	 * @return NetHEADInfo
	 * @see NetHEADInfo
	 */
	public NetHEADInfo getHEADInfo() {
		return mHeaderInformation;
	}

	/**
	 * Get the quality of this feed. The quality can be;
	 * <code>GOOD_QUALITY</code>,<code>MEDIUM_QUALITY</code>,
	 * <code>BAD_QUALITY</code> or <code>UNKNOW_QUALITY</code>.
	 * 
	 * @return Integer
	 */
	public int getQuality() {
		return mQuality;
	}

	/**
	 * Set the quality of this feed.
	 * 
	 * @param pQuality
	 *            int
	 */
	public void setQuality(int pQuality) {
		this.mQuality = pQuality;
		mController.setModified();
	}

	public URL getURL() {
		return mFeedURL;
	}

	/**
	 * Set the feed URL.
	 * 
	 * @param url
	 *            URL
	 */
	public void setURL(URL pUrl) {
		mFeedURL = pUrl;
		mController.setModified();
	}

	/**
	 * Get the URL of the feed.
	 * 
	 * @return URL
	 */
	public URL getRedirectURL() {
		return mRedirecturl;
	}

	/**
	 * Set the feed URL.
	 * 
	 * @param url
	 *            URL
	 */
	public void setRedirectURL(URL pRedirecturl) {
		mRedirecturl = pRedirecturl;
		mController.setModified();
	}

	/**
	 * Gets if the feed should be polled. This is a wrapper for a tablemodel.
	 * 
	 * @return boolean
	 */
	public boolean getPoll() {
		return mSubscribed;
	}

	/**
	 * Set if the feed should be polled.
	 * 
	 * @param value
	 *            boolean
	 */
	public void setSubscribed(boolean value) {
		this.mSubscribed = value;
		mController.setModified();
	}

	/**
	 * Returns the status of the given index
	 * 
	 * @param pIndex
	 *            The index of the related feed.
	 * @return Either -1 indicating it is a local file (or no file available) or
	 *         the status as sum of: 2: indicating a downloadable candidate 4:
	 *         indicating a incomplete download
	 */
	public int getEnclosureStatus(int pIndex) {
		int lReturn = -1;
		if (pIndex < getEnclosureSize()) {
			lReturn = 0;
			IXPersonalEnclosure encl = (IXPersonalEnclosure) getEnclosure(pIndex);
			if (getPoll() && encl.isCandidate() && encl.isMarked()) {
				lReturn = lReturn + 2;
			}
			try {
				if (encl.isIncomplete()) {
					lReturn = lReturn + 4;
				}
			} catch (JPodderException e) {
			}
			
			if(encl.isLocal()){
				lReturn = lReturn + 8;
			}
		}
		return lReturn;
	}

	/**
	 * Set the instruction for this feed.
	 * 
	 * @param src
	 * 
	 * @param collect
	 *            boolean
	 * @param encl
	 *            boolean
	 * @param mark
	 *            boolean
	 * @param inspect
	 *            boolean
	 * @param download
	 *            boolean
	 * @param store
	 *            boolean
	 * @see FeedInstruction
	 */
	public void setInstruction(Object src, boolean collect, boolean encl,
			boolean mark, boolean inspect, boolean download, boolean store) {
		mCurrentInstruction = new XFeedInstruction(src, collect, encl, mark,
				inspect, download, store);
		mFeedHistory.addEvent(this, mCurrentInstruction);
	}

	/**
	 * Get the current instruction. Instructions are volatile. This method
	 * returns only the current instruction.
	 * 
	 * @return FeedInstruction.
	 */
	public XFeedInstruction getInstruction() {
		return mCurrentInstruction;
	}

	/**
	 * Get if the inspection has been ordered before. This is to avoid
	 * un-necessary resource insentive inspection.
	 * 
	 * @return
	 */
	public boolean getInspectInstructed() {
		if (mCurrentInstruction != null) {
			if (mCurrentInstruction.isInspect()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * * This methods flags the enclosures in a feed, which are a candidate for
	 * downloading. The first # of enclosures are marked according to the
	 * maximum number of downloads for the feed.
	 * 
	 * @return int The number of candidates
	 */
	public int updateAllCandidates(boolean pUpdateMax) {
		int counter = 0;
		int maxDownloads = getMaxDownloads();
		Iterator it = getEnclosureIterator();
		while (it.hasNext()) {
			XPersonalEnclosure encl = (XPersonalEnclosure) it.next();
			if (encl.isMarked()) {
				if (pUpdateMax) {
					if (counter < maxDownloads) {
						counter++;
						encl.setCandidate(true);
					} else {
						encl.setCandidate(false);
					}
				} else {
					encl.setCandidate(true);
				}
			} else {
				encl.setCandidate(false);
			}
		}
		setCandidatesCount(counter);
		return counter;
	}

	public void updateSingleCandidate(IXPersonalEnclosure encl,
			boolean pUpdateMax) {
		int counter = getCandidatesCount();
		int maxDownloads = getMaxDownloads();
		if (encl.isMarked()) {
			if (pUpdateMax) {
				if (counter < maxDownloads) {
					encl.setCandidate(true);
				} else {
					encl.setCandidate(false);
				}
			} else {
				encl.setCandidate(true);
			}
		} else {
			encl.setCandidate(false);
		}
		setCandidatesCount(countCandidates());
	}

	public class FeedCredentials {
		protected String password;

		protected String name;

		/**
		 * @param password
		 * @param name
		 */
		public FeedCredentials(String name, String password) {
			super();
			this.password = password;
			this.name = name;
		}

		/**
		 * @return Returns the name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            The name to set.
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return Returns the password.
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @param password
		 *            The password to set.
		 */
		public void setPassword(String password) {
			this.password = password;
		}
	}

	/**
	 * @return Returns the credentials.
	 */
	public FeedCredentials getCredentials() {
		return mCredentials;
	}

	/**
	 * @param _credentials
	 *            The credentials to set.
	 */
	public void setCredentials(FeedCredentials _credentials) {
		this.mCredentials = _credentials;
	}

	/**
	 * @return Returns the history.
	 */
	public XFeedEventHistory getHistory() {
		return mFeedHistory;
	}

	/**
	 * @param history
	 *            The history to set.
	 */
	public void setHistory(XFeedEventHistory history) {
		this.mFeedHistory = history;
	}

	/**
	 * Add an id3Tag.
	 * 
	 * @param tag
	 */
	public void addId3Tag(ID3TagRewrite tag) {
		if (!mID3RewriteList.contains(tag)) {
			mID3RewriteList.add(tag);
			mController.setModified();
			Collections.sort(mID3RewriteList, new Comparator() {
				public int compare(Object pFirst, Object pSecond) {
					ID3TagRewrite lFirst = (ID3TagRewrite) pFirst;
					ID3TagRewrite lSecond = (ID3TagRewrite) pSecond;
					return lFirst.getName().compareTo(lSecond.getName());
				}

				public boolean equals(Object pTest) {
					return pTest == this;
				}
			});
		}
	}

	public void removeId3Tag(ID3TagRewrite tag) {
		sLog.info("removeId3Tag(), tag: " + tag + ", list: " + mID3RewriteList);
		if (mID3RewriteList.contains(tag)) {
			sLog.info("removeId3Tag(), tag found so remove it");
			mID3RewriteList.remove(tag);
			mController.setModified();
			sLog.info("removeId3Tag(), list after removal: " + mID3RewriteList);
		}
	}

	/**
	 * get an id3Tag.
	 * 
	 * @param name
	 * @return ID3TagRewrite Returns <code>null</code> if no tag is found
	 *         whith this name.
	 */
	public ID3TagRewrite getId3Tag(String name) {
		for (Iterator iter = mID3RewriteList.iterator(); iter.hasNext();) {
			ID3TagRewrite element = (ID3TagRewrite) iter.next();
			if (element.getName().equals(name)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * @return Iterator on the Tags for Rewrite List
	 */
	public Iterator getTagListIterator() {
		return mID3RewriteList.iterator();
	}

	/**
	 * @return Rewrite List
	 */
	public void setTagList(List pTagsForRewriteList) {
		mID3RewriteList = (ArrayList) pTagsForRewriteList;
	}

	/**
	 * @return Rewrite List
	 */
	public List getTagList() {
		return mID3RewriteList;
	}

	/**
	 * @return Returns the qualityDescription.
	 */
	public String getQualityDescription() {
		return mQualityDescription;
	}

	/**
	 * @param pQualityDescription
	 *            The qualityDescription to set.
	 */
	public void setQualityDescription(String pQualityDescription) {
		if (pQualityDescription != null) {
			mQualityDescription = pQualityDescription;
		} else {
			throw new IllegalArgumentException();
		}

	}

	public String toString() {

		StringBuffer lBuf = new StringBuffer();

		String lFolder = getFolder();
		if (lFolder != null) {
			lBuf.append(" \"" + Util.getName(lFolder) + "\" ");
		}
		URL lUrl = getURL();
		if (lUrl != null) {
			lBuf.append(" \"" + lUrl.toExternalForm() + "\" ");
		}
		return lBuf.toString();
	}

	public void releaseEnclosures() throws XFeedException {
		mEnclosures.clear();
	}

}