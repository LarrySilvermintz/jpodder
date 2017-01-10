package com.jpodder.ui.swt.download;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.IView;
import com.jpodder.util.Messages;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.SWTX;

/**
 * This class sets up the Feed View and interacts with the main application
 * 
 * @version 1.1
 */
public class DownloadsView implements IView {

    protected Button mAbortSelectedButton;
    protected Button mPauzeResumeAllButton;
    protected Button mCleanAllCompletedButton;

    protected MenuItem mDownloadMenu;
    protected MenuItem mPauzeResumeAllMenu;
    protected MenuItem mCleanAllCompletedMenu;
    protected MenuItem mAbortSelectedMenu;
    // CB Not implemented.
    // protected MenuItem mDownloadRetryDownloadMenu;
    // protected MenuItem mDownloadGotoFeedMenu;

    protected Menu mDownloadPopupMenu;
    protected MenuItem mRetryPopupMenu;
    protected MenuItem mGotoFeedPopupMenu;

    protected KTable mDownloadTable;
    protected Composite mView;

    protected Group lButtonGroup;

    private Logger mLog = Logger.getLogger(getClass().getName());

    /**
     * Constructor. Defines the actions in this controller.
     * 
     * @param App
     *            MainUI
     */
    public DownloadsView() {
        mLog.info("<init>");
        initialize();
    }

    public void initialize() {

        mView = new Composite(UILauncher.getInstance().getShell(), SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        mView.setLayout(gridLayout);

        // ------------- The ButtonComposite

        lButtonGroup = new Group(mView, SWT.SHADOW_IN);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = true;
        lButtonGroup.setLayoutData(gridData);

        RowLayout lButtonRowLayout = new RowLayout(SWT.HORIZONTAL);
        lButtonRowLayout.wrap = false;
        lButtonRowLayout.pack = true;
        lButtonGroup.setLayout(lButtonRowLayout);
        mAbortSelectedButton = new Button(lButtonGroup, SWT.PUSH);
        mPauzeResumeAllButton = new Button(lButtonGroup, SWT.PUSH);
        mPauzeResumeAllButton.setToolTipText(Messages
                .getString("gui.tooltip.download"));
        mCleanAllCompletedButton = new Button(lButtonGroup, SWT.PUSH);

        // -------------- Download table.

        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.VERTICAL_ALIGN_FILL);
        // gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        // lMiddleComposite.setLayoutData(gridData);
        // lMiddleComposite.setLayout(new FillLayout());
        mDownloadTable = new KTable(mView, SWT.FULL_SELECTION | SWT.MULTI
                | SWTX.AUTO_SCROLL | SWTX.FILL_WITH_LASTCOL);
        mDownloadTable.setLayoutData(gridData);
        mDownloadTable.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {
                mLog.info("Menuevent at: " + event.x + event.y);
                mDownloadPopupMenu.setLocation(event.x, event.y);
                mDownloadPopupMenu.setVisible(true);
                while (!mDownloadPopupMenu.isDisposed()
                        && mDownloadPopupMenu.isVisible()) {
                    if (!Display.getDefault().readAndDispatch())
                        Display.getDefault().sleep();
                }
            }
        });

        // --- Download Menu ----------------------------------------------

        mDownloadMenu = new MenuItem(UILauncher.lWindowUI.lMainMenu, SWT.CASCADE, 4);
        mDownloadMenu.setText(Messages.getString("gui.menu.downloads"));
        mDownloadMenu.setAccelerator(SWT.CTRL + 'd');
        mDownloadMenu.setMenu(new Menu(UILauncher.getInstance().getShell(),
                SWT.DROP_DOWN));

        mPauzeResumeAllMenu = new MenuItem(mDownloadMenu.getMenu(),
                SWT.PUSH);
        mAbortSelectedMenu = new MenuItem(mDownloadMenu.getMenu(),
                SWT.PUSH);
        mCleanAllCompletedMenu = new MenuItem(mDownloadMenu.getMenu(),
                SWT.PUSH);

        // --- Popup Menu ---------------------------------------------

        mDownloadPopupMenu = new Menu(UILauncher.getInstance().getShell(), SWT.POP_UP);
        mRetryPopupMenu = new MenuItem(mDownloadPopupMenu, SWT.PUSH);
        mGotoFeedPopupMenu = new MenuItem(mDownloadPopupMenu, SWT.PUSH);
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

        KDownloadTableModel lModel = (KDownloadTableModel) mDownloadTable
                .getModel();
        int[] lSelection = mDownloadTable.getRowSelection();
        int[] lReturn = new int[lSelection.length];
        for (int i = 0; i < lSelection.length; i++) {
            lReturn[i] = lModel.mapRowIndexToModel(lSelection[i] - 1);
        }
        return lReturn;
    }

    /**
     * Return the first selected row.
     * 
     * @return Get the selected row index.
     */
    public int getSelectedRow() {
        KDownloadTableModel lModel = (KDownloadTableModel) mDownloadTable
                .getModel();
        int[] lSelection = mDownloadTable.getRowSelection();
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
        KDownloadTableModel lModel = (KDownloadTableModel) mDownloadTable
                .getModel();
        int lViewRow = lModel.mapRowIndexToTable(pRow);
        mDownloadTable.setSelection(0, lViewRow, true);
    }

    /**
     * Select multiple rows.
     * 
     * @return
     */
    public void setSelectedRows(int[] pRows) {
        KDownloadTableModel lModel = (KDownloadTableModel) mDownloadTable
                .getModel();
        for (int i = 0; i < pRows.length; i++) {
            int j = pRows[i];
            int lViewRow = lModel.mapRowIndexToTable(j);
            mDownloadTable.setSelection(0, lViewRow, true);
        }
    }

    /**
     * Update a row.
     * 
     * @return
     */
    public void updateRow(int pRow) {
        KDownloadTableModel lModel = (KDownloadTableModel) mDownloadTable
                .getModel();
        int lViewRow = lModel.mapRowIndexToTable(pRow);
        for (int k = 0; k < lModel.getColumnCount(); k++) {
            mDownloadTable.updateCell(k, lViewRow);
        }
    }

    /**
     * Update an array of rows.
     * 
     * @return
     */
    public void updateRows(int[] pRows) {
        KDownloadTableModel lModel = (KDownloadTableModel) mDownloadTable
                .getModel();
        for (int i = 0; i < pRows.length; i++) {
            int j = pRows[i];
            int lViewRow = lModel.mapRowIndexToTable(j);
            for (int k = 0; k < lModel.getColumnCount(); k++) {
                mDownloadTable.updateCell(k, lViewRow);
            }
        }
    }

    public Composite getView() {
        return mView;
    }

    public boolean isStatic() {
        return true;
    }

    public void setStatic(boolean pStatic) {
        // this is always static.
    }
}