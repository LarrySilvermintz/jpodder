package com.jpodder.ui.swt.media;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import com.jpodder.JPodderException;
import com.jpodder.data.content.ContentAssociation;
import com.jpodder.data.download.Download;
import com.jpodder.data.download.DownloadEvent;
import com.jpodder.data.download.IDownloadListener;
import com.jpodder.data.download.DownloadLogic;
import com.jpodder.data.feeds.IXFeed;
import com.jpodder.data.feeds.IXFeedListener;
import com.jpodder.data.feeds.IXFile;
import com.jpodder.data.feeds.IXItem;
import com.jpodder.data.feeds.IXPersonalEnclosure;
import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.stats.XFeedEvent;
import com.jpodder.data.id3.ID3Logic;
import com.jpodder.data.player.IPlayer;
import com.jpodder.data.player.NoPlayer;
import com.jpodder.data.player.PlayerLogic;
import com.jpodder.net.NetTaskEvent;
import com.jpodder.net.INetTaskListener;
import com.jpodder.ui.swt.IController;
import com.jpodder.ui.swt.IView;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.comp.BaseAction;
import com.jpodder.ui.swt.comp.IBaseActionCondition;
import com.jpodder.ui.swt.id3.ID3Control;
import com.jpodder.ui.swt.status.StatusController;
import com.jpodder.util.Messages;
import com.jpodder.util.Util;

