package com.jpodder.ui.swt.feeds;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @since 1.0
 * @version 1.1
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Widget;

import com.jpodder.FileHandler;
import com.jpodder.JPodderException;
import com.jpodder.clock.Clock;
import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.data.configuration.IDataHandler;
import com.jpodder.data.feeds.IXFeedListener;
import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.XFeedLogic;
import com.jpodder.data.feeds.XPersonalFeed;
import com.jpodder.data.feeds.list.XPersonalFeedList;
import com.jpodder.data.feeds.stats.XFeedEvent;
import com.jpodder.data.m3u.M3ULogic;
import com.jpodder.data.opml.IOPMLListener;
import com.jpodder.data.opml.OPMLEvent;
import com.jpodder.data.opml.OPMLLogic;
import com.jpodder.data.opml.OPMLParser;
import com.jpodder.data.opml.Outline;
import com.jpodder.directory.XMLRPC;
import com.jpodder.html.HTMLLogic;
import com.jpodder.net.NetTask;
import com.jpodder.remote.IRPCListener;
import com.jpodder.remote.RPCEvent;
import com.jpodder.remote.RPCLogic;
import com.jpodder.schedule.SchedulerLogic;
import com.jpodder.tasks.ITask;
import com.jpodder.tasks.ITaskListener;
import com.jpodder.tasks.TaskEvent;
import com.jpodder.tasks.TaskLogic;
import com.jpodder.ui.swt.IController;
import com.jpodder.ui.swt.IView;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.comp.BaseAction;
import com.jpodder.ui.swt.comp.CheckBoxDialog;
import com.jpodder.ui.swt.comp.CheckTableDialog;
import com.jpodder.ui.swt.comp.CheckTableItem;
import com.jpodder.ui.swt.comp.ComboDialog;
import com.jpodder.ui.swt.comp.ICheckTableItem;
import com.jpodder.ui.swt.comp.ProgressDialog;
import com.jpodder.ui.swt.directory.DirectoryController;
import com.jpodder.ui.swt.feeds.manager.FeedDialog;
import com.jpodder.ui.swt.id3.ID3Control;
import com.jpodder.ui.swt.media.MediaController;
import com.jpodder.ui.swt.media.MediaView;
import com.jpodder.ui.swt.status.StatusBar;
import com.jpodder.ui.swt.status.StatusController;
import com.jpodder.ui.swt.tabs.ITabListener;
import com.jpodder.ui.swt.tabs.TabEvent;
import com.jpodder.util.Messages;
import com.jpodder.util.Util;

import de.kupzog.ktable.KTableCellSelectionListener;
import de.kupzog.ktable.KTableSortComparator;
import de.kupzog.ktable.KTableSortOnClick;

/**
 * An RSS feed control class. It implements various actions related to feeds. -
 * Add a feed to the current feed list. - Remove a feed from the feed list. -
 * Add feeds from an OPML file. - Find a feed in an OPML file.
 */
