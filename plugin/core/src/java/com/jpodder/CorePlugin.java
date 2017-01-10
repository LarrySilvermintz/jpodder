package com.jpodder;

import com.jpodder.ui.swt.UIHelper;
import com.jpodder.ui.swt.conf.ConfigurationController;
import com.jpodder.ui.swt.conf.ConfigurationView;
import com.jpodder.ui.swt.properties.panel.OneClickController;
import com.jpodder.ui.swt.properties.panel.OneClickPanel;
import com.jpodder.util.Logger;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */

public class CorePlugin {
    private Logger mLog = Logger.getLogger(getClass().getName());

    public CorePlugin() {
        new Starter().start();
    }

    private class Starter
            extends Thread {

        public void run() {
            mLog.info( ".Starter.run(), started" );
            ConfigurationView lConfView = null;
            // this loop is only activated when we select the settings tab
            while( lConfView == null ) {
                try {
                    Thread.sleep( 1000 );
                } catch( Exception e ) {
                }
                // Where all begins
                ConfigurationController lConfController = UIHelper.mConfController;
                if( lConfController != null ) {
                    lConfView = lConfController.getView();
                }
            }
            mLog.debug( ".Starter.run(), view found: " + lConfView );
            OneClickPanel lView = new OneClickPanel();
            lView.setController(new OneClickController());
            lConfView.addPluginPanel(
                Messages.getString("oneclickpanel.label"),
                lView
            );
            
            // We can't set the view, it's dynamicly created.
            // Plugins which have view need to tell their controllers. 
//            new OneClickController().setView( lView );
            
            mLog.info( ".Starter.run(), done" );
        }
    }
}