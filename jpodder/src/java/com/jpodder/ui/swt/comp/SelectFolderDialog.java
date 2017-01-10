package com.jpodder.ui.swt.comp;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jpodder.ui.swt.UILauncher;
import com.jpodder.util.Messages;

/**
 * Dialog with radion buttons to let the user select an option
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.1
 **/
public class SelectFolderDialog {

    private static final String OK = Messages.getString("general.ok");
    
    private Shell mShell;
    
    private Logger mLog = Logger.getLogger( SelectFolderDialog.class.getName() );
    private int mResult = -1;
    public static final int OK_SELECTED = 1;
    
    private String mFolder;

    public SelectFolderDialog(String pFolder) {
    	mFolder = pFolder;
    }

    /**
     * Show a confirmation dialog with options.
     *
     * @param pTitle
     * @param pMessage
     * @param pOptions
     * @return in The selected option
     */
    public int showDialog( String pTitle, String pMessage, String[] pOptions ) {
    	
    	// We create a new display for this dialog. 
        mShell = new Shell(Display.getDefault(), SWT.DIALOG_TRIM );
        
        GridLayout lLayout = new GridLayout();
        lLayout.numColumns = 2;
        mShell.setLayout(lLayout);
        
        Label lMessageArea = new Label(mShell, SWT.LEFT | SWT.NONE);
        GridData lData = new GridData(GridData.FILL_BOTH);
        lData.horizontalSpan = 2;
//        lData.widthHint = 200;
        lMessageArea.setLayoutData(lData);
        lMessageArea.setText(pMessage);
        
        final Text lFolderField = new Text(mShell, SWT.BORDER);
        lData = new GridData(GridData.GRAB_HORIZONTAL);
        lData.widthHint = 250;
        lFolderField.setLayoutData(lData);

        Button lBrowseButton = new Button(mShell, SWT.PUSH);
        lBrowseButton.setText(Messages.getString("general.browse"));
        lBrowseButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                
                DirectoryDialog dialog = new DirectoryDialog(UILauncher.getInstance()
                        .getShell(), SWT.OPEN);
                dialog.setFilterPath(mFolder);
                dialog.open();
                String lFileName = dialog.getFilterPath();
                File lFile = new File(lFileName);
                if (lFile.isDirectory()) {
                    lFolderField.setText(lFile.getPath());
                }
            }
        });

        lFolderField.setText(mFolder == null ? "" : mFolder);

        Composite lButtonPanel = new Composite(mShell, SWT.NONE);
        lButtonPanel.setLayout(new RowLayout());
        Button lOk = new Button( lButtonPanel, SWT.PUSH);
        
        lOk.setText(OK);
        lOk.addListener(SWT.Selection, 
            new Listener() {
				public void handleEvent(Event e) {
                    mLog.info( "Dialog was closed" );                    
                    mResult = OK_SELECTED;
                    mFolder = lFolderField.getText();
                    mShell.close();
                }
            }
        );
        
 
        mShell.setText( pTitle );
        mShell.setSize( 350, 150);
        mShell.setLocation(DisplayTool.getCenterPosition(mShell.getSize()));
        mShell.setVisible(true);
        makeModal();
        return mResult;
    }
    
    /**
     * Get the selected folder.
     * @return
     */
    public String getFolder(){
    	return mFolder;
    }
    
    public void makeModal(){
        Display display = Display.getDefault();
        while (!mShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }
}