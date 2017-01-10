package com.jpodder.data.configuration;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import com.jpodder.JPodderException;
import com.jpodder.configuration.JPodderDocument;
import com.jpodder.configuration.TConnection;
import com.jpodder.configuration.TGui;
import com.jpodder.configuration.TJPodder;
import com.jpodder.configuration.TProduction;
import com.jpodder.configuration.TScheduleTypes;
import com.jpodder.configuration.TScheduling;

/**
 * Data Handler implementation for the Configuration Data.
 * 
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class ConfigurationDataHandler implements IDataHandler {

    protected Logger mLog = Logger.getLogger(getClass().getName());

    private boolean mIsModified = true;

    private Configuration mConfiguration;

	private File mFile;

    public ConfigurationDataHandler(Configuration pDataContainer) {
        mConfiguration = pDataContainer;
    }

    public int getIndex() {
        return ConfigurationLogic.CONFIGURATION_INDEX;
    }

    public boolean isModified() {
        return mConfiguration.isModified();
    }

    public String getContent() throws Exception {
        mLog.info("getContent(), return the content of the feed list");
        JPodderDocument lDocument = JPodderDocument.Factory.newInstance();
        TJPodder lRoot = lDocument.addNewJPodder();
        Configuration lConfiguration = Configuration.getInstance();

        lRoot.setFolder(lConfiguration.getFolder());
        lRoot.setSound(lConfiguration.getSound());
        lRoot.setAuto(lConfiguration.getAuto());
        lRoot.setDelay(IDataHandler.Util
                .getBigInteger(lConfiguration.getDelay()));
        lRoot.setPlayer(lConfiguration.getPlayer());
        
        if (lConfiguration.getLanguage() != null) {
            lRoot.setLanguage(lConfiguration.getLanguage());
        }
        URL lUrl = lConfiguration.getOpmlUrl();
        if (lUrl != null) {
            lRoot.setOpmlUrl(lUrl.toString());
        }
        lRoot.setAutoPreview(lConfiguration.getAutoPreview());
        lRoot.setTorrentDefault(lConfiguration.getTorrentDefault());
        lRoot.setCacheLearn(lConfiguration.getCacheLearn());
        lRoot.setCacheFile(lConfiguration.getCacheFile());
        lRoot.setMarkMax(lConfiguration.getMarkMax());
        lRoot.setLogSize(IDataHandler.Util.getBigInteger(lConfiguration
                .getLogSize()));
        lRoot.setOpmlUrl(lConfiguration.getOMPLSync());
        TConnection lTConnection = lRoot.addNewConnection();
        Configuration.Connection lConnection = lConfiguration.getConnection();
        lTConnection.setTimeout(IDataHandler.Util.getBigInteger(lConnection
                .getTimeout()));
        lTConnection.setProxyEnabled(lConnection.getProxyEnabled());
        lUrl = lConnection.getProxy();
        if (lUrl != null) {
            lTConnection.setProxy(lUrl.toExternalForm());
        }

        lTConnection.setProxyPort(IDataHandler.Util.getBigInteger(lConnection
                .getProxyPort()));
        lTConnection.setProxyUser(lConnection.getUserName());
        lTConnection.setProxyPassword(lConnection.getPassword());

        TGui lTGui = lRoot.addNewGui();
        Configuration.Gui lGui = lConfiguration.getGui();
        lTGui.setIconified(lGui.getIconified());
        lTGui.setMaximized(lGui.getMaximized());
        lTGui.setHeight(IDataHandler.Util.getBigInteger(lGui.getHeight()));
        lTGui.setWidth(IDataHandler.Util.getBigInteger(lGui.getWidth()));
        lTGui.setVisible(lGui.getVisible());
        lTGui.setX(IDataHandler.Util.getBigInteger(lGui.getX()));
        lTGui.setY(IDataHandler.Util.getBigInteger(lGui.getY()));
        lTGui.setDirectory(lGui.getDirectory());
        lTGui.setLog(lGui.getLog());
        lTGui.setProduction(lGui.getProduction());
        lTGui.setDownload(lGui.getDownload());
        lTGui.setTorrent(lGui.getTorrent());
        lTGui.setSettings(lGui.getSettings());
        lTGui.setFileview(lGui.getFileview());
        lTGui.setHelp(lGui.getHelp());

        TScheduling lTScheduling = lRoot.addNewScheduling();
        Configuration.Scheduling lScheduling = lConfiguration.getScheduling();
        int lTypeIndex = lScheduling.getType();
        int lType = -1;
        switch (lTypeIndex) {
        case Configuration.SCHEDULING_TYPE_TIMER:
            lType = TScheduleTypes.INT_TIMER;
            break;
        case Configuration.SCHEDULING_TYPE_INTERVAL:
            lType = TScheduleTypes.INT_INTERVAL;
            break;
        }
        if (lType > 0) {
            lTScheduling.setType(TScheduleTypes.Enum.forInt(lType));
        }
        
//        lTScheduling.setIntervalType(lScheduling.getIntervalType());
//        lTScheduling.setTimeType(lScheduling.getTimeType());
        
        lTScheduling.setInterval(IDataHandler.Util.getBigInteger(lScheduling
                .getInterval()));
        lTScheduling.setExecuteOnStartup(lScheduling.getExecuteOnStartup());
        Iterator i = lScheduling.getTimerIterator();
        while (i.hasNext()) {
            Configuration.Scheduling.Timer lTimer = (Configuration.Scheduling.Timer) i
                    .next();
            lTScheduling.addTimer(lTimer.getTimerString());
        }
        
        TProduction lTProduction = lRoot.addNewProduction();
        lTProduction.setFile(lConfiguration.getProduction().getFile());
        lTProduction.setFolder(lConfiguration.getProduction().getFolder());
        lTProduction.setRecorder(lConfiguration.getProduction().getRecorder());
        
        StringWriter lOutput = new StringWriter();
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSavePrettyPrint();
        lDocument.save(lOutput);
        //AS NOTE: This could be dangerouse when we mark the content update but
        // the save to the file
        //AS fails afterwards
        mConfiguration.setUpdated();
        return lOutput.toString();
    }

    public void setContent(String pContent) throws Exception {
        mLog.info("setContent(), content to be set: " + pContent);
        if (pContent != null && pContent.trim().length() != 0) {
            Configuration lConfiguration = new Configuration();
            try {
                JPodderDocument lDocument = null;
                try {
                    lDocument = JPodderDocument.Factory.parse(pContent);
                } catch (Exception e) {
                    throw new JPodderException(CONTENT_CORRUPT, e);
                }
                TJPodder lRoot = lDocument.getJPodder();
                lConfiguration.setFolder(lRoot.getFolder());
                lConfiguration.setSound(lRoot.getSound());
                lConfiguration.setAuto(lRoot.getAuto());
                lConfiguration.setDelay(IDataHandler.Util.getInt(lRoot
                        .getDelay(), -1));
                lConfiguration.setPlayer(lRoot.getPlayer());

                String lLocale = lRoot.getLanguage();
                if (lLocale != null) {
                    lConfiguration.setLanguage(lLocale);
//                    String lLanguage = lLocale.substring(0, lLocale
//                            .indexOf(":"));
//                    String lCountry = lLocale.substring(
//                            lLocale.indexOf(":") + 1, lLocale.length());
//                    lConfiguration.setLanguage(new Locale(lLanguage, lCountry));
                } else {
                    // Make sure that a language is set
                    lConfiguration.setLanguage(Locale.getDefault().getDisplayName());
                }
                if ( lRoot.getOpmlUrl() != null) {
                    try {
                        URL lUrl = new URL(lRoot.getOpmlUrl());
                        lConfiguration.setOpmlUrl(lUrl);
                    } catch (Exception e) {
                    }
                }
                lConfiguration.setAutoPreview(lRoot.getAutoPreview());
                lConfiguration.setTorrentDefault(lRoot.getTorrentDefault());
                lConfiguration.setCacheLearn(lRoot.getCacheLearn());
                lConfiguration.setCacheFile(lRoot.getCacheFile());
                lConfiguration.setMarkMax(lRoot.getMarkMax());
                lConfiguration.setLogSize( 
                    IDataHandler.Util.getInt( lRoot.getLogSize(), 10000 )
                );
                lConfiguration.setOMPLSync(lRoot.getOpmlUrl());
                
                TConnection lTConnection = lRoot.getConnection();
                Configuration.Connection lConnection = lConfiguration
                        .getConnection();
                lConnection.setTimeout(IDataHandler.Util.getInt(lTConnection
                        .getTimeout(), -1));
                lConnection.setProxyEnabled(lTConnection.getProxyEnabled());
                String lProxy = lTConnection.getProxy();
                if (lProxy != null) {
                    try {
                        URL lUrl = new URL(lProxy);
                        lConnection.setProxy(lUrl);
                    } catch (Exception e) {
                    }
                }
                lConnection.setProxyPort(IDataHandler.Util.getInt(lTConnection
                        .getProxyPort(), 0));
                lConnection.setUserName(lTConnection.getProxyUser());
                lConnection.setPassword(lTConnection.getProxyPassword());

                TGui lTGui = lRoot.getGui();
                Configuration.Gui lGui = lConfiguration.getGui();
                lGui.setIconified(lTGui.getIconified());
                lGui.setMaximized(lTGui.getMaximized());
                lGui.setHeight(IDataHandler.Util.getInt(lTGui.getHeight(), -1));
                lGui.setWidth(IDataHandler.Util.getInt(lTGui.getWidth(), -1));
                lGui.setVisible(lTGui.getVisible());
                lGui.setX(IDataHandler.Util.getInt(lTGui.getX(), -1));
                lGui.setY(IDataHandler.Util.getInt(lTGui.getY(), -1));
                lGui.setDirectory(lTGui.getDirectory());
                lGui.setLog(lTGui.getLog());
//                lGui.setProduction(lTGui.getProduction());
                lGui.setDownload(lTGui.getDownload());
//                lGui.setTorrent(lTGui.getTorrent());
                lGui.setSettings(lTGui.getSettings());
                lGui.setHelp(lTGui.getHelp());
                lGui.setFileview(lTGui.getFileview());

                TScheduling lTScheduling = lRoot.getScheduling();
                Configuration.Scheduling lScheduling = lConfiguration
                        .getScheduling();
                TScheduleTypes.Enum lType = lTScheduling.getType();
                int lTypeIndex = Configuration.SCHEDULING_OFF;
                if (lType != null) {
                    switch (lType.intValue()) {
                    case TScheduleTypes.INT_TIMER:
                        lTypeIndex = Configuration.SCHEDULING_TYPE_TIMER;
                        break;
                    case TScheduleTypes.INT_INTERVAL:
                        lTypeIndex = Configuration.SCHEDULING_TYPE_INTERVAL;
                        break;
                    }
                }
                lScheduling.setType(lTypeIndex);
                lScheduling.setInterval(IDataHandler.Util.getInt(lTScheduling
                        .getInterval(), -1));
//                lScheduling.setIntervalType(lTScheduling.getIntervalType());
//                lScheduling.setTimeType(lTScheduling.getTimeType());
                lScheduling.setExecuteOnStartup(lTScheduling
                        .getExecuteOnStartup());
                String[] lTimers = lTScheduling.getTimerArray();
                for (int i = 0; i < lTimers.length; i++) {
                    lScheduling.addTimer(lTimers[i]);
                }
                
                TProduction lTProduction = lRoot.getProduction();
                Configuration.Production lProduction = lConfiguration.getProduction();
                lProduction.setFile(lTProduction.getFile());
                lProduction.setFolder(lTProduction.getFolder());
                lProduction.setRecorder(lTProduction.getRecorder());
                
            } catch( JPodderException jpe ) {
                throw jpe;
            } catch (Exception e) {
                mLog.warn("setContent(), failed", e);
            }
            mConfiguration.set(lConfiguration);
            mConfiguration.setUpdated();
            mLog.info("setContent(), configuration: " + lConfiguration);
        } else {
            throw new JPodderException(CONTENT_EMPTY);
        }
    }

    public boolean validate(String pContent, boolean pCompare) {
        return true; // Validation is implicit
    }

	public void setPersistentFile(File pFile) {
		mFile = pFile;
	}

	public File getPersistentFile() {
		return mFile;
	}
}