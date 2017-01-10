package com.jpodder.data.feeds;

/**
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @version 1.1
 **/
import java.io.File;
import java.net.URL;
import java.util.Date;

import com.jpodder.data.content.ContentLogic;

/**
 * A file on the file system class wrapped in a custom object which implements
 * the <code>XFile</code> interface.
 *  
 */
public class XLocalFile implements IXFile, Comparable {

    // ----- XFile
    private File mFile;
    private boolean mMarked;
    private boolean mCached;
    private boolean mInPlayer;
    private boolean mInspected;
    
    private IXPersonalFeed mFeed;

    /**
     * Constructor
     * 
     * @param pFile
     *            File
     * @param pFeed
     *            Feed
     */
    public XLocalFile(File pFile, IXPersonalFeed pFeed) {
        mFeed = pFeed;
        mFile = pFile;
    }
    
    
    
    /**
     */
    public String toString() {
        return mFile.getPath() + " Ma=" + new Boolean(mMarked).toString() 
        + " Pl=" + new Boolean(mInPlayer).toString();
    }
    /**
     * 
     * @param o
     *            Object
     * @return int
     */
    public int compareTo(Object o) {
        IXFile f = (IXFile) o;
        int cmpResult = -1;
        if (f.getDate() != null && getDate() != null) {
            cmpResult = f.getDate().compareTo(getDate());
        }
        return cmpResult;
    }

    /**
     * Return if this file is also an enclosure in the feed.
     * 
     * @return boolean
     */
    public boolean isEnclosure() {
        return false;
    };

    /**
     * The parent feed to which this file belongs.
     * 
     * @return Feed
     */
    public IXPersonalFeed getFeed() {
        return mFeed;

    };

    /**
     * Set the parent to while this file belongs.
     * 
     * @param feed
     *            Feed
     */
    public void setFeed(IXPersonalFeed pFeed) {
        mFeed = pFeed;
    }

    /**
     * Get the file name.
     * 
     * @return String
     */
    public String getName() {
    	if( mFile != null){
    		return mFile.getName();	
    	}
    	return null;
    }

    /**
     * Get the URL of the file.
     * 
     * @return URL
     */
    public URL getFileURL() {
        if (mFile != null) {
            try {
                return mFile.toURL();
            } catch (java.net.MalformedURLException mue) {
                return null;
            }
        }
        return null;
    }

    /**
     * Set the URL of the file.
     * 
     * @param url
     *            URL
     */
    public void setFileURL(URL url) {
        // ignored, the url is derived from the File class.
    }

    /**
     * Get the file.
     * 
     * @return File
     */
    public File getFile() {
        return mFile;
    }

    public File getFile(boolean pRefresh) {
        return mFile;
    }

    /**
     * Set the file.
     * 
     * @param file
     *            File
     */
    public void setFile(File file) {
        this.mFile = file;
    }

    /**
     * Set the file.
     * 
     * @param file
     *            File
     */
    public void setFileName(String pName) {
    	throw new IllegalArgumentException();
    }

    /**
     * Marking is context sensitive. For enclosures it has a different meaning
     * then for files.
     * 
     * @return boolean
     */
    public boolean isMarked() {
        return mMarked;
    }

    /**
     * Set if the file should be marked.
     * 
     * @param marked
     */
    public void setMarked(boolean marked) {
        this.mMarked = marked;
    }

    /**
     * 
     * @return boolean
     */
    public boolean isCached() {
        return mCached;
    }

    /**
     * Set if the file is cached.
     * 
     * @param cached
     *            boolean
     */
    public void setCached(boolean cached) {
        this.mCached = cached;
    }

    /**
     * Query if the file is stored in the selected player.
     * 
     * @return boolean
     */
    public boolean getInPlayer() {
        return mInPlayer;
    }

    /**
     * Set that the file is stored in the selected player.
     * 
     * @param inPlayer
     *            boolean
     */
    public void setInPlayer(boolean inPlayer) {
        this.mInPlayer = inPlayer;
    }

    /**
     * Get if the file is stored locally.
     * 
     * @return boolean
     */
    public boolean isLocal() {
        return true;
    };

    /**
     * The if the file is stored on the local storage
     * 
     * @param local
     *            boolean
     */
    public void setLocal(boolean local) {
        // ignored, this class is always local.
    }

    /**
     * Get the last modified date of the file.
     * @return Date
     */
    public Date getDate() {
        return new Date(mFile.lastModified());
    }

    /**
     * Return the MIME type for the file. (Not implemented yet).
     * @return String The MIME type.
     */ 

    public String getFileType() {
        return ContentLogic.getContentFromFileName(mFile.getName());
    }

    /**
     * The length of the file.
     * 
     * @return int
     */
    public int getFileLength() {
        if (mFile != null) {
            return new Long(mFile.length()).intValue();
        }
        return 0;
    }
    /**
     * Set that the enclosure is now inspected.
     */
    public void setInspected(){
        if(mInspected){
            throw new IllegalArgumentException();
        }else{
            mInspected = true;
        }
    }
    
    public boolean isInspected(){
        return this.mInspected;
    }
}