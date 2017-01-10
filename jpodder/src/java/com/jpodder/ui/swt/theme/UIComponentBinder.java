package com.jpodder.ui.swt.theme;

import org.apache.log4j.Logger;

/**
 * The idea of UI ComponentBinding is to let a UI component register itself to a
 * Theme with an ID. The ID corresponds to a CSS Style rule which is the Theme
 * should implement. A lookup is performed to find the style rule and
 * 
 * 
 * 
 * @author christophe.bouhier
 * 
 */
public class UIComponentBinder implements IUIComponentBinder {

	Object mComponent;

	String mID;

	String mForgroundID;
	String mBackgroundID;
	
	
	private Logger mLog = Logger.getLogger(getClass().getName());

	public UIComponentBinder(Object pComponent, String pID) {
		mID = pID;
		mComponent = pComponent;
	}

	public String getID() {
		return mID;
	}

	public void applyAllStyles() {

		applyBackgroundcolor();
		applyForgroundColor();
		applyFont();
	}

	public void applyBackgroundcolor() {

	}

	public void applyForgroundColor() {

	}

	public void applyFont() {

	}

	public void setID(String pID) {
		mID = pID;
	}

	public void setBackgroundColorID(String pID) {
		mBackgroundID = pID;
	}

	public void setForgroundColorID(String pID) {
		mForgroundID = pID;
	}

}