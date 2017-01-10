package com.jpodder.ui.swt.media;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.feeds.IXFile;
import com.jpodder.data.feeds.IXPersonalEnclosure;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class MediaTableView implements IStructuredContentProvider, ICellModifier {

	protected Table mTable;

	protected TableViewer mTableViewer;

	protected MediaView.ModelPreparator mPreparator;

	String[] mColumnNames = { 
			Messages.getString("fileTable.header.index"),
			Messages.getString("fileTable.header.fileName"),
			Messages.getString("fileTable.header.sizeWeb"),
			Messages.getString("fileTable.header.sizeDisc"),
			Messages.getString("fileTable.header.inPlayer"),
			Messages.getString("fileTable.header.date"),
			Messages.getString("fileTable.header.mark"), "" // Dummy column
	};

	int[] mColumnWidth = { 40, 250, 80, 80, 80, 200, 80, 350 };

	int[] mColumnAlignments = { SWT.NONE, SWT.LEFT, SWT.LEFT, SWT.LEFT,
			SWT.CENTER, SWT.LEFT, SWT.CENTER, SWT.CENTER };

	public int getColIndex(String pName) {
		for (int i = 0; i < mColumnNames.length; i++) {
			String lName = mColumnNames[i];
			if (lName.equals(pName)) {
				return i;
			}
		}
		return -1;
	}

	public MediaTableView(Composite pParent) {
		mTable = new Table(pParent, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		mTable.setHeaderVisible(true);
		GridData lData = new GridData(GridData.FILL_BOTH);
		mTable.setLayoutData(lData);
		buildColumns();
		mTableViewer = new TableViewer(mTable);
		mTableViewer.setUseHashlookup(true);
		mTableViewer.setContentProvider(this);
        CellEditor[] editors = new CellEditor[mColumnNames.length];
        editors[0] = new TextCellEditor(mTable);        
        editors[1] = new TextCellEditor(mTable);        
        editors[2] = new TextCellEditor(mTable);        
        editors[3] = new TextCellEditor(mTable);        
        editors[4] = new TextCellEditor(mTable);        
        editors[5] = new TextCellEditor(mTable);
        
        editors[6] = new CheckboxCellEditor(mTable);
        editors[7] = new TextCellEditor(mTable);
        
        
        mTableViewer.setCellEditors(editors);
        mTableViewer.setCellModifier(this);
        mTableViewer.setColumnProperties(mColumnNames);
        mTableViewer.setColumnProperties(mColumnNames);
	}

	/**
	 * Generate the table columns. The column text is aligened in the center.
	 */
	public void buildColumns() {
		for (int i = 0; i < mColumnNames.length; i++) {
			TableColumn lColumn = new TableColumn(mTable, mColumnAlignments[i]);
			lColumn.setText(mColumnNames[i]);
			lColumn.setWidth(mColumnWidth[i]);
		}
	}

	/**
	 * Returns the XFile objects.
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof MediaView.ModelPreparator) {
			mPreparator = (MediaView.ModelPreparator) inputElement;
			return mPreparator.mElements;
		} else {
			if (inputElement == null) {
				return new Object[0];
			}
			throw new IllegalArgumentException();
		}
	}

	public void dispose() {
	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// Could be used to register a listener on the model.
	}

	public boolean canModify(Object arg0, String arg1) {
		int lIndex = getColIndex(arg1);
		if (lIndex == 6) {
			return true;
		} else {
			return false;
		}
	}

	public Object getValue(Object arg0, String arg1) {
		int lIndex = getColIndex(arg1);
		if (lIndex == 6) {
			if (arg0 instanceof IXFile) {
				IXFile pFile = (IXFile) arg0;
				return new Boolean(pFile.isMarked());
			}
		}
		return null;
	}

	public void modify(Object arg0, String arg1, Object arg2) {
		if(arg0 instanceof TableItem){
			TableItem lTableItem  = (TableItem)arg0;
			IXFile lFile = (IXFile)lTableItem.getData();
			if(arg2 instanceof Boolean){
				Boolean lValue = (Boolean)arg2;
				lFile.setMarked(lValue.booleanValue());
				if(lFile instanceof IXPersonalEnclosure ){
					IXPersonalEnclosure lEncl = (IXPersonalEnclosure)lFile;
	                lEncl.getFeed().updateAllCandidates(Configuration
	                        .getInstance().getMarkMax());
//					lEncl.getFeed().updateSingleCandidate(lEncl, Configuration
//	                        .getInstance().getMarkMax());
	                mTableViewer.refresh();
				}else{
					mTableViewer.refresh(lFile);
				}
				
			}
		}
	}
}
