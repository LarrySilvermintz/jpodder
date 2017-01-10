package com.jpodder.ui.swt.download;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.1
 */
import org.eclipse.swt.graphics.Point;

import com.jpodder.data.download.Download;
import com.jpodder.util.Messages;

import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableSortedModel;
import de.kupzog.ktable.renderers.FixedCellRenderer;

/**
 * A custom table model. The model is a collection of XPersonalFeed objects.
 * Some information from each of the collection items is displayed in a sortable
 * table. We use a custom renderer and comparator.
 * 
 */
public class KDownloadTableModel extends KTableSortedModel {

    private int[] mColumnWidths;
    private Object[] mDownloadList;

    private static String[] COLUMN_TOOLTIPS;
    private static String[] COLUMN_NAMES;
    protected static String[] STATUS_NAMES;
    private static int MIN_ROWS = 50;
    private static int ROW_HEIGHT = 20;
    
    private KTableCellRenderer m_FixedRenderer = new FixedCellRenderer(
            FixedCellRenderer.STYLE_PUSH | FixedCellRenderer.INDICATION_SORT
                    | FixedCellRenderer.INDICATION_FOCUS
                    | FixedCellRenderer.INDICATION_CLICKED);

    public KDownloadTableModel(Object[] pDownloadList) {

        mDownloadList = pDownloadList;

        mColumnWidths = new int[] { 40, 100, 100, 300, 60, 60, 100, 50 };

        COLUMN_TOOLTIPS = new String[] {

        }; // TODO, Add Tooltips.

        COLUMN_NAMES = new String[] { Messages.getString("downloadtable.item"),
                Messages.getString("downloadtable.filename"),
                Messages.getString("downloadtable.feed"),
                Messages.getString("downloadtable.progress"),
                Messages.getString("downloadtable.speed"),
                Messages.getString("downloadtable.time"),
                Messages.getString("downloadtable.status"),
                Messages.getString("downloadtable.downloaded") };

        STATUS_NAMES = new String[] {
                Messages.getString("downloadtable.status.connecting"),
                Messages.getString("downloadtable.status.downloading"),
                Messages.getString("downloadtable.status.retrying"),
                Messages.getString("downloadtable.status.error"),
                Messages.getString("downloadtable.status.cancelled"),
                Messages.getString("downloadtable.status.completed"),
                Messages.getString("downloadtable.status.queued"),
                "", // IDLE status
                Messages.getString("downloadtable.status.pauzed"),
                Messages.getString("downloadtable.status.release") };
        initialize();
    }

    public void setSource(Object[] pSource) {
        mDownloadList = pSource;
    }

    /**
     * overridden from superclass We don't have cell editors in this table.
     */
    public KTableCellEditor doGetCellEditor(int col, int row) {
        return null;
    }

    /**
     * Returns a customer text cell renderer for all columns except for the
     * progress column. Here we return a Bar diagram renderer.
     */
    public KTableCellRenderer doGetCellRenderer(int col, int row) {
        if (isHeaderCell(col, row))
            return m_FixedRenderer;
        if (col == 3 && row < mDownloadList.length + 1) {
            return new KDownloadProgressCellRenderer(
                    KDownloadProgressCellRenderer.INDICATION_GRADIENT | KDownloadProgressCellRenderer.INDICATION_FOCUS_ROW);
        }
        return new KDownloadTableRenderer(0);
    }

    /**
     * Content is updated for the first column. We expect a boolean object.
     */
    public void doSetContentAt(int col, int row, Object value) {
    }

    public Object doGetContentAt(int col, int row) {
        if (row == 0) {
            return COLUMN_NAMES[col];
        } else {
            if (row < mDownloadList.length + 1) {
                Download lDownload = (Download) mDownloadList[row - 1];
                if (col == 3) {
                    int lProgress = lDownload.getCurrent();
                    int lLength = lDownload.getLengthOfTask();
                    float lTemp = ((float) lProgress / (float) lLength);
                    return new Float(lTemp);
                } else {
                    return lDownload;
                }

            } else {
                return new TableSpaceHolder(row - 1);
            }
        }
    }

    /**
     * We had a MIN_ROWS number of rows to display a table with empty rows.
     * (Just looks nicer). Empty rows return a <code>TableSpaceHolder</code>
     * which is detected when redering to render an empty row.
     */
    public int doGetRowCount() {
        return mDownloadList.length + MIN_ROWS;
    }

    /**
     * Number of rows which represent the header.
     */
    public int getFixedHeaderRowCount() {
        return 1;
    }

    /**
     * This table contains 4 columns
     */
    public int doGetColumnCount() {
        return 8;
    }

    /**
     * No Column Header.
     */
    public int getFixedHeaderColumnCount() {
        return 0;
    }

    /**
     * 
     */
    public int doGetColumnWidth(int col) {
        return mColumnWidths[col];
    }

    /**
     */
    public boolean isColumnResizable(int col) {
        return (col != 0);
    }

    /**
     */
    public void setColumnWidth(int col, int value) {
        if (value > 120)
            mColumnWidths[col] = value;
    }

    /**
     * We don't allow resizing of rows.
     */
    public boolean isRowResizable(int row) {
        return false;
    }

    /**
     * Not applicable, when row is not resizable.
     */
    public int getRowHeightMinimum() {
        return ROW_HEIGHT - 8;
    }

    /**
     * Not applicable, when row is not resizable.
     */
    public void setRowHeight(int row, int value) {
        // Ignore
    }

    public Point belongsToCell(int col, int row) {
        return null;
    }

    /**
     * Get the toolip for a cell.
     */
    public String doGetTooltipAt(int col, int row) {
        // if (row == 0) {
        // return COLUMN_TOOLTIPS[col];
        // }
        return null;
    }

    public int getFixedSelectableRowCount() {
        // all fixed rows are non-selectable.
        return 0;
    }

    public int getFixedSelectableColumnCount() {
        // all fixed columns are non-selctable.
        return 0;
    }

    /**
     * An empty space holder for the tableviewer to be recognized by its type
     * and return an empty label.
     */
    public class TableSpaceHolder {
        int mIndex;

        public TableSpaceHolder(int pIndex) {
            mIndex = pIndex;
        }
    }

    public int getInitialColumnWidth(int column) {
        return doGetColumnWidth(column);
    }

    public int getInitialRowHeight(int row) {
        if (row == 0)
            return ROW_HEIGHT;
        return ROW_HEIGHT;
    }

}
