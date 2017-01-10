package com.jpodder.ui.swt.feeds.manager;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.1
 */
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jpodder.data.feeds.IXFeedListener;
import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.XFeedLogic;
import com.jpodder.data.feeds.stats.XFeedEvent;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.comp.DisplayTool;
import com.jpodder.ui.swt.feeds.FeedController;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Messages;

/**
 * This view displays the non-volatile (Non-RSS) feed information. This is: The
 * Subscription state. The maximum number of downloads.
 */
public class FeedDialog implements IXFeedListener, CTabFolder2Listener {

    private Logger mLog = Logger.getLogger(getClass().getName());
    public static final int EDIT_MODE = 501;
    public static final int ADD_MODE = 502;
    
    public static final int OK_SELECTED = 1;
    public static final int CANCEL_SELECTED = 2;
    
    protected Button previewButton;
    protected Button mOKButton;
    protected Label statusLabel;
    protected Shell mShell;
    protected final CTabFolder mTabs;

    protected final String GENERAL_TAB;
    // CB TODO, not ported yet. (Media tab)
    //    protected final String MEDIA_TAB;
//    protected final String HISTORY_TAB;
    protected final String RSS_TAB;
    
    public FeedRSSPanel mRssPanel;;
    public FeedGeneralPanel mGeneralPanel;
    
    private IXPersonalFeed mFeed = null;

