package com.jpodder.ui.swt.id3;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.jpodder.ui.swt.text.InsightControl;
import com.jpodder.util.Messages;

import de.vdheide.mp3.TagContent;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class ID3Table implements IStructuredContentProvider,
		ITableLabelProvider, ICellModifier {

	protected Table mTable;
	protected TableViewer mTableViewer;

	String[] mColumnNames = {
			Messages.getString("id3tagtable.name"),
			Messages.getString("id3tagtable.value"), 
			Messages.getString("id3tagtable.description")};

	int[] mColumnWidth = { 100, 250, 300 };
	private ID3ModelAdapter mAdapter;


	public ID3Table(Composite pParent) {
		mTable = new Table(pParent, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		mTable.setHeaderVisible(true);
		// Assuming the parent layout is a GridData.
		GridData lData = new GridData(GridData.FILL_BOTH);
		mTable.setLayoutData(lData);
		buildColumns();
		mTableViewer = new TableViewer(mTable);
		mTableViewer.setContentProvider(this);
		mTableViewer.setLabelProvider(this);
		// Create the cell editors

		CellEditor[] lEditors = new CellEditor[mColumnNames.length];
		// lEditors[0] = new CheckboxCellEditor(mTable);
		lEditors[0] = null;
		lEditors[1] = new TextCellEditor(mTable);
		mTableViewer.setCellEditors(lEditors);
		mTableViewer.setCellModifier(this);
		mTableViewer.setColumnProperties(mColumnNames);
		
		
		mTableViewer.setSorter(new ViewerSorter(){

			public int compare(Viewer arg0, Object arg1, Object arg2) {
				
				if(arg1 instanceof SpaceHolder){
					return -1;
				}
				
				if((arg1 instanceof String) && (arg2 instanceof String)){
					String lString1 = (String)arg1;
					String lString2 = (String)arg2;
					return lString1.compareTo(lString2);
				}
				
				
				if((arg1 instanceof TagContent) && (arg2 instanceof TagContent)){
					TagContent lContent1 = (TagContent)arg1;
					TagContent lContent2 = (TagContent)arg2;
					
					//  a bit of hack as the index is hard-coded, as we sort on the first column.
					String lText1 = mAdapter.getText(lContent1, 0);
					String lText2 = mAdapter.getText(lContent2, 0);
					return lText1.compareTo(lText2);	
				}	
				return -1;	
			}
		});
		
		TableColumn[] lColumns = mTableViewer.getTable().getColumns();
		mTableViewer.getTable().setSortColumn(lColumns[0]);
		
		InsightControl lControl = new InsightControl( lEditors[1].getControl(), "[I18n]ID3");

	}
	
	class TagSorter extends ViewerSorter {
		
	}
	
	
	

	public void buildColumns() {
		for (int i = 0; i < mColumnNames.length; i++) {
			TableColumn lColumn = new TableColumn(mTable, SWT.LEFT);
			lColumn.setText(mColumnNames[i]);
			lColumn.setWidth(mColumnWidth[i]);
		}
	}

	public TableViewer getTableView() {
		return mTableViewer;
	}

	public void dispose() {

	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

	}

	public Object[] getElements(Object pID3) {
		mAdapter = new ID3ModelAdapter();
		return mAdapter.getID3Model(pID3);
	}

	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	public String getColumnText(Object pTag, int pIndex) {
		return mAdapter.getText(pTag, pIndex);
	}

	public void addListener(ILabelProviderListener arg0) {

	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	public void removeListener(ILabelProviderListener arg0) {

	}

	public boolean canModify(Object arg0, String arg1) {
		if (arg0 instanceof SpaceHolder) {
			return false;
		}
		
		// Should check if the TagContent is a text content. 
		// This fails for pictures. (Picture tags should not be added to this displayable model 
		// in any case. 
		
		switch (indexOf(arg1)) {
		case 0:
			return false;
		case 1:
			return true;
		case 2:
			return false;
		}
		return false;
	}

	public Object getValue(Object pTag, String arg1) {
				
		int lIndex = indexOf(arg1);
		return mAdapter.getText(pTag, lIndex);
		
// CB Remove later, abstracted the model for this call.
//		if (pTag instanceof ID3TagRewrite) {
//			ID3TagRewrite lTag = (ID3TagRewrite) pTag;
//
//			switch () {
//			// case 0:
//			// return new Boolean(true);
//			case 1:
//				return lTag.getValue();
//			}
//		}
//		return null;
	}

	public int indexOf(String pProperty) {
		Object[] lProperties = mTableViewer.getColumnProperties();
		for (int i = 0; i < lProperties.length; i++) {
			Object object = lProperties[i];
			if (((String) object).equals(pProperty)) {
				return i;
			}
		}
		return -1;
	}

	public void modify(Object pTableItem, String arg1, Object arg2) {
		TableItem lItem = (TableItem) pTableItem;
		Object pTag = lItem.getData();
		int lIndex = indexOf(arg1);		
		mAdapter.setValue(pTag, lIndex, (String)arg2);
		mTableViewer.update(pTag, null);
	}

	public List getEditedElements() {
		return mAdapter.getEditedElements();
	}
}