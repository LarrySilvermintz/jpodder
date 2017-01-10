package com.jpodder.data.configuration;

import java.util.EventListener;


/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */

/**
 * A property listener when implented is notified of properties changed.
 * Currently the following modules use properties.
 * <p>
 * Tasks: - podcastfolder Net: - proxy settings Scheduler - (This module
 * implements a direct listener, so does not use the property interface.
 *  
 */
public interface IConfigurationListener extends EventListener {
    public void configurationChanged(ConfigurationEvent event);
}