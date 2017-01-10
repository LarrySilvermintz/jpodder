package com.jpodder.data.opml;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 */
public interface IOPMLListener {
    
    /**
     * A task failed.
     * @param e
     */
    public void opmlCompleted(OPMLEvent e);
    
    public void opmlAborted(OPMLEvent e);

}
