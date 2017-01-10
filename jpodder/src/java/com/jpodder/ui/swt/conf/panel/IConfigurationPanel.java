package com.jpodder.ui.swt.conf.panel;

import org.eclipse.swt.widgets.Composite;

import com.jpodder.ui.swt.conf.IConfigurationBinder;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public interface IConfigurationPanel {
    /**
     * Get a collection of UI component to value binders. 
     * @return
     */
    public IConfigurationBinder[] getBindings();
    public abstract void initialize(Composite pParent);
    public Composite getView();

}
