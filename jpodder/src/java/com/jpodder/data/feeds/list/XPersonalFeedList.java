package com.jpodder.data.feeds.list;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IDataController;
import com.jpodder.data.feeds.IXFeed;
import com.jpodder.data.feeds.IXPersonalEnclosure;
import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.XFeedDataHandler;
import com.jpodder.data.feeds.XPersonalFeed;
import com.jpodder.net.NetHEADInfo;
import com.jpodder.ui.swt.feeds.FeedController;
import com.jpodder.util.Messages;

/**
 * A collection of RSS feed objects available to the application.
 * 
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class XPersonalFeedList implements IDataController {

	public static final int GOOD_QUALITY = 100;

	public static final int MEDIUM_QUALITY = 101;

	public static final int BAD_QUALITY = 102;

	public static final int UNKNOW_QUALITY = 103;

	private static XPersonalFeedList sInstance = new XPersonalFeedList();

	public static XPersonalFeedList getInstance() {
		return sInstance;
	}

	private static FeedsSummary lSummary = new FeedsSummary();

	private Logger mLog = Logger.getLogger(getClass().getName());

	private CallbackList mFeedList = new CallbackList(this);

	private XFeedDataHandler mDataHandler = new XFeedDataHandler(this);

	private boolean mIsModified;

	/**
	 * Constructor. Creates the collection.
	 */
	private XPersonalFeedList() {
		try {
			ConfigurationLogic.getInstance().addDataHandler(mDataHandler);
		} catch (Exception e) {
			mLog.warn("Read feedlist error:" + e.getMessage());
		}
	}

	public int size() {
		return mFeedList.size();
	}

	public void swap(int lFirstIndex, int lSecondIndex) {
		int lSize = mFeedList.size();
		if (lFirstIndex != lSecondIndex) {
			if (lFirstIndex < 0 || lFirstIndex >= lSize) {
				return;
			}
			if (lSecondIndex < 0 || lSecondIndex >= lSize) {
				return;
			}
			Object lFirst = mFeedList.get(lFirstIndex);
			Object lSecond = mFeedList.get(lSecondIndex);
			mFeedList.set(lFirstIndex, lSecond);
			mFeedList.set(lSecondIndex, lFirst);
			setModified();
		}
	}

	/**
	 * Get the feed object.
	 * 
	 * @param index
	 *            int
	 * @return Feed
	 * @see Feed
	 */
	public IXPersonalFeed getFeed(int index) {
		if (index >= 0 && index < mFeedList.size()) {
			IXPersonalFeed lFeed = (IXPersonalFeed) mFeedList.get(index);
			return lFeed;
		} else
			return null;
	}

	/**
	 * Get the index of a feed.
	 * 
	 * @param feed
	 *            Feed
	 * @return int
	 */
	public int getIndexOf(IXFeed feed) {
		return mFeedList.indexOf(feed);
	}

	public int indexOfUrl(String pUrl) {
		Object[] lFeeds = mFeedList.toArray();
		for (int i = 0; i < lFeeds.length; i++) {
			IXPersonalFeed lFeed = (IXPersonalFeed) lFeeds[i];
			if (lFeed.getURL().toExternalForm().equals(pUrl)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Add an already created feed;
	 * 
	 * @param feed
	 */
	public void addFeed(IXPersonalFeed pFeed) {
		mFeedList.add(pFeed);
		((XPersonalFeed) pFeed).setDataController(this);
		setModified();
	}

	/**
	 * Add a new feed with respective feed values;
	 * 
	 * @param url
	 *            URL
	 * @param poll
	 *            boolean
	 * @param quality
	 *            int
	 * @param maxDownloads
	 *            int
	 * @return Feed The newly create feed.
	 */
	public IXFeed addFeed(URL url, boolean poll, int quality, int maxDownloads) {
		XPersonalFeed lFeed = new XPersonalFeed(url, poll, quality,
				maxDownloads);
		lFeed.setHEADInfo(new NetHEADInfo());
		addFeed(lFeed);
		return lFeed;
	}

	/**
	 * Add (Merge) a list of feeds into the existing poll model list.
	 * 
	 * @param _feeds
	 */
	public void addFeeds(List _feeds) {

		Iterator it1 = _feeds.iterator();
		while (it1.hasNext()) {
			URL feedURL = null;
			XPersonalFeed newFeed = null;
			Object o = it1.next();
			if (o instanceof URL) {
				feedURL = (URL) o;
				newFeed = new XPersonalFeed();
				newFeed.setURL(feedURL);
			} else if (o instanceof IXPersonalFeed) {
				newFeed = (XPersonalFeed) o;
			}
			if (newFeed != null) {
				addFeed(newFeed);
				// newFeed.setMaxDownloads(1);
				// newFeed.setDescription("Imported");
				// newFeed.setPoll(true);
			}
		}
	}

	/**
	 * Remove a feed from the model.
	 * 
	 * @param index
	 *            int
	 * @return Feed The removed feed.
	 */
	public void willRemoveFeed(IXPersonalFeed pFeed) {
		mFeedList.willRemove(pFeed);
	}

	/**
	 * Remove a feed from the model.
	 * 
	 * @param index
	 *            int
	 * @return Feed The removed feed.
	 */
	public IXFeed removeFeed(int index) {
		IXPersonalFeed feed = (IXPersonalFeed) mFeedList.get(index);
		mFeedList.remove(index);
		return feed;
	}

	/**
	 * Remove a feed.
	 * 
	 * @param feed
	 */
	public void removeFeed(IXPersonalFeed feed) {
		mFeedList.remove(feed);
	}

	/** @return The iterator over the feed list */
	public Iterator getFeedIterator() {
		return mFeedList.iterator();
	}

	/** @return The iterator over the feed list */
	public Object[] getFeedArray() {
		return mFeedList.toArray();
	}
	
	public CallbackList getFeedList(){
		return mFeedList;
	}
	
	/**
	 * Find a feed for a specific url.
	 * 
	 * @param url
	 *            String The url of the feed.
	 * @return Feed
	 */
	public IXPersonalFeed getFeed(String url) {
		Iterator it = mFeedList.iterator();
		while (it.hasNext()) {
			XPersonalFeed lFeed = (XPersonalFeed) it.next();
			if (lFeed.getURL() != null && lFeed.getURL().toString().equals(url)) {
				return lFeed;
			}
		}
		return null;
	}

	/**
	 * Get an array of feed from a set of indices. Invalid indices are ignored.
	 * 
	 * @param pIndices
	 * @return
	 */
	public Object[] getFeeds(int[] pIndices) {
		ArrayList lReturnList = new ArrayList();
		for (int i = 0; i < pIndices.length; i++) {
			if (i < mFeedList.size()) {
				lReturnList.add(getFeed(pIndices[i]));
			}
		}
		return lReturnList.toArray();
	}

	/**
	 * @param pFeeds
	 * @return
	 * @deprecated
	 */
	public List trimExisting(List pFeeds) {
		Iterator it1 = pFeeds.iterator();
		while (it1.hasNext()) {
			boolean foundURL = false;
			URL feedURL = null;
			Object o = it1.next();
			if (o instanceof URL) {
				feedURL = (URL) o;
			}
			if (o instanceof IXPersonalFeed) {
				feedURL = ((XPersonalFeed) o).getURL();
			}
			Iterator it = getInstance().getFeedIterator();
			while (it.hasNext()) {
				XPersonalFeed feed = (XPersonalFeed) it.next();
				// Compare URL.
				if (feedURL != null) {
					if (feed.getURL().toString().equals(feedURL.toString())) {
						foundURL = true;
					}
				}
			}
			if (foundURL) {
				it1.remove();
				setModified();
			}
		}
		return pFeeds;
	}

	/**
	 * The argument list is are paths to local folders. The list will be trimmed
	 * to only retain the entries which are not in the personal feed list.
	 * 
	 * @see FeedController
	 * @param pFeeds
	 * @return
	 */
	public List excludeExisting(List pFeeds) {
		Iterator it1 = pFeeds.iterator();
		ArrayList lNewList = new ArrayList();
		while (it1.hasNext()) {
			Object lFeed = it1.next();
			String lFolder = "";
			if (lFeed instanceof String) {
				lFolder = (String) lFeed;
			}
			if (lFeed instanceof IXPersonalFeed) {
				lFolder = ((IXPersonalFeed) lFeed).getFolder();
			}
			File lFolderFile = new File(lFolder);
			String lFolderName = lFolderFile.getName();
			Iterator it = getInstance().getFeedIterator();
			boolean match = false;
			while (it.hasNext()) {
				XPersonalFeed feed = (XPersonalFeed) it.next();
				// Compare URL.
				String lFeedFolder = feed.getFolder();
				// lFolder.compareTo(lFeedFolder);
				if (lFeedFolder != null) {
					File lFile = new File(lFeedFolder);
					if (lFolderName.equals(lFile.getName()))
						match = true;
				}
			}
			if (!match) {
				lNewList.add(lFeed);
			}
		}
		return lNewList;
	}

	/**
	 * Build a String which is represented in the statusbar showing a summary of
	 * feeds and enclosures.
	 * 
	 * @return String
	 */
	public static FeedsSummary getSummary() {

		int subscribed = 0;
		int enclosures = 0;
		int marked = 0;
		int candidates = 0;

		Iterator it = getInstance().getFeedIterator();
		while (it.hasNext()) {
			IXPersonalFeed feed = (IXPersonalFeed) it.next();
			if (feed.getPoll()) {
				subscribed++;
				candidates += feed.getCandidatesCount();
				Iterator it1 = feed.getEnclosureIterator();
				enclosures += feed.getEnclosureSize();
				while (it1.hasNext()) {
					IXPersonalEnclosure encl = (IXPersonalEnclosure) it1.next();
					if (encl.isMarked()) {
						marked++;
					}
				}
			}
		}
		lSummary.setSubscribed(subscribed);
		lSummary.setEnclosures(enclosures);
		lSummary.setMarked(marked);
		lSummary.setCandidates(candidates);
		return lSummary;
	}

	public void clear() {
		mFeedList.clear();
		setModified();
	}

	public void setModified() {
		mIsModified = true;
	}

	public boolean isModified() {
		return mIsModified;
	}

	/** Marks this Data Controller to be unmodified * */
	public void setUpdated() {
		mIsModified = false;
	}

	/**
	 * Provide a I18n compatible, formatted summary of the feeds, enclosures,
	 * subscriptions, downloads, candidates etc.. The formatting is hardcoded.
	 */
	public static class FeedsSummary {

		protected int mSubscribed = 0;

		protected int mEnclosures = 0;

		protected int mMarked = 0;

		protected int mCandidates = 0;

		public FeedsSummary() {
		}

		public FeedsSummary(int pSubscribed, int pEnclosures, int pMarked,
				int pCandidates) {
			this.mSubscribed = pSubscribed;
			this.mEnclosures = pEnclosures;
			this.mMarked = pMarked;
			this.mCandidates = pCandidates;
		}

		/**
		 * @param candidates
		 *            The candidates to set.
		 */
		public void setCandidates(int candidates) {
			mCandidates = candidates;
		}

		/**
		 * @param enclosures
		 *            The enclosures to set.
		 */
		public void setEnclosures(int enclosures) {
			mEnclosures = enclosures;
		}

		/**
		 * @param marked
		 *            The marked to set.
		 */
		public void setMarked(int marked) {
			mMarked = marked;
		}

		/**
		 * @param subscribed
		 *            The subscribed to set.
		 */
		public void setSubscribed(int subscribed) {
			mSubscribed = subscribed;
		}

		public String getFeedURLs() {
			StringBuffer summary = new StringBuffer();
			for (Iterator it = getInstance().getFeedIterator(); it.hasNext();) {
				URL url = ((IXPersonalFeed) it.next()).getURL();
				if (url != null)
					summary.append(url.toExternalForm() + "\n");
			}
			return summary.toString();
		}

		/**
		 * Returns the summary in the following format: S=13(60) feeds,
		 * M=40,(100) meaning 13 feeds out of 60 are subscribed to and 40
		 * enclosures out of 100 are marked.
		 * 
		 * @return String Return a short summary.
		 */
		public String getShortSummary() {
			StringBuffer summary = new StringBuffer();
			summary.append(Messages.getString("feedlist.summary.shortI",
					mSubscribed, getInstance().mFeedList.size()));
			summary.append(Messages.getString("feedlist.summary.shortII",
					mMarked, mEnclosures));
			return summary.toString();
		}

		/**
		 * Return a long summary.
		 * 
		 * @return String return a long summar.
		 */
		public String getLongSummary() {
			StringBuffer summary = new StringBuffer();
			summary.append(Messages.getString("feedlist.summary.longI",
					mSubscribed, getInstance().mFeedList.size()));
			summary.append(Messages.getString("feedlist.summary.longII",
					mCandidates, mEnclosures));
			return summary.toString();
		}

		public String getCandidatesSummary() {
			return Messages.getString("feedlist.summary.longII", mCandidates,
					mEnclosures);
		}
	}

	// ---- MODEL CHANGE HANDLING

	ArrayList<IXPersonalFeedListListener> pListenerList = new ArrayList<IXPersonalFeedListListener>();

	public void addListener(IXPersonalFeedListListener pListener) {
		if (!pListenerList.contains(pListener)) {
			pListenerList.add(pListener);
		}
	}

	public void removeListener(IXPersonalFeedListListener pListener) {
		if (pListenerList.contains(pListener)) {
			pListenerList.remove(pListener);
		}
	}

	public void fireFeedListChanged(XPersonalFeedListEvent pEvent) {
		Iterator<IXPersonalFeedListListener> lIter = pListenerList.iterator();
		while (lIter.hasNext()) {
			lIter.next().feedListChanged(pEvent);
		}
	}
}