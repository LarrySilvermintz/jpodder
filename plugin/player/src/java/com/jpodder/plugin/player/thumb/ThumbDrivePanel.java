package com.jpodder.plugin.player.thumb;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jpodder.plugin.PluginLogic;
import com.jpodder.plugin.PluginRegistryEntry;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.conf.IConfigurationBinder;
import com.jpodder.ui.swt.conf.panel.IConfigurationPanel;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class ThumbDrivePanel implements IConfigurationPanel {
    
    protected Text mDrivePath;
    protected Composite mView;
    
    protected IConfigurationBinder lDrivePathBinder;
    
    
    public IConfigurationBinder[] getBindings() {
        return null;
    }
    
    public ThumbDrivePanel() {
        
    }
    
    public ThumbDrivePanel(Composite pParent) {
        initialize(pParent);
    }
    
    public Composite getView(){
        return mView;
        
    }
        
    public void initialize(Composite pParent) {
        
        mView = new Composite(pParent, SWT.NONE);
        
        FormLayout lMainLayout = new FormLayout();
        mView.setLayout(lMainLayout);
        
        Group lGroup = new Group(mView, SWT.SHADOW_IN);
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        lGroup.setLayoutData(formData);
        lGroup.setText("Thumb drive plugin settings");

        GridLayout lLayout = new GridLayout();
        lLayout.numColumns = 3;
        lGroup.setLayout(lLayout);
        Label lLabel = new Label(lGroup, SWT.NONE);
        lLabel.setText("Select the path to your network drive");
        mDrivePath = new Text(lGroup, SWT.SINGLE | SWT.BORDER);
        GridData lData = new GridData(GridData.FILL_HORIZONTAL);
//        lData.minimumWidth = 300;
        mDrivePath.setLayoutData(lData);
        mDrivePath.setTextLimit(20);
        
        Button lButton = new Button(lGroup, SWT.PUSH);
        lButton.setText("...");
        lButton.addListener(SWT.Selection, new FolderSelector(UILauncher.getInstance().getShell()));
        // Get a specific property.
        PluginRegistryEntry lEntry = PluginLogic.getInstance().getRegistryEntry("thumbdrive");
        if(lEntry != null){
        	mDrivePath.setText(lEntry.getValue());	
        }
    }
    
    class FolderSelector implements Listener {
        Shell mShell;

        public FolderSelector(Shell pShell) {
            mShell = pShell;
        }

        public void handleEvent(Event event) {
            DirectoryDialog dialog = new DirectoryDialog(UILauncher.getInstance()
                    .getShell());
            dialog.open();
            String lPath = dialog.getFilterPath();
            if(lPath.length() > 0){
                mDrivePath.setText(lPath);
                ThumbDrivePlayer.sCacheFolder = lPath;
                ThumbDrivePlayer.setData();
                PluginLogic.getInstance().setRegistryEntry("thumbdrive", lPath);            	
            }
//            String lText = dialog.getText();
        }
    }
}
