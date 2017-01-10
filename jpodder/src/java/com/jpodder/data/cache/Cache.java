package com.jpodder.data.cache;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IDataController;

/**
 * The caching class keeps track of downloaded enclosures. The model is is a
 * List which is not exposed externally. The class methods can be used to
 * manipulate the cache model.
 */
public class Cache implements IDataController {

    public static Cache mInstance;

    public static Cache getInstance() {
        if (mInstance == null) {
            mInstance = new Cache();
        }
        return mInstance;
    }

    protected Logger mLog = Logger.getLogger(getClass().getName());

    /**
     * A synchronized list of download objects.
     */
    private List mList = Collections.synchronizedList(new CallbackList(this));

    private CacheDataHandler mDataHandler = new CacheDataHandler(this);

    private boolean mIsModified;

    public Cache() {
    	this(null);
    }
    
    public Cache(File pFile) {
        try {
            ConfigurationLogic.getInstance().addDataHandler(mDataHandler, pFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the cachec meaning it removes all the entries
     */
    public void clear() {
        mList.clear();
    }

    public Iterator getIterator() {
        return mList.iterator();
    }

    public void addTracks(List pTracks) {
        Iterator i = pTracks.iterator();
        while (i.hasNext()) {
            CacheEntry lElement = null;
            try {
                lElement = (CacheEntry) i.next();
                mList.add(lElement);
                lElement.setDataController(this);
            } catch (ClassCastException cce) {
                mLog.warn("Could not add element: " + lElement
                        + " because it is not of type CacheEntry", cce);
            }
        }
        setModified();
    }

    public void addTrack(File file) {
        addTrack(file, null);
    }

    public void removeTrack(File file) {
    	Iterator lIter = mList.iterator();
    	while (lIter.hasNext()) {
			CacheEntry lEntry = (CacheEntry) lIter.next();
			if(lEntry.getName().equals(file.getAbsolutePath())){
				lIter.remove();
				setModified();
				break;
			}
		}
    }

    /**
     * Add a track to the register. A track consists of:
     * 
     * @param file
     *            The File of the track.
     * @param GUID
     *            The Global identity of a track.
     */
    public void addTrack(String pTrack) {
        if (!hasTrack(pTrack)) {
            CacheEntry lEntry = new CacheEntry(pTrack, null);
            mList.add(lEntry);
            lEntry.setDataController(this);
            setModified();
        }
    }

    /**
     * Add a track to the register. A track consists of:
     * 
     * @param file
     *            The File of the track.
     * @param GUID
     *            The Global identity of a track.
     */
    public void addTrack(File file, String GUID) {
        if (!hasTrack(file.getName())) {
            CacheEntry lEntry = new CacheEntry(file.getName(), GUID);
            mList.add(lEntry);
            lEntry.setDataController(this);
            setModified();
        }
    }


    /**
     * Check if the the track is already stored in the cache register. (It
     * currently checks the full URL only.
     * 
     * @param trackFile
     *            File
     * @return boolean
     */
    public boolean hasTrack(File trackFile) {
            return hasTrack(trackFile.getName());
    }

    public boolean hasTrack(String pName) {
        synchronized (mList) {
            Iterator it = mList.iterator();
            while (it.hasNext()) {
                CacheEntry track = (CacheEntry) it.next();
                if (track.equals(pName)) {
                    return true;
                }
            }
            return false;
        }
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
    
    public String toString(){
    	return "Cache Interface";
    }
}