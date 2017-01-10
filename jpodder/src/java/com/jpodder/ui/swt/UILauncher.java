package com.jpodder.ui.swt;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.jpodder.FileHandler;
import com.jpodder.Logic;
import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.language.ILanguageListener;
import com.jpodder.data.language.LanguageEvent;
import com.jpodder.data.language.LanguageLogic;
import com.jpodder.tasks.ITaskListener;
import com.jpodder.tasks.TaskEvent;
import com.jpodder.ui.swt.comp.DisplayTool;
import com.jpodder.ui.swt.conf.ConfigurationController;
import com.jpodder.ui.swt.directory.DirectoryController;
import com.jpodder.ui.swt.download.DownloadsController;
import com.jpodder.ui.swt.feeds.FeedController;
import com.jpodder.ui.swt.help.HelpView;
import com.jpodder.ui.swt.log.LogController;
import com.jpodder.ui.swt.remote.RPCServerController;
import com.jpodder.ui.swt.tabs.TabController;
import com.jpodder.ui.swt.tabs.TabView;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.ui.swt.window.Systray;
import com.jpodder.ui.swt.window.WindowController;
import com.jpodder.ui.swt.window.WindowView;

/**
 * The UI Launcher creates the UI Controllers. ( Classes containing actions
 * which are tied to UI controls). The UI controllers are instantiated.
 * 
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class UILauncher implements ITaskListener, ILanguageListener {

	Logger mLog = Logger.getLogger(getClass().getName());

	// Maintain status for the UI, to be used when re-initializing the UI.
	private boolean mUIReboot = false;

	private boolean mUIInitialized = false;

	public static WindowView lWindowUI;

	protected Systray mSystemTray;

	private Display mDisplay;

	private Shell mShell;

	private Point mSize;

	private Point mLocation;

	ConfigurationController lConfController;

	WindowController lWindowController;

	FeedController lFeedController;

	LogController lLogController;

	DownloadsController lDownloadsController;

	DirectoryController lDirectoryController;

	RPCServerController lRPCServerController;

	static UILauncher sSelf;

	public boolean getRebooting() {
		return mUIReboot;
	}

	public static UILauncher getInstance() {
		return sSelf;
	}

	public Display getDisplay() {
		return mDisplay;
	}

	public Shell getShell() {
		return mShell;
	}

	public UILauncher(String pArg0) {
		sSelf = this;
		launch();
		ConfigurationLogic.getInstance().fireConfigurationChanged(
				new ConfigurationEvent(ConfigurationLogic.class));

		LanguageLogic.getInstance().addListener(this);
		dispatch();
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// --- CODE BEYOND HERE (DISPATCH) IS NEVER EVER EXECUTED --------
	}

	private void launch() {

		// ///////////////////////////////////////////////////////////////////////
		// SWT port.
		// UI Initialization steps:
		// 1) Create the controllers (Only once).
		// 2) Set the controller - cross references.
		// 3) Create a controller map and add it to the Tab Controller.
		// ///////////////////////////////////////////////////////////////////////

		// CB Sleak checks for SWT memory leaks.
		// Comment out, if when not DEBUGGING

		if (mDisplay == null) {
			// Dispose can not happen when ongoing async requests are happening.
			// DeviceData data = new DeviceData();
			// data.tracking = true;
			// mDisplay = new Display(data);
			// Sleak sleak = new Sleak();
			// sleak.open();
			mDisplay = new Display();
		}

		UITheme.getInstance().initializeLegacy();

		// All below should be re-initialized when re-launching.
		mShell = new Shell(mDisplay);
		mShell.addControlListener(new ControlListener() {

			public void controlMoved(ControlEvent arg0) {
				storeLocation();
			}

			public void controlResized(ControlEvent arg0) {
				storeSize();
			}
		});

		mShell.addDisposeListener(new UIDisposeListener());

		Intro lIntro = new Intro();
		lIntro.show();

		launchSystray();
		if (Configuration.getInstance().getGui().getVisible()) {
			// If it's iconified, we don't need to initialiaze the shell.
			// Instantiate the system tray.
			initialize();
		}
		// lConfiguration.getGui().getMaximized();
		// lConfiguration.getGui().getVisible();

		lIntro.hide();

		// Wait another second before firing configuration changes.

	}

	public void relaunch() {

		mUIReboot = true;
		Thread lDelayedRestart = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(500);
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							mDisplay.getShells()[0].dispose();
							mUIInitialized = false;
							launch();
							mUIReboot = false;
						}
					});
				} catch (InterruptedException e) {
					// Should not be interrupted.
				}
			}
		});
		lDelayedRestart.start();
	}

	private class UIDisposeListener implements DisposeListener {
		public void widgetDisposed(DisposeEvent e) {

			// This is a re-launch.
			if (mUIReboot) {
				saveUI();
				UITheme.getInstance().dispose();
				TabController.getInstance().saveProperties();
				mSystemTray.dispose();

			} else {
				bye();
			}
		}
	}

	public void initialize() {
		// if (!mUIRelaunched) {
		launchControllers();
		// }
		launchViews();
		mUIInitialized = true;
	}

	/**
	 * As the WindowUI been initialized?
	 * 
	 * @return If initialized true, and if not false.
	 */
	public boolean isInitialized() {
		return mUIInitialized;
	}

	public void dispatch() {

		while (!mShell.isDisposed()) {
			if (!mDisplay.readAndDispatch())
				mDisplay.sleep();
		}
		mDisplay.dispose();
	}

	/**
	 * Store application propeties, save xml file and ...bye
	 */
	public void bye() {
		try {
			finalizeUI();
			Logic.getInstance().finalizeLogic();
		} catch (Exception ie) {
			mLog.warn(ie);
		} finally {
			System.exit(0);
		}
	}

	private void finalizeUI() {
		saveUI();
		TabController.getInstance().saveProperties();
		UITheme.getInstance().dispose();
		mSystemTray.dispose();
	}

	public void launchSystray() {
		mSystemTray = new Systray();
		ConfigurationLogic.getInstance().addConfigListener(mSystemTray);
	}

	public void launchControllers() {

		lConfController = new ConfigurationController();
		UIHelper.mConfController = lConfController;
		lWindowController = new WindowController();
		lFeedController = new FeedController();
		lLogController = new LogController();
		lDownloadsController = new DownloadsController();
		lDirectoryController = new DirectoryController();
		lRPCServerController = new RPCServerController();

		lDownloadsController.setController(lFeedController);
		lDirectoryController.setController(lFeedController);
		lRPCServerController.setControllers(lFeedController);
		lFeedController.setControllers(null, null);
	}

	public void launchViews() {

		// ---------
		lWindowUI = new WindowView();

		lWindowUI.setController(lWindowController);
		lWindowController.setView(lWindowUI);

		TabView lTabView = new TabView();

		HashMap<String, Object> lControllerMap = new HashMap<String, Object>();
		lControllerMap.put(lTabView.TAB_FEEDS_TITLE, lFeedController);
		lControllerMap.put(lTabView.TAB_DIRECTORY_TITLE, lDirectoryController);
		lControllerMap.put(lTabView.TAB_LOG_TITLE, lLogController);
		lControllerMap.put(lTabView.TAB_SETTINGS_TITLE, lConfController);
		lControllerMap.put(lTabView.TAB_DOWNLOAD_TITLE, lDownloadsController);

		TabController.getInstance().setControllers(lControllerMap);
		TabController.getInstance().setView(lTabView);

		HelpView lHelpView = new HelpView();

		// mShell.pack();
		setUI();
		open();
	}

	/**
	 * Show a splash Screen.
	 */
	class Intro {

		Shell lIntroShell;

		// Region region;
		Image mImage;

		public void show() {

			// final Image image = mDisplay.getSystemImage(SWT.ICON_WARNING);
			String lImagePath = FileHandler.sImageDirectory + File.separator
					+ "splash.png";
			if (!(new File(lImagePath).exists())) {
				return;
			}
			mImage = new Image(mDisplay, lImagePath);

			// Shell must be created with style SWT.NO_TRIM
			lIntroShell = new Shell(mDisplay, SWT.NO_TRIM);
			// shell.setBackground(mDisplay.getSystemColor(SWT.COLOR_RED));
			// define a region
			// region = new Region();
			// Rectangle pixel = new Rectangle(0, 0, 1, 1);
			// for (int y = 0; y < mImage.getBounds().width; y += 2) {
			// for (int x = 0; x < mImage.getBounds().height; x += 2) {
			// pixel.x = x;
			// pixel.y = y;
			// region.add(pixel);
			// }
			// }
			// define the shape of the shell using setRegion
			// shell.setRegion(region);
			// Rectangle size = region.getBounds();
			Rectangle size = mImage.getBounds();

			lIntroShell.setSize(size.width, size.height);
			lIntroShell.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					Rectangle bounds = mImage.getBounds();
					Point size = lIntroShell.getSize();
					e.gc.drawImage(mImage, 0, 0, bounds.width, bounds.height,
							10, 10, size.x - 20, size.y - 20);
				}
			});
			lIntroShell.setLocation(DisplayTool.getCenterPosition(size.width,
					size.height));
			lIntroShell.open();

		}

		public void hide() {
			lIntroShell.dispose();
			// region.dispose();
			mImage.dispose();
		}
	}

	// ------ Tasks related to the loading of the configuration.
	public void taskCompleted(TaskEvent e) {
		System.out.println(e.getSource());
		ConfigurationLogic.getInstance().fireConfigurationChanged(
				new ConfigurationEvent(ConfigurationLogic.class));
	}

	public void taskAborted(TaskEvent e) {

	}

	public void taskFailed(TaskEvent e) {

	}

	public void languageChanged(LanguageEvent e) {
		relaunch();
	}

	public void showConsole() {

	}

	public void open() {
		mShell.setVisible(true);
		mShell.setActive();
	}

	public void close() {
		mShell.setVisible(false);

	}

	/**
	 * Save the UI properties in the properties handler.
	 */
	public void saveUI() {
		Configuration lConfiguration = Configuration.getInstance();

		lConfiguration.getGui().setIconified(mShell.getMinimized());
		lConfiguration.getGui().setMaximized(mShell.getMaximized());
		lConfiguration.getGui().setVisible(mShell.getVisible());

		if (mSize != null) {
			lConfiguration.getGui().setWidth(mSize.x);
			lConfiguration.getGui().setHeight(mSize.y);
		}
		if (mLocation != null) {
			lConfiguration.getGui().setX(mLocation.x);
			lConfiguration.getGui().setY(mLocation.y);
		}
	}

	private void storeLocation() {
		if (mShell.getLocation().x > 0) {
			mLocation = mShell.getLocation();
			mLog.info("Repositioned to: " + mLocation.x + "," + mLocation.y);
		}
	}

	private void storeSize() {
		if (mShell.getSize().x > 0) {
			mSize = mShell.getSize();
			// Also fired, when maximizing. We should ignore in this case.
			mLog.info("Resized to: " + mSize.x + "x" + mSize.y);
		}
	}

	public void setUI() {

		Configuration lConfiguration = Configuration.getInstance();
		int lWidth = lConfiguration.getGui().getWidth();
		int lHeight = lConfiguration.getGui().getHeight();

		mShell.setSize(lWidth, lHeight);
		int x = lConfiguration.getGui().getX();
		int y = lConfiguration.getGui().getY();

		if (x >= 0 && y >= 0) {
			mShell.setLocation(new Point(x, y));
		}
		// if (lConfiguration.getGui().getIconified()) {
		// mShell.setMinimized(lConfiguration.getGui().getIconified());
		// }
		if (lConfiguration.getGui().getMaximized()) {
			mShell.setMaximized(lConfiguration.getGui().getMaximized());
		}
		if (lConfiguration.getGui().getVisible()) {
			mShell.setVisible(lConfiguration.getGui().getVisible());
		}
	}

}
