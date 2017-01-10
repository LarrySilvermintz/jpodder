package com.jpodder.data.player;
/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class PlayerException extends Exception {
    
	private static final long serialVersionUID = 6756292481838426932L;

	public PlayerException(String pMessage){
        super(pMessage);
    }
}
