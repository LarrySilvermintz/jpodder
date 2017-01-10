package com.jpodder.ui.swt.tabs;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.jpodder.ui.swt.IView;
import com.jpodder.ui.swt.UIDnD;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.conf.ConfigurationView;
import com.jpodder.ui.swt.directory.DirectoryView;
import com.jpodder.ui.swt.download.DownloadsView;
import com.jpodder.ui.swt.feeds.FeedsView;
import com.jpodder.ui.swt.log.LogView;
import com.jpodder.ui.swt.theme.IUIComponentBinder;
import com.jpodder.ui.swt.theme.IUIThemeView;
import com.jpodder.ui.swt.theme.UIComponentBinder;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */

public class TabView implements IUIThemeView {

	public String TAB_FEEDS_TITLE;

	public String TAB_DIRECTORY_TITLE;

	public String TAB_DOWNLOAD_TITLE;

	public String TAB_TORRENT_TITLE;

	public String TAB_LOG_TITLE;

	public String TAB_PRODUCTION_TITLE;

	public String TAB_HELP_TITLE;

	public String TAB_SETTINGS_TITLE;

	protected MenuItem viewMenu;

	protected MenuItem feedsMenu;

	protected MenuItem directoryMenu;

	protected MenuItem logMenu;

	protected MenuItem downloadMenu;

	protected MenuItem torrentMenu;

	// protected MenuItem productionMenu;
	protected MenuItem settingsMenu;

	protected CTabFolder mFolder = null;

	Logger mLog = Logger.getLogger(getClass().getName());

