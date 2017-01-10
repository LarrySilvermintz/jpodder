package com.jpodder.ui.swt.feeds;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.IView;
import com.jpodder.ui.swt.comp.BaseAction;
import com.jpodder.ui.swt.media.MediaView;
import com.jpodder.util.Messages;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.SWTX;

/**
 * This class sets up the Feed View and interacts with the main application
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @version 1.1
 */
public class FeedsView implements IView {

	protected Button mDownloadButton;

	protected Button mPreviewFeedButton;

	protected MenuItem mFeedMenu;

	protected MenuItem mFeedImportMenu;

	protected MenuItem mFeedExportMenu;

	protected MenuItem mFeedPreviewMenu;

	protected MenuItem mFeedPreviewAllMenu;

	protected MenuItem mFeedDownloadMenu;

	protected MenuItem mFeedAddMenu;

	protected MenuItem mFeedRemoveMenu;

	protected MenuItem mFeedEditMenu;

	protected MenuItem mFeedUpMenu;

	protected MenuItem mFeedDownMenu;

	protected MenuItem mToolsMenu;

	protected MenuItem mToolsLookUpURLMenu;

	protected MenuItem mToolsAddLocalMenu;

	protected MenuItem mToolsCreatePlaylistMenu;

	protected Menu mFeedPopupMenu;

	protected MenuItem mFeedAddPopupMenu;

	protected MenuItem mPreviewFeedPopupMenu;

	protected MenuItem mPreviewAllFeedPopupMenu;

	protected MenuItem mEditFeedPopupMenu;

	protected MenuItem mRemoveFeedPopupMenu;

	protected MenuItem mToolsSyncPopupMenu;

	protected KTable mFeedTable;

	protected Composite mView;

	protected SashForm mSashForm;

	protected FeedInfoView mInfoView;

	private Logger mLog = Logger.getLogger(getClass().getName());

	private Label mActionLabel;

	private MediaView mFilesView;

	/**
	 * Constructor. Defines the actions in this controller.
	 * 
	 * @param App
	 *            MainUI
	 */
	public FeedsView() {
		mLog.info("<init>");
		initialize();
	}

