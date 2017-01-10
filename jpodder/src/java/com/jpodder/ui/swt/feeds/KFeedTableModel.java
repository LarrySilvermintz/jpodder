package com.jpodder.ui.swt.feeds;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @version 1.1
 */
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.util.Messages;

import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableSortedModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.editors.KTableCellEditorCheckbox2;
import de.kupzog.ktable.renderers.CheckableCellRenderer;
import de.kupzog.ktable.renderers.FixedCellRenderer;

/**
 * A custom table model. The model is a collection of XPersonalFeed objects.
 * Some information from each of the collection items is displayed in a sortable
 * table. We use a custom renderer and comparator.
 * 
 */
public class KFeedTableModel extends KTableSortedModel {

	private int[] mColWidths;

	Object[] mFeedList;

	private static String[] COLUMN_TOOLTIPS;

	private static String[] COLUMN_NAMES;

	private static int MIN_ROWS = 50;

	private KTableCellRenderer m_FixedRenderer = new FixedCellRenderer(
			FixedCellRenderer.STYLE_PUSH | FixedCellRenderer.INDICATION_SORT
					| FixedCellRenderer.INDICATION_FOCUS
					| FixedCellRenderer.INDICATION_CLICKED);

	// private KTableCellRenderer m_CheckableRenderer = new
	// CheckableCellRenderer(
	// CheckableCellRenderer.INDICATION_CLICKED
	// | CheckableCellRenderer.INDICATION_FOCUS);

	public KFeedTableModel(Object[] pFeedList) {

		mFeedList = pFeedList;

		mColWidths = new int[getColumnCount()];
		mColWidths[0] = 40;
		mColWidths[1] = 250;
		mColWidths[2] = 50;
		mColWidths[3] = 50;

		COLUMN_TOOLTIPS = new String[] {
				Messages.getString("feedTable.tooltip.subscribed"),
				Messages.getString("feedTable.tooltip.feed"),
				Messages.getString("feedTable.tooltip.max"),
				Messages.getString("feedTable.tooltip.quality") };
		COLUMN_NAMES = new String[] {
				Messages.getString("feedTable.header.subscribed"),
				Messages.getString("feedTable.header.feed"),
				Messages.getString("feedTable.header.max"),
				Messages.getString("feedTable.header.quality") };
		initialize();
	}

	public void updateModel(Object[] pFeedList) {
		mFeedList = pFeedList;
	}

	/**
	 * overridden from superclass
	 */
	public KTableCellEditor doGetCellEditor(int col, int row) {

		if (!isHeaderCell(col, row)) {
			if (row < mFeedList.length && col == 2) {
				return new KFeedCellEditorSpinner();
			}
			if (row < mFeedList.length && col == 0) {
				Rectangle imgBounds = CheckableCellRenderer.IMAGE_CHECKED
						.getBounds();
				Point lSensibleArea = new Point(imgBounds.width,
						imgBounds.height);
				return new KTableCellEditorCheckbox2(lSensibleArea,
						SWTX.ALIGN_HORIZONTAL_CENTER,
						SWTX.ALIGN_VERTICAL_CENTER);
			}
		}
		return null;
	}

	/**
	 * Column 0 render is a standard checktable renderer. The other columns
	 * require specific rendering and therfor use a custom cell renderer.
	 */
	public KTableCellRenderer doGetCellRenderer(int col, int row) {
		if (isHeaderCell(col, row))
			return m_FixedRenderer;
		if (col == 0 && row < mFeedList.length + 1) {
			return new KFeedCheckCellRenderer(
					CheckableCellRenderer.INDICATION_CLICKED
							| CheckableCellRenderer.INDICATION_FOCUS
							| CheckableCellRenderer.SIGN_CHECK);
		}

		return new KFeedTableRenderer(0);
	}

	/**
	 * Content is updated for the first column. We expect a boolean object.
	 */
	public void doSetContentAt(int col, int row, Object value) {
		if (col == 0) {
			if (value instanceof Boolean) {
				int lModelIndex = mapRowIndexToModel(row - 1);
				if (lModelIndex >= 0 && lModelIndex < mFeedList.length) {
					IXPersonalFeed lFeed = (IXPersonalFeed) mFeedList[lModelIndex];
					lFeed.setSubscribed(((Boolean) value).booleanValue());
				}
			}
		}
	}

	/**
	 * We had a MIN_ROWS number of rows to display a table with empty rows.
	 * (Just looks nicer). Empty rows return a <code>TableSpaceHolder</code>
	 * which is detected when redering to render an empty row.
	 */
	public int doGetRowCount() {
		return mFeedList.length + MIN_ROWS;
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
		return 4;
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
		return mColWidths[col];
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
			mColWidths[col] = value;
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
		return 30;
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
		if (row == 0) {
			return COLUMN_TOOLTIPS[col];
		}
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
			return 20;
		return 30;
	}

	public Object doGetContentAt(int col, int row) {
		if (row == 0) {
			return COLUMN_NAMES[col];
		} else {
			if (row < mFeedList.length + 1) {
				IXPersonalFeed lFeed = (IXPersonalFeed) mFeedList[row - 1];
				if (col == 0) {
					return new Boolean(lFeed.getPoll());
				}
				return lFeed;
			} else {
				return new TableSpaceHolder(row - 1);
			}
		}
	}
}
