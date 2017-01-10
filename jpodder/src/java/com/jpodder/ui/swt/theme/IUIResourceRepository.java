package com.jpodder.ui.swt.theme;

import org.eclipse.swt.graphics.Color;

public interface IUIResourceRepository {

	public void put(String pID, Color pColor);
	
	public Color get(String pID);
	
	public boolean has(Color pColor);
	
	public String get(Color pColor);
	
	/**
	 * Dispose all resources.
	 */
	public void dispose();
	
}
