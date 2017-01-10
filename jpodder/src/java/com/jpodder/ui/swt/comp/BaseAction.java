package com.jpodder.ui.swt.comp;

import org.eclipse.swt.widgets.Listener;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public abstract class BaseAction implements IBaseAction, Listener {
    
    protected String mName;
    protected boolean mEnabled = true;
    
    public BaseAction(){
    }

    public void init(String pName, boolean pEnabled){
        mEnabled = pEnabled;
        init(pName);  
    }
    
    public void init(String pName){
        mName = pName;
    }
    
    public String getName(){
        return mName;
    }

    public boolean enabled(){
        return mEnabled;
    }
    
}