package com.jpodder.ui.swt.conf.panel;

/**
 * @author <a href="mailto:christophe@kualasoft.com" >Christophe Bouhier </a>
 * @version 1.1
 */
import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

import com.jpodder.FileHandler;
import com.jpodder.JPodderException;
import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.content.IContent;
import com.jpodder.data.player.IPlayer;
import com.jpodder.data.player.NoPlayer;
import com.jpodder.data.player.PlayerLogic;
import com.jpodder.plugin.PluginLogic;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.conf.ConfigurationBinder;
import com.jpodder.ui.swt.conf.IConfigurationBinder;
import com.jpodder.util.Messages;
import com.jpodder.util.PersistentObject;

public class MediaPanel implements IConfigurationPanel {

    public Button mRetrievePluginButton;
    protected Label mPlayerAuthorValue = null;
    protected Label mPlayerDescriptionValue = null;
    protected List mPlayerMimeTypeList;
    protected Combo mPlayerCombo;
    
    public IConfigurationBinder mPlayerBinder;
    public IConfigurationBinder[] mBinderList = new IConfigurationBinder[1];

    protected Composite mView;
    
    public MediaPanel(Composite pParent) {
        initialize(pParent);
    }
    
    public MediaPanel() {
    }

    public Composite getView(){
        return mView;
    }
    
    public void initialize(Composite pParent) {
        
        mView = new Composite(pParent, SWT.NONE);
        
        // ------- Plugin scanner and description
        FormLayout lMainLayout = new FormLayout();
        mView.setLayout(lMainLayout);

        Group lGroup = new Group(mView, SWT.SHADOW_IN);
        FormData formData2 = new FormData();
        formData2.top = new FormAttachment(0, 5);
        formData2.left = new FormAttachment(0, 5);
        formData2.right = new FormAttachment(100, -5);
        lGroup.setLayoutData(formData2);
        lGroup.setText(Messages.getString("mediapanel.checkplugin.title"));

        GridLayout lGridLayout = new GridLayout();
        lGridLayout.numColumns = 2;
        lGroup.setLayout(lGridLayout);

        Label pluginDescriptionValue = new Label(lGroup, SWT.LEFT | SWT.NONE);
        StringBuffer sb = new StringBuffer();
        String s = new File(FileHandler.sApplicationDirectory, "plugin")
                .getAbsolutePath();
        sb.append(Messages.getString("mediapanel.plugindescription", s));
        GridData lData = new GridData(GridData.FILL_HORIZONTAL);
        lData.horizontalSpan = 2;
        lData.widthHint = 200;
        pluginDescriptionValue.setLayoutData(lData);
        pluginDescriptionValue.setText(sb.toString());
        
        Label checkPluginLabel = new Label(lGroup, SWT.NONE);
        checkPluginLabel.setText(Messages.getString("mediapanel.checkplugin"));
        mRetrievePluginButton = new Button(lGroup, SWT.PUSH);
        mRetrievePluginButton
                .setText(Messages.getString("playercontrol.check"));
        mRetrievePluginButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                // We also need to clean the plugin tab in configuration. 
                PluginLogic.getInstance().scanPluginFolder();
                loadPlayers(true);
            }
        });

        Group lSelectionGroup = new Group(mView, SWT.SHADOW_IN);
        FormData formData = new FormData();
        formData.top = new FormAttachment(lGroup, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);

        lSelectionGroup.setLayoutData(formData);
        lSelectionGroup.setText(Messages.getString("mediapanel.select"));

        GridLayout lGridLayout2 = new GridLayout();
        lGridLayout2.numColumns = 3;
        lSelectionGroup.setLayout(lGridLayout2);
        
        lData = new GridData();
        lData.horizontalSpan = 3;

        Label playerSelectLabel = new Label(lSelectionGroup, SWT.NONE);
        playerSelectLabel.setText(Messages.getString("mediapanel.select"));

        mPlayerCombo = new Combo(lSelectionGroup, SWT.NONE | SWT.READ_ONLY);
        mPlayerCombo.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                int lSelection = mPlayerCombo.getSelectionIndex();
                String lItem = mPlayerCombo.getItem(lSelection);
                setSelectedPlayer(lItem);
            }
        });
        lData = new GridData();
        lData.horizontalSpan = 2;
        lData.widthHint = 200;
        mPlayerCombo.setLayoutData(lData);

        Label playerAuthor = new Label(lSelectionGroup, SWT.NONE);
        playerAuthor.setText(Messages.getString("mediapanel.author"));

        mPlayerAuthorValue = new Label(lSelectionGroup, SWT.NONE);
        lData = new GridData(GridData.GRAB_HORIZONTAL);
        lData.horizontalSpan = 2;
