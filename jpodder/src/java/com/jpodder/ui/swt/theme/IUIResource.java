package com.jpodder.ui.swt.theme;

/**
 * Holds a resource.
 * 
 * @author christophe.bouhier
 *
 */
public interface IUIResource {

	public String getID();
	
	/**
	 * Dispose all resources.
	 */
	public void dispose();
}
