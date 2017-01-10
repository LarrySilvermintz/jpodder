package com.jpodder.data.feeds;

import com.jpodder.data.feeds.stats.XFeedEvent;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 */
public interface IXFeedListener {
    
    /**
     * A task failed.
     * @param e
     */
    public void instructionSucceeded(XFeedEvent e);
    
    /**
     * A task succeeded.
     * @param e
     */
    public void instructionFailed(XFeedEvent e);

    /**
     * A task information event.
     * @param e
     */
    public void instructionInfo(XFeedEvent e);

}
