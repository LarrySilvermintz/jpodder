package com.jpodder.ui.swt.comp;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Messages;

/**
 * A dialog containing a table, OK and Cancel button. 
 * The table's first column is checked, the other columns are provided.
 */
public class CheckTableDialog {

    protected Button okButton;
    protected Button cancelButton;
    protected Table mTable;

    public static int OK_SELECTED = 501;
    public static int CANCEL_SELECTED = 502;
    protected int result;
    protected List mItems;
    private Shell mShell;

    /**
     */
    public CheckTableDialog(String pTitle) {

        mShell = new Shell(UILauncher.getInstance().getShell(), SWT.DIALOG_TRIM 
                | SWT.RESIZE);
        mShell.setImage(UITheme.getInstance().getImages().get(UITheme.IMAGE_JPODDER));
        mShell.setText(pTitle);
        
        GridLayout lLayout = new GridLayout();
        lLayout.numColumns = 1;
        mShell.setLayout(lLayout);

        mTable = new Table(mShell, SWT.CHECK);
        mTable.setLinesVisible(false);
        mTable.setHeaderVisible(true);
        GridData lData = new GridData(GridData.FILL_BOTH);
        mTable.setLayoutData(lData);

        TableColumn lColumn0 = new TableColumn(mTable, SWT.LEFT, 0);
        lColumn0.setWidth(300);

        Composite buttonPanel = new Composite(mShell, SWT.NONE);
        RowLayout lButtonLayout = new RowLayout();
        buttonPanel.setLayout(lButtonLayout);
        lData = new GridData(GridData.FILL_HORIZONTAL);
        buttonPanel.setLayoutData(lData);

        okButton = new Button(buttonPanel, SWT.PUSH);
        okButton.setText(Messages.getString("general.ok"));

        okButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                result = OK_SELECTED;
                mItems = read();
                mShell.close();
            }
        });
        cancelButton = new Button(buttonPanel, SWT.PUSH);
        cancelButton.setText(Messages.getString("general.cancel"));
        cancelButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                result = CANCEL_SELECTED;
                mShell.close();
            }
        });
        mShell.setText(pTitle);
        mShell.setSize(300, 400);
        // mShell.pack();
        mShell.setLocation(DisplayTool.getCenterPosition(mShell.getSize()));
        mShell.setDefaultButton(okButton);
        mShell.open();
    }
    
    public void makeModal(){
        Display display = mShell.getParent().getDisplay();
        while (!mShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

    public List get(){
        return mItems;
    }
    
    
    /**
     * The list should be filled with <code>CheckTableItem</code> elements.
     * Note: For now the value is expected to be an Array of Strings. the first
     * element of this array is the value of the first (and only) column.
     * 
     * @param pItems
     *            A collection of items.
     * @see ICheckTableItem
     */
    public void fill(List pItems) {
        mItems = pItems;
        Iterator lIt = pItems.iterator();
        while (lIt.hasNext()) {
            Object lO = lIt.next();
            if (lO instanceof ICheckTableItem) {
                ICheckTableItem lCheckItem = (ICheckTableItem) lO;
                TableItem lItem = new TableItem(mTable, SWT.NONE);
                lItem.setChecked(lCheckItem.isChecked());
                Object value = lCheckItem.getValue();
                if(value instanceof Object[]){
                    Object[] valueArray = (Object[])value;
                    lItem.setText(valueArray[0].toString());    
                }else{
                    lItem.setText(value.toString());
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    private List read() {
        TableItem[] lTableItems = mTable.getItems();
        Object[] lItemArray = mItems.toArray();
        for (int i = 0; i < lTableItems.length; i++) {
            TableItem item = lTableItems[i];
            ICheckTableItem lItem = (ICheckTableItem)lItemArray[i];
            lItem.setChecked(item.getChecked());
        }
        return mItems;
    }

    public int getResult() {
        return this.result;
    }

}
