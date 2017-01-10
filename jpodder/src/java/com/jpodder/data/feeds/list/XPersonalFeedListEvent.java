package com.jpodder.data.feeds.list;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @version 1.1
 */
public class XPersonalFeedListEvent extends java.util.EventObject {

	private static final long serialVersionUID = 1L;

	protected Object mSubject;

	/**
	 * @param pSource
	 * @param task
	 * @param mSubject
	 * @param exception
	 */
	public XPersonalFeedListEvent(Object pSource) {
		super(pSource);
		mSubject = pSource;
	}

}
