package com.jpodder.html.style;
/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class StyleNotFoundException extends Exception {
    
    /**
	 */
	private static final long serialVersionUID = 6756292481838426932L;

	public StyleNotFoundException(){
        this("");
    }

	public StyleNotFoundException(String pMessage){
        super(pMessage);
    }
}
