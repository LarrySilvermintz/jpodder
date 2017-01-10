package com.jpodder.ui.swt.conf;

import java.util.TreeMap;
import com.jpodder.ui.swt.conf.panel.IConfigurationPanel;

/**
 * A property node which is a base container for 
 * a branch. It contains the associated panel.
 */
public class ConfigurationNode {
    
    public String mNodeName;
    public TreeMap mChildren;
    public IConfigurationPanel panel;
    public ConfigurationNode mParent;
    
    public ConfigurationNode(String pNodeName, IConfigurationPanel pPanel) {
        this(null, pNodeName, pPanel);
    }
    
    public ConfigurationNode(ConfigurationNode pParent, String pNodeName, IConfigurationPanel pPanel) {
        mParent = pParent;
        mNodeName = pNodeName;
        panel = pPanel;
    }

    public String toString() {
        return mNodeName;
    }
}