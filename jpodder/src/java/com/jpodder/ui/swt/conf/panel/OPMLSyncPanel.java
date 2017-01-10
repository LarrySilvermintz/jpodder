package com.jpodder.ui.swt.conf.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.jpodder.ui.swt.conf.IConfigurationBinder;
import com.jpodder.ui.swt.conf.ConfigurationBinder;
import com.jpodder.util.Messages;

public class OPMLSyncPanel  implements IConfigurationPanel {

    protected Text mOPMLSyncTextField;
    protected Button opmlServerCheck;
    
    protected ConfigurationBinder mOPMLSyncBinder;
    protected IConfigurationBinder[] mBinderArray = new IConfigurationBinder[1];
    
    protected Composite mView;
    
    public OPMLSyncPanel(Composite pParent) {
        initialize(pParent);
    }
    public OPMLSyncPanel() {
        // TODO Auto-generated constructor stub
    }
    public Composite getView(){
        return mView;
    }
    
    public void initialize(Composite pParent) {
        mView = new Composite(pParent, SWT.NONE);
        
        FormLayout lMainLayout = new FormLayout();
        mView.setLayout(lMainLayout);

        Group lGroup = new Group(mView, SWT.SHADOW_IN);
        FormData formData2 = new FormData();
        formData2.top = new FormAttachment(0, 5);
        formData2.left = new FormAttachment(0, 5);
        formData2.right = new FormAttachment(100, -5);
        lGroup.setLayoutData(formData2);
        lGroup.setText(Messages.getString("opmlsyncpanel.title.opml"));

        GridLayout lGroupLayout = new GridLayout();
        lGroup.setLayout(lGroupLayout);
        lGroupLayout.numColumns = 2;
        
        Label mOPMLSyncLabel = new Label(lGroup, SWT.NONE);
        mOPMLSyncLabel.setText(Messages.getString("opmlsyncpanel.text.url"));
        mOPMLSyncTextField = new Text(lGroup, SWT.SINGLE| SWT.BORDER);
        GridData lData = new GridData();
        lData.widthHint = 300;
        lData.grabExcessHorizontalSpace = true;
        mOPMLSyncTextField.setLayoutData(lData);
//        Messages.getString("opmlsyncpanel.title");
        
        try {

            mOPMLSyncBinder = new ConfigurationBinder(mOPMLSyncTextField,
                    IConfigurationBinder.SUB_TYPE_CONFIGURATION, "OMPLSync");
            mBinderArray[0] = mOPMLSyncBinder;
        } catch (Exception e) {
            // CB TODO, report to log.
            e.printStackTrace();
        }

        ///////////////////////////////////////////////////////////////////////////////
        // PROPERTY PANEL - OPML
        ///////////////////////////////////////////////////////////////////////////////

        // CB TODO, Should be removed later.
//        mOpmlServerText = new JTextField();
//        mOpmlServerText.setColumns(25);
//        JPanel opmlPanel = new JPanel();
//
//        JLabel opmlServerLabel = new JLabel();
//        opmlServerCheck = new JButton();
//        //    opmlPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
//        opmlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//        opmlPanel.add(opmlServerLabel);
//        opmlPanel.add(mOpmlServerText);
//        opmlPanel.add(opmlServerCheck);

    }

    public IConfigurationBinder[] getBindings() {
        return mBinderArray;
    }
}