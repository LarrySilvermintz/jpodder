/**
 * jPodder
 * Created on Mar 27, 2005
 *
 */
package com.jpodder.xml;

/**
 * jPodder podcasting software.
 * 
 * GPL License. 
 *
 * @author etmchbo
 */
public interface RSSListener {
    
    /**
     * A RSS parsing completed. 
     * @param e
     */
    public void parsingCompleted(RSSEvent e);
    
}
