package com.jpodder.ui.swt.comp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.jpodder.ui.swt.UILauncher;
import com.jpodder.util.Messages;

/**
 * A Dialog used to edit feeds. Shows a dialog, which allows the user to add a
 * feed.
 */
public class ComboDialog {

    protected Button okButton;
    protected Button cancelButton;

    public static int OK_SELECTED = 501;
    public static int CANCEL_SELECTED = 502;
    protected int mOption;
    protected String mResults;
    protected Combo mCombo;
    private Shell mShell;

    /**
     * Constructor.
     * 
     * @param feeds
     *            A list of feeds.
     */
    public ComboDialog(String pTitle) {

        mShell = new Shell(UILauncher.getInstance().getShell(), SWT.SYSTEM_MODAL
                | SWT.RESIZE);

        GridLayout lLayout = new GridLayout();
        lLayout.numColumns = 1;
        mShell.setLayout(lLayout);
        
        mCombo = new Combo(mShell, SWT.NONE);
        GridData lData = new GridData(GridData.FILL_HORIZONTAL);
        mCombo.setLayoutData(lData);
        
        
        Composite buttonPanel = new Composite(mShell, SWT.NONE);
        RowLayout lButtonLayout = new RowLayout();
        buttonPanel.setLayout(lButtonLayout);
        lData = new GridData(GridData.FILL_HORIZONTAL);
        buttonPanel.setLayoutData(lData);

        okButton = new Button(buttonPanel, SWT.PUSH);
        okButton.setText(Messages.getString("general.ok"));

        okButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                mOption = OK_SELECTED;
                mResults = read();
                mShell.close();
            }
        });
        cancelButton = new Button(buttonPanel, SWT.PUSH);
        cancelButton.setText(Messages.getString("general.cancel"));
        cancelButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                mOption = CANCEL_SELECTED;
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

    public String GetResult(){
        return mResults;
    }
    
    
    /**
     * Fill with Strings.
     */
    public void fill(String[] pItems) {
        for (int i = 0; i < pItems.length; i++) {
            String string = pItems[i];
            mCombo.add(string);
        }
    }

    private String read() {
        int lSelected = mCombo.getSelectionIndex();
        return mCombo.getItem(lSelected);
    }

    public int getOption() {
        return mOption;
    }

}