	public void initialize() {

		// mFilesView = new FilesView();
		mView = new Composite(UILauncher.getInstance().getShell(), SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginBottom = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		mView.setLayout(gridLayout);

		// ------------- The ButtonComposite

		Group lButtonGroup = new Group(mView, SWT.SHADOW_ETCHED_IN);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = true;
		lButtonGroup.setLayoutData(gridData);

		RowLayout lButtonRowLayout = new RowLayout(SWT.HORIZONTAL);
		lButtonRowLayout.wrap = false;
		lButtonRowLayout.pack = false;

		lButtonGroup.setLayout(lButtonRowLayout);

		mDownloadButton = new Button(lButtonGroup, SWT.PUSH);
		mDownloadButton.setToolTipText(Messages
				.getString("gui.tooltip.download"));
		mPreviewFeedButton = new Button(lButtonGroup, SWT.PUSH);
		mActionLabel = new Label(lButtonGroup, SWT.NONE);
		RowData lRowData = new RowData();
		lRowData.exclude = true;
		lRowData.width = 300;
		mActionLabel.setLayoutData(lRowData);

		mSashForm = new SashForm(mView, SWT.VERTICAL);
		mSashForm.setLayout(new FillLayout());
		mSashForm.setOrientation(SWT.VERTICAL);
		gridData = new GridData(GridData.FILL_BOTH);
		mSashForm.setLayoutData(gridData);

		SashForm mFeedSashForm = new SashForm(mSashForm, SWT.HORIZONTAL);
		GridLayout lFeedLayout = new GridLayout();
		lFeedLayout.numColumns = 1;
		lFeedLayout.marginLeft = 0;
		lFeedLayout.marginRight = 0;
		lFeedLayout.marginTop = 0;
		lFeedLayout.marginBottom = 0;
		mFeedSashForm.setLayout(lFeedLayout);

		gridData = new GridData(GridData.FILL_BOTH);
		mFeedSashForm.setLayoutData(gridData);

		mFeedTable = new KTable(mFeedSashForm, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI | SWTX.AUTO_SCROLL | SWTX.FILL_WITH_LASTCOL);
		gridData = new GridData(GridData.FILL_BOTH);
		mFeedTable.setLayoutData(gridData);

		mFeedTable.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				mLog.debug("Menuevent at: " + event.x + event.y);
				mFeedPopupMenu.setLocation(event.x, event.y);
				mFeedPopupMenu.setVisible(true);
				while (!mFeedPopupMenu.isDisposed()
						&& mFeedPopupMenu.isVisible()) {
					if (!Display.getDefault().readAndDispatch())
						Display.getDefault().sleep();
				}
				// mFeedPopupMenu.dispose();
			}
		});

		mInfoView = new FeedInfoView(mFeedSashForm);

		// --- Feed Menu ----------------------------------------------

		mFeedMenu = new MenuItem(UILauncher.lWindowUI.lMainMenu, SWT.CASCADE, 1);
		mFeedMenu.setText(Messages.getString("gui.menu.feeds"));
		mFeedMenu.setAccelerator(SWT.CTRL + 'd');
		mFeedMenu
				.setMenu(new Menu(UILauncher.getInstance().getShell(), SWT.DROP_DOWN));
		mFeedImportMenu = new MenuItem(mFeedMenu.getMenu(), SWT.PUSH);
		mFeedExportMenu = new MenuItem(mFeedMenu.getMenu(), SWT.PUSH);
		new MenuItem(mFeedMenu.getMenu(), SWT.SEPARATOR);
		mFeedPreviewMenu = new MenuItem(mFeedMenu.getMenu(), SWT.PUSH);
		mFeedPreviewAllMenu = new MenuItem(mFeedMenu.getMenu(), SWT.PUSH);
		mFeedDownloadMenu = new MenuItem(mFeedMenu.getMenu(), SWT.PUSH);
		new MenuItem(mFeedMenu.getMenu(), SWT.SEPARATOR);
		mFeedAddMenu = new MenuItem(mFeedMenu.getMenu(), SWT.PUSH);
		mFeedEditMenu = new MenuItem(mFeedMenu.getMenu(), SWT.PUSH);
		mFeedRemoveMenu = new MenuItem(mFeedMenu.getMenu(), SWT.PUSH);
		new MenuItem(mFeedMenu.getMenu(), SWT.SEPARATOR);
		mFeedUpMenu = new MenuItem(mFeedMenu.getMenu(), SWT.PUSH);
		mFeedDownMenu = new MenuItem(mFeedMenu.getMenu(), SWT.PUSH);

		// --- Tools Menu ----------------------------------------------

		mToolsMenu = new MenuItem(UILauncher.lWindowUI.lMainMenu, SWT.CASCADE, 2);
		mToolsMenu.setText(Messages.getString("gui.menu.tools"));
		mToolsMenu.setAccelerator(SWT.CTRL + 't');
		mToolsMenu.setMenu(new Menu(UILauncher.getInstance().getShell(),
				SWT.DROP_DOWN));

		mToolsCreatePlaylistMenu = new MenuItem(mToolsMenu.getMenu(), SWT.PUSH);
		new MenuItem(mToolsMenu.getMenu(), SWT.SEPARATOR);
		mToolsLookUpURLMenu = new MenuItem(mToolsMenu.getMenu(), SWT.PUSH);
		mToolsAddLocalMenu = new MenuItem(mToolsMenu.getMenu(), SWT.PUSH);

		// --- Popup Menu ---------------------------------------------

		mFeedPopupMenu = new Menu(UILauncher.getInstance().getShell(), SWT.POP_UP);
		mPreviewFeedPopupMenu = new MenuItem(mFeedPopupMenu, SWT.PUSH);
		mPreviewAllFeedPopupMenu = new MenuItem(mFeedPopupMenu, SWT.PUSH);
		new MenuItem(mFeedPopupMenu, SWT.SEPARATOR);
		mFeedAddPopupMenu = new MenuItem(mFeedPopupMenu, SWT.PUSH);
		mEditFeedPopupMenu = new MenuItem(mFeedPopupMenu, SWT.PUSH);
		mRemoveFeedPopupMenu = new MenuItem(mFeedPopupMenu, SWT.PUSH);
	}

	public MediaView getFilesView() {
		return mFilesView;
	}

	public Composite getLowerSash() {
		return this.mSashForm;
	}

	// -------------
	// Convenience classes for friendly methods for getting and updating
	// rows in the Feed Table.

	/**
	 * Return an array of selected rows.
	 * 
	 * @return
	 */
	public int[] getSelectedRows() {

		KFeedTableModel lModel = (KFeedTableModel) mFeedTable.getModel();
		int[] lSelection = mFeedTable.getRowSelection();
		int[] lReturn = new int[lSelection.length];
		for (int i = 0; i < lSelection.length; i++) {
			lReturn[i] = lModel.mapRowIndexToModel(lSelection[i]);
		}
		return lReturn;
	}

	/**
	 * Return the first selected row.
	 * 
	 * @return Get the selected row index.
	 */
	public int getSelectedRow() {
		KFeedTableModel lModel = (KFeedTableModel) mFeedTable.getModel();
		int[] lSelection = mFeedTable.getRowSelection();
		if (lSelection == null || lSelection.length == 0) {
			return -1;
		}
		int lFirstRow = lSelection[0];
		if (!lModel.isHeaderCell(0, lFirstRow)) {
			return (lModel.mapRowIndexToModel(lFirstRow) - 1);
		} else {
			return -1;
		}
	}

	/**
	 * Select a row.
	 * 
	 * @param pRow
	 */
	public void setSelectedRow(int pRow) {
		KFeedTableModel lModel = (KFeedTableModel) mFeedTable.getModel();
		int lViewRow = lModel.mapRowIndexToTable(pRow);
		mFeedTable.setSelection(0, lViewRow, true);
	}

	/**
	 * Select multiple rows.
	 * 
	 * @return
	 */
	public void setSelectedRows(int[] pRows) {
		KFeedTableModel lModel = (KFeedTableModel) mFeedTable.getModel();
		Point[] lSelection = new Point[pRows.length];
		for (int i = 0; i < pRows.length; i++) {
			int j = pRows[i];
			int lViewRow = lModel.mapRowIndexToTable(j);
			lSelection[i] = new Point(0,lViewRow);
		}
		mFeedTable.setSelection(lSelection, true);
		
	}

	/**
	 * Update a row.
	 * 
	 * @return
	 */
	public void updateRow(int pRow) {
		KFeedTableModel lModel = (KFeedTableModel) mFeedTable.getModel();
		int lViewRow = lModel.mapRowIndexToTable(pRow);
		for (int k = 0; k < lModel.getColumnCount(); k++) {
			mFeedTable.updateCell(k, lViewRow);
		}
	}

	/**
	 * Update an array of rows.
	 * 
	 * @return
	 */
	public void updateRows(int[] pRows) {
		KFeedTableModel lModel = (KFeedTableModel) mFeedTable.getModel();
		for (int i = 0; i < pRows.length; i++) {
			int j = pRows[i];
			int lViewRow = lModel.mapRowIndexToTable(j);
			for (int k = 0; k < lModel.getColumnCount(); k++) {
				mFeedTable.updateCell(k, lViewRow);
			}
		}
	}

	public void updateTable() {
		KFeedTableModel lModel = (KFeedTableModel) mFeedTable.getModel();
		int lRows = lModel.getRowCount();
		for (int i = 0; i < lRows; i++) {
			for (int k = 0; k < lModel.getColumnCount(); k++) {
				mFeedTable.updateCell(k, i);
			}
		}
	}

	public Label getActionPanel() {
		return mActionLabel;
	}

	public Composite getView() {
		return mView;
	}

	/**
	 * A context sensitive mouse adapter, depending on the
	 * clicked item.
	 *
	 */
	public class FeedMouseAdapter extends MouseAdapter {

		private BaseAction mDefaultOnElementAction;

		private BaseAction mDefaultNoElementAction;

		private Logger mLog = Logger.getLogger(getClass().getName());

		public FeedMouseAdapter(BaseAction pDefaultOnElementAction,
				BaseAction pDefaultNoElementAction) {
			if (mLog.isDebugEnabled()) {
				mLog.info("<init>DOEA: " + pDefaultOnElementAction + ", DNEA: "
						+ pDefaultNoElementAction);
			}
			mDefaultOnElementAction = pDefaultOnElementAction;
			mDefaultNoElementAction = pDefaultNoElementAction;
		}
		
		
		
		public void mouseDoubleClick(MouseEvent e) {
			
			int lState = e.stateMask;
			
			super.mouseDoubleClick(e);
			
			Point lCol = mFeedTable.calcColumnNum(e.x, e.y);
			Object lContent = mFeedTable.getModel()
					.getContentAt(lCol.x, lCol.y);
			if (lContent instanceof KFeedTableModel.TableSpaceHolder) {
				if (mDefaultNoElementAction != null) {
					if (mLog.isDebugEnabled()) {
						mLog.info("mouseDoubleClick(), no element found");
						mLog.info("mouseDoubleClick(), invoke action: "
								+ mDefaultNoElementAction);
					}
					mDefaultNoElementAction.handleEvent(null);
				}
			} else {
				if (mDefaultOnElementAction != null) {
					if (mLog.isDebugEnabled()) {
						mLog.info("mouseDoubleClick(), element found on row: "
								+ lCol.y);
						mLog.info("mouseDoubleClick(), invoke action: "
								+ mDefaultOnElementAction);
					}
					mDefaultOnElementAction.handleEvent(null);
				}
			}
		}

		public void mouseDown(MouseEvent e) {
			super.mouseDown(e);
		}

		public void mouseUp(MouseEvent e) {
			super.mouseUp(e);
		}
	}

	public boolean isStatic() {
		return true;
	}

	public void setStatic(boolean pStatic) {
		// This view is hardcoded to be static
	}
}