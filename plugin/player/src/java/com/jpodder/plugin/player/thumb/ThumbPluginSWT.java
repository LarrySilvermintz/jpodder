package com.jpodder.plugin.player.thumb;

import com.jpodder.ui.swt.UIHelper;
import com.jpodder.ui.swt.conf.ConfigurationController;
import com.jpodder.ui.swt.conf.ConfigurationView;
import com.jpodder.util.Logger;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */

public class ThumbPluginSWT {
    private Logger mLog = Logger.getLogger(getClass().getName());

    public ThumbPluginSWT() {
        new Starter().start();
    }

    private class Starter
            extends Thread {

        public void run() {
            mLog.info( ".Starter.run(), started" );
            ConfigurationView lConfView = null;
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
            
            ThumbDrivePanel lView = new ThumbDrivePanel();
            
            lConfView.addPluginPanel( "Thumb Drive",
                lView
            );
            
//            new OneClickController().setView( lView );
            mLog.info( ".Starter.run(), done" );
        }
    }
}