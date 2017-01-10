package com.jpodder.directory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IDataController;

import org.apache.log4j.Logger;

/**
 * A collection of RSS feed objects available to the application.
 * 
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class DirectoryList
        implements IDataController {

    private static DirectoryList sInstance = new DirectoryList();

    public static DirectoryList getInstance() {
        return sInstance;
    }

    private Logger mLog = Logger.getLogger( getClass().getName() );
    private CallbackList mDirectoryList = new CallbackList();
    private DirectoryDataHandler mDataHandler = new DirectoryDataHandler( this );
    private boolean mIsModified;

    /**
     * Constructor. Creates the collection.
     */
    private DirectoryList() {
        try {
            ConfigurationLogic.getInstance().addDataHandler( mDataHandler );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public int size() {
        return mDirectoryList.size();
    }


    /**
     * Get the feed object.
     * 
     * @param index
     *            int
     * @return Feed
     * @see Feed
     */
    public DirectoryEntry getFeed(int index) {
        DirectoryEntry feed = (DirectoryEntry) mDirectoryList.get(index);
        return feed;
    }

    /**
     * Get the index of a feed.
     * 
     * @param feed
     *            Feed
     * @return int
     */
    public int getIndexOf(DirectoryEntry feed) {
        return mDirectoryList.indexOf(feed);
    }

    /**
     * Add an already created feed;
     * 
     * @param feed
     */
    public void addFeed(DirectoryEntry pFeed) {
        mDirectoryList.add(pFeed);
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
    public DirectoryEntry addFeed() {
        DirectoryEntry feed = new DirectoryEntry();
        addFeed(feed);
        return feed;
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
            DirectoryEntry newFeed = null;
            Object o = it1.next();
            if (o instanceof URL) {
                feedURL = (URL) o;
                newFeed = new DirectoryEntry();
                newFeed.setURL(feedURL.toExternalForm());
            } else if (o instanceof DirectoryEntry) {
                newFeed = (DirectoryEntry) o;
            }
            if (newFeed != null) {
                addFeed(newFeed);
                newFeed.setDescription("Imported");
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
    public DirectoryEntry removeFeed(int index) {
        DirectoryEntry feed = (DirectoryEntry) mDirectoryList.get(index);
        mDirectoryList.remove(index);
        setModified();
        return feed;
    }

    /**
     * Remove a feed.
     * 
     * @param feed
     */
    public void removeFeed(DirectoryEntry feed) {
        mDirectoryList.remove(feed);
    }

    /** @return The iterator over the feed list */
    public Iterator getIterator() {
        return mDirectoryList.iterator();
    }
    
    public List getFeeds(){
        return mDirectoryList;
    }
    
    
    /**
     * Find a feed for a specific url.
     * 
     * @param url
     *            String The url.
     * @return Feed
     */
    public DirectoryEntry getFeed(String url) {
        Iterator it = mDirectoryList.iterator();
        while (it.hasNext()) {
            DirectoryEntry feed = (DirectoryEntry) it.next();
            if (feed.getURL() != null && feed.getURL().toString().equals(url)) {
                return feed;
            }
        }
        return null;
    }


    public List trimExisting(List _feeds) {
        Iterator it1 = _feeds.iterator();
        while (it1.hasNext()) {
            boolean foundURL = false;
            boolean foundTitle = false;
            String feedURL = null;
            String feedTitle = null;

            Object o = it1.next();
            if (o instanceof DirectoryEntry) {
                feedURL = ((DirectoryEntry) o).getURL();
                feedTitle = ((DirectoryEntry) o).getTitle();
            }
            Iterator it = getInstance().mDirectoryList.iterator();
            while (it.hasNext()) {
                DirectoryEntry feed = (DirectoryEntry) it.next();

                // Compare URL.
                if (feedURL != null) {
                    if (feed.getURL().toString().equals(feedURL.toString())) {
                        foundURL = true;
                    }
                }
                // Compare Tile.
                if (feedTitle != null & feed.getTitle() != null) {
                    if (feed.getTitle().equals(feedTitle)) {
                        foundTitle = true;
                    }
                }
            }
            if (foundTitle || foundURL) {
                it1.remove();
                setModified();
            }
        }
        return _feeds;
    }

    public void clear() {
        mDirectoryList.clear();
        setModified();
    }

    public void setModified() {
        mIsModified = true;
    }

    public boolean isModified() {
        return mIsModified;
    }

    /** Marks this Data Controller to be unmodified **/
    public void setUpdated() {
        mIsModified = false;
    }

    /**
     * This class makes sure that any removal of an entry through
     * the iterator does mark the feed list as updated so that it
     * can be saved later
     **/
    public class CallbackList
            extends ArrayList {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1960351505966056593L;
		public Object remove( int pIndex ) {
            Object lReturn = super.remove( pIndex );
            setModified();
            return lReturn;
        }
        public boolean add(Object pObject){
            boolean lReturn = super.add(pObject);
            setModified();
            return lReturn;
        }
    }
}