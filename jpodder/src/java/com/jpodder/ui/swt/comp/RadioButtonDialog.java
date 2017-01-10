package com.jpodder.ui.swt.comp;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.jpodder.util.Messages;

/**
 * Dialog with radion buttons to let the user select an option
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.1
 **/
public class RadioButtonDialog {

    private static final String OK = Messages.getString("general.ok");

    private Shell mShell;
    private Logger mLog = Logger.getLogger( RadioButtonDialog.class.getName() );
    private Button[] lRadios; 
    private int mResult = -1;


    public RadioButtonDialog() {
    }

    /**
     * Show a confirmation dialog with options.
     *
     * @param pTitle
     * @param pMessage
     * @param pOptions
     * @return in The selected option
     */
    public int showConfirmDialog( String pTitle, String pMessage, String[] pOptions ) {
    	
    	// We create a new display for this dialog. 
        mShell = new Shell(Display.getDefault(), SWT.DIALOG_TRIM );
        GridLayout lLayout = new GridLayout();
        lLayout.numColumns = 1;
        mShell.setLayout(lLayout);
        
        Label lMessageArea = new Label(mShell, SWT.NONE);
        lMessageArea.setText(pMessage);
        GridData lData = new GridData(GridData.FILL_HORIZONTAL);
        lMessageArea.setLayoutData(lData);
        
        lRadios = new Button[pOptions.length];
        Group lRadioButtonGroup = new Group(mShell, SWT.NONE);
        lRadioButtonGroup.setLayout(new GridLayout());
        for( int i = 0; i < pOptions.length; i++ ) {
            Button lRadio = new Button(lRadioButtonGroup, SWT.RADIO );
            lRadio.setText(pOptions[i]);
            lRadio.setSelection( i == 0 );
            lRadios[i] = lRadio;
        }
        
        Composite lButtonPanel = new Composite(mShell, SWT.NONE);
        lButtonPanel.setLayout(new RowLayout());
        Button lOk = new Button( lButtonPanel, SWT.PUSH);
        
        lOk.setText(OK);
        lOk.addListener(SWT.Selection, 
            new Listener() {
				public void handleEvent(Event e) {
                    mLog.info( "Dialog was closed" );                    
                    for( int i = 0; i < lRadios.length; i++ ) {
                        mLog.info( "Radio Button: " + i + " is selected: " + lRadios[ i ].getSelection() );
                        if( lRadios[ i ].getSelection() ) {
                        	mResult = i;
                        }
                    }
                    mShell.close();
                }
            }
        );
 
        mShell.setText( pTitle );
        mShell.setSize( 280, 220);
        mShell.setLocation(DisplayTool.getCenterPosition(mShell.getSize()));
        mShell.setVisible(true);
        makeModal();
        return mResult;
    }
    public void makeModal(){
        Display display = Display.getDefault();
        while (!mShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }
}