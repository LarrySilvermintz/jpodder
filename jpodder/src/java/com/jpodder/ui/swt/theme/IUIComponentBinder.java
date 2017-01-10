package com.jpodder.ui.swt.theme;

/**
 * A typical component Style binding is aware of the
 * 
 * @author christophe.bouhier
 * 
 */
public interface IUIComponentBinder {

	/**
	 * Get the name of this binding/CSS style.
	 * 
	 * @return
	 */
	public String getID();

	/**
	 * Set the ID for this binder.
	 * 
	 * @param pID
	 */
	public void setID(String pID);

	/**
	 * Apply the style to the component.
	 */
	public void applyAllStyles();

	/**
	 * 
	 */
	public void applyBackgroundcolor();

	/**
	 * 
	 */
	public void applyForgroundColor();

	public void setBackgroundColorID(String pID);

	public void setForgroundColorID(String pID);

	/**
	 * 
	 */
	public void applyFont();

}
