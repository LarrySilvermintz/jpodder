/*
 * jPodder
 * Created on May 15, 2005
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
public class RSSEvent extends java.util.EventObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2605647103238359221L;

	/**
     * @param source
     */
    public RSSEvent(Object source) {
        super(source);
    }
    
    
}