/**
 * A file control class. This class defines a list selection listener.
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class MediaController implements IController, ActionListener,
		IXFeedListener, IDownloadListener, INetTaskListener {
	public BaseAction mFileDeleteSelectedAction;

	public BaseAction mFileAddMarkedToPlayerAction;

	public BaseAction mFileMarkAllEnclAction;

	public BaseAction mFileClearMarkAllAction;

	public BaseAction mFileDeleteMarkedAction;

	public BaseAction mFileAddSelectedToPlayerAction;

	public BaseAction mFileAddToPlayerAction;

	public BaseAction mFileAddAllToPlayerAction;

	public BaseAction mFileDownloadAction;

	public BaseAction mFileOpenAction;

	public BaseAction mFilePlayAction;

	public BaseAction mFileID3RewriteTagsAction;

	public BaseAction mFileID3ViewAction;

	public BaseAction mFileDeleteAction;

	public BaseAction mFileFilterItemsAction;

	private MediaView mView;

	private Logger mLog = Logger.getLogger(getClass().getName());

	protected ID3Control mID3Controller = new ID3Control();

	protected IXPersonalFeed mFeed;

	public MediaController() {
	}

	public void setView(IView pView) {
		mView = (MediaView) pView;
		initializeUI();
	}

	// CB Remove later if no external controllers are set.
	// public void setControllers(ID3Control pID3Controller) {
	// mID3Controller = pID3Controller;
	// initialize();
	// }

	public void initialize() {
	}

	public void initializeUI() {

		// --- DOWNLOAD ACTIONS.
		mFileDownloadAction = new FileDownloadAction();
		mView.mFileDownloadMenu.addListener(SWT.Selection, mFileDownloadAction);
		mView.mFileDownloadMenu.setText(mFileDownloadAction.getName());
		mView.mFileDownloadMenu.setEnabled(mFileDownloadAction.enabled());

		mView.mFileDownloadPopupMenu.addListener(SWT.Selection,
				mFileDownloadAction);
		mView.mFileDownloadPopupMenu.setText(mFileDownloadAction.getName());
		mView.mFileDownloadPopupMenu.setEnabled(mFileDownloadAction.enabled());

		// --- PLAYER ACTIONS.
		mFilePlayAction = new FilePlayTrackAction();

		mView.mFilePlayMenu.addListener(SWT.Selection, mFilePlayAction);
		mView.mFilePlayMenu.setText(mFilePlayAction.getName());
		mView.mFilePlayMenu.setEnabled(mFilePlayAction.enabled());

		mView.mFilePlayPopupMenu.addListener(SWT.Selection, mFilePlayAction);
		mView.mFilePlayPopupMenu.setText(mFilePlayAction.getName());
		mView.mFilePlayPopupMenu.setEnabled(mFilePlayAction.enabled());

		mFileAddSelectedToPlayerAction = new SyncSelectedWithPlayerAction();
		mView.mFileAddSelectedToPlayerMenu.addListener(SWT.Selection,
				mFileAddSelectedToPlayerAction);
		mView.mFileAddSelectedToPlayerMenu
				.setText(mFileAddSelectedToPlayerAction.getName());
		mView.mFileAddSelectedToPlayerMenu
				.setEnabled(mFileAddSelectedToPlayerAction.enabled());
		mView.mFileAddSelectedToPlayerPopupMenu.addListener(SWT.Selection,
				mFileAddSelectedToPlayerAction);
		mView.mFileAddSelectedToPlayerPopupMenu
				.setText(mFileAddSelectedToPlayerAction.getName());
		mView.mFileAddSelectedToPlayerPopupMenu
				.setEnabled(mFileAddSelectedToPlayerAction.enabled());

		mFileAddAllToPlayerAction = new SyncFeedAction();

		mView.mToolsSyncMenu.addListener(SWT.Selection,
				mFileAddAllToPlayerAction);
		mView.mToolsSyncMenu.setText(mFileAddAllToPlayerAction.getName());

		mFileDeleteSelectedAction = new DeleteSelectedAction();
		mView.mFileDeleteSelectedMenu.addListener(SWT.Selection,
				mFileDeleteSelectedAction);
		mView.mFileDeleteSelectedMenu.setText(mFileDeleteSelectedAction
				.getName());
		mView.mFileDeleteSelectedMenu.setEnabled(mFileDeleteSelectedAction
				.enabled());

		mView.mFileDeleteSelectedPopupMenu.addListener(SWT.Selection,
				mFileDeleteSelectedAction);
		mView.mFileDeleteSelectedPopupMenu.setText(mFileDeleteSelectedAction
				.getName());
		mView.mFileDeleteSelectedPopupMenu.setEnabled(mFileDeleteSelectedAction
				.enabled());

		mFileOpenAction = new FileOpenAction();
		mView.mFileOpenSelectedPopupMenu.addListener(SWT.Selection,
				mFileOpenAction);
		mView.mFileOpenSelectedPopupMenu.setText(mFileOpenAction.getName());
		mView.mFileOpenSelectedPopupMenu.setEnabled(mFileOpenAction.enabled());

		// ---- MARKING Actions.

		mFileMarkAllEnclAction = new MarkAllEnclosuresAction();
		mFileClearMarkAllAction = new UnmarkAllEnclosuresAction();
		mFileDeleteMarkedAction = new DeleteMarkedAction();
		mFileAddMarkedToPlayerAction = new MarkedToPlayerAction();

		mView.mFileMarkingMenu.setText(Messages
				.getString("gui.menu.enclosures.marking"));

		mView.mFileMarkAllMenu.addListener(SWT.Selection,
				mFileMarkAllEnclAction);
		mView.mFileMarkAllMenu.setText(mFileMarkAllEnclAction.getName());
		mView.mFileMarkAllMenu.setEnabled(mFileMarkAllEnclAction.enabled());

		mView.mFileClearMarkingMenu.addListener(SWT.Selection,
				mFileClearMarkAllAction);
		mView.mFileClearMarkingMenu.setText(mFileClearMarkAllAction.getName());
		mView.mFileClearMarkingMenu.setEnabled(mFileClearMarkAllAction
				.enabled());

		mView.mFileDeleteMarkedMenu.addListener(SWT.Selection,
				mFileDeleteMarkedAction);
		mView.mFileDeleteMarkedMenu.setText(mFileDeleteMarkedAction.getName());
		mView.mFileDeleteMarkedMenu.setEnabled(mFileDeleteMarkedAction
				.enabled());

		mView.mFileAddMarkedToPlayerMenu.addListener(SWT.Selection,
				mFileAddMarkedToPlayerAction);
		mView.mFileAddMarkedToPlayerMenu.setText(mFileAddMarkedToPlayerAction
				.getName());
		mView.mFileAddMarkedToPlayerMenu
				.setEnabled(mFileAddMarkedToPlayerAction.enabled());

		// mView.mFileMarkAllPopupMenu.addListener(SWT.Selection,
		// mFileMarkAllEnclAction);
		// mView.mFileClearMarkingPopupMenu.addListener(SWT.Selection,
		// mFileUnMarkAllEnclAction);
		// mView.mFileDeleteMarkedPopupMenu.addListener(SWT.Selection,
		// mFileDeleteMarkedAction);
		// mView.mFileAddMarkedToPlayerPopupMenu.addListener(SWT.Selection,
		// mFileAddMarkedToPlayerAction);

		// -- ID3 ACTIONS
		mFileID3RewriteTagsAction = new FileID3RewriteAction();
		mView.mFileID3RewriteMenu.addListener(SWT.Selection,
				mFileID3RewriteTagsAction);
		mView.mFileID3RewriteMenu.setText(mFileID3RewriteTagsAction.getName());
		mView.mFileID3RewriteMenu.setEnabled(mFileID3RewriteTagsAction
				.enabled());
		mView.mFileID3RewritePopupMenu.addListener(SWT.Selection,
				mFileID3RewriteTagsAction);
		mView.mFileID3RewritePopupMenu.setText(mFileID3RewriteTagsAction
				.getName());
		mView.mFileID3RewritePopupMenu.setEnabled(mFileID3RewriteTagsAction
				.enabled());

		mFileID3ViewAction = new FileID3ViewAction();

		mView.mFileID3ViewMenu.addListener(SWT.Selection, mFileID3ViewAction);
		mView.mFileID3ViewMenu.setText(mFileID3ViewAction.getName());
		mView.mFileID3ViewMenu.setEnabled(mFileID3ViewAction.enabled());

		mView.mFileID3ViewPopupMenu.addListener(SWT.Selection,
				mFileID3ViewAction);
		mView.mFileID3ViewPopupMenu.setText(mFileID3ViewAction.getName());
		mView.mFileID3ViewPopupMenu.setEnabled(mFileID3ViewAction.enabled());

		// mFileID3EditAction = new FileID3EditAction();
		// mView.mFileID3EditMenu.addListener(SWT.Selection,
		// mFileID3EditAction);
		// mView.mFileID3EditMenu.setText(mFileID3EditAction.getName());
		// mView.mFileID3EditMenu.setEnabled(mFileID3EditAction.enabled());
		//
		// mView.mFileID3EditPopupMenu.addListener(SWT.Selection,
		// mFileID3EditAction);
		// mView.mFileID3EditPopupMenu.setText(mFileID3EditAction.getName());
		// mView.mFileID3EditPopupMenu.setEnabled(mFileID3EditAction.enabled());

		mFileFilterItemsAction = new FileViewSelector();

		mView.mFileFilterMenu.setText(Messages.getString("fileControl.view"));

		mView.mFileViewItemsMenu.addListener(SWT.Selection,
				mFileFilterItemsAction);
		mView.mFileViewItemsMenu.setText(mFileFilterItemsAction.getName());

		mView.setViewer(MediaView.MEDIA_VIEW);
		mView.mMediaViewer.mTableViewer
				.addSelectionChangedListener(new FileSelection());
		setMarkingActionStatus(false);
	}

	public MediaView getView() {
		return mView;
	}

	public void setFileView(IXPersonalFeed pFeed) {
		mFeed = pFeed;
		// Check this feed as the RSS model might not be available.
		// TODO, move assertion
		if (pFeed != null
				&& (pFeed.isParsed() || pFeed.getFiles(true).size() > 0)) {
			mView.setInput(pFeed);
			setMarkingActionStatus(true);
		} else {
			mView.clean();
			setActionStatus(false, null);
			setMarkingActionStatus(false);
		}
	}

	private class FileSelection implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			// if the selection is empty clear the label
			if (event.getSelection().isEmpty()) {
				return;
			}
			if (event.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				mView.mSelection = selection.getFirstElement();
				mView.mSelectionArray = selection.toArray();

				if (mView.mSelection instanceof IXFile) {
					IXFile lFile = (IXFile) mView.mSelection;
					setActionStatus(true, lFile);
				}
				if (mView.mSelection instanceof IXItem) {
					setActionStatus(false, null);
				}
			}
		}
	};

	public void setActionStatus(boolean pStatus, IXFile pFile) {
		((IBaseActionCondition) mFileID3RewriteTagsAction).setEnabled(pStatus,
				pFile);
		((IBaseActionCondition) mFileDownloadAction).setEnabled(pStatus, pFile);
		((IBaseActionCondition) mFilePlayAction).setEnabled(pStatus, pFile);
		((IBaseActionCondition) mFileOpenAction).setEnabled(pStatus, pFile);
		((IBaseActionCondition) mFileDeleteSelectedAction).setEnabled(pStatus,
				pFile);
		((IBaseActionCondition) mFileAddSelectedToPlayerAction).setEnabled(
				pStatus, pFile);
		((IBaseActionCondition) mFileID3ViewAction).setEnabled(pStatus, pFile);

		// mView.mFileID3EditMenu.setEnabled(pStatus);
		// mView.mFileID3EditPopupMenu.setEnabled(pStatus);
	}

	/**
	 * The marking actions are not depending on the selection of a file. These
	 * are activated when a feed is selected and de-activated when a feed is not
	 * selected.
	 * 
	 * @param pStatus
	 */
	public void setMarkingActionStatus(boolean pStatus) {
		mView.mFileAddMarkedToPlayerMenu.setEnabled(pStatus);
		mView.mFileDeleteMarkedMenu.setEnabled(pStatus);
		mView.mFileClearMarkingMenu.setEnabled(pStatus);
		mView.mFileMarkAllMenu.setEnabled(pStatus);
		mView.mFileMarkingMenu.setEnabled(pStatus);
	}

	private class SyncFeedAction extends BaseAction {

		public SyncFeedAction() {
			init(Messages.getString("feedcontrol.addall"));
		}

		public void handleEvent(Event event) {

			if (mFeed != null) {
				Object[] lFiles = mFeed.getMergedArray(false);
				mLog.info("Synchronizing: " + lFiles.length
						+ " files/enclosures");
				for (int index = 0; index < lFiles.length; index++) {
					IXFile lFile = (IXFile) lFiles[index];
					if (PlayerLogic.getInstance().addTrack(mFeed.getTitle(),
							lFile)) {
						lFile.setInPlayer(true);
						mView.update(lFile);
					} else {
						mLog.warn("Could not synchronize with player: "
								+ lFile.getName());
					}
				}
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	public class FileViewSelector extends BaseAction {

		public FileViewSelector() {
			init(Messages.getString("fileControl.items"), true);
		}

		public void handleEvent(Event event) {
			if (mView.mFileViewItemsMenu.getSelection()) {
				if (mView.mCurrentView != MediaView.ITEM_VIEW) {
					mView.setViewer(MediaView.ITEM_VIEW);
					mView.mItemViewer.mTreeViewer
							.addSelectionChangedListener(new FileSelection());

				}
			} else {
				if (mView.mCurrentView != MediaView.MEDIA_VIEW) {
					mView.setViewer(MediaView.MEDIA_VIEW);
					mView.mMediaViewer.mTableViewer
							.addSelectionChangedListener(new FileSelection());
				}
			}
			if (mFeed != null) {
				mView.setInput(mFeed);
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class FileOpenAction extends BaseAction implements
			IBaseActionCondition {

		public FileOpenAction() {
			init(Messages.getString("general.open"), false);
		}

		public void handleEvent(Event e) {
			IXFile lFile = getSelectedFile();
			if (lFile != null) {
				String lPath = lFile.getFile().getPath();
				String lExtension = Util.stripName(lFile.getFile().getName());
				ContentAssociation.openProgram(lExtension, lPath);
			}
		}

		public boolean conditionMet(Object pConditionObject) {
			boolean lEnable = false;
			if (pConditionObject instanceof IXFile) {
				IXFile lFile = (IXFile) pConditionObject;
				if (lFile.isLocal()) {
					lEnable = true;
				}
			}
			return lEnable;
		}

		public void setEnabled(boolean pEnabled, Object pConditionObject) {
			mView.mFileOpenSelectedPopupMenu
					.setEnabled(conditionMet(pConditionObject));
		}

		public boolean conditionMet(Object[] pConditionObjects) {
			// CB Not used.
			return false;
		}

		public void setEnabled(boolean pEnabled, Object[] pConditionObjects) {
			// CB Not used.
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	};

	private class FilePlayTrackAction extends BaseAction implements
			IBaseActionCondition {

		public FilePlayTrackAction() {
			init(Messages.getString("playercontrol.play"), false);
		}

		public void handleEvent(Event e) {
			IXFile lFile = getSelectedFile();
			if (lFile != null) {
				PlayerLogic.getInstance().playTrack(lFile);
			}
		}

		public boolean conditionMet(Object pConditionObject) {
			if (pConditionObject instanceof IXFile) {
				IXFile lFileWrapper = (IXFile) pConditionObject;
				IPlayer lPlayer = PlayerLogic.getInstance().getBestPlayer(
						lFileWrapper);
				if (lPlayer instanceof NoPlayer) {
					return false;
				} else {
					if (lFileWrapper.isLocal()) {
						return true;
					}
				}
			}
			return false;
		}

		public void setEnabled(boolean pEnabled, Object pConditionObject) {
			mView.mFilePlayMenu.setEnabled(conditionMet(pConditionObject));
			mView.mFilePlayPopupMenu.setEnabled(conditionMet(pConditionObject));
		}

		public boolean conditionMet(Object[] pConditionObjects) {
			// Shall not be used.
			throw new IllegalArgumentException();
		}

		public void setEnabled(boolean pEnabled, Object[] pConditionObjects) {
			// shall not be used.
			throw new IllegalArgumentException();
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	};

	private class FileID3ViewAction extends BaseAction implements
			IBaseActionCondition {
		public FileID3ViewAction() {
			init(Messages.getString("id3control.mp3view"), false);
		}

		public void handleEvent(Event e) {
			IXFile lFile = getSelectedFile();
			if (lFile != null && mID3Controller != null) {
				mID3Controller.showID3View(lFile);
			}
		}

		public boolean conditionMet(Object pConditionObject) {
			if (pConditionObject instanceof IXFile) {
				IXFile lFile = (IXFile) pConditionObject;
				return ID3Logic.getInstance().supportsID3(lFile);
			}
			return false;
		}

		public void setEnabled(boolean pEnabled, Object pConditionObject) {
			if (conditionMet(pConditionObject)) {
				mView.mFileID3ViewMenu.setEnabled(true);
				mView.mFileID3ViewPopupMenu.setEnabled(true);
			} else {
				mView.mFileID3ViewMenu.setEnabled(false);
				mView.mFileID3ViewPopupMenu.setEnabled(false);
			}
		}

		public boolean conditionMet(Object[] pConditionObjects) {
			// Not implemented yet.
			throw new IllegalArgumentException();
		}

		public void setEnabled(boolean pEnabled, Object[] pConditionObjects) {
			// Not implemented yet.
			throw new IllegalArgumentException();
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class FileID3RewriteAction extends BaseAction implements
			IBaseActionCondition {

		public FileID3RewriteAction() {
			init(Messages.getString("fileControl.rewrite"), false);
		}

		public void handleEvent(Event e) {
			Object[] lFileSelection = getSelectedFiles();
			if (lFileSelection.length != 0) {
				ID3Logic.getInstance().ID3Rewrite(lFileSelection);
			}
		}

		public void setEnabled(boolean pEnabled, Object pConditionObject) {
			mView.mFileID3RewriteMenu
					.setEnabled(conditionMet(pConditionObject));
			mView.mFileID3RewritePopupMenu
					.setEnabled(conditionMet(pConditionObject));
		}

		public boolean conditionMet(Object pConditionObject) {
			boolean lEnable = false;
			if (pConditionObject instanceof IXFile) {
				IXFile lFile = (IXFile) pConditionObject;
				lEnable = ID3Logic.getInstance().supportsID3(lFile);
			}
			return lEnable;
		}

		public boolean conditionMet(Object[] pConditionObjects) {
			// Not implemented yet.
			throw new IllegalArgumentException();
		}

		public void setEnabled(boolean pEnabled, Object[] pConditionObjects) {
			// Not implemented yet.
			throw new IllegalArgumentException();
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class FileDownloadAction extends BaseAction implements
			IBaseActionCondition {
		public FileDownloadAction() {
			init(Messages.getString("downloadscontrol.download"), false);
		}

		public void handleEvent(Event e) {
			Object[] lFiles = getSelectedFiles();
			for (int j = 0; j < lFiles.length; j++) {
				IXFile lFile = (IXFile) lFiles[j];
				if (lFile instanceof IXPersonalEnclosure) {
					IXPersonalEnclosure lEnclosure = (IXPersonalEnclosure) lFile;
					DownloadLogic.getInstance().addDownload(lEnclosure);
				}
			}
		}

		public void setEnabled(boolean pEnabled, Object pConditionObject) {
			mView.mFileDownloadMenu.setEnabled(conditionMet(pConditionObject));
			mView.mFileDownloadPopupMenu
					.setEnabled(conditionMet(pConditionObject));
		}

		public boolean conditionMet(Object pConditionObject) {
			boolean lEnable = false;
			if (pConditionObject instanceof IXPersonalEnclosure) {
				IXPersonalEnclosure lEnclosure = (IXPersonalEnclosure) pConditionObject;
				try {
					
					// Check if we are downloading. 
					if( DownloadLogic.getInstance().getDownload(lEnclosure) == null){
						if (!lEnclosure.isLocal() || lEnclosure.isIncomplete()) {
							mView.mFileDownloadMenu.setText(Messages
									.getString("fileControl.download")
									+ lEnclosure.getName());
							lEnable = true;
						}
					}
				} catch (JPodderException e) {
					e.printStackTrace();
				}
			} else {
				mView.mFileDownloadMenu.setText(Messages
						.getString("fileControl.download"));
			}
			return lEnable;
		}

		public boolean conditionMet(Object[] pConditionObjects) {
			// Not implemented yet.
			throw new IllegalArgumentException();
		}

		public void setEnabled(boolean pEnabled, Object[] pConditionObjects) {
			// Not implemented yet.
			throw new IllegalArgumentException();
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class MarkAllEnclosuresAction extends BaseAction {
		public MarkAllEnclosuresAction() {
			init(Messages.getString("feedcontrol.markAll"), true);
		}

		public void handleEvent(Event e) {
			Iterator it = mFeed.getMerged(false).iterator();
			while (it.hasNext()) {
				IXFile file = (IXFile) it.next();
				file.setMarked(true);
			}
			mView.update();
			StatusController.getInstance().updateSummary();
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class UnmarkAllEnclosuresAction extends BaseAction {
		public UnmarkAllEnclosuresAction() {
			init(Messages.getString("feedcontrol.clearAll"), true);
		}

		public void handleEvent(Event e) {
			Iterator it = mFeed.getMerged(false).iterator();
			while (it.hasNext()) {
				IXFile file = (IXFile) it.next();
				file.setMarked(false);
			}
			mView.update();
			StatusController.getInstance().updateSummary();
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class DeleteMarkedAction extends BaseAction {

		public DeleteMarkedAction() {
			init(Messages.getString("fileControl.delete"), true);
		}

		public void handleEvent(Event e) {
			int count = mFeed.getMergedCount(true); // Only count files on
			// the disc.
			if (count == 0) {
				MessageDialog.openInformation(UILauncher.getInstance()
						.getShell(), Messages
						.getString("fileControl.noSelection"), Messages
						.getString("fileControl.noSelection.title"));
				return;
			}

			boolean confirmed = MessageDialog.openConfirm(UILauncher
					.getInstance().getShell(),
					Messages.getString("fileControl.confirmDelete",
							new Integer(count).toString()), Messages
							.getString("fileControl.confirmDelete.title"));
			if (confirmed) {
				int deleteLocalCount = 0;
				Iterator it = mFeed.getMerged(false).iterator();
				while (it.hasNext()) {
					IXFile lFile = (IXFile) it.next();
					if (lFile.isMarked()) { // Marking is context sensitive.
						// Delete the track from the player first.
						if (PlayerLogic.getInstance().deleteTrack(lFile)) {
							lFile.setInPlayer(false);
						}

						// Delete the local file.
						if (lFile.getFile() != null && lFile.getFile().exists()) {
							if (lFile.getFile().delete()) {
								lFile.setLocal(false);
								deleteLocalCount++;
								it.remove();
							}
						}
						lFile.setMarked(false);
					}
					mView.update(lFile);
				}

				// Rebuild the merge model and refresh the table after
				// succesfull
				// deletion of the files.
				mFeed.getMerged(true);
				StatusController.getInstance().updateFeedBar(mFeed);
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class DeleteSelectedAction extends BaseAction implements
			IBaseActionCondition {

		public DeleteSelectedAction() {
			init(Messages.getString("fileControl.delete.selection"), false);
		}

		public void handleEvent(Event e) {

			Object[] lFiles = getSelectedFiles();
			if (lFiles == null || lFiles.length == 0) {
				return;
			}
			boolean confirmed = MessageDialog.openConfirm(UILauncher
					.getInstance().getShell(), Messages.getString(
					"fileControl.confirmDelete.selection", new Integer(
							lFiles.length).toString()), Messages
					.getString("fileControl.confirmDelete.title"));
			if (confirmed) {
				removeFiles(lFiles);
				StatusController.getInstance().updateFeedBar(mFeed);
			}
		}

		public boolean conditionMet(Object pConditionObject) {
			boolean lEnable = false;
			if (pConditionObject instanceof IXFile) {
				IXFile lFile = (IXFile) pConditionObject;
				if (lFile.isLocal()) {
					lEnable = true;
				}
			} else {
				mView.mFileDownloadMenu.setText(Messages
						.getString("fileControl.download"));
			}
			return lEnable;
		}

		public void setEnabled(boolean pEnabled, Object pConditionObject) {

			boolean lStatus;
			if (conditionMet(pConditionObject)) {
				lStatus = true;
			} else {
				lStatus = false;
			}
			mView.mFileDeleteSelectedMenu.setEnabled(lStatus);
			mView.mFileDeleteSelectedPopupMenu.setEnabled(lStatus);
		}

		public boolean conditionMet(Object[] pConditionObjects) {
			// Not implemented yet.
			throw new IllegalArgumentException();
		}

		public void setEnabled(boolean pEnabled, Object[] pConditionObjects) {
			// Not implemented yet.
			throw new IllegalArgumentException();
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	protected void removeFiles(Object[] pFiles) {
		for (int index = 0; index < pFiles.length; index++) {
			IXFile file = (IXFile) pFiles[index];
			if (PlayerLogic.getInstance().deleteTrack(file)) {
				file.setInPlayer(false);
			}
			if (file.getFile() != null && file.getFile().exists()) {
				if (file.getFile().delete()) {
					file.setLocal(false);
				} else {
					mLog.warn("Delete file error: " + file.getFile().getName());
				}
			}
			mView.getModelPreparator().updateModel();
			mView.update();
		}
	}

	private class MarkedToPlayerAction extends BaseAction {

		public MarkedToPlayerAction() {
			init(Messages.getString("fileControl.addMarked"), true);
		}

		public void handleEvent(Event e) {

			int count = mFeed.getMergedCount(true);
			if (count == 0) {
				MessageDialog.openInformation(UILauncher.getInstance()
						.getShell(), Messages
						.getString("fileControl.noSelection"), Messages
						.getString("fileControl.noSelection.title"));
				return;
			}
			boolean confirmed = MessageDialog.openConfirm(UILauncher
					.getInstance().getShell(), Messages
					.getString("fileControl.addMissing"), Messages
					.getString("fileControl.addMissing.tittle"));
			if (confirmed) {
				Iterator it = mFeed.getMerged(false).iterator();
				while (it.hasNext()) {
					IXFile lFile = (IXFile) it.next();
					boolean incomplete = false;
					if (lFile instanceof IXPersonalEnclosure) {
						try {
							incomplete = ((IXPersonalEnclosure) lFile)
									.isIncomplete();
						} catch (Exception e1) {
							// File is incomplete
						}
					}
					if (!incomplete && lFile.isMarked() && lFile.isLocal()
							&& !lFile.getInPlayer()) {
						// Tasks.storeInPlayer(file);
						PlayerLogic.getInstance().addTrack(
								lFile.getFeed().getTitle(), lFile);
						mView.update(lFile);
					}
				}
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class SyncSelectedWithPlayerAction extends BaseAction implements
			IBaseActionCondition {

		public SyncSelectedWithPlayerAction() {
			init(Messages.getString("fileControl.addSelected"), false);
		}

		public void handleEvent(Event e) {
			Object[] lFiles = getSelectedFiles();
			for (int index = 0; index < lFiles.length; index++) {
				IXFile lFile = (IXFile) lFiles[index];
				if (PlayerLogic.getInstance().addTrack(mFeed.getTitle(), lFile)) {
					lFile.setInPlayer(true);
					mView.update(lFile);
				}
			}
			StatusController.getInstance().updateFeedBar(mFeed);
		}

		public boolean conditionMet(Object pConditionObject) {
			if (pConditionObject instanceof IXFile) {
				IXFile lFileWrapper = (IXFile) pConditionObject;
				IPlayer lPlayer = PlayerLogic.getInstance().getBestPlayer(
						lFileWrapper);
				if (lPlayer instanceof NoPlayer) {
					return false;
				} else {
					if (lFileWrapper.isLocal()) {
						return true;
					}
				}
			}
			return false;
		}

		public void setEnabled(boolean pEnabled, Object pConditionObject) {
			boolean lStatus;
			if (conditionMet(pConditionObject)) {
				lStatus = true;
			} else {
				lStatus = false;
			}
			mView.mFileAddSelectedToPlayerMenu.setEnabled(lStatus);
			mView.mFileAddSelectedToPlayerPopupMenu.setEnabled(lStatus);

		}

		public boolean conditionMet(Object[] pConditionObjects) {
			// Not implemented yet.
			throw new IllegalArgumentException();
		}

		public void setEnabled(boolean pEnabled, Object[] pConditionObjects) {
			// Not implemented yet.
			throw new IllegalArgumentException();
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	public IXFile getSelectedFile() {
		if (mView.mSelection instanceof IXFile) {
			return (IXFile) mView.mSelection;
		} else
			return null;
	}

	public Object[] getSelectedFiles() {

		if (mView.mSelectionArray == null) {
			return null;
		}
		ArrayList lFiles = new ArrayList();
		for (int i = 0; i < mView.mSelectionArray.length; i++)
			if (mView.mSelectionArray[i] instanceof IXFile) {
				lFiles.add(mView.mSelectionArray[i]);
			}
		return lFiles.toArray();
	}

	public void setSelectedFile(IXFile pFile) {
		mView.setSelected(pFile);
	}

	public void instructionSucceeded(XFeedEvent e) {
		if (mView == null || mView.getView().isDisposed()) {
			return;
		}

		if (e.getSubject() instanceof IXFile) {
			IXFile lFile = (IXFile) e.getSubject();
			if (e.getTask() == XFeedEvent.INSTRUCTION_INSPECT
					|| e.getTask() == XFeedEvent.INSTRUCTION_MARK
					|| e.getTask() == XFeedEvent.INSTRUCTION_COLLECT_ENCL
					&& this.mFeed.equals(lFile.getFeed())) {
				mView.update(lFile); // DOESN'T CALL.
			}
		}
		if (e.getSubject() instanceof IXPersonalFeed) {
			IXPersonalFeed lFeed = (IXPersonalFeed) e.getSubject();
//			if (e.getTask() == XFeedEvent.INSTRUCTION_INSPECT
//					|| e.getTask() == XFeedEvent.INSTRUCTION_MARK
//					|| e.getTask() == XFeedEvent.INSTRUCTION_COLLECT_ENCL
//					&& mFeed.equals(lFeed)) {
//				mView.update();
//			}
			if( e.getTask() == XFeedEvent.INSTRUCTION_COLLECT){
				setFileView(lFeed);
			}
		}
	}

	public void instructionFailed(XFeedEvent e) {
		if (mView == null || mView.getView().isDisposed()) {
			return;
		}

	}

	public void instructionInfo(XFeedEvent e) {
		if (mView == null || mView.getView().isDisposed()) {
			return;
		}
	}

	public void actionPerformed(ActionEvent arg0) {

		if (mFeed == null || mView == null || mView.getView().isDisposed()) {
			return;
		}

		Iterator it = DownloadLogic.getInstance().getDownloadIterator();
		while (it.hasNext()) {
			Download lDownload = (Download) it.next();
			if (lDownload.getEnclosure().getFeed().equals(mFeed)) {
				switch (lDownload.getState()) {
				case DownloadLogic.DOWNLOADING: {
					mView.updateDownload(lDownload.getEnclosure());
				}
				}
			}
		}
	}

	/**
	 * Listen for network events.
	 */
	public void netActionPerformed(final NetTaskEvent event) {
		// Do some state updating an a UI update.
		if (mView == null && mView.getView().isDisposed()) {
			return;
		}

		final Download lDownload = (Download) event.getSource();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (event.getNetEvent() == NetTaskEvent.DOWNLOAD_SUCCESS) {
				}
				if (event.getNetEvent() == NetTaskEvent.DOWNLOAD_STATUS_CHANGED) {
				}
				if (event.getNetEvent() == NetTaskEvent.DOWNLOAD_FAILED) {
				}
				mView.update(lDownload.getEnclosure());
			}
		});
	}

	public void modelChanged(DownloadEvent pEvent) {
	}
}