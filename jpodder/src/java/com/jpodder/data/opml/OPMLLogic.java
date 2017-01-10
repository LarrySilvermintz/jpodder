package com.jpodder.data.opml;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.tasks.AbstractTaskWorker;
import com.jpodder.tasks.ITask;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class OPMLLogic implements IConfigurationListener, IOPMLListener {

    String allPodCastsSearchOPML = "http://www.allpodcasts.com/SearchOpml.aspx";
    String allPodCastsSearchRSS = "http://www.allpodcasts.com/SearchRss.aspx";

    public static final String OPML_EXT = "opml";
    private URL mOPMLUrl = null;
    private static OPMLLogic sSelf;
    public ArrayList listeners = new ArrayList();
    Logger mLog = Logger.getLogger(getClass().getName());

    public static OPMLLogic getInstance() {
        if (sSelf == null) {
            sSelf = new OPMLLogic();
        }
        return sSelf;
    }

    public OPMLLogic() {
        addListener(this);
    }

    public void configurationChanged(ConfigurationEvent event) {
        if (!event.getSource().equals(ConfigurationLogic.class)) {
            return;
        }
        String mOPMLString = Configuration.getInstance().getOMPLSync();
        try {
            mOPMLUrl = new URL(mOPMLString);
            // CB TODO OPML Sync should be a config option
            // Synching now implies synchronization on startup.
            // This should perhaps be an option.
            sync();
        } catch (MalformedURLException e) {
            mLog.info("OPML URL not available/invalid");
        }
    }

    public ITask sync() {
        if (mOPMLUrl != null) {
            OPMLTask lTask = new OPMLTask(mOPMLUrl, OPMLTask.OPML_GET_SRC_URL);
            lTask.start();
            return lTask;
        } else {
            mLog.info("OPML sync is disabled (No URL)");
            return null;
        }
    }

    public ITask parse(File pFile) {
        OPMLTask mWorker = new OPMLTask(pFile, OPMLTask.OPML_GET_SRC_FILE);
        mWorker.start();
        return mWorker;
    }

    public void parseiPodderdotOrg() {
        URL opmlLink = null;

        // Simply parses the ipodder.org tree.
        String opmlDirectory = "http://www.ipodder.org/discuss/reader$4.opml";

        try {
            opmlLink = new URL(opmlDirectory);
            OPMLTask lTask = new OPMLTask(opmlLink, OPMLTask.OPML_GET_TREE);
            lTask.start();
        } catch (java.net.MalformedURLException mue) {
            mLog.warn("ipodder.org url: " + mue.getMessage());
        }
    }

    public List feedsToOPML(Object[] pFeeds) {
        ArrayList lList = new ArrayList();
        for (int i = 0; i < pFeeds.length; i++) {
            Object pSrc = pFeeds[i];
            if (pSrc instanceof IXPersonalFeed) {
                IXPersonalFeed lFeed = (IXPersonalFeed) pSrc;
                String lText = lFeed.getPersonalTitle();
                URL lUrl = lFeed.getURL();
                if (lUrl != null && lText != null && lText.length() >= 0) {
                    lList.add(new Outline(lText, lUrl));
                }else{
                    mLog.warn("feedsToOPML(): Title or URL missing");
                }
            } else {
                mLog.warn("feedsToOPML(): Invalid type for " + pSrc);
            }
        }
        mLog.info("feedsToOPML(): " + lList.size() + " Outlines created"); 
        return lList;
    }

    public void opmlCompleted(OPMLEvent e) {
        mLog.info("OPML Parsing completed, src:" + e.getSource());
    }

    public void opmlAborted(OPMLEvent e) {
        mLog.info("OPML task aborted");
    }
    
    /**
     * @param listener
     */
    public synchronized void addListener(IOPMLListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * @param listener
     */
    public synchronized void removeListener(IOPMLListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    /**
     * @param event
     */
    protected synchronized void fireOPMLCompleted(OPMLEvent event) {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            IOPMLListener element = (IOPMLListener) iter.next();
            element.opmlCompleted(event);
        }
    }

    /**
     * 
     * @param event
     */
    protected synchronized void fireOPMLAborted(OPMLEvent event) {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            IOPMLListener element = (IOPMLListener) iter.next();
            element.opmlAborted(event);
        }
    }
    
    /**
     * An OPML worker class. It provides threaded functions to
     * parse OPML from a file or from a URL. 
     * 
     */
    public class OPMLTask extends AbstractTaskWorker {

        public static final short OPML_GET_SRC_URL = 555;
        public static final short OPML_GET_SRC_FILE = 777;
        public static final short OPML_SET_DST_FILE = 888;
        public static final short OPML_GET_TREE = 666;

        /**
         * Can be <code>OPML_GET_SRC_URL</code>, <code>OPML_GET_SRC_FILE</code>
         */
        private short mTaskType;
        
        // --- OPML sources.
        private URL mUrl;
        private File mFile;
        private String mQuery;
        
        // ---- OPML result.
        
        private Object result;
        
        public OPMLTask(File pFile, short pTaskType) {
            mFile = pFile;
            mTaskType = pTaskType;
        }
        
        /**
         * @param url
         * @param pTaskType
         *            Can be OPML_GET_LINKS or OPML_GET_TREE
         */
        public OPMLTask(URL url, short pTaskType) {
            this(url, null, pTaskType);
        }

        /**
         * Constructor
         * @param app
         *            MainUI
         * @param url
         *            URL
         * @param query
         *            String
         */
        public OPMLTask(URL pUrl, String pQuery, short pTaskType) {
            mUrl = pUrl;
            mQuery = pQuery;
            mLength = 0;
            mTaskType = pTaskType;
        }


        public void stop() {
            mCancelled = true;
            mMessage = null;
        }
        
        public URL getURL(){
            return mUrl;
        }
        
        public File getFile(){
            return mFile;
        }
        
        public int getType(){
            return mTaskType;
        }
        
        public Object construct() {
            setCurrent(0);
            setDone(false);
            switch (getType()) {
                case OPMLTask.OPML_GET_TREE:
                    OPMLParser.getInstance().getOPMLTree(getURL(),
                            this);
                    break;
                case OPMLTask.OPML_GET_SRC_URL:
                    result = OPMLParser.getInstance().getOPML(
                            getURL(), this);
                    break;
                case OPMLTask.OPML_GET_SRC_FILE:
                    result = OPMLParser.getInstance().getOPML(
                            getFile(), this);
                    break;
                default:
                    break;
            }
            return null;
        }

        public void finished() {
            // The task is complete, get the result.
            if (!isCancelled()) {
                fireOPMLCompleted(new OPMLEvent(this, result));
            }else{
            	fireOPMLAborted(new OPMLEvent(this, result));
            }           
        }
    }
}