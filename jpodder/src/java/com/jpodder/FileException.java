package com.jpodder;
/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class FileException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 6756292481838426932L;

	public FileException(String pMessage){
        super(pMessage);
    }
}
