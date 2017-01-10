package com.jpodder.ui.swt.conf.panel;

import java.io.File;

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
import org.eclipse.swt.widgets.Text;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.conf.ConfigurationBinder;
import com.jpodder.ui.swt.conf.IConfigurationBinder;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class DownloadPanel implements IConfigurationPanel {

    protected Text mFolderField;
    protected Button mSoundEffectCheckBox;
    protected Button mMarkMaxOnlyCheckBox;
    protected Button mAutoPreviewCheckBox;

    protected IConfigurationBinder mAutoPreviewBinder;
    protected IConfigurationBinder mSoundBinder;
    protected IConfigurationBinder mFolderBinder;
    protected IConfigurationBinder mMarkMaxBinder;

    public IConfigurationBinder[] mBinderList = new IConfigurationBinder[4];
    protected Composite mView;

    public DownloadPanel() {
    }

    public DownloadPanel(Composite pParent) {
        initialize(pParent);
    }

    public Composite getView() {
        return mView;
    }

    public void initialize(Composite pParent) {
        mView = new Composite(pParent, SWT.NONE);

        FormLayout lMainLayout = new FormLayout();
        mView.setLayout(lMainLayout);

        // ----------------- FOLDER OPTIONS

        Group lGroup = new Group(mView, SWT.SHADOW_IN);
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        lGroup.setLayoutData(formData);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;

        lGroup.setLayout(gridLayout);
        lGroup.setText(Messages.getString("downloadpanel.folder"));

        Label folderLabel = new Label(lGroup, SWT.NONE);
        folderLabel.setText(Messages.getString("downloadpanel.folder.select"));

        mFolderField = new Text(lGroup, SWT.SINGLE | SWT.BORDER);
        mFolderField.setTextLimit(20);
        GridData lFolderData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        lFolderData.widthHint = 300;
        mFolderField.setLayoutData(lFolderData);

        Button folderButton = new Button(lGroup, SWT.PUSH);
        folderButton.setText(Messages.getString("general.browse"));
        folderButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                String lPath = mFolderField.getText();       
                DirectoryDialog dialog = new DirectoryDialog(UILauncher.getInstance()
                        .getShell(), SWT.OPEN);
                dialog.setFilterPath(lPath);
                dialog.open();
                String lFileName = dialog.getFilterPath();
                if (!"".equals(lFileName) && !lFileName.equalsIgnoreCase(lPath)) {
                    File lFile = new File(lFileName);
                    if (lFile.isDirectory()) {
                        mFolderField.setText(lFile.getPath());
                    }
                }
            }
        });

        // ------------ SOUND EFFECT

        Group lVariousGroup = new Group(mView, SWT.SHADOW_IN);
        FormData formData2 = new FormData();
        formData2.top = new FormAttachment(lGroup, 10);
        formData2.left = new FormAttachment(0, 5);
        formData2.right = new FormAttachment(100, -5);
        lVariousGroup.setLayoutData(formData2);

        GridLayout lVarGridLayout = new GridLayout();
        lVarGridLayout.numColumns = 1;
        lVariousGroup.setLayout(lVarGridLayout);
        lVariousGroup.setText(Messages.getString("downloadpanel.various"));

        mAutoPreviewCheckBox = new Button(lVariousGroup, SWT.CHECK);
        mAutoPreviewCheckBox
                .setText(Messages.getString("downloadpanel.autorefresh"));
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.grabExcessHorizontalSpace = true;
        mAutoPreviewCheckBox.setLayoutData(gridData);

        mSoundEffectCheckBox = new Button(lVariousGroup, SWT.CHECK);
        mSoundEffectCheckBox.setText(Messages.getString("downloadpanel.sound"));

        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.grabExcessHorizontalSpace = true;
        mSoundEffectCheckBox.setLayoutData(gridData);

        mMarkMaxOnlyCheckBox = new Button(lVariousGroup, SWT.CHECK);
        mMarkMaxOnlyCheckBox
                .setText(Messages.getString("downloadpanel.latest"));
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.grabExcessHorizontalSpace = true;
        mMarkMaxOnlyCheckBox.setLayoutData(gridData);

//        mTorrentCheckBox = new Button(lVariousGroup, SWT.CHECK);
//        mTorrentCheckBox.setText(Messages
//                .getString("downloadpanel.torrent.init"));
//        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//        gridData.grabExcessHorizontalSpace = true;
//        mTorrentCheckBox.setLayoutData(gridData);

        // mCacheLearnCheckBox = new Button(lVariousGroup, SWT.CHECK);
        // mCacheLearnCheckBox.setText(string);

        try {
            mAutoPreviewBinder = new ConfigurationBinder(mAutoPreviewCheckBox,
                    IConfigurationBinder.SUB_TYPE_CONFIGURATION,
                    Configuration.CONFIG_AUTOPREVIEW);
            mSoundBinder = new ConfigurationBinder(mSoundEffectCheckBox,
                    IConfigurationBinder.SUB_TYPE_CONFIGURATION,
                    Configuration.CONFIG_SOUND);
            mFolderBinder = new ConfigurationBinder(mFolderField,
                    IConfigurationBinder.SUB_TYPE_CONFIGURATION,
                    Configuration.CONFIG_FOLDER);
            mMarkMaxBinder = new ConfigurationBinder(mMarkMaxOnlyCheckBox,
                    IConfigurationBinder.SUB_TYPE_CONFIGURATION,
                    Configuration.CONFIG_MARKMAX);

            mBinderList[0] = mSoundBinder;
            mBinderList[1] = mFolderBinder;
            mBinderList[2] = mMarkMaxBinder;
            mBinderList[3] = mAutoPreviewBinder;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IConfigurationBinder[] getBindings() {
        return mBinderList;
    }
}