public class FeedController implements ActionListener, IController,
		KTableCellSelectionListener, IXFeedListener, ITabListener,
		IOPMLListener, ITaskListener, IConfigurationListener, IRPCListener {

	public BaseAction scanAndDownloadAction;

	public BaseAction scanAction;

	public BaseAction addFeedAction;

	public BaseAction removeFeedAction;

	public BaseAction upFeedAction;

	public BaseAction upFeedTextAction;

	public BaseAction downFeedAction;

	public BaseAction downFeedTextAction;

	public BaseAction previewFeedIconAction;

	public BaseAction previewFeedAction;

	public BaseAction editFeedAction;

	public BaseAction saveFeedListAction;

	public BaseAction openFeedListAction;

	public BaseAction addLocalFeedsAction;

	public BaseAction lookupFeedURLAction;

	public BaseAction createPlayListAction;

	public FeedsView mView;

	private StatusBar mStatusBar;

	protected MediaController mFileController;

	protected DirectoryController mDirController;

	protected ID3Control mID3Controller;

	protected FeedController mSelf;

	private Logger mLog = Logger.getLogger(getClass().getName());

	private MediaView mFilesView;

	/**
	 * Constructor. Defines the actions in this controller.
	 * 
	 * @param pFrame
	 *            The parent Frame that can be used to create dialogs
	 * @param pView
	 *            The view that is used by this controler to view the content
	 */
	public FeedController() {
		initialize();
	}

	public void initialize() {
		mSelf = this;
		mLog.info("<init>");
		mFileController = new MediaController();
	}

	public void setControllers(DirectoryController pDirController,
			ID3Control pID3Controller) {
		mID3Controller = pID3Controller;
		mDirController = pDirController;
	}

	public MediaController getFileController() {
		return mFileController;
	}

	public void setView(IView pView) {
		mView = (FeedsView) pView;
		mView.getView().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				// We remove ourselves from the Logics we are listening to.
				mFilesView.getView().dispose();
				
				XFeedLogic.getInstance().removeListener(mSelf);
				Clock.getInstance().removeActionListener(mSelf);
				ConfigurationLogic.getInstance().removeConfigListener(mSelf);
				TaskLogic.getInstance().removeListener(mSelf);
				RPCLogic.getInstance().removeListener(mSelf);
				OPMLLogic.getInstance().removeListener(mSelf);				
				
				NetTask.getInstance().removeNetActionListener(mFileController);
				XFeedLogic.getInstance().removeListener(mFileController);
				Clock.getInstance().removeActionListener(mFileController);
				
				ConfigurationLogic.getInstance().removeConfigListener(
						StatusController.getInstance());
				
			}
		});
		
		mFilesView = new MediaView(mView.getLowerSash());
		mFileController.setView(mFilesView);
		// Is this view disposed properly?
		mStatusBar = new StatusBar(mView.getView());

		TaskLogic.getInstance().addListener(mSelf);
		XFeedLogic.getInstance().addListener(mSelf);
		Clock.getInstance().addActionListener(mSelf);
		RPCLogic.getInstance().addListener(mSelf);
		ConfigurationLogic.getInstance().addConfigListener(mSelf);
		OPMLLogic.getInstance().addListener(mSelf);
		
		NetTask.getInstance().addNetActionListener(mFileController);
		XFeedLogic.getInstance().addListener(mFileController);
		Clock.getInstance().addActionListener(mFileController);
		
		ConfigurationLogic.getInstance().addConfigListener(
				StatusController.getInstance());
		
		initializeUI();
	}

	public void initializeUI() {

		createPlayListAction = new CreatePlaylistAction();
		scanAndDownloadAction = new ScanAndDownloadAction();
		scanAction = new ScanAction();
		addFeedAction = new AddFeedAction();
		editFeedAction = new EditFeedAction();
		removeFeedAction = new RemoveFeedAction();
		upFeedTextAction = new UpAction(Messages.getString("feedcontrol.up"));
		upFeedAction = new UpAction();
		downFeedTextAction = new DownAction(Messages
				.getString("feedcontrol.down"));
		downFeedAction = new DownAction();

		previewFeedAction = new PreviewFeedAction();
		previewFeedIconAction = new PreviewFeedAction();
		saveFeedListAction = new SaveFeedListAction();
		openFeedListAction = new LoadFeedListAction();
		addLocalFeedsAction = new AddFromFoldersAction();
		lookupFeedURLAction = new LookupFeedURLAction();

		mView.mFeedImportMenu.addListener(SWT.Selection, openFeedListAction);
		mView.mFeedImportMenu.setText(openFeedListAction.getName());

		mView.mFeedExportMenu.addListener(SWT.Selection, saveFeedListAction);
		mView.mFeedExportMenu.setText(saveFeedListAction.getName());

		mView.mDownloadButton.addListener(SWT.Selection, scanAndDownloadAction);
		mView.mDownloadButton.setText(scanAndDownloadAction.getName());

		mView.mFeedDownloadMenu.addListener(SWT.Selection,
				scanAndDownloadAction);
		mView.mFeedDownloadMenu.setText(scanAndDownloadAction.getName());
		mView.mFeedPreviewMenu.addListener(SWT.Selection, previewFeedAction);
		mView.mFeedPreviewMenu.setText(previewFeedAction.getName());
		mView.mPreviewFeedButton.addListener(SWT.Selection,
				previewFeedIconAction);
		mView.mPreviewFeedButton.setText(previewFeedIconAction.getName());
		mView.mFeedPreviewAllMenu.addListener(SWT.Selection, scanAction);
		mView.mFeedPreviewAllMenu.setText(scanAction.getName());
		mView.mFeedAddMenu.addListener(SWT.Selection, addFeedAction);
		mView.mFeedAddMenu.setText(addFeedAction.getName());
		mView.mFeedEditMenu.addListener(SWT.Selection, editFeedAction);
		mView.mFeedEditMenu.setText(editFeedAction.getName());
		mView.mFeedRemoveMenu.addListener(SWT.Selection, removeFeedAction);
		mView.mFeedRemoveMenu.setText(removeFeedAction.getName());
		mView.mFeedUpMenu.addListener(SWT.Selection, upFeedTextAction);
		mView.mFeedUpMenu.setText(upFeedTextAction.getName());
		mView.mFeedDownMenu.addListener(SWT.Selection, downFeedTextAction);
		mView.mFeedDownMenu.setText(downFeedTextAction.getName());

		// --- Popup Menu for the Feeds
		mView.mFeedAddPopupMenu.addListener(SWT.Selection, addFeedAction);
		mView.mFeedAddPopupMenu.setText(addFeedAction.getName());

		mView.mPreviewFeedPopupMenu.addListener(SWT.Selection,
				previewFeedAction);
		mView.mPreviewFeedPopupMenu.setText(previewFeedAction.getName());

		mView.mPreviewAllFeedPopupMenu.addListener(SWT.Selection, scanAction);
		mView.mPreviewAllFeedPopupMenu.setText(scanAction.getName());

		mView.mEditFeedPopupMenu.addListener(SWT.Selection, editFeedAction);
		mView.mEditFeedPopupMenu.setText(editFeedAction.getName());

		mView.mRemoveFeedPopupMenu.addListener(SWT.Selection, removeFeedAction);
		mView.mRemoveFeedPopupMenu.setText(removeFeedAction.getName());

		// mView.mToolsSyncPopupMenu.addListener(SWT.Selection, )

		mView.mToolsAddLocalMenu
				.addListener(SWT.Selection, addLocalFeedsAction);
		mView.mToolsAddLocalMenu.setText(addLocalFeedsAction.getName());
		mView.mToolsLookUpURLMenu.addListener(SWT.Selection,
				lookupFeedURLAction);
		mView.mToolsLookUpURLMenu.setText(lookupFeedURLAction.getName());
		mView.mToolsCreatePlaylistMenu.addListener(SWT.Selection,
				createPlayListAction);
		mView.mToolsCreatePlaylistMenu.setText(createPlayListAction.getName());

		// Add this controler as cell selection listener
		mView.mFeedTable.addCellSelectionListener(this);

		mView.mFeedTable.addMouseListener(mView.new FeedMouseAdapter(
				editFeedAction, addFeedAction));

		Object[] pModel = XPersonalFeedList.getInstance().getFeedArray();
		KFeedTableModel lFeedModel = new KFeedTableModel(pModel);
		mView.mFeedTable.setModel(lFeedModel);

		// mView.mFeedTable.addCellSelectionListener(new KTableSortOnClick(
		// mView.mFeedTable, new KFeedComparator(lFeedModel, -1,
		// KTableSortComparator.SORT_NONE)));

		setSelectedActionStatus(false);
		StatusController.getInstance().updateFeedBar(getSelectedFeed());
		StatusController.getInstance().updateSummary();

	}

	public FeedsView getView() {
		return mView;
	}

	/**
	 * Uses the <code>FeedList</code> as the model.
	 */
	public void setFeedView(final Object[] pModel) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				// 1) Set a new model for the feed table.
				KFeedTableModel lFeedModel = new KFeedTableModel(pModel);
				mView.mFeedTable.setModel(lFeedModel);

				// TODO Can not add selection listeners everytime we add or
				// remove a feed.
				mView.mFeedTable
						.addCellSelectionListener(new KTableSortOnClick(
								mView.mFeedTable, new KFeedComparator(
										lFeedModel, -1,
										KTableSortComparator.SORT_NONE)));
				setSelectedActionStatus(false);
				mView.mInfoView.clearInfo();
				StatusController.getInstance().updateSummary();
			}
		});
	}

	public void updateFeedView(final Object[] pModel) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				((KFeedTableModel) mView.mFeedTable.getModel())
						.updateModel(pModel);
				mView.mFeedTable.redraw(); // Force a redraw.
			}
		});
	}

	/**
	 * Update the view of the feed list (Adding or editing a feed). Also
	 * invokes,
	 * 
	 * @param pAdd
	 * @param pFeedToBeUpdated
	 */
	public void updateFeed(boolean pAdd, IXPersonalFeed pFeedToBeUpdated) {

		if (pAdd) {
			XPersonalFeedList.getInstance().addFeed(pFeedToBeUpdated);
			updateFeedView(XPersonalFeedList.getInstance().getFeedArray());
			setSelectedFeed(pFeedToBeUpdated);
		} else {
			updateFeed(pFeedToBeUpdated);
			// // Force an update of the fileview, folder could have changed.
			XFeedLogic.getInstance().collectFeed(FeedController.class,
					pFeedToBeUpdated, true);
		}

		StatusController.getInstance().updateFeedBar(pFeedToBeUpdated);
		StatusController.getInstance().updateSummary();

	}

	// --------
	// Some methods for mapping of view and model entries.

	public void updateFeed(IXPersonalFeed pFeed) {
		int lIndex = XPersonalFeedList.getInstance().getIndexOf(pFeed);
		mView.updateRows(new int[] { lIndex + 1 });
	}

	public void setSelectedFeed(IXPersonalFeed pFeed) {
		int lIndex = XPersonalFeedList.getInstance().getIndexOf(pFeed);
		mView.setSelectedRow(lIndex + 1);
	}

	public int getSelectedFeedIndex() {
		int[] lSelection = mView.getSelectedRows();
		return lSelection.length > 0 ? lSelection[0] - 1 : null;
	}

	public IXPersonalFeed getSelectedFeed() {
		int[] lSelection = mView.getSelectedRows();
		return lSelection.length > 0 ? XPersonalFeedList.getInstance().getFeed(
				lSelection[0] - 1) : null;
	}

	public Object[] getSelectedFeeds() {
		int[] lIndices = mView.getSelectedRows();
		// CB the selection array values, should be decreased by 1
		// to skip the header row.
		for (int i = 0; i < lIndices.length; i++) {
			lIndices[i] -= 1;
		}
		return XPersonalFeedList.getInstance().getFeeds(lIndices);
	}

	public class CreatePlaylistAction extends BaseAction {

		public CreatePlaylistAction() {
			init(Messages.getString("feedcontrol.playlist"));
		}

		public void handleEvent(Event event) {
			IXPersonalFeed lFeed = getSelectedFeed();
			if (lFeed != null) {
				FileDialog dialog = new FileDialog(UILauncher.getInstance()
						.getShell(), SWT.SAVE);
				dialog.setFilterExtensions(new String[] { "*."
						+ M3ULogic.M3U_EXTENSION });
				dialog.open();
				String lFileName = dialog.getFileName();
				File lFile = new File(lFileName);
				if (lFile.exists()) {
				} else {
					try {
						lFile.createNewFile();
					} catch (IOException e) {
					}
				}
				try {
					M3ULogic.getInstance().writePlaylist(lFile,
							lFeed.getLocals());
				} catch (JPodderException e) {
				} catch (IOException e) {
				}
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class ScanAndDownloadAction extends BaseAction {

		public ScanAndDownloadAction() {
			init(Messages.getString("sequenceControl.download"));
		}

		public void handleEvent(Event event) {
			int lScanned = XFeedLogic.getInstance().scan(FeedController.class,
					true);
			int lSize = XPersonalFeedList.getInstance().size();

			mView.getActionPanel().setText(
					Messages
							.getString("sequenceControl.final", new Integer(
									lScanned).toString(), new Integer(lSize)
									.toString()));
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class ScanAction extends BaseAction {
		public ScanAction() {
			init(Messages.getString("feedcontrol.previewFeedAll"));
		}

		public void handleEvent(Event event) {
			int lScanned = XFeedLogic.getInstance().scan(FeedController.class,
					false);
			int lSize = XPersonalFeedList.getInstance().size();

			mView.getActionPanel().setText(
					Messages
							.getString("sequenceControl.final", new Integer(
									lScanned).toString(), new Integer(lSize)
									.toString()));
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class AddFeedAction extends BaseAction {

		public AddFeedAction() {
			init(Messages.getString("feedcontrol.addFeed"));
		}

		public void handleEvent(Event event) {
			mLog.info("actionPerformed(), event: " + event);
			showFeedManager(true, "", null);
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class EditFeedAction extends BaseAction {

		public EditFeedAction() {
			init(Messages.getString("feedcontrol.editFeed"));
		}

		public void handleEvent(Event event) {
			mLog.debug("EditFeedAction.actionPerformed(), event: " + event);
			IXPersonalFeed feed = getSelectedFeed();
			mLog.info("EditFeedAction.actionPerformed(), selected feed: "
					+ feed);
			if (feed != null) {
				showFeedManager(false, feed.getURL() != null ? feed.getURL()
						.toString() : "", feed);
			} else {
				showFeedManager(true, "", feed);
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class RemoveFeedAction extends BaseAction {

		public RemoveFeedAction() {
			init(Messages.getString("feedcontrol.removeFeed"));
		}

		public void handleEvent(Event event) {
			removeFeeds(getSelectedFeeds());
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class UpAction extends BaseAction {

		public UpAction() {
		}

		public UpAction(String pName) {
			init(pName);
		}

		public void handleEvent(Event event) {
			int lRow = mView.getSelectedRow();
			int lSize = XPersonalFeedList.getInstance().size();
			if (lRow != 0 && lRow < lSize) {
				XPersonalFeedList.getInstance().swap(lRow - 1, lRow);
				updateFeedView(XPersonalFeedList.getInstance().getFeedArray());
				mView.setSelectedRow(lRow);
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class DownAction extends BaseAction {

		public DownAction() {
		}

		public DownAction(String pName) {
			init(pName);
		}

		public void handleEvent(Event event) {
			int row = mView.getSelectedRow();
			int lSize = XPersonalFeedList.getInstance().size();
			if (row < lSize - 1) {
				XPersonalFeedList.getInstance().swap(row, row + 1);
				updateFeedView(XPersonalFeedList.getInstance().getFeedArray());
				mView.setSelectedRow(row + 2);
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class PreviewFeedAction extends BaseAction {

		public PreviewFeedAction() {
			init(Messages.getString("feedcontrol.previewFeed"), false);
		}

		public void handleEvent(Event e) {

			IXPersonalFeed lFeed = getSelectedFeed();
			if (lFeed != null) {
				XFeedLogic.getInstance().previewFeed(
						FeedController.class.getName(), lFeed);

				mView.getActionPanel().setText(
						Messages.getString("feedcontrol.previewFeedTitle")
								+ (lFeed.getTitle() != null ? lFeed.getTitle()
										: lFeed.getURL().toExternalForm()));
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class SaveFeedListAction extends BaseAction {
		public SaveFeedListAction() {
			init(Messages.getString("feedcontrol.saveFeed"));
		}

		public void handleEvent(Event e) {
			FileDialog lFileDialog = new FileDialog(UILauncher.getInstance()
					.getShell(), SWT.SAVE);
			lFileDialog.setFilterExtensions(new String[] { "*."
					+ OPMLLogic.OPML_EXT });
			String lResult = lFileDialog.open();
			if (lResult != null) {
				String lFileName = lFileDialog.getFileName();
				String lPath = lFileDialog.getFilterPath();
				File pFile = new File(lPath + File.separator + lFileName);
				List lList = OPMLLogic.getInstance().feedsToOPML(
						XPersonalFeedList.getInstance().getFeedArray());
				OPMLParser.getInstance().writeOPML(lList, pFile);
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	/**
	 * Use with care! Simply cleans the personalfeedlist.
	 */
	private class CleanFeedListAction extends BaseAction {
		public CleanFeedListAction() {
			init("[I18n] Clean");
		}

		public void handleEvent(Event e) {

			Iterator lFeeds = XPersonalFeedList.getInstance().getFeedIterator();
			for (; lFeeds.hasNext();) {
				XPersonalFeedList.getInstance().removeFeed(
						(IXPersonalFeed) lFeeds.next());
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class LoadFeedListAction extends BaseAction {
		public LoadFeedListAction() {
			init(Messages.getString("feedcontrol.loadFeed"));
		}

		public void handleEvent(Event e) {
			FileDialog lFileDialog = new FileDialog(UILauncher.getInstance()
					.getShell(), SWT.OPEN);
			lFileDialog.setFilterExtensions(new String[] { "*."
					+ OPMLLogic.OPML_EXT });
			String lResult = lFileDialog.open();
			if (lResult != null) {
				String lFileName = lFileDialog.getFileName();
				String lPath = lFileDialog.getFilterPath();
				ITask lTask = OPMLLogic.getInstance().parse(
						new File(lPath + File.separator + lFileName));
				ProgressDialog lDialog = new ProgressDialog(
						"[I18n] Loading OPML");
				lDialog.setTask(lTask);
				lDialog.makeModal();
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class AddFromFoldersAction extends BaseAction {
		public AddFromFoldersAction() {
			init(Messages.getString("feedcontrol.scanFolder"), true);
		}

		public void handleEvent(Event e) {

			List lScannedFeeds = XFeedLogic.scanLocalFolder();
			lScannedFeeds = XPersonalFeedList.getInstance().excludeExisting(
					lScannedFeeds);

			ArrayList<CheckTableItem> lDialogValues = new ArrayList<CheckTableItem>();
			Iterator lIt = lScannedFeeds.iterator();
			while (lIt.hasNext()) {
				IXPersonalFeed lFeed = (IXPersonalFeed) lIt.next();
				File lFile = new File(lFeed.getFolder());
				lDialogValues.add(new CheckTableItem(false,
						new String[] { lFile.getName() }));
			}

			CheckTableDialog lDialog = new CheckTableDialog(Messages
					.getString("feedcontrol.addFeeds"));
			lDialog.fill(lDialogValues);
			lDialog.makeModal();

			if (lDialog.getResult() == CheckTableDialog.OK_SELECTED) {
				List lReturnValues = lDialog.get();

				// List list = dialog.model.getCheckedObjects();
				Iterator i = lReturnValues.iterator();
				int lAdded = 0;
				IXPersonalFeed lFeed = null;
				while (i.hasNext()) {
					ICheckTableItem lItem = (ICheckTableItem) i.next();
					if (lItem.isChecked()) {
						lFeed = new XPersonalFeed();
						String[] lValues = (String[]) lItem.getValue();
						lFeed.setPersonalTitle(lValues[0]);
						lFeed.setFolder(FileHandler.getFeedFolder(lValues[0]));
						XPersonalFeedList.getInstance().addFeed(lFeed);
						lAdded++;
					}
				}
				if (lFeed != null) {
					setFeedView(XPersonalFeedList.getInstance().getFeedArray());
					updateFeed(lFeed);
					setSelectedFeed(lFeed);
					StatusController.getInstance().updateSummary();
					StatusController.getInstance().updateFeedBar(lFeed);
				}
				mView.getActionPanel().setText(
						Messages.getString("feedcontrol.addedFeeds", Integer
								.toString(lAdded)));
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private class LookupFeedURLAction extends BaseAction {
		public LookupFeedURLAction() {
			init(Messages.getString("feedcontrol.lookupURL"));
		}

		public void handleEvent(Event e) {
			IXPersonalFeed feed = getSelectedFeed();
			if (feed.getURL() == null) {
				String lQuery = Util.getName(feed.getFolder());

				Vector dirResult = null;
				try {
					dirResult = XMLRPC.searchFeeds(lQuery);
				} catch (Exception e1) {
					mLog.info("Can not access IPX directory");
					MessageDialog.openError(
							UILauncher.getInstance().getShell(), Messages
									.getString("directorytree.xError"),
							Messages.getString("directorytree.xError.dialog"));
				}
				if (dirResult != null && dirResult.size() > 0) {
					String[] lUrls = new String[dirResult.size()];
					int i = 0;
					Iterator it = dirResult.iterator();
					while (it.hasNext()) {
						Hashtable ht = (Hashtable) it.next();
						String url = (String) ht.get(XMLRPC.IPX_KEY_FEED_URL);
						url = url.trim();
						lUrls[i++] = url;
					}
					ComboDialog lDialog = new ComboDialog("Select a URL");
					lDialog.fill(lUrls);
					if (lDialog.getOption() == ComboDialog.OK_SELECTED) {

						Object url = lDialog.GetResult();
						try {
							feed.setURL(new URL((String) url));
							int lRow = mView.getSelectedRow();
							mView.updateRow(lRow);
						} catch (MalformedURLException e2) {
							// URL not valid.
						}
					}

				} else {
					MessageDialog.openInformation(UILauncher.getInstance()
							.getShell(), "[I18n] Title",
							"[I18n] No matching URL found.");
				}
			}
		}

		public void setControls(Widget[] pControls) {
			// TODO Auto-generated method stub
			
		}
	}

	private void setFeedSubscription(boolean pSelect) {
		Iterator it = XPersonalFeedList.getInstance().getFeedIterator();
		while (it.hasNext()) {
			IXPersonalFeed feed = (IXPersonalFeed) it.next();
			feed.setSubscribed(pSelect);
		}
		mView
				.updateRows(new int[] { 0,
						XPersonalFeedList.getInstance().size() });
	}

	/**
	 * Remove feeds.
	 * 
	 * @param pFeeds
	 */
	private void removeFeeds(Object[] pFeeds) {
		
		boolean lReselect = false;
		
		for (int i = 0; i < pFeeds.length; i++) {
			IXPersonalFeed lFeed = (IXPersonalFeed) pFeeds[i];
			if (lFeed != null) {
				// First we check if this feed has a folder.
				boolean lFolderExists = false;
				File lFeedFolder = null;
				if (lFeed.getFolder() != null) {
					lFeedFolder = new File(lFeed.getFolder());
					if (lFeedFolder.exists() && lFeedFolder.isDirectory()) {
						lFolderExists = true;
					}
				}
				
				String[] lOptions = {
						lFolderExists ? Messages
								.getString("feedcontrol.removeFeedFolders")
								: null,
						Messages.getString("feedcontrol.removeFeedPlaylist") };

				CheckBoxDialog.showConfirmDialog(Messages
						.getString("feedcontrol.removeAFeed"), Messages
						.getString("feedcontrol.removeFeedConfirm")
						+ lFeed.getTitle()
						+ Messages.getString("feedcontrol.removeFeedSelect"),
						lOptions);
				CheckBoxDialog.makeModal();
				int result = CheckBoxDialog.getResult();
				if (result == CheckBoxDialog.OK_SELECTED) {
					lReselect = true;
					mLog.info("Deleting of: " + lFeed.getTitle()
							+ ", confirmed");
					Boolean[] options = CheckBoxDialog.getLatestOptions();

					boolean removeDisc = options[0] != null ? options[0]
							.booleanValue() : false;
					boolean removePlayer = options[1].booleanValue();

					XFeedLogic.getInstance().removeFeed(lFeed, removePlayer,
							removeDisc);
					updateFeedView(XPersonalFeedList.getInstance()
							.getFeedArray());

					mView.getActionPanel().setText(
							Messages.getString("feedcontrol.removedFeed")
									+ lFeed.getTitle());
				} else {

				}
			}
		}

		if (lReselect) {
			int lSelection = getSelectedFeedIndex();
			int lLastFeed = XPersonalFeedList.getInstance().getFeedArray().length - 1;
			int lFinalSelection = 0;
			if (lSelection + 1 <= lLastFeed) {
				lFinalSelection = lSelection + 1;
			} else {
				lFinalSelection = lLastFeed;
			}

			if (lFinalSelection >= 0) {
				setSelectedFeed((IXPersonalFeed) XPersonalFeedList
						.getInstance().getFeedArray()[lFinalSelection]);
			} else {
				mFileController.setFileView(null);
				mView.mInfoView.clearInfo();
			}
		}
	}

	/**
	 * When a new row is selected, the feed information, like the included
	 * enclosures is retrieved. As this occurs in an asynchronuous manner, it
	 * could be that the feed information hasn't been retrieved as the user
	 * moves on to the next row.
	 */
	public void cellSelected(int col, int row, int statemask) {
		updateSelection();
	}

	public void fixedCellSelected(int col, int row, int statemask) {
		// We don't do anything with the fixed cell selection.
	}

	private void updateSelection() {

		int lLead = mView.getSelectedRow();
		// Make sure that it does not fail in cause there is not row
		// selected at all
		IXPersonalFeed lFeed = null;
		if (lLead >= 0 && lLead < XPersonalFeedList.getInstance().size()) {
			lFeed = XPersonalFeedList.getInstance().getFeed(lLead);
			mLog.info(lFeed);
			setSelectedActionStatus(true);
			// mView.mInfoView.formatFeed(lFeed);
			mView.mInfoView.formatFeed(lFeed, HTMLLogic.STYLE_SHEET);
			StatusController.getInstance().updateFeedBar(lFeed);
			StatusController.getInstance().updateSummary();
			XFeedLogic.getInstance().collectFeed(FeedController.class, lFeed);
			if (mLog.isDebugEnabled()) {
				mLog.info("Selected a feed (" + lLead + ") name="
						+ lFeed.getTitle());
			}
		} else {
			setSelectedActionStatus(false);
			StatusController.getInstance().clearBar();
			mView.mInfoView.clearInfo();
		}
		if (mFileController != null) {
			mFileController.setFileView(lFeed);
		}
	}

	public void setSelectedActionStatus(boolean pStatus) {

		// Actions which depend on a selected feed.

		mView.mFeedRemoveMenu.setEnabled(pStatus);
		mView.mFeedEditMenu.setEnabled(pStatus);
		mView.mFeedPreviewMenu.setEnabled(pStatus);
		mView.mPreviewFeedButton.setEnabled(pStatus);
		mView.mFeedUpMenu.setEnabled(pStatus);
		mView.mFeedDownMenu.setEnabled(pStatus);
		mView.mToolsLookUpURLMenu.setEnabled(pStatus);
		mView.mRemoveFeedPopupMenu.setEnabled(pStatus);
		mView.mEditFeedPopupMenu.setEnabled(pStatus);
	}

	public void showFeedManager(boolean pAdd, String pURL) {
		showFeedManager(pAdd, pURL, null);
	}

	/**
	 * Show the FeedManager
	 * 
	 * @param pAdd,
	 *            If true, the add dialog will be shown.
	 * @param pURL
	 * @param pFeed
	 */
	public void showFeedManager(boolean pAdd, String pURL, IXPersonalFeed pFeed) {
		mLog.debug("showFeedManager(), add: " + pAdd + ", url: " + pURL
				+ ", feed: " + pFeed);
		if (pFeed == null) {
			pFeed = getTempFeed();
			if (pURL != null) {
				try {
					pFeed.setURL(new URL(pURL));
				} catch (MalformedURLException e) {
				}
			}
		}

		FeedDialog lDialog = new FeedDialog(this);
		lDialog
				.show((pAdd ? FeedDialog.ADD_MODE : FeedDialog.EDIT_MODE),
						pFeed);

		if (!pAdd) {
			mView.getActionPanel().setText(
					Messages.getString("feedcontrol.editFeedDesc")
							+ pFeed.getURL());
		}

	}

	/**
	 * Give us a feed, in which we can store, un-committed feed information A
	 * snapshot is taken, from the current component values. It is the
	 * responsibility of the class closing this dialog to get the latest values
	 * from the various components.
	 * 
	 * @return Feed A temporary feed.
	 */
	public IXPersonalFeed getTempFeed() {
		IXPersonalFeed feed = new XPersonalFeed();
		return feed;
	}

	/**
	 * Is notified of completed tasks. The main goal is to update the UI with
	 * the results from the tasks. For a feed inspection, this is the feed title
	 * and t
	 */
	public void instructionSucceeded(final XFeedEvent e) {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				
				if(mView.mFeedTable.isDisposed()){
					return;
				}
				Object src = e.getSubject();
				int task = e.getTask();

				if (mLog.isDebugEnabled()) {
					mLog.debug("instructionSucceeded(), event: " + e.getTask()
							+ src + task);
				}

				// Check if this notification is for me.
				if (assertSource(src)) {
					IXPersonalFeed lFeed;
					if (e.getSubject() instanceof IXPersonalFeed) {
						lFeed = (IXPersonalFeed) e.getSubject();
					} else {
						return;
					}
					switch (task) {
					case XFeedEvent.INSTRUCTION_COLLECT:
						if (assertVisible(lFeed)) {
							updateFeed(lFeed);
							mView.mInfoView.formatFeed(lFeed,
									com.jpodder.html.HTMLLogic.STYLE_SHEET);
						}
						break;
					case XFeedEvent.INSTRUCTION_INSPECT:
						StatusController.getInstance().updateFeedBar(lFeed);
						StatusController.getInstance().updateSummary();
						break;
					}
				}
			}
		});
	}

	/**
	 * Notification of a task failure. Results in UI updates of the Feed or File
	 * table.
	 */
	public void instructionFailed(final XFeedEvent e) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (assertSource(e.getSource())) {
					IXPersonalFeed lFeed = (IXPersonalFeed) e.getSubject();
					if (e.getTask() == XFeedEvent.INSTRUCTION_COLLECT) {
						updateFeed(lFeed);
						mLog.warn(Messages.getString("tasks.errorCollecting")
								+ e.getException() + ": " + lFeed.getURL());
						if (assertVisible(lFeed)) {
							mView.mInfoView.setNoNetwork();
						}
					}
				}
			}
		});
	}

	public void instructionInfo(final XFeedEvent e) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(mView.getActionPanel().isDisposed()){
					return;
				}
				mView.getActionPanel().setText(e.getInformation());
			}
		});
	}

	public boolean assertSource(Object pSrc) {
		if (pSrc instanceof Class) {
			return ((Class) pSrc).getName().equals(
					FeedController.class.getName());
		} else if (pSrc instanceof String) {
			return ((String) pSrc).equals(FeedController.class.getName());
		}
		return true;
	}

	public boolean assertVisible(IXPersonalFeed pFeed) {
		int lFeedIndex = XPersonalFeedList.getInstance().getIndexOf(pFeed);
		return (mView.getSelectedRow() == lFeedIndex) ? true : false;
	}

	/**
	 * We are informed of a tab change, we might need when the UI refresher is
	 * not updating because all downloads have completed.
	 */
	public void visibleTabChanged(TabEvent pEvent) {
	}

	/**
	 * @see com.jpodder.data.opml.IOPMLListener#opmlCompleted(com.jpodder.data.opml.OPMLEvent)
	 */
	public void opmlCompleted(final OPMLEvent e) {
		// This method is called after some OPML parsing actions
		// are perfomed.
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				Object lResult = e.getResult();
				if (lResult instanceof ArrayList) {
					ArrayList lList = (ArrayList) lResult;

					ArrayList<CheckTableItem> lDialogValues = new ArrayList<CheckTableItem>();
					Iterator lIt = lList.iterator();
					while (lIt.hasNext()) {
						Outline lOutline = (Outline) lIt.next();
						lDialogValues.add(new CheckTableItem(true,
								new Object[] { lOutline }));
					}

					CheckTableDialog lDialog = new CheckTableDialog(Messages
							.getString("feedcontrol.addFeeds"));
					lDialog.fill(lDialogValues);
					lDialog.makeModal();

					if (lDialog.getResult() == CheckTableDialog.OK_SELECTED) {
						List lReturnValues = lDialog.get();

						// List list = dialog.model.getCheckedObjects();
						Iterator i = lReturnValues.iterator();
						int lAdded = 0;
						IXPersonalFeed lFeed = null;
						while (i.hasNext()) {
							ICheckTableItem lItem = (ICheckTableItem) i.next();
							if (lItem.isChecked()) {
								lFeed = new XPersonalFeed();
								Object lValue = lItem.getValue();
								Outline lOutline = null;
								if (lValue instanceof Object[]) {
									Object[] lValues = (Object[]) lValue;
									lOutline = (Outline) lValues[0];
								} else {
									lOutline = (Outline) lValue;
								}
								lFeed.setURL(lOutline.getURL());
								lFeed.setPersonalTitle(lOutline.getText());
								lFeed.setFolder(FileHandler.getPodcastFolder()
										+ File.separator + lOutline.getText());
								lFeed.setSubscribed(true);
								lFeed.setMaxDownloads(1);
								XPersonalFeedList.getInstance().addFeed(lFeed);
								lAdded++;
							}
						}
						if (lFeed != null) {
							setFeedView(XPersonalFeedList.getInstance()
									.getFeedArray());
							updateFeed(lFeed);
							setSelectedFeed(lFeed);
							StatusController.getInstance().updateSummary();
							StatusController.getInstance().updateFeedBar(lFeed);
						}
						mView.getActionPanel().setText(
								Messages.getString("feedcontrol.addedFeeds",
										Integer.toString(lAdded)));
					}
				}
			}
		});
	}

	public void opmlAborted(OPMLEvent e) {

	}

	public void taskCompleted(TaskEvent e) {
		Object lSrc = e.getSource();
		if (lSrc instanceof IDataHandler) {
			if (((IDataHandler) lSrc).getIndex() == ConfigurationLogic.FEED_INDEX) {
				setFeedView(XPersonalFeedList.getInstance().getFeedArray());
			}
		}
	}

	public void taskAborted(TaskEvent e) {
	}

	public void taskFailed(TaskEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		if (SchedulerLogic.getInstance().getScheduler().getEnabled()) {
			StatusController.getInstance().updateScheduleField(
					SchedulerLogic.getInstance().getScheduler()
							.getTimeRemaining());
		}
	}

	public void configurationChanged(ConfigurationEvent event) {
		// We want to know if the player changes, so we can re-inspect a feed.
		int lLead = mView.getSelectedRow();
		IXPersonalFeed lFeed = null;
		if (lLead >= 0 && lLead < XPersonalFeedList.getInstance().size()) {
			lFeed = XPersonalFeedList.getInstance().getFeed(lLead);
			XFeedLogic.getInstance().collectFeed(FeedController.class, lFeed,
					true);
		}
	}

	public void rpcInvoked(RPCEvent pEvent) {
		// CB No data validation.
		showFeedManager(true,(String)pEvent.getSource());
	}
}