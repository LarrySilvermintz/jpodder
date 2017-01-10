package com.jpodder.data.configuration;

import java.util.EventObject;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */

/**
 * A property event contains the source and the new properties to be
 * consumed.
 */
public class ConfigurationEvent extends EventObject {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 219406714614931999L;
	private String mPropertyName;
    
    public ConfigurationEvent(Object source) {
        super(source);
    }
    
    public ConfigurationEvent(Object source, String pPropertyName) {
        super(source);
        mPropertyName = pPropertyName;
    }
    
    public String getPropertyName(){
        if(mPropertyName != null){
            return mPropertyName;
        }else{
            return "";
        }
    }
}