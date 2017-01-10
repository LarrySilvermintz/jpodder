package com.jpodder.ui.swt.window;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.jpodder.ui.swt.UILauncher;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class WindowController {
    
    private static Logger sLog = Logger.getLogger(WindowController.class.getName());

    public Listener exitAction;
    public Listener newAction;
    public Listener startUIAction;
    protected WindowView mMainUI;
    
    public WindowController(){
        initialize();
    }
    
    public void initialize(){
        sLog.info("<init>");
    }
    
    public void setView(WindowView pUI){
        mMainUI = pUI;
        initializeUI();
    }
    
    public void initializeUI(){
        exitAction = new ExitAction();
        newAction = new NewAction();
        mMainUI.lFileSystemExit.addListener(SWT.Selection, exitAction);
        mMainUI.setController(this);
    }
    
    public class ExitAction implements Listener {
        public void handleEvent(Event event) {
        	UILauncher.getInstance().bye();
        }
    }

    public class NewAction implements Listener {
        public void handleEvent(Event event) {
        }
    }
    

}
