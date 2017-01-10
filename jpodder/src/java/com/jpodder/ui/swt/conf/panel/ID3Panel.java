package com.jpodder.ui.swt.conf.panel;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import com.jpodder.data.id3.ID3Generic;
import com.jpodder.ui.swt.conf.IConfigurationBinder;
import com.jpodder.ui.swt.id3.ID3SelectDialog;
import com.jpodder.ui.swt.id3.ID3Table;
import com.jpodder.util.Messages;

public class ID3Panel implements IConfigurationPanel {

    public IConfigurationBinder[] mBinderList = new IConfigurationBinder[4];
    Logger mLog = Logger.getLogger(getClass().getName());
    protected ID3Table mID3Table;
    protected Composite mView;
    protected ID3ConfigurationBinder mID3Binder;
    
    public ID3Panel() {
    }

    public ID3Panel(Composite pParent) {
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
        lGroup.setLayout(gridLayout);
        lGroup.setText(Messages.getString("id3panel.title"));

        mID3Table = new ID3Table(lGroup);
        
        Composite lButtonPanel = new Composite(lGroup, SWT.NONE);
        GridData lData = new GridData(GridData.FILL_HORIZONTAL);
        lButtonPanel.setLayoutData(lData);
        RowLayout lButtonLayout = new RowLayout();
        lButtonLayout.wrap = false;
        lButtonLayout.pack = true;
        lButtonPanel.setLayout(lButtonLayout);
        
        Button lSelectTagsButton = new Button(lButtonPanel, SWT.PUSH);
        lSelectTagsButton.setText(Messages.getString("id3panel.select.label"));
        lSelectTagsButton.addListener(SWT.Selection, new Listener(){
            public void handleEvent(Event arg0) {
                ID3SelectDialog lDialog = new ID3SelectDialog();
                List lTags = ID3SelectDialog.getID3TagItems();
                lDialog.fill(lTags);
                lDialog.makeModal();
                if( lDialog.getResult() == ID3SelectDialog.OK_SELECTED){
                    lDialog.saveSelection();
                    mLog.info("ID3 Tags saved in generic list");
                    mID3Table.getTableView().setInput(ID3Generic.getInstance().getTagList());
                }else{
                    mLog.info("ID3 Tag selection cancelled");
                }
            }
        });
        mID3Binder = new ID3ConfigurationBinder();
        mBinderList[0] = mID3Binder;
    }

    public IConfigurationBinder[] getBindings() {
        return mBinderList;
    }
    
    class ID3ConfigurationBinder implements IConfigurationBinder{
        public String getName() {
            return null;
        }

        public void read() {
        	mID3Table.getTableView().setInput(ID3Generic.getInstance().getTagList());
        }

        public void save() {
        	List lElements = mID3Table.getEditedElements();
        	ID3Generic.getInstance().setTagList(lElements);
             try {
				ID3Generic.getInstance().writeResource();
			} catch (Exception e) {
				mLog.warn("Can't write generic ID3 tags");
			}
        }
    }
}