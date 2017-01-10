package com.jpodder.ui.swt.conf.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

import com.jpodder.ui.swt.conf.IConfigurationBinder;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 * 
 * TODO, Enabling or Disabling has no function today. (Should be config entry).
 */
public class MimePanel implements IConfigurationPanel {

    protected Composite mView;
    protected List mOSProgramList;
    protected Button mUseOSProgramCheck;
    
    
    public MimePanel(){
    }
    
    public IConfigurationBinder[] getBindings() {
        return null;
    }

    public void initialize(Composite pParent) {
        mView = new Composite(pParent, SWT.NONE);

        // ------- Plugin scanner and description
        FormLayout lMainLayout = new FormLayout();
        mView.setLayout(lMainLayout);

        Group lMiscGroup = new Group(mView, SWT.SHADOW_IN);
        lMiscGroup.setText(Messages.getString("mimepanel.association.content"));				
        FormData formData3 = new FormData();
        formData3.top = new FormAttachment(0, 5);
        formData3.left = new FormAttachment(0, 5);
        formData3.right = new FormAttachment(100, -5);
        lMiscGroup.setLayoutData(formData3);

        GridLayout lMiscGridLayout = new GridLayout();
        lMiscGridLayout.numColumns = 3;
        lMiscGroup.setLayout(lMiscGridLayout);

        mUseOSProgramCheck = new Button(lMiscGroup, SWT.CHECK);
        mUseOSProgramCheck
                .setText(Messages.getString("mimepanel.association.title"));
        mUseOSProgramCheck.setSelection(true);
        
        
        mUseOSProgramCheck.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (mUseOSProgramCheck.getSelection()) {
                    loadOSProgram();
                    mOSProgramList.setEnabled(true);
                }else{
                    mOSProgramList.setEnabled(false);
                }
            }
        });
        GridData lData = new GridData();
        lData.horizontalSpan = 3;
        lData.grabExcessHorizontalSpace = true;
        mUseOSProgramCheck.setLayoutData(lData);

        mOSProgramList = new List(lMiscGroup, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
    	loadOSProgram();

        lData = new GridData();
        lData.horizontalSpan = 3;
        lData.grabExcessHorizontalSpace = true;
        lData.widthHint = 300;
        lData.heightHint = 300;
        mOSProgramList.setLayoutData(lData);
    }

    private void loadOSProgram() {
        mOSProgramList.removeAll();
        Program[] lPrograms = Program.getPrograms();
        for (int i = 0; i < lPrograms.length; i++) {
            Program program = lPrograms[i];            
            mOSProgramList.add(program.toString());    
        }
    }

    public Composite getView() {
        return mView;
    }
}
