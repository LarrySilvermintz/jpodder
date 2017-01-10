package com.jpodder.ui.swt.conf;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.ui.swt.IController;
import com.jpodder.ui.swt.IView;
import com.jpodder.ui.swt.comp.BaseAction;
import com.jpodder.ui.swt.conf.panel.IConfigurationPanel;
import com.jpodder.util.Messages;

/**
 * Properties control class. The class get updates from the GUI. it holds the
 * latest properties.
 * 
 */
public class ConfigurationController implements IController,
        IConfigurationListener {

    private static Logger sLog = Logger.getLogger(ConfigurationController.class
            .getName());

    public BaseAction propsSaveAction;
    public TreeSelector mTreeSelectionAction;
    private ConfigurationView mView;
    protected IConfigurationPanel mCurrentPanel;
    public String mCurrentNode;
    protected ConfigurationController mSelf;

    /**
     * Constructor.
     * 
     * @param _currentProps
     *            Properties
     */
    public ConfigurationController() {
        initialize();
    }

    public void initialize() {
        sLog.info("<init>");
        mTreeSelectionAction = new TreeSelector();
        mSelf = this;
    }

    public void setControllers() {
    }

    public void setView(IView pView) {
        mView = (ConfigurationView) pView;
        mView.getView().addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent arg0) {
                ConfigurationLogic.getInstance().removeConfigListener(mSelf);
            }
        });
        initializeUI();
        ConfigurationLogic.getInstance().addConfigListener(mSelf);
    }

    public void initializeUI() {

        propsSaveAction = new SaveAction();
        mView.propsSaveButton.setText(propsSaveAction.getName());
        mView.propsSaveButton.addListener(SWT.Selection, propsSaveAction);
        mView.mConfTree.getViewer().addTreeListener(mTreeSelectionAction);
        mView.mConfTree.getTree().addSelectionListener(mTreeSelectionAction);
    }

    public class TreeSelector implements ITreeViewerListener, SelectionListener {

        protected Composite mSelection;

        public void widgetSelected(SelectionEvent e) {
            Tree lTree = (Tree) e.widget;
            TreeItem[] t = lTree.getSelection();
            Object o = ((TreeItem) t[0]).getData();

            if (o instanceof ConfigurationNode) {
                ConfigurationNode lNode = (ConfigurationNode) o;

                mCurrentNode = lNode.mNodeName;

                mView.mPanels.dispose();
                mView.mPanels = new Composite(mView.getView(), SWT.NONE);
                GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
                        | GridData.VERTICAL_ALIGN_FILL);
                gridData.horizontalSpan = 1;
                gridData.widthHint = 300;
                mView.mPanels.setLayoutData(gridData);
                mView.mPanels.setLayout(new FillLayout());

                Composite lCompositePanel = null;
                IConfigurationPanel lPanel = lNode.panel;

                if (lPanel != null && lPanel instanceof IConfigurationPanel) {
                    ((IConfigurationPanel) lPanel).initialize(mView.mPanels);
                    lCompositePanel = ((IConfigurationPanel) lPanel).getView();
                    IConfigurationBinder[] lBindings = ((IConfigurationPanel) lPanel)
                            .getBindings();
                    if (lBindings != null) {
                        setUIValues(lBindings);
                    }
                } else {
                    lCompositePanel = new Composite(mView.mPanels, SWT.NONE);
                }

                if (lCompositePanel != null) {
                    mView.getView().layout(true);
                } else {
                    // An unassigned tree entry
                }

                if (lPanel != null) {
                    mCurrentPanel = (IConfigurationPanel) lPanel;
                }
            }
        }

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void treeCollapsed(TreeExpansionEvent event) {
        }

        public void treeExpanded(TreeExpansionEvent event) {
        }
    }

    public ConfigurationView getView() {
        return mView;
    }

    public class SaveAction extends BaseAction {

        public SaveAction() {
            init(Messages.getString("propertiescontrol.save"), true);
        }

        public void handleEvent(Event event) {
            if (mCurrentPanel != null && !mCurrentPanel.getView().isDisposed()
                    && mCurrentPanel.getBindings() != null) {
                getUIValues(mCurrentPanel.getBindings());
                sLog.info(Messages.getString("propertiescontrol.savelog"));
            }
            sLog.info("Configuration saved, notify listeners...");
            ConfigurationLogic.getInstance().fireConfigurationChanged(
                    new ConfigurationEvent(ConfigurationLogic.class));
        }

		public void setControls(Widget[] pControls) {
			// TODO Implement
		}
    };

    protected void setUIValues(IConfigurationBinder[] lBinderCollection) {
    	if(lBinderCollection == null){
    		return;
    	}
        for (int i = 0; i < lBinderCollection.length; i++) {
            IConfigurationBinder lBinder = lBinderCollection[i];
            if (lBinder != null) {
                lBinder.read();
            }
        }
    }

    protected void getUIValues(IConfigurationBinder[] lBinderCollection) {
        for (int i = 0; i < lBinderCollection.length; i++) {
            IConfigurationBinder lBinder = lBinderCollection[i];
            if (lBinder != null) {
                lBinder.save();
            }
        }
    }

    /**
     * We listen to property changes fired by other controllers. to update the
     * UI. Note the value is not passed by the property event.
     */
    public void configurationChanged(ConfigurationEvent event) {
    	//we don't update if no panel is active :-) 
    	if(mCurrentPanel == null  ){
    		return;
    	}
        if (event.getSource() != ConfigurationLogic.class) {
            String lPropertyName = event.getPropertyName();
            setUIValues(mCurrentPanel.getBindings());
        }
    }
}