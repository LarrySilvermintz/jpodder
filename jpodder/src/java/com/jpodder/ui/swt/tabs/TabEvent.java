package com.jpodder.ui.swt.tabs;
/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class TabEvent extends java.util.EventObject {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 5552628428462082761L;
	private String mTitle;
    public TabEvent(Object pSrc, String pTitle){
        super(pSrc);
        mTitle = pTitle;
    }
    public String getTabTitle(){
        return mTitle;
    }
}
