package com.jpodder.xml;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import com.jpodder.rss20.RssDocument;
import com.jpodder.rss20.TRss;
import com.jpodder.rss20.TChannel;
public class RSSWrapper {

    protected static TRss rss = null;

    protected boolean done = false;

    public static ArrayList listeners = new ArrayList();

    /**
     * Constructor.
     * 
     * @param rssFile
     */
    public RSSWrapper(File pRssFile) {
        if (pRssFile != null) {
            parse(pRssFile);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param pRssFile
     * @return
     */
    public static RSSWrapper getInstance(File pRssFile) {
        return new RSSWrapper(pRssFile);
    }

    /**
     * @param listener
     */
    public synchronized void addListener(RSSListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * @param listener
     */
    public synchronized void removeListener(RSSListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    /**
     * Fire an event, notifying any listener of a succesful parsing completion.
     * 
     * @param event
     */
    protected synchronized void fireParsingCompleted(RSSEvent event) {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            RSSListener element = (RSSListener) iter.next();
            element.parsingCompleted(event);
        }
    }

    /**
     * Parse an rssFile
     * Don't parse in a swingworker for now. 
     * @param rssFile
     */
    public void parse(final File rssFile) {
        RssDocument doc;
        if ((doc = RSSBinding.parse(rssFile)) != null) {
            rss = doc.getRss();
        }else{
            rss = null;
        }
        
        //        final com.jpodder.ui.SwingWorker worker = new
        // com.jpodder.ui.SwingWorker() {
        //            public Object construct() {
        //                RssDocument doc;
        //                if ((doc = RSSBinding.parse(rssFile)) != null) {
        //                    rss = doc.getRss();
        //                    return rss;
        //                }
        //                done = true;
        //                return null;
        //            }
        //
        //            //Runs on the event-dispatching thread.
        //            public void finished() {
        //                done = true;
        //                // Should fire a completion event.
        //                fireParsingCompleted(new RSSEvent(this));
        //            }
        //        };
        //        worker.start();
    }

    /**
     * Get an RSS tag from the current feed. An RSS tag could be empty or not
     * present. In this case <code>null</code> is returned.
     * 
     * @param content
     * @return String the RSS Channel title.
     */
    public String getContent(String token) {
        String result = null;
        if (rss != null) {
            TChannel channel = rss.getChannel();
            token = "get" + token;
            try {
                Method mGetMethod = TChannel.class.getMethod(token, null);
                result = (String) mGetMethod.invoke(channel, null);
            } catch (SecurityException e) {
            } catch (NoSuchMethodException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }
        return result;
    }

    /**
     * @return Returns the done.
     */
    public boolean isDone() {
        return done;
    }
}