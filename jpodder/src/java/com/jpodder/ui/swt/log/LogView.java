package com.jpodder.ui.swt.log;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.jpodder.FileHandler;
import com.jpodder.data.content.ContentAssociation;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.IView;
import com.jpodder.util.Messages;

/**
 * A logview.
 */
public class LogView implements IView {

    protected Button mClearButton;
    private Logger mLog = Logger.getLogger(getClass().getName());
    protected Composite mView;
    protected LogArea mLogArea;
    protected SashForm mSashForm;
    
    public LogView() {
        mLog.info("<init>");
        initializeUI();
    }

    /**
     * The UI is a Composite which has a GridLayout. 
     * The top Composite is a button pane, with some actions for the log view.
     * The Bottom is a Sash which shows the Application and Torrent log views. 
     */
    public void initializeUI() {

        mView = new Composite(UILauncher.getInstance().getShell(), SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        mView.setLayout(gridLayout);

        // --------- Button Group

        Group lButtonGroup = new Group(mView, SWT.SHADOW_IN);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        lButtonGroup.setLayoutData(gridData);
        RowLayout lButtonRowLayout = new RowLayout(SWT.HORIZONTAL);
        lButtonRowLayout.wrap = false;
        lButtonRowLayout.pack = true;
        lButtonGroup.setLayout(lButtonRowLayout);

        mClearButton = new Button(lButtonGroup, SWT.PUSH);
        mClearButton.setText(Messages.getString("gui.clearLog"));
        mClearButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                mLogArea.removeAll();
            }
        });
        RowData lData =new RowData();
        mClearButton.setLayoutData(lData);
        
//        mShowTorrentLogButton = new Button(lButtonGroup, SWT.Selection);
        
        Link lLogFileLink = new Link(lButtonGroup, SWT.NONE);
        lData = new RowData();
        lLogFileLink.setLayoutData(lData);
        
        lLogFileLink.setText("<a href=\"jPodder.log\">"+ Messages.getString("log.open")+"</a>");
        lLogFileLink.addListener (SWT.Selection, new Listener () {
            public void handleEvent(Event event) {
                String lLogFilePath = FileHandler.sBinDirectory.getPath() + File.separator + "jPodder.log";
                ContentAssociation.openProgram("log", lLogFilePath);
            }
        });
        
        // --------- App Log view.

        mSashForm = new SashForm(mView, SWT.VERTICAL);
        gridData = new GridData(GridData.FILL_BOTH);
        mSashForm.setLayoutData(gridData);
        
        mLogArea = new LogArea(mSashForm);
        mLogArea.getLog().setSize(3000, 400);
        
        new Text(mSashForm, SWT.MULTI);
        mSashForm.setWeights(new int[] { 100, 0 });
        mView.addDisposeListener(new DisposeListener(){

            public void widgetDisposed(DisposeEvent arg0) {
                // Remove the appender for LOG4J
                mLogArea.dispose();
            }
        });
    }

    public Composite getView() {
        return mView;
    }

    public void setLogSize(int pLogSize) {
        mLogArea.setLogSize(pLogSize);
    }

    public boolean isStatic() {
        return true;
    }

    public void setStatic(boolean pStatic) {
        // this view is always statci.
        
    }
}
