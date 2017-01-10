package com.jpodder.ui.swt.window;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.data.download.Download;
import com.jpodder.data.download.DownloadLogic;
import com.jpodder.data.feeds.XFeedLogic;
import com.jpodder.net.NetTask;
import com.jpodder.net.NetTaskEvent;
import com.jpodder.net.INetTaskListener;
import com.jpodder.schedule.SchedulerLogic;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.help.HelpAbout;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Messages;

/**
 * System tray class
 */
public class Systray implements IConfigurationListener, INetTaskListener {

	Logger mLog = Logger.getLogger(Systray.class.getName());

	private MenuItem mMenuItemAuto;

	private boolean enabled = false;

	protected TrayItem mItem;

	protected Tray mTray;

	/**
	 * Constructor: Create a system tray.
	 * 
	 * @param app
	 *            Main
	 */
	public Systray() {

		mTray = Display.getDefault().getSystemTray();

		if (mTray == null) {
			mLog.info("The system tray is not available");
		} else {
			enabled = true;
			mItem = new TrayItem(mTray, SWT.NONE);
			// mItem.setToolTipText("SWT TrayItem");
			mItem.addListener(SWT.Show, new Listener() {
				public void handleEvent(Event event) {
					mLog.info("System Menu");
				}
			});
			mItem.addListener(SWT.Hide, new Listener() {
				public void handleEvent(Event event) {
					mLog.info("System Menu hide");
				}
			});
			mItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {

					if (UILauncher.getInstance().isInitialized()) {
						if (UILauncher.getInstance().getShell().isVisible()) {
							UILauncher.getInstance().close();
						} else {
							UILauncher.getInstance().open();
							UILauncher.getInstance().getShell().forceFocus();
						}
					} else {
						UILauncher.getInstance().initialize();
					}

				}
			});
			mItem.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent(Event event) {
					mLog.info("default selection (Double-Click)");
				}
			});

			final Menu menu = new Menu(UILauncher.getInstance().getShell(),
					SWT.POP_UP);

			MenuItem lMenuItemPodcast = new MenuItem(menu, SWT.CASCADE);
			lMenuItemPodcast.setText(Messages.getString("systray.podcast"));

			Menu lPodcastMenu = new Menu(UILauncher.getInstance().getShell(),
					SWT.DROP_DOWN);
			lMenuItemPodcast.setMenu(lPodcastMenu);

			mMenuItemAuto = new MenuItem(lPodcastMenu, SWT.CHECK);
			mMenuItemAuto.setText(Messages.getString("systray.auto"));
			mMenuItemAuto.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					SchedulerLogic.getInstance().setSchedulingState(
							mMenuItemAuto.getSelection(), this);
				}
			});

			MenuItem lMenuItemDownload = new MenuItem(lPodcastMenu, SWT.PUSH);
			lMenuItemDownload.setText(Messages.getString("systray.download"));
			lMenuItemDownload.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					XFeedLogic.getInstance().scan(Systray.class, true);
				}
			});

			MenuItem lMenuItemExit = new MenuItem(menu, SWT.PUSH);
			lMenuItemExit.setText(Messages.getString("systray.exit"));
			lMenuItemExit.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					UILauncher.getInstance().bye();
				}
			});

			MenuItem lMenuItemConsole = new MenuItem(menu, SWT.PUSH);
			lMenuItemConsole.setText("[I18n]Console");
			lMenuItemConsole.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					UILauncher.getInstance().showConsole();
				}
			});

			MenuItem lMenuItemAbout = new MenuItem(menu, SWT.PUSH);
			lMenuItemAbout.setText(Messages.getString("systray.about"));
			lMenuItemAbout.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					new HelpAbout();
				}
			});

			mItem.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) {
					menu.setVisible(true);
				}
			});

			setIdle();
			// mItem.setImage(pMainUI.getImages().get(
			// MainUI.IMAGE_JPODDER));
			NetTask.getInstance().addNetActionListener(this);
		}
	}

	/**
	 * Return if the system tray is enabled.
	 * 
	 * @return boolean If the system tray is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Initialize the system tray.
	 * 
	 * @throws Exception
	 */
	public void initialize() {

	}

	public void dispose() {
		mTray.dispose();
	}

	/**
	 * Change the system tray icon to downloading.
	 */
	public void setDownloading() {
		if (isEnabled()) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (mItem.isDisposed()) {
						return;
					}
					mItem.setImage(UITheme.getInstance().getImages().get(
							UITheme.IMAGE_TRAY_DOWNLOADING));

				}
			});
		}
	}

	/**
	 * Change the system tray icon to idle.
	 */
	public void setIdle() {
		if (isEnabled()) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					mItem.setImage(UITheme.getInstance().getImages().get(
							UITheme.IMAGE_TRAY_IDLE));
				}
			});
		}
	}

	/**
	 * Change the system tray icon to disconnected.
	 */
	public void setDisconnected() {
		if (isEnabled()) {
			// CB TODO, Create the image
			// item.setImage(UILauncher.lMainUI.getImages().get(
			// MainUI.IMAGE_TRAY_DISCONNECT));
		}
	}

	/**
	 * Change the system tray icon to completed.
	 */
	public void setCompleted() {
		if (isEnabled()) {

		}
	}

	public void configurationChanged(ConfigurationEvent event) {
		if (event.getSource() != this) {
			Configuration lConfiguration = Configuration.getInstance();
			boolean lAuto = lConfiguration.getAuto();
			if (mMenuItemAuto.isDisposed()) {
				return;
			}
			mMenuItemAuto.setSelection(lAuto);
		}
	}

	public void netActionPerformed(NetTaskEvent event) {

		// We need to know if the systemtray icon should be on or off.
		// as we are only notified of individual events, it's likely better
		// to get access to the download controller.
		Download lDownload = (Download) event.getSource();
		short lEvent = event.getNetEvent();

		if (lEvent == NetTaskEvent.DOWNLOAD_STATUS_CHANGED) {
			// If the status goes to completed we should set icon.
			// int lCount = DownloadLogic.getInstance()
			// .getNumberOfActiveDownloads();
			int lState = lDownload.getState();

			if (lState == DownloadLogic.COMPLETED) {
				setCompleted();
				mLog.debug("System tray, set completed");
			}
			if (lState == DownloadLogic.DOWNLOADING) {
				setDownloading();
				mLog.debug("System tray, set downloading");
			}
			if (lState == DownloadLogic.PAUZED) {
				setIdle();
				mLog.debug("System tray, set idle");
			}
			if (lState == DownloadLogic.CANCELLED) {
				setIdle();
				mLog.debug("System tray, set idle");
			}
			return;
		}

		if (lEvent == NetTaskEvent.DOWNLOAD_SUCCESS) {
			// Check if this is the last download in the quue, turn of icon.
			int lCount = DownloadLogic.getInstance()
					.getNumberOfActiveDownloads();
			if (lCount == 0) {
				setIdle();
				mLog.info("System tray, set idle");
			}
			return;
		}

		if (lEvent == NetTaskEvent.DOWNLOAD_FAILED) {
			int lCount = DownloadLogic.getInstance()
					.getNumberOfActiveDownloads();
			if (lCount == 0) {
				setIdle();
				mLog.info("System tray, set idle");
			}
			return;
		}
	}
}