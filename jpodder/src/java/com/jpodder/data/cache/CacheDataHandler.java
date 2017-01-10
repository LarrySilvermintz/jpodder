package com.jpodder.data.cache;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import com.jpodder.cache.CacheDocument;
import com.jpodder.cache.TCache;
import com.jpodder.cache.TCacheTrack;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IDataHandler;

/**
 * Data Handler implementation for the Feeds.
 * 
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class CacheDataHandler implements IDataHandler {

    protected Logger mLog = Logger.getLogger(getClass().getName());
    private Cache mCacheList;
	private File mFile;

    public CacheDataHandler(Cache pList) {
        mCacheList = pList;
    }

    public int getIndex() {
        return ConfigurationLogic.CACHE_INDEX;
    }

    public boolean isModified() {
        return mCacheList.isModified();
    }

    public String getContent() throws Exception {
        mLog.info("getContent(), return the content of the feed list");
        CacheDocument lDocument = CacheDocument.Factory.newInstance();
        TCache lRoot = lDocument.addNewCache();
        Iterator i = mCacheList.getIterator();
        while (i.hasNext()) {
            CacheEntry lEntry = (CacheEntry) i.next();
            TCacheTrack lTCacheTrack = lRoot.addNewCachetrack();
            lTCacheTrack.setUrl(lEntry.getName() + "");
            String lGUID = lEntry.getGUID();
            if (lGUID != null) {
                lTCacheTrack.setGUID(lGUID);
            }
        }
        StringWriter lOutput = new StringWriter();
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSavePrettyPrint();
        lDocument.save(lOutput);
        //AS NOTE: This could be dangerouse when we mark the content update but
        // the save to the file
        //AS fails afterwards
        mCacheList.setUpdated();
        return lOutput.toString();
    }

    public void setContent(String pContent) throws Exception {
        if (mLog.isDebugEnabled()) {
            mLog.debug("setContent(), content to be set: '" + pContent + "'");
        }
        if (pContent != null && pContent.trim().length() != 0) {
            CacheDocument lDocument = CacheDocument.Factory.parse(pContent);
            TCache lRoot = lDocument.getCache();
            if (mLog.isDebugEnabled()) {
                mLog.debug("Cache Array: "
                        + java.util.Arrays.asList(lRoot.getCachetrackArray()));
            }

            ArrayList<CacheEntry> lCacheTrackList = new ArrayList<CacheEntry>();
            TCacheTrack[] lCacheTracks = lRoot.getCachetrackArray();
            for (int i = 0; i < lCacheTracks.length; i++) {
                TCacheTrack lCacheTrack = lCacheTracks[i];
                String lGUID = lCacheTrack.getGUID();
                lCacheTrackList
                        .add(new CacheEntry(lCacheTrack.getUrl(), lGUID));
            }
            mCacheList.clear();
            mCacheList.addTracks(lCacheTrackList);
            mCacheList.setUpdated();
            if (mLog.isDebugEnabled()) {
                mLog.debug("setContent(), feed list: " + lCacheTrackList);
            }
        } else {
            mLog
                    .warn("setContent(), given content is empty and it is assumed that there is no cache at all");
        }
    }

    public boolean validate(String pContent, boolean pCompare) {
        return true; // Validation is implicit
    }

	public void setPersistentFile(File pFile) {
		mFile = pFile;
	}

	public File getPersistentFile() {
		return mFile;
	}
}