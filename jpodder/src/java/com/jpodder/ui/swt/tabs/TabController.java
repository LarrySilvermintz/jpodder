package com.jpodder.ui.swt.tabs;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.ui.swt.IController;
import com.jpodder.ui.swt.IView;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class TabController {

	Logger mLog = Logger.getLogger(TabController.class.getName());

	protected TabView mView;

	protected IView mCurrentView;

	protected String visibleFeed;

	protected ArrayList<ITabListener> listeners = new ArrayList<ITabListener>();

	private HashMap mControllerMap;

	private HashMap mViewMap = new HashMap();

	private static TabController sSelf = null;

	ViewSelectionListener mFeedListener;

	ViewSelectionListener mDirectoryListener;

	ViewSelectionListener mDownloadListener;

	ViewSelectionListener mLogListener;

	ViewSelectionListener mSettingsListener;

	public static TabController getInstance() {
		if (sSelf == null) {
			sSelf = new TabController();
		}
		return sSelf;
	}

	/**
	 * @param listener
	 */
	public void addTabListener(ITabListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}

	/**
	 * @param listener
	 */
	public synchronized void removeTabListener(ITabListener listener) {
		synchronized (listeners) {
			if (listeners.contains(listener)) {
				listeners.remove(listener);
			}
		}
	}

	// CB TODO Not used.
	protected void fireVisibleTabChanged(TabEvent event) {
		ITabListener[] lListeners = (ITabListener[]) listeners
				.toArray(new ITabListener[] {});
		mLog.debug("Changed visible feed to : " + event.getTabTitle());
		for (int i = 0; i < lListeners.length; i++) {
			ITabListener lListener = lListeners[i];
			if (listeners.contains(lListener)) {
				mLog.debug("Notifying: " + lListener.getClass().getName());
				lListener.visibleTabChanged(event);
			}
		}
	}

	public void setView(TabView pTabView) {
		mView = pTabView;

		mFeedListener = new ViewSelectionListener(mView.TAB_FEEDS_TITLE, 0);
		mDirectoryListener = new ViewSelectionListener(
				mView.TAB_DIRECTORY_TITLE, 1);
		mDownloadListener = new ViewSelectionListener(mView.TAB_DOWNLOAD_TITLE,
				2);
		mLogListener = new ViewSelectionListener(mView.TAB_LOG_TITLE, 3);
		mSettingsListener = new ViewSelectionListener(mView.TAB_SETTINGS_TITLE,
				4);
		initializeUI();
	}

	public TabView getView() {
		return mView;
	}

	public void setControllers(HashMap pControllerMap) {
		mControllerMap = pControllerMap;
	}

	public void initializeUI() {

		mView.feedsMenu.addSelectionListener(mFeedListener);
		mView.directoryMenu.addSelectionListener(mDirectoryListener);
		mView.downloadMenu.addSelectionListener(mDownloadListener);
		mView.logMenu.addSelectionListener(mLogListener);
		mView.settingsMenu.addSelectionListener(mSettingsListener);
		

		mView.addTab(mView.TAB_FEEDS_TITLE, null, 0);
		mView.feedsMenu.setSelection(true);

		Configuration lConfig = Configuration.getInstance();
		mView.directoryMenu.setSelection(lConfig.getGui().getDirectory());
		if (lConfig.getGui().getDirectory()) {
			mView.addTab(mView.TAB_DIRECTORY_TITLE, null, 1);
		}
		mView.downloadMenu.setSelection(lConfig.getGui().getDownload());
		if (lConfig.getGui().getDownload()) {
			mView.addTab(mView.TAB_DOWNLOAD_TITLE, null, 2);
		}

		mView.logMenu.setSelection(lConfig.getGui().getLog());
		if (lConfig.getGui().getLog()) {
			mView.addTab(mView.TAB_LOG_TITLE, null, 3);
		}
		mView.settingsMenu.setSelection(lConfig.getGui().getSettings());
		if (lConfig.getGui().getSettings()) {
			mView.addTab(mView.TAB_SETTINGS_TITLE, null, 4);
		}

		mView.mFolder.addSelectionListener(new TabSelectionListener());

		// CB TODO For some reason selection, doesn't trigger the selection
		// listener.
		instantiateView(mView.TAB_FEEDS_TITLE);
		setVisible(mView.TAB_FEEDS_TITLE);
	}

	public void finalizeUI() {
		mView.feedsMenu.removeSelectionListener(mFeedListener);
		mView.directoryMenu.removeSelectionListener(mDirectoryListener);
		mView.downloadMenu.removeSelectionListener(mDownloadListener);
		mView.logMenu.removeSelectionListener(mLogListener);
		mView.settingsMenu.removeSelectionListener(mSettingsListener);

	}

	private void instantiateView(String pKey) {

		boolean lInstantiateView = false;
		if (mCurrentView != null) {
			if (!mCurrentView.isStatic()) {
				mCurrentView.getView().dispose();
			}
		}
		// Do we have the view?
		if (mViewMap.containsKey(pKey)) {
			mCurrentView = (IView) mViewMap.get(pKey);
		} else {
			lInstantiateView = true;
		}

		if (lInstantiateView && mControllerMap != null) {
			mCurrentView = mView.getViewInstance(pKey);
			if (mCurrentView.isStatic()) {
				mViewMap.put(pKey, mCurrentView);
			}
			IController lController = (IController) mControllerMap.get(pKey);
			lController.setView(mCurrentView);
		}
		// Now we set the view for parent and child.
		mCurrentView.getView().setParent(mView.mFolder);

		CTabItem lItem = mView.getTab(pKey);
		lItem.setControl(mCurrentView.getView());
	}

	/**
	 * Updates the current view object. Depending if a view is static or
	 * dynamic, it will be instantiated if dynamic.
	 */
	class TabSelectionListener implements SelectionListener {
		public void widgetSelected(SelectionEvent arg0) {
			CTabItem lItem = (CTabItem) arg0.item;
			String lKey = lItem.getText();
			instantiateView(lKey);
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {
			mLog.debug("TabSelection: defaultselected");
		}
	}

	/**
	 * Shows or hides a view.
	 */
	public class ViewSelectionListener implements SelectionListener {

		String mKey; // key and title of the tab.

		int mPosition; // position of the tab.

		public ViewSelectionListener(String pKey, int pPosition) {
			mKey = pKey;
			mPosition = pPosition;
		}

		public void widgetSelected(SelectionEvent e) {
			// int lCount = mView.getFolder().getItemCount();
			CTabItem lItem = mView.getTab(mKey);
			if (lItem != null) {
				if (mViewMap.containsKey(mKey)) {
					IView lView = (IView) mViewMap.get(mKey);
					if (!lView.isStatic()) {
						lItem.getControl().dispose();
					}
				}
				lItem.dispose();
			} else {
				lItem = mView.addTab(mKey, null, mPosition);
				lItem.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {

					}
				});

				mView.getFolder().setSelection(lItem);
				Event lEvent = new Event();
				lEvent.item = lItem;
				mView.getFolder().notifyListeners(SWT.Selection, lEvent);
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {

		}
	}

	public void saveProperties() {
		Configuration lConfig = Configuration.getInstance();

		lConfig.getGui().setDirectory(mView.directoryMenu.getSelection());
		lConfig.getGui().setLog(mView.logMenu.getSelection());
		lConfig.getGui().setDownload(mView.downloadMenu.getSelection());
		lConfig.getGui().setSettings(mView.settingsMenu.getSelection());
	}

	public boolean isVisible(String pTitle) {
		CTabItem lItem = mView.getTab(pTitle);
		if (lItem != null) {
			int lRequestedIndex = mView.getFolder().indexOf(lItem);
			int lSelectionIndex = mView.getFolder().getSelectionIndex();
			if (lRequestedIndex == lSelectionIndex) {
				return true;
			}
		}
		return false;
	}

	public void setVisible(String pTitle) {
		CTabItem lItem = mView.getTab(pTitle);
		if (lItem != null) {
			mView.getFolder().setSelection(lItem);
		}
	}
}