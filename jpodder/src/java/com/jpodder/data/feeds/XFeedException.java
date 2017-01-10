package com.jpodder.data.feeds;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class XFeedException extends Exception {
	
	static String GENERAL_MSG = "General Exception occured";
	
	private static final long serialVersionUID = -743860573691665203L;

	public XFeedException() {
		this(GENERAL_MSG);
	}

	public XFeedException(String pMessage) {
		super(pMessage);
	}
}
