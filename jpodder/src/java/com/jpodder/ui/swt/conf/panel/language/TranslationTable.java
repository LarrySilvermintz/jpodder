package com.jpodder.ui.swt.conf.panel.language;

import org.eclipse.jface.viewers.CellEditor;
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

import com.jpodder.ui.swt.conf.panel.language.TranslationModel.TranslationEntry;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class TranslationTable implements IStructuredContentProvider, ICellModifier {

    protected Table mTable;
    protected TableViewer mTableViewer;
    
    String[] mColumnNames = new String[] { "#",
            Messages.getString("languagepanel.edit.key"),
            Messages.getString("languagepanel.edit.original"),
            Messages.getString("languagepanel.edit.string") };

    
    int[] mColumnWidth = { 25, 250, 250, 250};
    int[] mColumnAlignments = { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT};
    
    protected TranslationModel mModel;
    protected Object[] mTranslations;
    
    protected boolean mModified;
    
    
    public TranslationTable(Composite pParent) {
        mTable = new Table(pParent, SWT.BORDER | SWT.FULL_SELECTION
                | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        mTable.setHeaderVisible(true);
        GridData lData = new GridData(GridData.FILL_BOTH);
        mTable.setLayoutData(lData);
        buildColumns();
        mTableViewer = new TableViewer(mTable);
        mTableViewer.setContentProvider(this);
                
        CellEditor[] editors = new CellEditor[mColumnNames.length];
        editors[0] = new TextCellEditor(mTable);
        editors[1] = new TextCellEditor(mTable);
        editors[2] = new TextCellEditor(mTable);
        editors[3] = new TextCellEditor(mTable);        
        mTableViewer.setCellEditors(editors);
        mTableViewer.setCellModifier(this);
        mTableViewer.setColumnProperties(mColumnNames);
    }
    
    public TableViewer getTableViewer(){
        return mTableViewer;
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
     * Return an array of the define master keys
     */
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof TranslationModel ) {
            mModel = (TranslationModel)inputElement;
            
            mTranslations = mModel.mTranslations.toArray();
            
            return mTranslations;
        }
        return null;
    }

    public void dispose() {

    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
        // Could be used to register a listener on the model.
    }
    
    public int getColIndex(String pName){
        for (int i = 0; i < mColumnNames.length; i++) {
            String lName = mColumnNames[i];
            if(lName.equals(pName)){
                return i;
            }
        }
        return -1;
    }
    
    public boolean canModify(Object arg0, String arg1) {
        int lIndex = getColIndex(arg1);
        if(lIndex == 3){
            return true; 
        }else{
            return false;    
        }
    }
    
    /*
     * Used for editing, see Label provider for the renderer of columns. 
     * 
     */
    public Object getValue(Object arg0, String arg1) {
        int lIndex = getColIndex(arg1);
        if(lIndex == 3){
        	TranslationEntry lEntry = (TranslationEntry)arg0;
        	return lEntry.getTranslation();
        }
    	return null;
    }

    public void modify(Object arg0, String arg1, Object arg2) {
        int lIndex = getColIndex(arg1);
        if(lIndex == 3){
            TableItem lItem = (TableItem)arg0;
            TranslationEntry lEntry = (TranslationEntry)lItem.getData();
            String lKey = lEntry.getKey();
            lEntry.setTranslation((String)arg2);
            mModel.setTransLation(lKey, (String)arg2); 
            mTableViewer.update(lEntry,null);
            mTableViewer.refresh(lKey);
        }
    }
}
