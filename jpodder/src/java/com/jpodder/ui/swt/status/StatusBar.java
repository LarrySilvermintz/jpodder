package com.jpodder.ui.swt.status;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.jpodder.util.Messages;

/**
 * A status bar should information about feeds, enclosures, schedule interval
 * and more...
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class StatusBar extends Composite {
    
    public static int BAR_HEIGHT = 15;
    
//    private Font barFont = new Font("MS Sans Serif", 0, 11);

    protected Text mFolderField;
    protected Text mPlayerField;
    protected Text mSizeField;
    protected Text mFeedField;
    protected Text mScheduleField;
    protected Button mScheduleCheck;

    public StatusBar(Composite pParent) {
        super(pParent, SWT.NONE);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = true;
        gridData.heightHint = StatusBar.BAR_HEIGHT + 8;
        setLayoutData(gridData);
        
        GridLayout lLayout = new GridLayout();
        lLayout.marginHeight = 0;
        lLayout.marginWidth = 0;
        lLayout.horizontalSpacing = 0;
        lLayout.numColumns = 6;
        setLayout(lLayout);

        mFeedField = new Text(this, SWT.BORDER);
        mFeedField.setEditable(false);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 200;
        gridData.heightHint = BAR_HEIGHT;
        mFeedField.setLayoutData(gridData);
        
        // CB TODO, change the font for statusbar.
//        Font lFont = mFeedField.getFont();
//        FontData[] lData = lFont.getFontData();
        
        
        mScheduleCheck = new Button(this, SWT.CHECK | SWT.BORDER);
//        mScheduleCheck.setBounds(0, 0, 50, 12);
        mScheduleCheck.setText(Messages.getString("status.schedule.check"));
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.grabExcessHorizontalSpace = true;
//        gridData.widthHint = 50;
        gridData.heightHint = BAR_HEIGHT + 2;
        mScheduleCheck.setLayoutData(gridData);
        
        mScheduleField = new Text(this, SWT.BORDER);
        mScheduleField.setEditable(false);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 50;
        gridData.heightHint = BAR_HEIGHT;
        mScheduleField.setLayoutData(gridData);
        
        mFolderField = new Text(this, SWT.BORDER);
        mFolderField.setEditable(false);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 200;
        gridData.heightHint = BAR_HEIGHT;
        mFolderField.setLayoutData(gridData);
        
        mSizeField = new Text(this, SWT.BORDER);
        mSizeField.setEditable(false);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 100;
        gridData.heightHint = BAR_HEIGHT;
        mSizeField.setLayoutData(gridData);

        mPlayerField = new Text(this, SWT.BORDER);
        mPlayerField.setEditable(false);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 100;
        gridData.heightHint = BAR_HEIGHT;
        mPlayerField .setLayoutData(gridData);
        StatusController.getInstance().initialize(this);
    }
}