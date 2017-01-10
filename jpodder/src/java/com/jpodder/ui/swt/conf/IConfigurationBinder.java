package com.jpodder.ui.swt.conf;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */

/**
 * Binds a UI component to a property.
 */
public interface IConfigurationBinder {

    public static final int SUB_TYPE_CONFIGURATION = 0;

    public static final int SUB_TYPE_CONNECTION = 1;

    public static final int SUB_TYPE_GUI = 2;

    public static final int SUB_TYPE_PRODUCTION = 3;

    public static final int SUB_TYPE_SCHEDULING = 4;
    
    public static final int SUB_TYPE_PLUGIN = 5;
    
    
    public String getName();
    
    public void read();

    public void save();
}