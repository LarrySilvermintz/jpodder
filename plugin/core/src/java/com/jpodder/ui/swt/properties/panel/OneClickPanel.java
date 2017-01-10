package com.jpodder.ui.swt.properties.panel;

/**
 * @author <a href="mailto:christophe@kualasoft.com" >Christophe Bouhier </a>
 * @version 1.0
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.jpodder.ui.swt.conf.ConfigurationBinder;
import com.jpodder.ui.swt.conf.IConfigurationBinder;
import com.jpodder.ui.swt.conf.panel.IConfigurationPanel;
import com.jpodder.util.Messages;

public class OneClickPanel implements IConfigurationPanel {

    protected Button mCheckRegistrationButton;
    protected Button mSetRegistrationButton;
    protected Button mRemoveRegistrationButton;

    protected Composite mView;
    protected OneClickController mController; 
    
    public ConfigurationBinder mOneClickBinder;

    public OneClickPanel() {
    }

    public OneClickPanel(Composite pParent) {
        initialize(pParent);
    }
    
    public void setController(OneClickController pController){
        mController = pController;
    }
    
    public void initialize(Composite pParent) {

        mView = new Composite(pParent, SWT.NONE);
        GridLayout lGridLayout = new GridLayout();
        lGridLayout.numColumns = 2;
        mView.setLayout(lGridLayout);

        Label lCheckRegistrationLabel = new Label(mView, SWT.NONE);
        lCheckRegistrationLabel.setText(Messages
                .getString("oneclickpanel.check.description"));
        mCheckRegistrationButton = new Button(mView, SWT.PUSH);
        
        Label lSetRegistrationLabel = new Label(mView, SWT.NONE);
        lSetRegistrationLabel.setText(Messages
                .getString("oneclickpanel.set.description"));
        mSetRegistrationButton = new Button(mView, SWT.PUSH);

        Label lRemoveRegistrationLabel = new Label(mView, SWT.NONE);
        lRemoveRegistrationLabel.setText(Messages
                .getString("oneclickpanel.remove.description"));
        mRemoveRegistrationButton = new Button(mView, SWT.PUSH);
        mController.setView(this);
    }

    public IConfigurationBinder[] getBindings() {
        return null;
    }

    public Composite getView() {
        return mView;
    }
}