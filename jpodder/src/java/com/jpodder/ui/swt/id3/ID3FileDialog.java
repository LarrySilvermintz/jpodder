package com.jpodder.ui.swt.id3;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.jpodder.data.id3.ID3Wrapper;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.comp.DisplayTool;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.ui.swt.window.WindowView;
import com.jpodder.util.Messages;

import de.vdheide.mp3.NoID3v2TagException;

public class ID3FileDialog {
	
    private Shell mShell;
	private ID3Table mID3Table;
	private Button mOkButton;
	private Button cancelButton;
	private Button mSaveButton;
	
    public static int OK_SELECTED = 501;
    public static int CANCEL_SELECTED = 502;
	private int mResult = 0;
	private ID3Wrapper mWrapper;
	
	
    public ID3FileDialog(){
        mShell = new Shell(UILauncher.getInstance().getShell(), SWT.DIALOG_TRIM 
                | SWT.RESIZE);
        mShell.setImage(UITheme.getInstance().getImages().get(UITheme.IMAGE_JPODDER));
        mShell.setText(Messages.getString("id3control.mp3view"));    	
        mShell.setSize(500,300);
        GridLayout lLayout = new GridLayout();
        lLayout.numColumns = 1;
        mShell.setLayout(lLayout);
        mID3Table = new ID3Table(mShell);
        Composite buttonPanel = new Composite(mShell, SWT.NONE);
        RowLayout lButtonLayout = new RowLayout();
        buttonPanel.setLayout(lButtonLayout);
        GridData lData = new GridData(GridData.FILL_HORIZONTAL);
        buttonPanel.setLayoutData(lData);

        mOkButton = new Button(buttonPanel, SWT.PUSH);
        mOkButton.setText(Messages.getString("general.ok"));
        mOkButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
                mResult = OK_SELECTED;
                mShell.close();
            }
        });
        
        mSaveButton = new Button(buttonPanel, SWT.PUSH);
        mSaveButton.setEnabled(false);
        mSaveButton.setText(Messages.getString("general.save"));
        mSaveButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if(mWrapper != null){
					mWrapper.update();					
				}
			}
        });

        cancelButton = new Button(buttonPanel, SWT.PUSH);
        cancelButton.setText(Messages.getString("general.cancel"));
        cancelButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                mResult = CANCEL_SELECTED;
                mShell.close();
            }
        });        
        
        mShell.setLocation(DisplayTool.getCenterPosition(mShell.getSize()));
        mShell.setDefaultButton(mOkButton);
        
    }
    
    public void fillDialog(File pFile){
    	if(pFile == null || !pFile.exists()){
    		return;
    	}
    	mWrapper = new ID3Wrapper(pFile);
    	try {
			mID3Table.getTableView().setInput(mWrapper.getAllContents());
			mSaveButton.setEnabled(true);
			mShell.open();
    	} catch (NoID3v2TagException e) {
			e.printStackTrace();
		}
    }
    
}