//        lData.widthHint = 220;
        mPlayerAuthorValue.setLayoutData(lData);
//        mPlayerAuthorValue.setBackground(UITheme.getInstance().TABLE_ODD_BACKGROUND_COLOR);
        
        Label playerDescription = new Label(lSelectionGroup, SWT.NONE);
        playerDescription.setText(Messages.getString("mediapanel.description"));
        mPlayerDescriptionValue = new Label(lSelectionGroup, SWT.WRAP);
        lData = new GridData();
        lData.horizontalSpan = 2;
        lData.heightHint = 40;
//        lData.widthHint = 220;
        mPlayerDescriptionValue.setLayoutData(lData);
//        mPlayerDescriptionValue.setBackground(UITheme.getInstance().TABLE_ODD_BACKGROUND_COLOR);
        
        Label playerMimeTypesLabel = new Label(lSelectionGroup, SWT.NONE);
        playerMimeTypesLabel
                .setText(Messages.getString("mediapanel.mimetypes"));
        mPlayerMimeTypeList = new List(lSelectionGroup, SWT.BORDER | SWT.SINGLE);
        lData = new GridData(GridData.GRAB_HORIZONTAL);
        lData.horizontalSpan = 2;
        lData.widthHint = 220;
        lData.heightHint = 40;
        mPlayerMimeTypeList.setLayoutData(lData);

        // ---- UI Property binding.

        try {
            mPlayerBinder = new ConfigurationBinder(mPlayerCombo,
                    ConfigurationBinder.SUB_TYPE_CONFIGURATION,
                    Configuration.CONFIG_PLAYER);
            mBinderList[0] = mPlayerBinder;
        } catch (JPodderException e1) {
        	e1.printStackTrace();
        }
        loadPlayers(false);
    }

    private void loadPlayers(boolean pNotify) {
        String[] lPlayerNames = PlayerLogic.getInstance().loadPlayers();
        String message = Messages.getString("playercontrol.checkdialog",
                (lPlayerNames.length - 1));
        if (pNotify) {
            MessageDialog.openInformation(UILauncher.getInstance().getShell(),
                    Messages.getString("playercontrol.checkdialogtitle"),
                    message);
        }
        setPlayers(lPlayerNames);
    }


    public void setPlayers(String[] pPlayerNames) {

        int lOldIndex = mPlayerCombo.getSelectionIndex();
        mPlayerCombo.setItems(pPlayerNames);
        String lSelection = null;
        if (lOldIndex != -1) {
            String lCurrentPlayer = mPlayerCombo.getItem(lOldIndex);
            int i = 0;
            for (; i < pPlayerNames.length; i++) {
                if (pPlayerNames[i].equals(lCurrentPlayer)) {
                    lSelection = lCurrentPlayer;
                    break;
                }
            }
            mPlayerCombo.select(i);
            return;
            // if (lSelection == null) {
            // mPlayerCombo.select(0);
            // } else {
            // mPlayerCombo.select(i);
            // }
        }
        lSelection = pPlayerNames[0];
        mPlayerCombo.select(0);
        setSelectedPlayer(lSelection);
    }

    /**
     * The UI fields related to a plyer name are updated with the specifics for
     * the player name. It also updates the MIME types which this player
     * supports. For the &quotNo Player&quot we create a new instance, as it's
     * not contained in the plugin database. <br>
     * 
     * @param pPlayerName
     *            The short descriptive name of the player.
     */
    private void setSelectedPlayer(String pPlayerName) {
        IPlayer lPlayer = null;
        IPlayer lNoPlayer = new NoPlayer();
        if (!pPlayerName.equals(lNoPlayer.getName())) {
            lPlayer = (IPlayer) new PersistentObject("jpodder.plugin.loader")
                    .invoke("findPluginInstance", new Object[] { pPlayerName });
        }
        if (lPlayer == null) {
            lPlayer = lNoPlayer;
        }

        mPlayerAuthorValue.setText(lPlayer.getAuthor());
        mPlayerDescriptionValue.setText(lPlayer.getDescription());

        IContent[] types = lPlayer.getMIMETypes();
        mPlayerMimeTypeList.removeAll();
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                mPlayerMimeTypeList.add(types[i].getName());
            }
        }
    }

    public IConfigurationBinder[] getBindings() {
        return mBinderList;
    }
}