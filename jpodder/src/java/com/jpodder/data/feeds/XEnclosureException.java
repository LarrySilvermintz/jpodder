package com.jpodder.data.feeds;
/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class XEnclosureException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 6756292481838426932L;

	public XEnclosureException(){
        this("");
    }
	public XEnclosureException(String pMessage){
        super(pMessage);
    }
}
