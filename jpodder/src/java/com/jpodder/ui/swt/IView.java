package com.jpodder.ui.swt;

import org.eclipse.swt.widgets.Composite;

/**
 * A implementor should return a Composite including UI widgets. 
 * If the view is static, the compsosite is not destroyed after loosing focus. 
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public interface IView {

    abstract Composite getView();
    abstract boolean isStatic();
    abstract void setStatic(boolean pStatic);
}
