package com.jpodder.ui.swt.help;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.jpodder.Main;
import com.jpodder.ui.swt.comp.DisplayTool;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class HelpAbout {
    
    Shell mShell;
    
    public HelpAbout(){
        mShell = new Shell(Display.getDefault(), SWT.APPLICATION_MODAL);
        GridLayout lLayout = new GridLayout();
        lLayout.numColumns = 2;
        mShell.setLayout(lLayout);
//        mShell.setBackground(UILauncher.lMGENERIC_BACKGROUND_COLOR);

        Label lImage = new Label(mShell, SWT.NONE);
        lImage.setImage(UITheme.getInstance().getImages().get(UITheme.IMAGE_SPLASH));
        lImage.setBackground(UITheme.getInstance().GENERIC_BACKGROUND_COLOR);
        
        
        Label lAbout = new Label(mShell, SWT.NONE | SWT.WRAP);
        StringBuffer lBuffer = new StringBuffer();
        lBuffer.append(Main.APP_TITLE + ": " + Main.APP_RELEASE + "\n");
        lBuffer.append(Messages.getString("help.about.text"));
        lAbout.setText(lBuffer.toString());
//        lAbout.setBackground(UILauncher.lMainUI.GENERIC_BACKGROUND_COLOR);
//        lAbout.setForeground(UILauncher.mDisplay.getSystemColor(SWT.COLOR_WHITE));
//        lAbout.set
        GridData lData = new GridData(GridData.FILL_BOTH);
        lAbout.setLayoutData(lData);
        
        Composite lButtons = new Composite(mShell, SWT.NONE);
        lData = new GridData(GridData.FILL_HORIZONTAL);
        lData.horizontalSpan =2;
        lButtons.setBackground(UITheme.getInstance().GENERIC_BACKGROUND_COLOR); 
        
        lButtons.setLayoutData(lData);
        lButtons.setLayout(new RowLayout());
        
        Button lOKButton = new Button(lButtons, SWT.PUSH);
        lOKButton.setText(Messages.getString("general.ok"));
        lOKButton.addListener(SWT.Selection, new Listener(){
            public void handleEvent(Event arg0) {
                mShell.setVisible(false);
                mShell.dispose();
            }
        });
        
        
        mShell.setSize(600, 350);
        mShell.setLocation(DisplayTool.getCenterPosition(mShell.getSize()));
        mShell.setVisible(true);
        dispatch();
    }
    
    public void dispatch(){
        Display display = mShell.getDisplay();
        while (!mShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }
}
