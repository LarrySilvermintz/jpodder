package com.jpodder.remote;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class RPCEvent extends java.util.EventObject {
	private static final long serialVersionUID = 6958212344345131993L;
	public RPCEvent(Object pSource){
        super(pSource);
    }
}
