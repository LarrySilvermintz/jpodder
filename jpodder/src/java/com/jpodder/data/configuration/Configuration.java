package com.jpodder.data.configuration;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.util.Debug;

/**
 * The caching class contains the current configuration of the application
 */
public class Configuration implements IDataController {

	public static final int SCHEDULING_OFF = -1;

	public static final int SCHEDULING_TYPE_TIMER = 0;

	public static final int SCHEDULING_TYPE_INTERVAL = 1;

	public static final String CONFIG_SOUND = "Sound";

	public static final String CONFIG_FOLDER = "Folder";

	public static final String CONFIG_MARKMAX = "MarkMax";

	public static final String CONFIG_TORRENTON = "TorrentDefault";

	public static final String CONFIG_CACHELEARN = "CacheLearn";

	public static final String CONFIG_CACHEFILE = "CacheFile";

	public static final String CONFIG_PLAYER = "Player";

	public static final String CONFIG_AUTO = "Auto";

	public static final String CONFIG_AUTOPREVIEW = "AutoPreview";

	public static final String CONFIG_LANGUAGE = "Language";

	public static final String CONNECTION_PROXYON = "ProxyEnabled";

	public static final String CONNECTION_PROXY_SERVER = "Proxy";

	public static final String CONNECTION_PROXY_PORT = "ProxyPort";

	public static final String CONNECTION_PROXY_USER = "UserName";

	public static final String CONNECTION_PROXY_PASSWORD = "Password";

	public static final String CONNECTION_HTTP_TIMEOUT = "Timeout";

	public static final String SCHEDULING_DELAY = "Delay";

	public static final String SCHEDULING_EXEC = "ExecuteOnStartup";

	public static final String SCHEDULING_TYPE = "Type";

	public static Configuration mInstance;

	public static Configuration getInstance() {
		if (mInstance == null) {
			mInstance = new Configuration(true);
		}
		return mInstance;
	}

	private static SimpleDateFormat sTimerFormatter;

	protected Logger mLog = Logger.getLogger(getClass().getName());

	/**
	 * A synchronized list of download objects.
	 */
	private ConfigurationDataHandler mDataHandler = new ConfigurationDataHandler(
			this);

	private boolean mIsModified;

	private String mFolder;

	private boolean mSound;

	private boolean mAuto;

	private int mDelay;

	private String mPlayer;

	private String mLanguage;

	private URL mOpmlUrl;

	private boolean mAutoPreview;

	private boolean mTorrentDefault;

	private boolean mCacheLearn;

	private String mCacheFile;

	private boolean mMarkMax;

	private int mLogSize;

	private String mOPMLSync;

	private Connection mConnection;

	private Gui mGui;

	private Production mProduction;

	private Scheduling mScheduling;

	public Configuration() {
	}

