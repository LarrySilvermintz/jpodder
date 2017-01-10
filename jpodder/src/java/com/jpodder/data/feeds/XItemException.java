package com.jpodder.data.feeds;
/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class XItemException extends Exception {
    
    /**
	 */
	private static final long serialVersionUID = 6756292481838426932L;

	public XItemException(){
        this("");
    }

	public XItemException(String pMessage){
        super(pMessage);
    }
}