	public TabView() {
		
		if( mFolder != null){
			mFolder.dispose();
		}
		
		mFolder = new CTabFolder(UILauncher.getInstance().getShell(),
				SWT.BORDER);
		mFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// TODO Migrate to theme support.
		mFolder.setSelectionBackground(new Color[] {
		UITheme.getInstance().APP_BACKGROUND_COLOR, 
		UITheme.getInstance().APP_BACKGROUND2_COLOR
				
		// CB TODO, Move these Theme settings to UITheme.		
//		Display.getDefault().getSystemColor(SWT.COLOR_WHITE),
//				Display.getDefault().getSystemColor(SWT.COLOR_BLUE),
//				Display.getDefault().getSystemColor(SWT.COLOR_WHITE), 
				
		},new int[] { 50});
		
		mFolder.setForeground(UITheme.getInstance().APP_FONT2_COLOR);
		mFolder.setSelectionForeground(UITheme.getInstance().APP_FONT2_COLOR);
		
		
		// mFolder.setSimple(false);
		mFolder.setMinimizeVisible(true);
		mFolder.setMaximizeVisible(true);

		mFolder.addCTabFolder2Listener(new CTabFolder2Listener() {
			public void close(CTabFolderEvent arg0) {
				deselect(arg0);
			}

			public void minimize(CTabFolderEvent event) {
				mFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
						false));
				mFolder.setMinimized(true);
				UILauncher.getInstance().getShell().layout(true);
			}

			public void maximize(CTabFolderEvent event) {
				mFolder.setMaximized(true);
				mFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						true));
				UILauncher.getInstance().getShell().layout(true);
			}

			public void restore(CTabFolderEvent event) {
				mFolder.setMinimized(false);
				mFolder.setMaximized(false);
				mFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						true));
				UILauncher.getInstance().getShell().layout(true);
			}

			public void showList(CTabFolderEvent arg0) {

			}
		});

		// folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		TAB_FEEDS_TITLE = Messages.getString("gui.tab.feeds");
		TAB_DIRECTORY_TITLE = Messages.getString("gui.tab.directory");
		TAB_DOWNLOAD_TITLE = Messages.getString("gui.tab.download");
		TAB_TORRENT_TITLE = Messages.getString("gui.tab.torrent");
		TAB_LOG_TITLE = Messages.getString("gui.tab.logging");
		TAB_PRODUCTION_TITLE = Messages.getString("gui.tab.produce");
		// TAB_HELP_TITLE = Messages.getString("gui.tab.help");
		TAB_SETTINGS_TITLE = Messages.getString("gui.tab.settings");

		viewMenu = new MenuItem(UILauncher.lWindowUI.lMainMenu, SWT.CASCADE);
		viewMenu.setText(Messages.getString("gui.menu.view"));
		viewMenu.setAccelerator(SWT.CTRL + 'v');
		viewMenu.setMenu(new Menu(UILauncher.getInstance().getShell(),
				SWT.DROP_DOWN));

		feedsMenu = new MenuItem(viewMenu.getMenu(), SWT.CHECK);
		feedsMenu.setText(TAB_FEEDS_TITLE);
		directoryMenu = new MenuItem(viewMenu.getMenu(), SWT.CHECK);
		directoryMenu.setText(TAB_DIRECTORY_TITLE);
		logMenu = new MenuItem(viewMenu.getMenu(), SWT.CHECK);
		logMenu.setText(TAB_LOG_TITLE);
		downloadMenu = new MenuItem(viewMenu.getMenu(), SWT.CHECK);
		downloadMenu.setText(TAB_DOWNLOAD_TITLE);
		// torrentMenu = new MenuItem(viewMenu.getMenu(), SWT.CHECK);
		// torrentMenu.setText(TAB_TORRENT_TITLE);
		// productionMenu = new MenuItem(viewMenu.getMenu(), SWT.CHECK);
		// productionMenu.setText(TAB_PRODUCTION_TITLE);
		settingsMenu = new MenuItem(viewMenu.getMenu(), SWT.CHECK);
		settingsMenu.setText(TAB_SETTINGS_TITLE);
		registerUIThemeBinders();

		//////////////////////////////////////////////////////
		// DND SUPPORT, EXPERIMEMTAL
		/////////////////////////////////////////////////////
		
		// Allow data to be copied or moved to the drop target
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		UIDnD.addTarget(mFolder, operations);
		
	}

	public CTabFolder getFolder() {
		return mFolder;
	}

	public IView getViewInstance(String pKey) {

		if (pKey == TAB_FEEDS_TITLE) {
			FeedsView lFeedsView = new FeedsView();
			return lFeedsView;

		}
		if (pKey == TAB_DOWNLOAD_TITLE) {
			return new DownloadsView();
		}

		if (pKey == TAB_DIRECTORY_TITLE) {
			return new DirectoryView();
		}

		if (pKey == TAB_LOG_TITLE) {
			return new LogView();
		}

		if (pKey == TAB_SETTINGS_TITLE) {
			return new ConfigurationView();
		}

		return null;
	}

	public CTabItem addTab(String pTitle, Control lControl, int pPosition) {
		CTabItem lItem = new CTabItem(mFolder, SWT.CLOSE, pPosition <= mFolder
				.getItemCount() ? pPosition : mFolder.getItemCount());
		lItem.setText(pTitle);
		if (lControl != null) {
			lControl.setParent(mFolder);
			lItem.setControl(lControl);
		}
		return lItem;
	}

	public CTabItem getTab(String pTitle) {
		CTabItem[] lItems = mFolder.getItems();
		for (int i = 0; i < lItems.length; i++) {
			CTabItem item = lItems[i];
			if (pTitle.equals(item.getText())) {
				return item;
			}
		}
		return null;
	}

	private MenuItem getMenuItem(String pText) {
		MenuItem[] lItems = viewMenu.getMenu().getItems();
		for (int i = 0; i < lItems.length; i++) {
			MenuItem item = lItems[i];
			if (item.getText().equals(pText)) {
				return item;
			}
		}
		return null;
	}

	public void deselect(CTabFolderEvent event) {
		CTabItem lItem = (CTabItem) event.item;
		String lTitle = lItem.getText();
		MenuItem lMenuItem = getMenuItem(lTitle);
		if (lMenuItem != null) {
			lMenuItem.setSelection(false);
		}
	}

	public IUIComponentBinder[] getBinders() {
		return new UIComponentBinder[] { new UIComponentBinder(mFolder,
				STYLE_GENERIC) };
	}

	public void registerUIThemeBinders() {
		UITheme.getInstance().addUIThemeView(this);
	}

}
