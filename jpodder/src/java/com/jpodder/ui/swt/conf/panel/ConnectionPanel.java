package com.jpodder.ui.swt.conf.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.ui.swt.conf.ConfigurationBinder;
import com.jpodder.ui.swt.conf.IConfigurationBinder;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class ConnectionPanel implements IConfigurationPanel {

    protected Text mProxyServerField;
    protected Text lProxyPortField;
    protected Button mProxyCheckBox;
    protected Text mProxyUserIdField;
    protected Text mProxyPasswordField;
    
    public IConfigurationBinder mProxyBinder;
    public IConfigurationBinder mProxyServerBinder;
    public IConfigurationBinder mProxyPortBinder;
    public IConfigurationBinder mProxyUserBinder;
    public IConfigurationBinder mProxyPasswordBinder;
    public IConfigurationBinder[] mBinderList = new IConfigurationBinder[5];
    
    protected Composite mView;
    
    public ConnectionPanel(Composite pParent) {
        initialize(pParent);
    }
    
    public ConnectionPanel() {
    }

    public Composite getView(){
        return mView;
    }
    
    public void initialize(Composite pParent) {
        
        mView = new Composite(pParent, SWT.NONE);
        
        FormLayout lMainLayout = new FormLayout();
        mView.setLayout(lMainLayout);

        // /////////////////////////////////////////////////////////////////////////////
        // PROPERTY PANEL - CONNECTION
        // /////////////////////////////////////////////////////////////////////////////

        mProxyCheckBox = new Button(mView, SWT.CHECK);
        mProxyCheckBox.setText(Messages.getString("connectionPanel.proxyon"));
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        mProxyCheckBox.setLayoutData(formData);
        
        mProxyCheckBox.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                setProxyOn(mProxyCheckBox.getSelection());
            }
        });

        Group lGroup = new Group(mView, SWT.SHADOW_IN);
        FormData formData2 = new FormData();
        formData2.top = new FormAttachment(mProxyCheckBox, 10);
        formData2.left = new FormAttachment(0, 5);
        formData2.right = new FormAttachment(100, -5);
        lGroup.setLayoutData(formData2);
        lGroup.setText(Messages.getString("connectionPanel.proxy"));
        
        GridLayout lGridLayout = new GridLayout();
        lGridLayout.numColumns = 2;
        lGroup.setLayout(lGridLayout);
        
        Label proxyServerLabel = new Label(lGroup, SWT.NONE);
        proxyServerLabel.setText(Messages
                .getString("connectionPanel.proxyserver"));
        mProxyServerField = new Text(lGroup, SWT.SINGLE| SWT.BORDER);
        GridData lGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        lGridData.widthHint = 300;
        mProxyServerField.setLayoutData(lGridData);
        
        Label proxyPortLabel = new Label(lGroup, SWT.NONE);
        proxyPortLabel.setText(Messages.getString("connectionPanel.proxyport"));
        
        lProxyPortField = new Text(lGroup, SWT.SINGLE|SWT.BORDER);
        lGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        lGridData.widthHint = 300;
        lProxyPortField.setLayoutData(lGridData);

        Label proxyUserIdLabel = new Label(lGroup, SWT.NONE);
        proxyUserIdLabel.setText(Messages.getString("connectionPanel.proxyUserid"));
        mProxyUserIdField = new Text(lGroup, SWT.SINGLE|SWT.BORDER);
        lGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        lGridData.widthHint = 300;
        mProxyUserIdField.setLayoutData(lGridData);

        
        Label proxyPasswordLabel = new Label(lGroup, SWT.NONE);
        proxyPasswordLabel.setText(Messages
                .getString("connectionPanel.proxyPassword"));
        mProxyPasswordField = new Text(lGroup, SWT.BORDER | SWT.PASSWORD);
        lGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        lGridData.widthHint = 300;
        mProxyPasswordField.setLayoutData(lGridData);

        try {

            mProxyBinder = new ProxyBinder();

            mProxyServerBinder = new ConfigurationBinder(mProxyServerField,
                    IConfigurationBinder.SUB_TYPE_CONNECTION, "Proxy");

            mProxyPortBinder = new ConfigurationBinder(lProxyPortField,
                    IConfigurationBinder.SUB_TYPE_CONNECTION, "ProxyPort");

            mProxyUserBinder = new ConfigurationBinder(mProxyUserIdField,
                    IConfigurationBinder.SUB_TYPE_CONNECTION, "UserName");

            mProxyPasswordBinder = new ConfigurationBinder(mProxyPasswordField,
                    IConfigurationBinder.SUB_TYPE_CONNECTION, "Password");

            mBinderList[0] = mProxyBinder;
            mBinderList[1] = mProxyServerBinder;
            mBinderList[2] = mProxyPortBinder; 
            mBinderList[3] = mProxyUserBinder; 
            mBinderList[4] = mProxyPasswordBinder;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private class ProxyBinder implements IConfigurationBinder{

		public String getName() {
			return "ProxyEnabled";
		}

		public void read() {
			boolean lAuto = Configuration.getInstance().getAuto();
			mProxyCheckBox.setSelection(lAuto);
			setProxyOn(lAuto);
		}

		public void save() {
			Configuration.getInstance().setAuto(mProxyCheckBox.getSelection());
		}
    }
    
    public void setProxyOn(boolean pOn){
        mProxyServerField.setEnabled(pOn);
        lProxyPortField.setEnabled(pOn);
        mProxyUserIdField.setEnabled(pOn);
        mProxyPasswordField.setEnabled(pOn);
    }
    
    public IConfigurationBinder[] getBindings() {
        return mBinderList;
    }
}