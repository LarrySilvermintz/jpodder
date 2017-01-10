package com.jpodder.ui.swt.log;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */

import org.apache.log4j.Logger;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.ui.swt.IController;
import com.jpodder.ui.swt.IView;
import com.jpodder.ui.swt.comp.BaseAction;

public class LogController implements IController, IConfigurationListener {

    private Logger mLog = Logger.getLogger(getClass().getName());

    private BaseAction showTorrentLogAction;

    private LogView mView;

    public LogController() {
        initialize();
    }

    public void initialize() {
        mLog.info("<init>");
    }

    public void setView(IView pView) {
        mView = (LogView)pView;
        initializeUI();
    }

    public void initializeUI() {
    }

    public void configurationChanged(ConfigurationEvent event) {
        if (!event.getSource().equals(ConfigurationLogic.class)) {
            return;
        }
        int lLogSize = Configuration.getInstance().getLogSize();
        mView.setLogSize(lLogSize);
    }
}
