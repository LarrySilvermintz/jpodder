package com.jpodder.ui.swt.media;

import java.io.File;
import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.jpodder.data.feeds.IXItem;
import com.jpodder.data.feeds.IXPersonalEnclosure;
import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.XFeedException;
import com.jpodder.data.feeds.XItemException;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Messages;
import com.jpodder.util.Util;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class MediaTreeView implements ITreeContentProvider, ITableLabelProvider,
		ITableColorProvider {

	Logger mLog = Logger.getLogger(getClass().getName());

	protected Object[] mElements;

	protected Tree mTree;

	protected TreeViewer mTreeViewer;

	protected MediaView.ModelPreparator mPreparator;

	protected boolean mShowItems = true;

	public MediaTreeView(Composite pParent) {

		mTree = new Tree(pParent, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		mTree.setHeaderVisible(true);

		// mTree.setLinesVisible(true);
		GridData lData = new GridData(GridData.FILL_BOTH);
		lData.grabExcessHorizontalSpace = true;
		lData.grabExcessVerticalSpace = true;
		mTree.setLayoutData(lData);

		mTreeViewer = new TreeViewer(mTree);
		mTreeViewer.setContentProvider(this);
		mTreeViewer.setLabelProvider(this);
		// mTreeViewer.setAutoExpandLevel(2);
		buildColumns();
	}

	public void buildColumns() {
		for (int i = 0; i < getColumnCount(); i++) {
			TreeColumn lColumn = new TreeColumn(mTree, SWT.LEFT);
			lColumn.setWidth(getColumnWidth(i));
			lColumn.setText(getColumnName(i));
		}
	}

	/**
	 * Should be invoked before setting the input.
	 */
	public void setItemsVisible(boolean pVisible) {
		mShowItems = pVisible;
	}

	/**
	 * The number of columns. This is a fix number.
	 * 
	 * @return int
	 */
	public int getColumnCount() {
		return 6;
	}

	String[] mColumnNames = { Messages.getString("fileTable.header.title"),
			Messages.getString("fileTable.header.date"),
			Messages.getString("fileTable.header.sizeWeb"),
			Messages.getString("fileTable.header.sizeDisc"),
			Messages.getString("fileTable.header.inPlayer"),
			Messages.getString("fileTable.header.mark") };

	public String getColumnName(int col) {
		return mColumnNames[col];
	}

	int[] mColumnWidths = { 450, 200, 80, 80, 80, 80 };

	/**
	 * Gets the columns width.
	 * 
	 * @param col
	 *            int
	 * @return int
	 */
	public int getColumnWidth(int col) {
		return mColumnWidths[col];
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IXItem) {
			IXItem lItem = (IXItem) parentElement;
			if (mPreparator != null) {
				// We get the personal enclosure instead, much better.
				IXPersonalEnclosure lEncl = mPreparator.getEnclosure(lItem);
				return new Object[] { lEncl };
			} else {
				return null;
			}
		}
		return null;
	}

	public Object getParent(Object element) {
		return null; // Parent not used.
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IXItem) {
			IXItem lItem = (IXItem) element;
			if (mPreparator.getEnclosure(lItem) != null) {
				return true;
			}
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof MediaView.ModelPreparator) {
			mPreparator = (MediaView.ModelPreparator) inputElement;
			IXPersonalFeed lFeed = mPreparator.getFeed();
			try {
				if (mShowItems) {
					mElements = lFeed.getItemArray();
				} else {
					mElements = lFeed.getEnclosureArray();
				}
				return mElements;
			} catch (XFeedException e) {
				mLog.warn("getElements(), the feed model is not accessible");
				return new Object[0];
			}
		} else {
			if (inputElement == null) {
				return new Object[0];
			}
			throw new IllegalArgumentException();
		}
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void scrollToFirstRow() {
		if (mElements.length > 0) {
			mTreeViewer.setSelection(new StructuredSelection(mElements[0]),
					true);
		}
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof IXPersonalEnclosure) {
			IXPersonalEnclosure lEnclosure = (IXPersonalEnclosure) element;
			switch (columnIndex) {
			case 0: {
				if (this.mPreparator != null) {
					return mPreparator.getImage(lEnclosure);
				}
			}
			}
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {

		if (element instanceof IXItem) {
			IXItem lItem = (IXItem) element;
			switch (columnIndex) {
			case 0: {
				try {
					return lItem.getTitle();
				} catch (XItemException e) {
					return "No Title";
				}
			}

			case 1: {

				Date lDate;
				try {
					String lDateString = lItem.getPubDate();
					lDate = Util.resolvedDateRFC822(lDateString);
					return Util.formatDate(lDate, "EEEE, MMM d hh:mm a");
				} catch (ParseException e) {
				} catch (IllegalArgumentException iae) {
				} catch (XItemException e) {
				}
			}
			}
		}

		if (element instanceof IXPersonalEnclosure) {
			IXPersonalEnclosure lEncl = (IXPersonalEnclosure) element;
			Object lObj = getValueAt(lEncl, columnIndex);
			if (lObj instanceof String) {
				return (String) lObj;
			}
			if (lObj instanceof Boolean) {
				if (((Boolean) lObj).booleanValue()) {
					return "Yes";
				} else {
					return "No";
				}
			}
			if (lObj instanceof Integer) {
				String form = com.jpodder.util.Util.formatSize(lObj);
				return ((Number) lObj).intValue() == 0 ? "" : form;
			}
		}

		return null;
	}

	public Object getValueAt(IXPersonalEnclosure pEnclosure, int column) {
		Object lReturn = "";
		switch (column) {
		case 0: {
			File lFile = pEnclosure.getFile();
			if (lFile != null) {
				lReturn = lFile.getName();
			} else {
				return pEnclosure.getPersonalURL().toExternalForm();
			}
			break;
		}
		case 1: {
		}
			break;
		case 2: { // Size web

			lReturn = new Integer(pEnclosure.getFileLength());
			break;
		}
		case 3: {// Size disk
			File lFile = pEnclosure.getFile();
			if (lFile != null) {
				lReturn = new Integer((int) lFile.length());
			} else {
				lReturn = "?";
			}
			break;
		}
		case 4: { // If in player.
			lReturn = new Boolean(pEnclosure.getInPlayer());
			break;
		}
		case 5: {
			lReturn = new Boolean(pEnclosure.isMarked());
			break;
		}
		case 6: {
			break;
		}
		default:
			lReturn = "";
		}

		return lReturn;
	}

	public Color getForeground(Object element, int columnIndex) {
		if (element instanceof IXItem) {
			if (columnIndex == 0) {
				return UITheme.getInstance().FEED_TITLE_COLOR;
			}
		}
		return null;
	}

	public Color getBackground(Object element, int columnIndex) {
		if (element instanceof IXPersonalEnclosure) {
			return UITheme.getInstance().TABLE_ODD_BACKGROUND_COLOR;
		}
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}
}