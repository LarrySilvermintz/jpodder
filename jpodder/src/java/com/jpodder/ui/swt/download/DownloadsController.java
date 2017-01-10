package com.jpodder.ui.swt.download;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import com.jpodder.clock.Clock;
import com.jpodder.data.download.Download;
import com.jpodder.data.download.DownloadEvent;
import com.jpodder.data.download.DownloadLogic;
import com.jpodder.data.download.IDownloadListener;
import com.jpodder.net.INetTaskListener;
import com.jpodder.net.NetTask;
import com.jpodder.net.NetTaskEvent;
import com.jpodder.ui.swt.IController;
import com.jpodder.ui.swt.IView;
import com.jpodder.ui.swt.comp.BaseAction;
import com.jpodder.ui.swt.comp.IBaseActionCondition;
import com.jpodder.ui.swt.feeds.FeedController;
import com.jpodder.ui.swt.tabs.ITabListener;
import com.jpodder.ui.swt.tabs.TabController;
import com.jpodder.ui.swt.tabs.TabEvent;
import com.jpodder.util.Messages;

import de.kupzog.ktable.KTableCellSelectionListener;
import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.KTableSortComparator;
import de.kupzog.ktable.KTableSortOnClick;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class DownloadsController implements IController,
		KTableCellSelectionListener, ActionListener, INetTaskListener,
		ITabListener, IDownloadListener {

	public CleanAllCompletedAction mCleanAllCompletedAction;

	public AbortSelectedAction mAbortSelectedAction;

	public RetrySelectedAction mRetrySelectedAction;

	public GotoFeedAction mGotoFeedAction;

	public PauzeResumeAllAction mPauzeResumeAllAction;

	public BaseAction mPauzeSelectedAction;

	private DownloadsView mView;

	private Logger mLog = Logger.getLogger(getClass().getName());

	private FeedController mFeedController;

	protected DownloadsController mSelf;

	private Download lSelectedDownloadItems[];

	/**
	 * Constructor
	 */
	public DownloadsController() {
		initialize();
	}

	public void initialize() {
		mLog.info("<init>");
		mSelf = this;
	}

	public void setController(FeedController pFeedController) {
		mFeedController = pFeedController;
	}

	public void setView(IView pView) {
		mView = (DownloadsView) pView;
		mView.getView().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				// we remove ourselves from Logic notifiers.
				DownloadLogic.getInstance().removeListener(mSelf);
				NetTask.getInstance().removeNetActionListener(mSelf);
				Clock.getInstance().removeActionListener(mSelf);
			}
		});
		initializeUI();
		DownloadLogic.getInstance().addListener(mSelf);
		NetTask.getInstance().addNetActionListener(mSelf);
		Clock.getInstance().addActionListener(mSelf);
	}

	public void initializeUI() {

		mCleanAllCompletedAction = new CleanAllCompletedAction();
		mCleanAllCompletedAction.setControls(new Widget[]{
				mView.mCleanAllCompletedMenu, 
				mView.mCleanAllCompletedButton
		});
		
		mView.mCleanAllCompletedMenu.setText(mCleanAllCompletedAction
				.getName());
		mView.mCleanAllCompletedMenu.setEnabled(false);
		
		mView.mCleanAllCompletedButton.setText(mCleanAllCompletedAction
				.getName());
		mView.mCleanAllCompletedButton.setEnabled(false);
		
		mAbortSelectedAction = new AbortSelectedAction();
		mAbortSelectedAction.setControls(new Widget[]{
				mView.mAbortSelectedMenu,
				mView.mAbortSelectedButton
		});
		
		mView.mAbortSelectedMenu
				.setText(mAbortSelectedAction.getName());
		mView.mAbortSelectedMenu.setEnabled(false);
		
		mView.mAbortSelectedButton.setText(mAbortSelectedAction
				.getName());
		mView.mAbortSelectedButton.setEnabled(false);
		
		
		mRetrySelectedAction = new RetrySelectedAction();
		mRetrySelectedAction.setControls(new Widget[]{
				mView.mRetryPopupMenu
		});
		mView.mRetryPopupMenu.setText(mRetrySelectedAction.getName());
		mView.mRetryPopupMenu.setEnabled(false);
				
		mPauzeResumeAllAction = new PauzeResumeAllAction();
		mPauzeResumeAllAction.setControls(new Widget[]{
				mView.mPauzeResumeAllMenu,
				mView.mPauzeResumeAllButton
		});
		mView.mPauzeResumeAllMenu.setText(mPauzeResumeAllAction
				.getName());
		mView.mPauzeResumeAllMenu.setEnabled(false);

		mView.mPauzeResumeAllButton.setText(mPauzeResumeAllAction
				.getName());
		mView.mPauzeResumeAllButton.setEnabled(false);

		mGotoFeedAction = new GotoFeedAction();
		mGotoFeedAction.setControls(new Widget[]{
				mView.mGotoFeedPopupMenu
		});
		mView.mGotoFeedPopupMenu.setText(mGotoFeedAction.getName());
		mView.mDownloadTable.addCellSelectionListener(this);

		setDownloadView();
	}

	public void setDownloadView() {
		KTableModel lModel = mView.mDownloadTable.getModel();
		if (lModel instanceof KDownloadTableModel) {
			((KDownloadTableModel) lModel).setSource(DownloadLogic
					.getInstance().getDownloadArray());
		} else {
			KDownloadTableModel lDownloadModel = new KDownloadTableModel(
					DownloadLogic.getInstance().getDownloadArray());
			mView.mDownloadTable.setModel(lDownloadModel);
			mView.mDownloadTable
					.addCellSelectionListener(new KTableSortOnClick(
							mView.mDownloadTable, new KDownloadComparator(
									lDownloadModel, -1,
									KTableSortComparator.SORT_NONE)));
		}

		// mView.mDownloadTable.setSelection()

		updateAllRows();
	}

	public void updateView(Download lDownload) {
		updateDownloadRow(lDownload);
	}

	public void updateDownloadRow(final Download pDownload) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				// mView.mDownloadTable.updateCell(index, 0);
				mView.mDownloadTable.redraw();
			}
		});
	}

	public void updateAllRows() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				mView.mDownloadTable.redraw();
			}
		});
	}

	public Download getSelectedDownload() {
		int row = mView.getSelectedRow();
		Download lDownload = DownloadLogic.getInstance().getDownload(row);
		return lDownload;
	}

	private class GotoFeedAction extends BaseAction implements
			IBaseActionCondition {
		public GotoFeedAction() {
			init(Messages.getString("downloadscontrol.gotofeed"), false);
		}

		public void handleEvent(Event e) {
			int row = mView.getSelectedRow();
			Download lDownload = DownloadLogic.getInstance().getDownload(row);
			TabController.getInstance().setVisible(
					TabController.getInstance().getView().TAB_FEEDS_TITLE);
			if (mFeedController != null) {
				mFeedController.setSelectedFeed(lDownload.getEnclosure()
						.getFeed());
			}
			if (mFeedController.getFileController() != null) {
				mFeedController.getFileController().setSelectedFile(
						lDownload.getEnclosure());
			}
		}

		public boolean conditionMet(Object pConditionObject) {
			if (pConditionObject == null
					|| pConditionObject instanceof Download) {
				return true;
			}
			return false;
		}

		public void setEnabled(boolean pEnabled, Object pConditionObject) {
			if (conditionMet(pConditionObject)) {
				mView.mGotoFeedPopupMenu.setEnabled(pEnabled);
			}
		}

		public void setControls(Widget[] pWidgets) {
			if (pWidgets != null) {
				for (int i = 0; i < pWidgets.length; i++) {
					Widget control = pWidgets[i];
					control.addListener(SWT.Selection, this);
					
//					control.setEnabled(enabled());
				}
			}
		}
	}

	private class CleanAllCompletedAction extends BaseAction {
		public CleanAllCompletedAction() {
			init(Messages.getString("downloadscontrol.deletecompleted"), false);
		}

		public void handleEvent(Event e) {
			DownloadLogic.getInstance().cleanAllCompleted();
			mView.mCleanAllCompletedMenu.setEnabled(false);
			mView.mCleanAllCompletedButton.setEnabled(false);
			setDownloadView();
		}

		public void update() {
			Iterator lIt = DownloadLogic.getInstance().getDownloadIterator();
			while (lIt.hasNext()) {
				Download lDownload = (Download) lIt.next();
				update(lDownload);
			}
		}

		public void update(Download pDownload) {
			int lState = pDownload.getState();
			// Do some state updating on controls.
			switch (lState) {

			case DownloadLogic.ERROR:
			case DownloadLogic.CANCELLED:
			case DownloadLogic.COMPLETED: {
				mView.mCleanAllCompletedMenu.setEnabled(true);
				mView.mCleanAllCompletedButton.setEnabled(true);
			}
				break;
			}
		}

		public void setControls(Widget[] pWidgets) {
			if (pWidgets != null) {
				for (int i = 0; i < pWidgets.length; i++) {
					Widget control = pWidgets[i];
					control.addListener(SWT.Selection, this);
				}
			}
		}
	}

	private class RetrySelectedAction extends BaseAction implements
			IBaseActionCondition {
		public RetrySelectedAction() {
			init(Messages.getString("downloadscontrol.retry"), false);
		}

		public void handleEvent(Event event) {
			for (int i = 0; i < lSelectedDownloadItems.length; i++) {
				Download lDownload = lSelectedDownloadItems[i];

				if (conditionMet(lDownload)) {
					DownloadLogic.getInstance().retry(lDownload);
				}
			}
		}

		public boolean conditionMet(Object pConditionObject) {
			if (pConditionObject instanceof Download) {
				Download lDownload = (Download) pConditionObject;
				int lState = lDownload.getState();
				switch (lState) {
				case DownloadLogic.CANCELLED:
				case DownloadLogic.ERROR:
					return true;
				}
			}
			return false;
		}

		public void setEnabled(boolean pEnabled, Object pConditionObject) {
			if (pConditionObject == null || conditionMet(pConditionObject)) {
				mView.mRetryPopupMenu.setEnabled(pEnabled);
			}
		}

		public void setControls(Widget[] pWidgets) {
			if (pWidgets != null) {
				for (int i = 0; i < pWidgets.length; i++) {
					Widget control = pWidgets[i];
					control.addListener(SWT.Selection, this);
				}
			}
		}
	}

	/**
	 * Intelligent action, which considers the status of a model object, when
	 * asked to enable or disable the action. It also works on multiple objects.
	 */
	private class AbortSelectedAction extends BaseAction implements
			IBaseActionCondition {
		public AbortSelectedAction() {
			init(Messages.getString("downloadscontrol.abort"), false);
		}

		public void handleEvent(Event e) {
			for (int i = 0; i < lSelectedDownloadItems.length; i++) {
				Download lDownload = lSelectedDownloadItems[i];
				DownloadLogic.getInstance().abort(lDownload);
			}
		}

		public boolean conditionMet(Object pConditionObject) {
			if (pConditionObject instanceof Download) {
				Download lDownload = (Download) pConditionObject;
				if (lDownload.getState() == DownloadLogic.DOWNLOADING
						|| lDownload.getState() == DownloadLogic.QUEUED
						|| lDownload.getState() == DownloadLogic.CONNECTING
						|| lDownload.getState() == DownloadLogic.PAUZED) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		}

		public void setEnabled(boolean pEnabled, Object pConditionObject) {
			if (conditionMet(pConditionObject)) {

				Download lDownload = (Download) pConditionObject;

				File lFile = lDownload.getEnclosure().getFile();

				String lName;
				if (lFile != null) {
					lName = lFile.getName();
				} else {
					lName = lDownload.getEnclosure().getPersonalURL()
							.toExternalForm();
				}
				enable(true, Messages.getString("downloadscontrol.abort") + " "
						+ lName);
			} else {
				enable(false, Messages.getString("downloadscontrol.abort"));
			}
		}

		public void setEnabled(boolean pEnabled) {
			setEnabled(pEnabled, null);
		}

		public void setEnabled(boolean pEnabled, Object[] pConditionObjects) {
			if (conditionMet(pConditionObjects)) {
				enable(true, Messages
						.getString("downloadscontrol.abort.multiple"));
			} else {
				enable(false, Messages.getString("downloadscontrol.abort"));
			}
		}

		public void update(Download pDownload) {
			int lState = pDownload.getState();
			switch (lState) {
			case DownloadLogic.COMPLETED: {
				mView.mAbortSelectedMenu.setText(Messages
						.getString("downloadscontrol.abort"));
			}
				break;
			}
		}

		private void enable(boolean pEnable, String pText) {
			mView.mAbortSelectedMenu.setEnabled(pEnable);
			mView.mAbortSelectedMenu.setText(pText);
			mView.mAbortSelectedButton.setEnabled(pEnable);
			mView.mAbortSelectedButton.setText(pText);
			mView.lButtonGroup.layout();
		}

		public boolean conditionMet(Object[] pConditionObjects) {
			// true if at least one condition.
			if (pConditionObjects == null) {
				return false;
			}
			boolean lAtLeastOneTrue = false;
			for (int i = 0; i < pConditionObjects.length; i++) {
				if (!lAtLeastOneTrue) {
					lAtLeastOneTrue = conditionMet(pConditionObjects[i]);
					break;
				}
			}
			return lAtLeastOneTrue;
		}

		public void setControls(Widget[] pWidgets) {
			if (pWidgets != null) {
				for (int i = 0; i < pWidgets.length; i++) {
					Widget control = pWidgets[i];
					control.addListener(SWT.Selection, this);
				}
			}
		}
	}

	private class PauzeResumeAllAction extends BaseAction {

		boolean mPauzed = false;

		public PauzeResumeAllAction() {
			init(Messages.getString("downloadscontrol.pauze"), false);
		}

		public void handleEvent(Event e) {
			if (!mPauzed) {
				mPauzed = true;
				DownloadLogic.getInstance().pauzeAll();
				mView.mPauzeResumeAllButton.setText(Messages
						.getString("downloadscontrol.resume"));
				mView.mPauzeResumeAllMenu.setText(Messages
						.getString("downloadscontrol.resume"));
				mView.lButtonGroup.layout();
			} else {
				mPauzed = false;
				DownloadLogic.getInstance().resumeAll();
				mView.mPauzeResumeAllButton.setText(Messages
						.getString("downloadscontrol.pauze"));
				mView.mPauzeResumeAllMenu.setText(Messages
						.getString("downloadscontrol.pauze"));
				mView.lButtonGroup.layout();
			}
		}

		public void reset() {
			mPauzed = false;
			mView.mPauzeResumeAllButton.setText(Messages
					.getString("downloadscontrol.pauze"));
			mView.mPauzeResumeAllMenu.setText(Messages
					.getString("downloadscontrol.pauze"));

		}

		public void update() {
			int lPauzed = DownloadLogic.getInstance()
					.getNumberOfPauzedDownloads();
			int lActive = DownloadLogic.getInstance()
					.getNumberOfActiveDownloads();
			if (lPauzed == 0) {
				reset();
				if (lActive > 0) {
					enable(true);
				} else {
					enable(false);
				}
			}
		}

		public void enable(boolean pEnable) {
			mEnabled = pEnable;
			mView.mPauzeResumeAllMenu.setEnabled(pEnable);
			mView.mPauzeResumeAllButton.setEnabled(pEnable);
		}

		public void setControls(Widget[] pWidgets) {
			if (pWidgets != null) {
				for (int i = 0; i < pWidgets.length; i++) {
					Widget control = pWidgets[i];
					control.addListener(SWT.Selection, this);
				}
			}
		}
	}

	public void cellSelected(int col, int row, int statemask) {

		int lSelection[] = mView.getSelectedRows();

		ArrayList<Download> lDownloads = new ArrayList<Download>();
		for (int i = 0; i < lSelection.length; i++) {
			int j = lSelection[i];
			if (DownloadLogic.getInstance().isValidIndex(j)) {
				lDownloads.add(DownloadLogic.getInstance().getDownload(j));
			}
		}

		lSelectedDownloadItems = new Download[lDownloads.size()];
		lSelectedDownloadItems = lDownloads.toArray(lSelectedDownloadItems);
		mLog.info("Selection of (" + lSelectedDownloadItems.length
				+ ") download items");

		if (lSelectedDownloadItems.length == 0) {
			mRetrySelectedAction.setEnabled(false, null);
			mGotoFeedAction.setEnabled(false, null);
			mAbortSelectedAction.setEnabled(false, null);
		} else {

			mRetrySelectedAction.setEnabled(true, lSelectedDownloadItems[0]);
			mGotoFeedAction.setEnabled(true, lSelectedDownloadItems[0]);

			// For multiple selection, we call a different action enabler.
			if (lSelectedDownloadItems.length == 1) {
				mAbortSelectedAction
						.setEnabled(true, lSelectedDownloadItems[0]);
			} else {
				mAbortSelectedAction.setEnabled(true, lSelectedDownloadItems);
			}
		}
	}

	public void fixedCellSelected(int col, int row, int statemask) {

	}

	public void actionPerformed(ActionEvent pEvent) {
		heartBeat();
	}

	/**
	 * Repetively update download tasks in the downloadlist. It performs various
	 * tasks depending on the status of the task.
	 */
	public void heartBeat() {
		Iterator it = DownloadLogic.getInstance().getDownloadIterator();
		while (it.hasNext()) {
			Download lDownload = (Download) it.next();
			switch (lDownload.getState()) {
			case DownloadLogic.CONNECTING:
			case DownloadLogic.RELEASING:
			case DownloadLogic.RETRYING:
			case DownloadLogic.DOWNLOADING: {
				updateView(lDownload);
			}
			}
		}
	}

	/**
	 * Listen for network events.
	 * 
	 * @see com.jpodder.net.INetTaskListener#netActionPerformed(com.jpodder.net.NetTaskEvent)
	 */
	public void netActionPerformed(final NetTaskEvent event) {

		final Download lDownload = (Download) event.getSource();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				mPauzeResumeAllAction.update();
				mCleanAllCompletedAction.update(lDownload);
			}
		});
		updateView(lDownload);
	}

	/**
	 * We are informed of a tab change, we might need when the UI refresher is
	 * not updating because all downloads have completed.
	 */
	public void visibleTabChanged(TabEvent pEvent) {
		if (pEvent.getTabTitle().equals(
				TabController.getInstance().getView().TAB_DOWNLOAD_TITLE)) {
			Iterator it = DownloadLogic.getInstance().getDownloadIterator();
			while (it.hasNext()) {
				updateDownloadRow((Download) it.next());
			}
		}
	}

	public void modelChanged(DownloadEvent pEvent) {
		// Underlying model changed, reload the view.
		if (mView != null && !mView.getView().isDisposed()) {
			setDownloadView();
		}
	}
}