    int result = -1;
    private int mCurrentMode = ADD_MODE;
    private FeedController mFeedController;
    public FeedDialog(FeedController pController) {
        mFeedController = pController;

        mShell = new Shell(UILauncher.getInstance().getShell(), SWT.DIALOG_TRIM | SWT.RESIZE);
        mShell.setImage(UITheme.getInstance().getImages().get(UITheme.IMAGE_JPODDER));
        mShell.setText(Messages.getString("feedInfoview.manage"));
        
        GridLayout lLayout = new GridLayout();
        lLayout.numColumns = 1;
        mShell.setLayout(lLayout);
        
        // CB TODO Some actions from the subpanels.
//        mTagPanel.tagSelectButton.setAction(new TagSelectAction());
    
        GENERAL_TAB = Messages.getString("feedInfoview.general");
//        MEDIA_TAB = Messages.getString("feedInfoview.media");
//        HISTORY_TAB = Messages.getString("feedInfoview.history");
        RSS_TAB = Messages.getString("feedInfoview.rss");
        
        mTabs = new CTabFolder(mShell, SWT.BORDER);
        mTabs.addCTabFolder2Listener(this);
        mGeneralPanel = new FeedGeneralPanel(mTabs); 
        mRssPanel = new FeedRSSPanel(mTabs);
        addTab(GENERAL_TAB,mGeneralPanel , 0);
//        addTab(MEDIA_TAB, null, 1);
//        addTab(HISTORY_TAB, null, 2);
        addTab(RSS_TAB, mRssPanel, 1);
//        addTab(Messages.getString("feedspacepanel.title"),mSpacePanel);
        GridData lData = new GridData(GridData.FILL_BOTH);
        mTabs.setLayoutData(lData);
        
        Composite lButtonPanel = new Composite(mShell, SWT.NONE);
        RowLayout lButtonLayout = new RowLayout();
        lButtonLayout.pack = false;
        lButtonPanel.setLayout(lButtonLayout);
        
        mOKButton = new Button(lButtonPanel, SWT.PUSH);
        mOKButton.setText(Messages.getString("general.ok"));
        mOKButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                result = OK_SELECTED;
                
                mGeneralPanel.setFeedValues(mFeed);
                //              save the tagsettings.
                // CB TODO, Feed ID3 tags.
//                mWorkingFeed.setTagList(mTagPanel.tagTable.getEditedModel());
                mFeedController.updateFeed(mCurrentMode == ADD_MODE,
                        mFeed);
                hide();
            }
        });
        Button cancelButton = new Button(lButtonPanel, SWT.PUSH);
        cancelButton.setText(Messages.getString("general.cancel"));
        cancelButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                // discard changes.
                result = CANCEL_SELECTED;
                hide();
            }
        });

        previewButton = new Button(lButtonPanel, SWT.PUSH);
        // In EDIT_MODE we need to refresh the feed in the background
        // as well as the feed in the dialog.
        // We have special preview function which creates a
        // temporary Feed. We also add a tasklistener so we
        // can show the status of the feed and add an RSS tab.
        previewButton.setText(Messages.getString("feedcontrol.previewFeed"));
        previewButton.setImage(UITheme.getInstance().getImages().get(UITheme.IMAGE_PREVIEW));
        previewButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                statusLabel.setText(Messages.getString("feedInfoview.preview"));
                mGeneralPanel.setFeedValues(mFeed);
                XFeedLogic.getInstance().previewFeed(FeedDialog.class,
                        mFeed);
            }
        });

        statusLabel = new Label(lButtonPanel, SWT.NONE);
        
        mShell.pack();
        mShell.setLocation(DisplayTool.getCenterPosition(mShell.getSize()));
        mShell.setDefaultButton(mOKButton);
        
    }

    public CTabItem addTab(String pTitle, Control lControl, int pPosition) {
        CTabItem lItem = new CTabItem(mTabs, SWT.CLOSE, pPosition <= mTabs
                .getItemCount() ? pPosition : mTabs.getItemCount());
        lItem.setText(pTitle);
        if (lControl != null) {
            lControl.setParent(mTabs);
            lItem.setControl(lControl);
        } else {
            ScrolledComposite lSC = new ScrolledComposite(mTabs, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
//            lSC.setLayout(new FillLayout());
            lSC.setExpandVertical(true);
            lSC.setExpandHorizontal(true);
            Text text = new Text(lSC, SWT.MULTI);
            lSC.setContent(text);
//            lSC.setMinHeight(400);
//            lSC.setMinWidth(400);
            text.setText(pTitle);
            lItem.setControl(lSC);
        }
        return lItem;
    }

    public CTabItem getTab(String pTitle) {
        CTabItem[] lItems = mTabs.getItems();
        for (int i = 0; i < lItems.length; i++) {
            CTabItem item = lItems[i];
            if (pTitle.equals(item.getText())) {
                return item;
            }
        }
        return null;
    }

    
    public void hide() {
        XFeedLogic.getInstance().removeListener(this);
        mShell.close();
    }

    public void show(int pMode, IXPersonalFeed pFeed) {
        XFeedLogic.getInstance().addListener(this);
        mFeed = pFeed;
        mCurrentMode = pMode;
        // Add a task listner for notification of feed collection result.
        XFeedLogic.getInstance().addListener(this);

        if (mCurrentMode == EDIT_MODE) {
            mGeneralPanel.fill(pFeed);
            mRssPanel.fill(mFeed);
            mOKButton.setText(Messages.getString("general.ok"));
        } else {
            mGeneralPanel.fillDefault(pFeed);
            mOKButton.setText(Messages.getString("general.add"));
            mTabs.setSelection(0); // Go back to first tab.
        }
        mGeneralPanel.mFeedFolderButton.addListener(SWT.Selection, mGeneralPanel.new FolderSelector(mFeed, mShell));

        // Disable some tabs, which are not revelant in Add mode.
//        CTabItem lItem = getTab(HISTORY_TAB);
//        lItem.getControl().setEnabled(mCurrentMode == EDIT_MODE);
        CTabItem lItem = getTab(RSS_TAB);
        lItem.getControl().setEnabled(mCurrentMode == EDIT_MODE);
        mShell.open();
    }


    /**
     * Fill the various fields with appropriate values from the feed object.
     * 
     * @param feed
     */
    private void fill(IXPersonalFeed pFeed) {
        mFeed = pFeed;
        mGeneralPanel.fill(mFeed);
        mRssPanel.fill(mFeed);
        
// CB TODO, Migrate history panel.
//        ////////////////////////////////////////////////////////
//        // HISTORY
//        ////////////////////////////////////////////////////////
//        mHistoryPanel.getDateField().setText("");
//        mHistoryPanel.getEventField().setText("");
//        XFeedEventHistory his = mWorkingFeed.getHistory();
//        his.getEvents();
//
//        DefaultComboBoxModel comboModel = new DefaultComboBoxModel(new Vector(
//                his.getEvents()));
//        mHistoryPanel.getHistoryCombo().setModel(comboModel);
//        if (his.getEvents().size() > 0) {
//            mHistoryPanel.getHistoryCombo().setSelectedIndex(-1); // Funny
//            // JComboBox
//            // behaviour????
//            mHistoryPanel.getHistoryCombo().setSelectedIndex(0);
//        }
//
//        ////////////////////////////////////////////////////////
//        // TAGS
//        ////////////////////////////////////////////////////////
//        if (mWorkingFeed.getTagList() != null) {
//            mTagPanel.tagTable.setModel(mWorkingFeed.getTagList());
//            ((ID3Table.TagTableModel) mTagPanel.tagTable.getModel())
//                    .fireTableDataChanged();
//        }
    }

    /**
     * Satisfy TaskListener. 
     * As this method is called from the non-UI thread, it
     * executes UI updates asynchroneounsly
     */
    public void instructionSucceeded(XFeedEvent e) {
        Object src = e.getSource();
        // Check is this notification is for me.
        if (assertSource(src)) {
            final IXPersonalFeed lFeed;
            Object subject = e.getSubject();
            if (subject instanceof IXPersonalFeed) {
                lFeed = (IXPersonalFeed) subject;
            } else {
                return;
            }
            if(e.getTask() == XFeedEvent.INSTRUCTION_COLLECT){
            	Display.getDefault().asyncExec(new Runnable() {
                   public void run(){
                       statusLabel.setText("");
                       fill(lFeed);       
                   } 
                });
            }
        }
    }

    public boolean assertSource(Object pSrc) {
        if (pSrc instanceof Class) {
            return ((Class) pSrc).getName().equals(
                    FeedDialog.class.getName());
        } else if (pSrc instanceof String) {
            return ((String) pSrc).equals(FeedDialog.class.getName());
        }
        return true;
    }
    
    /**
     * Satisfy TaskListener. 
     * As this method is called from the non-UI thread, it
     * executes UI updates asynchroneounsly
     */
    public void instructionFailed(XFeedEvent e) {
        Object src = e.getSource();
        // Check is this notification is for me.
        if (assertSource(src)) {
            final IXPersonalFeed lFeed;
            Object subject = e.getSubject();
            if (subject instanceof IXPersonalFeed) {
                lFeed = (IXPersonalFeed) subject;
            } else {
                return;
            }
            if(e.getTask() == XFeedEvent.INSTRUCTION_COLLECT){
            	Display.getDefault().asyncExec(new Runnable() {
                   public void run(){
                       statusLabel.setText("");
                       fill(lFeed);       
                   } 
                });
            }
        }

    }

    /**
     * Satisfy TaskListener.
     */
    public void instructionInfo(XFeedEvent e) {

    }

    public void close(CTabFolderEvent event) {
    }

    public void minimize(CTabFolderEvent event) {
    }

    public void maximize(CTabFolderEvent event) {
    }

    public void restore(CTabFolderEvent event) {
    }

    public void showList(CTabFolderEvent event) {
    }
}