package com.jpodder.ui.swt.comp;

import java.util.HashMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.jpodder.ui.swt.UILauncher;
import com.jpodder.util.Messages;

/**
 * @author Christophe Bouhier
 */
public class CheckBoxDialog {
    
    protected static Shell mShell;
    private static int mResult;
    private static HashMap checkResult;
    private static String[] mOptions;
    
    public static int OK_SELECTED = 501;
    public static int CANCEL_SELECTED = 502;
    
    private static final String OK = Messages.getString("general.ok");
    private static final String CANCEL = Messages.getString("general.cancel");

    public CheckBoxDialog() {

    }

    /**
     * Show a confirmation dialog with options.
     * 
     * @param title
     * @param text
     * @param pOptions
     * @return in The selected option
     */
    public static void showConfirmDialog(String title, String text,
            String[] pOptions) {
        
        mResult = -1;
        mOptions = pOptions;
        checkResult = new HashMap();
        
        mShell = new Shell(UILauncher.getInstance().getShell(),  SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        mShell.setText(title);
        mShell.setSize(300, 200);
        
        GridLayout lLayout = new GridLayout();
        lLayout.numColumns = 1;
        mShell.setLayout(lLayout);
        
        Label textLabel = new Label(mShell, SWT.NONE );
        textLabel.setText(text);
        GridData lData = new GridData(GridData.FILL_HORIZONTAL);
        lData.grabExcessHorizontalSpace = true;
        textLabel.setLayoutData(lData);
        
        Group optionGroup = new Group(mShell, SWT.NONE);
        lData = new GridData(GridData.FILL_BOTH);
        lData.grabExcessHorizontalSpace = true;
        lData.grabExcessVerticalSpace = true;
        optionGroup.setLayoutData(lData);
        
        FillLayout lOptionLayout = new FillLayout(SWT.VERTICAL);
        optionGroup.setLayout(lOptionLayout);
        
        for (int index = 0; index < mOptions.length; index++) {
            String lOption = mOptions[index];
            if (lOption != null) {
                Button checkBox = new Button(optionGroup, SWT.NONE | SWT.CHECK);
                checkBox.setText(lOption);
                checkBox.setSelection(true);
                checkResult.put(lOption, new Boolean(true));
                checkBox.addListener(SWT.Selection, new Listener() {
                    public void handleEvent(Event e) {
                        Object src = e.widget;
                        if (src instanceof Button) {
                            String key = ((Button) src).getText();
                            checkResult.put(key, new Boolean(((Button)src).getSelection()));
                        }
                    }
                });
            }
        }

        Composite lButtonPanel = new Composite(mShell, SWT.NONE);
        RowLayout lButtonLayout = new RowLayout();
        lButtonLayout.pack = false;
        lButtonPanel.setLayout(lButtonLayout);
        
        Button okButton = new Button(lButtonPanel, SWT.PUSH);
        okButton.setText(OK);
        okButton.addListener(SWT.Selection, new Listener(){
            public void handleEvent(Event event) {
                mResult = OK_SELECTED;
                mShell.close();
            }
        });

        Button cancelButton = new Button(lButtonPanel, SWT.PUSH);
        cancelButton.setText(CANCEL);
        cancelButton.addListener(SWT.Selection, new Listener(){
            public void handleEvent(Event event) {
              mResult = CANCEL_SELECTED;
              mShell.close();
            }
        });
        mShell.setDefaultButton(okButton);
        mShell.setLocation(DisplayTool.getCenterPosition(mShell.getSize()));
        mShell.open();
    }
    
    public static int getResult(){
        return mResult;
    }
    

    public static void makeModal(){
        Display display = mShell.getParent().getDisplay();
        while (!mShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

    /**
     * Get the selected options.
     * 
     * @return Boolean[] The checked options
     */
    public static Boolean[] getLatestOptions() {

        Boolean[] results = new Boolean[mOptions.length];
        for (int index = 0; index < mOptions.length; index++) {
            results[index] = (Boolean) checkResult.get(mOptions[index]);
        }
        return results;
    }
}