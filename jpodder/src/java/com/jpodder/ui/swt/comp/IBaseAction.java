package com.jpodder.ui.swt.comp;

import org.eclipse.swt.widgets.Widget;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public interface IBaseAction {

    public void init(String pName, boolean pEnabled);
    
    public void init(String pName);
    
    public String getName();

    public boolean enabled();
    
    public void setControls(Widget[] pControls);
    
}