	private Configuration(boolean pMarker) {
		try {
			ConfigurationLogic.getInstance()
					.addDataHandler(mDataHandler, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IDataHandler getHandler() {
		return mDataHandler;
	}

	/**
	 * Takes the given Configuration Instance a takes over its values
	 * 
	 * @param pConfiguration
	 *            Configuration instance providing the values. If null then
	 *            default values are set
	 */
	public void set(Configuration pConfiguration) {
		if (pConfiguration == null) {
			pConfiguration = new Configuration();
		}
		setFolder(pConfiguration.getFolder());
		setSound(pConfiguration.getSound());
		setAuto(pConfiguration.getAuto());
		setDelay(pConfiguration.getDelay());
		setPlayer(pConfiguration.getPlayer());
		setLanguage(pConfiguration.getLanguage());
		setOpmlUrl(pConfiguration.getOpmlUrl());
		setAutoPreview(pConfiguration.getAutoPreview());
		setTorrentDefault(pConfiguration.getTorrentDefault());
		setCacheLearn(pConfiguration.getCacheLearn());
		setCacheFile(pConfiguration.getCacheFile());
		setMarkMax(pConfiguration.getMarkMax());
		setLogSize(pConfiguration.getLogSize());
		// AS Go on with the inner classes
		setOMPLSync(pConfiguration.getOMPLSync());
		getConnection().set(pConfiguration.getConnection());
		getGui().set(pConfiguration.getGui());
		getProduction().set(pConfiguration.getProduction());
		getScheduling().set(pConfiguration.getScheduling());

	}

	/**
	 * @return the value of Folder.
	 */
	public String getFolder() {
		return mFolder;
	}

	/**
	 * Sets the value of Folder.
	 * 
	 * @param pFolder
	 *            The value to assign to mFolder.
	 */
	public void setFolder(String pFolder) {
		mFolder = pFolder;
	}

	/**
	 * @return the value of Sound.
	 */
	public boolean getSound() {
		if (Debug.WITH_DEV_DEBUG) {
			mLog.info("getSound(), this: " + this + ", value: " + mSound);
		}
		return mSound;
	}

	/**
	 * Sets the value of Sound.
	 * 
	 * @param pSound
	 *            The value to assign to mSound.
	 */
	public void setSound(boolean pSound) {
		if (Debug.WITH_DEV_DEBUG) {
			mLog.info("setSound(), this: " + this + ", old value: " + mSound
					+ ", new value: " + pSound);
		}
		mSound = pSound;
	}

	/**
	 * @return the value of Auto.
	 */
	public boolean getAuto() {
		return mAuto;
	}

	/**
	 * Sets the value of Auto.
	 * 
	 * @param pAuto
	 *            The value to assign to mAuto.
	 */
	public void setAuto(boolean pAuto) {
		mAuto = pAuto;
	}

	public void setOMPLSync(String pOPMLSync) {
		mOPMLSync = pOPMLSync;
	}

	public String getOMPLSync() {
		return mOPMLSync;
	}

	/**
	 * @return the value of Delay.
	 */
	public int getDelay() {
		return mDelay;
	}

	/**
	 * Sets the value of Delay.
	 * 
	 * @param pDelay
	 *            The value to assign to mDelay.
	 */
	public void setDelay(int pDelay) {
		mDelay = pDelay;
	}

	/**
	 * @return the value of Player.
	 */
	public String getPlayer() {
		return mPlayer;
	}

	/**
	 * Sets the value of Player.
	 * 
	 * @param pPlayer
	 *            The value to assign to mPlayer.
	 */
	public void setPlayer(String pPlayer) {
		mPlayer = pPlayer;
	}

	/**
	 * Get the language.
	 * 
	 * @return
	 */
	public String getLanguage() {
		return mLanguage;
	}

	/**
	 * Set the language.
	 * 
	 * @param pLanguage
	 */
	public void setLanguage(String pLanguage) {
		mLanguage = pLanguage;
	}

	/**
	 * @return the value of OpmlUrl.
	 */
	public URL getOpmlUrl() {
		return mOpmlUrl;
	}

	/**
	 * Sets the value of OpmlUrl.
	 * 
	 * @param pOpmlUrl
	 *            The value to assign to mOpmlUrl.
	 */
	public void setOpmlUrl(URL pOpmlUrl) {
		mOpmlUrl = pOpmlUrl;
	}

	/**
	 * @return the value of AutoPreview.
	 */
	public boolean getAutoPreview() {
		return mAutoPreview;
	}

	/**
	 * Sets the value of AutoPreview.
	 * 
	 * @param pAutoPreview
	 *            The value to assign to mAutoPreview.
	 */
	public void setAutoPreview(boolean pAutoPreview) {
		mAutoPreview = pAutoPreview;
	}

	/**
	 * @return the value of TorrentDefault.
	 */
	public boolean getTorrentDefault() {
		return mTorrentDefault;
	}

	/**
	 * Sets the value of TorrentDefault.
	 * 
	 * @param pTorrentDefault
	 *            The value to assign to mTorrentDefault.
	 */
	public void setTorrentDefault(boolean pTorrentDefault) {
		mTorrentDefault = pTorrentDefault;
	}

	/**
	 * @return the value of CacheLearn.
	 */
	public boolean getCacheLearn() {
		return mCacheLearn;
	}

	/**
	 * Sets the value of CacheLearn.
	 * 
	 * @param pCacheLearn
	 *            The value to assign to mCacheLearn.
	 */
	public void setCacheLearn(boolean pCacheLearn) {
		mCacheLearn = pCacheLearn;
	}

	/**
	 * @return the value of MarkMax.
	 */
	public boolean getMarkMax() {
		return mMarkMax;
	}

	/**
	 * Sets the value of MarkMax.
	 * 
	 * @param pMarkMax
	 *            The value to assign to mMarkMax.
	 */
	public void setMarkMax(boolean pMarkMax) {
		mMarkMax = pMarkMax;
	}

	/**
	 * @return the value of LogSize.
	 */
	public int getLogSize() {
		return mLogSize;
	}

	/**
	 * Sets the value of LogSize.
	 * 
	 * @param pLogSize
	 *            The value to assign to mLogSize.
	 */
	public void setLogSize(int pLogSize) {
		mLogSize = pLogSize;
	}

	public void setModified() {
		mIsModified = true;
	}

	public boolean isModified() {
		// AS return mIsModified;
		return true;
	}

	/** Marks this Data Controller to be unmodified * */
	public void setUpdated() {
		mIsModified = false;
	}

	/**
	 * @return the current connection settings instance and creates it if it
	 *         does not exists
	 */
	public Connection getConnection() {
		if (mConnection == null) {
			mConnection = new Connection();
		}
		return mConnection;
	}

	/**
	 * @return The path to the current cache file.
	 */
	public String getCacheFile() {
		return mCacheFile;
	}

	/**
	 * Set the cache file location.
	 * 
	 * @param pCacheFile
	 */
	public void setCacheFile(String pCacheFile) {
		mCacheFile = pCacheFile;
	}

	/**
	 * @return the current production settings instance and creates it if it
	 *         does not exists
	 */
	public Production getProduction() {
		if (mProduction == null) {
			mProduction = new Production();
		}
		return mProduction;
	}

	/**
	 * @return the current GUI settings instance and creates it if it does not
	 *         exists
	 */
	public Gui getGui() {
		if (mGui == null) {
			mGui = new Gui();
		}
		return mGui;
	}

	/**
	 * @return the current scheduling settings instance and creates it if it
	 *         does not exists
	 */
	public Scheduling getScheduling() {
		if (mScheduling == null) {
			mScheduling = new Scheduling();
		}
		return mScheduling;
	}

	public class Connection {
		private int mTimeout;

		private boolean mProxyEnabled;

		private URL mProxy;

		private String mUserName;

		private String mPassword;

		private int mProxyPort;

		public void set(Connection pSource) {
			setProxyEnabled(pSource.getProxyEnabled());
			setProxy(pSource.getProxy());
			setUserName(pSource.getUserName());
			setPassword(pSource.getPassword());
			setTimeout(pSource.getTimeout());
			this.setProxyPort(pSource.getProxyPort());
		}

		/**
		 * @return the value of Timeout.
		 */
		public int getTimeout() {
			return mTimeout;
		}

		/**
		 * Sets the value of Timeout.
		 * 
		 * @param pTimeout
		 *            The value to assign to mTimeout.
		 */
		public void setTimeout(int pTimeout) {
			mTimeout = pTimeout;
		}

		/**
		 * @return the value of ProxyEnabled.
		 */
		public boolean getProxyEnabled() {
			return mProxyEnabled;
		}

		/**
		 * Sets the value of ProxyEnabled.
		 * 
		 * @param pProxyEnabled
		 *            The value to assign to mProxyEnabled.
		 */
		public void setProxyEnabled(boolean pProxyEnabled) {
			mProxyEnabled = pProxyEnabled;
		}

		/**
		 * @return the value of Proxy.
		 */
		public URL getProxy() {

			return mProxy;
		}

		public void setProxyPort(int pProxyPort) {
			mProxyPort = pProxyPort;
		}

		public int getProxyPort() {
			return mProxyPort;
		}

		/**
		 * Sets the value of Proxy.
		 * 
		 * @param pProxy
		 *            The value to assign to mProxy.
		 */
		public void setProxy(URL pProxy) {
			mProxy = pProxy;
		}

		/**
		 * @return the value of UserName.
		 */
		public String getUserName() {
			return mUserName;
		}

		/**
		 * Sets the value of UserName.
		 * 
		 * @param pUserName
		 *            The value to assign to mUserName.
		 */
		public void setUserName(String pUserName) {
			mUserName = pUserName;
		}

		/**
		 * @return the value of Password.
		 */
		public String getPassword() {
			return mPassword;
		}

		/**
		 * Sets the value of Password.
		 * 
		 * @param pPassword
		 *            The value to assign to mPassword.
		 */
		public void setPassword(String pPassword) {
			mPassword = pPassword;
		}
	}

	public class Production {
		private String mFolder;

		private String mFile;

		private String mRecorder;

		public void set(Production pSource) {
			setFolder(pSource.getFolder());
			setFile(pSource.getFile());
			setRecorder(pSource.getRecorder());
		}

		/**
		 * @return the value of Folder.
		 */
		public String getFolder() {
			return mFolder;
		}

		/**
		 * Sets the value of Folder.
		 * 
		 * @param pFolder
		 *            The value to assign to mFolder.
		 */
		public void setFolder(String pFolder) {
			mFolder = pFolder;
		}

		/**
		 * @return the value of File.
		 */
		public String getFile() {
			return mFile;
		}

		/**
		 * Sets the value of File.
		 * 
		 * @param pFile
		 *            The value to assign to mFile.
		 */
		public void setFile(String pFile) {
			mFile = pFile;
		}

		public void setRecorder(String pRecorder) {
			mRecorder = pRecorder;
		}

		public String getRecorder() {
			return mRecorder;
		}

	}

	public class Gui {
		private boolean mIconified;

		private boolean mMaximized;

		private int mHeight;

		private int mWidth;

		private boolean mVisible = true;

		private int mX;

		private int mY;

		private boolean mDirectory;

		private boolean mLog;

		private boolean mProduction;

		private boolean mDownload;

		private boolean mTorrent;

		private boolean mSettings;

		private boolean mFileView;

		private boolean mHelp;

		public void set(Gui pSource) {
			setIconified(pSource.getIconified());
			setMaximized(pSource.getMaximized());
			setHeight(pSource.getHeight());
			setWidth(pSource.getWidth());
			setVisible(pSource.getVisible());
			setX(pSource.getX());
			setY(pSource.getY());
			setDirectory(pSource.getDirectory());
			setLog(pSource.getLog());
			setProduction(pSource.getProduction());
			setDownload(pSource.getDownload());
			setTorrent(pSource.getTorrent());
			setSettings(pSource.getSettings());
			setHelp(pSource.getHelp());
			setFileview(pSource.getFileview());
		}

		public boolean getFileview() {
			return mFileView;
		}

		public void setFileview(boolean pFileView) {
			mFileView = pFileView;
		}

		/**
		 * @return the value of Iconified.
		 */
		public boolean getIconified() {
			return mIconified;
		}

		/**
		 * Sets the value of Iconified.
		 * 
		 * @param pIconified
		 *            The value to assign to mIconified.
		 */
		public void setIconified(boolean pIconified) {
			mIconified = pIconified;
		}

		/**
		 * @return the value of Maximized.
		 */
		public boolean getMaximized() {
			return mMaximized;
		}

		/**
		 * Sets the value of Maximized.
		 * 
		 * @param pMaximized
		 *            The value to assign to mMaximized .
		 */
		public void setMaximized(boolean pMaximized) {
			mMaximized = pMaximized;
		}

		/**
		 * @return the value of Height.
		 */
		public int getHeight() {
			return mHeight;
		}

		/**
		 * Sets the value of Height.
		 * 
		 * @param pHeight
		 *            The value to assign to mHeight.
		 */
		public void setHeight(int pHeight) {
			mHeight = pHeight;
		}

		/**
		 * @return the value of Width.
		 */
		public int getWidth() {
			return mWidth;
		}

		/**
		 * Sets the value of Width.
		 * 
		 * @param pWidth
		 *            The value to assign to mWidth.
		 */
		public void setWidth(int pWidth) {
			mWidth = pWidth;
		}

		/**
		 * @return the value of Visible.
		 */
		public boolean getVisible() {
			return mVisible;
		}

		/**
		 * Sets the value of Visible.
		 * 
		 * @param pVisible
		 *            The value to assign to mVisible.
		 */
		public void setVisible(boolean pVisible) {
			mVisible = pVisible;
		}

		/**
		 * @return the value of X.
		 */
		public int getX() {
			return mX;
		}

		/**
		 * Sets the value of X.
		 * 
		 * @param pX
		 *            The value to assign to mX.
		 */
		public void setX(int pX) {
			mX = pX;
		}

		/**
		 * @return the value of Y.
		 */
		public int getY() {
			return mY;
		}

		/**
		 * Sets the value of Y.
		 * 
		 * @param pY
		 *            The value to assign to mY.
		 */
		public void setY(int pY) {
			mY = pY;
		}

		/**
		 * @return the value of Directory.
		 */
		public boolean getDirectory() {
			return mDirectory;
		}

		/**
		 * Sets the value of Directory.
		 * 
		 * @param pDirectory
		 *            The value to assign to mDirectory.
		 */
		public void setDirectory(boolean pDirectory) {
			mDirectory = pDirectory;
		}

		/**
		 * @return the value of Log.
		 */
		public boolean getLog() {
			return mLog;
		}

		/**
		 * Sets the value of Log.
		 * 
		 * @param pLog
		 *            The value to assign to mLog.
		 */
		public void setLog(boolean pLog) {
			mLog = pLog;
		}

		/**
		 * @return the value of Production.
		 */
		public boolean getProduction() {
			return mProduction;
		}

		/**
		 * Sets the value of Production.
		 * 
		 * @param pProduction
		 *            The value to assign to mProduction.
		 */
		public void setProduction(boolean pProduction) {
			mProduction = pProduction;
		}

		/**
		 * @return the value of Download.
		 */
		public boolean getDownload() {
			return mDownload;
		}

		/**
		 * Sets the value of Download.
		 * 
		 * @param pDownload
		 *            The value to assign to mDownload.
		 */
		public void setDownload(boolean pDownload) {
			mDownload = pDownload;
		}

		public void setTorrent(boolean pTorrent) {
			mTorrent = pTorrent;
		}

		public boolean getTorrent() {
			return mTorrent;
		}

		public void setSettings(boolean pSettings) {
			mSettings = pSettings;
		}

		public boolean getSettings() {
			return mSettings;
		}

		/**
		 * @return the value of Help.
		 */
		public boolean getHelp() {
			return mHelp;
		}

		/**
		 * Sets the value of Help.
		 * 
		 * @param pHelp
		 *            The value to assign to mHelp.
		 */
		public void setHelp(boolean pHelp) {
			mHelp = pHelp;
		}
	}

	public class Scheduling {
		private int mType;

		private int mInterval;

		private List<Timer> mTimer = new ArrayList<Timer>();

		private boolean mExecuteOnStartup;

		public void set(Scheduling pSource) {
			setType(pSource.getType());
			setInterval(pSource.getInterval());
			setExecuteOnStartup(pSource.getExecuteOnStartup());
			mTimer.clear();
			mTimer.addAll(pSource.mTimer);
		}

		/**
		 * @return the value of Type.
		 */
		public int getType() {
			return mType;
		}

		/**
		 * Sets the value of Type.
		 * 
		 * @param pType
		 *            The value to assign to mType.
		 */
		public void setType(int pType) {
			mType = pType;
		}

		/**
		 * @return the value of Interval.
		 */
		public int getInterval() {
			return mInterval;
		}

		/**
		 * Sets the value of Interval.
		 * 
		 * @param pInterval
		 *            The value to assign to mInterval.
		 */
		public void setInterval(int pInterval) {
			mInterval = pInterval;
		}

		/**
		 * @return the value of ExecuteOnStartup.
		 */
		public boolean getExecuteOnStartup() {
			return mExecuteOnStartup;
		}

		/**
		 * Sets the value of ExecuteOnStartup.
		 * 
		 * @param pExecuteOnStartup
		 *            The value to assign to mExecuteOnStartup.
		 */
		public void setExecuteOnStartup(boolean pExecuteOnStartup) {
			mExecuteOnStartup = pExecuteOnStartup;
		}

		/**
		 * Adds a new timer to the list
		 * 
		 * @param pTimer
		 *            Timer string to create the timer from (HHMM AM|PM)
		 */
		public void addTimer(String pTimer) {
			try {
				mTimer.add(new Timer(pTimer));
			} catch (ParseException pe) {
				// should turn this timer off.
				mLog.warn("getScheduleTimeCounters(), error setting time: "
						+ pTimer);
			}
		}

		public void addTimer(Date pTimer) {
			try {
				mTimer.add(new Timer(pTimer));
			} catch (ParseException pe) {
				// should turn this timer off.
				mLog.warn("getScheduleTimeCounters(), error setting time: "
						+ pTimer);
			}
		}

		public void clearTimers() {
			mTimer.clear();
		}

		public int getTimerSize() {
			return mTimer.size();
		}

		public Iterator getTimerIterator() {
			return mTimer.iterator();
		}

		public class Timer {
			private Date mTimer;

			public Timer(Date pTimer) throws ParseException {
				mTimer = pTimer;
			}

			public Timer(String pTimer) throws ParseException {
				if (sTimerFormatter == null) {
					sTimerFormatter = new SimpleDateFormat("hhmm a");
				}
				mTimer = sTimerFormatter.parse(pTimer);
			}

			public Date getTimer() {
				return mTimer;
			}

			public String getTimerString() {
				if (sTimerFormatter == null) {
					sTimerFormatter = new SimpleDateFormat("hhmm a");
				}
				return sTimerFormatter.format(mTimer);
			}
		}
	}
}