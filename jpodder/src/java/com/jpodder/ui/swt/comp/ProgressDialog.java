package com.jpodder.ui.swt.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.jpodder.tasks.ITask;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.util.Messages;

/**
 * A generic task progress dialog. Set the title and task to intiate the
 * progress of the task.
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class ProgressDialog {

    protected Shell mShell;
    protected ProgressBar mProgressBar;
    protected ITask mTask;
    protected Timer mTimer;

    public ProgressDialog(String pTitle) {
        mShell = new Shell(UILauncher.getInstance().getShell(), SWT.DIALOG_TRIM
                | SWT.APPLICATION_MODAL);
        mShell.setText(pTitle);
        mShell.setSize(300, 80);
        
        mShell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent arg0) {
                mTimer.stop();
                mTask.setCancelled(true);
            }
        });
        
        
        GridLayout lLayout = new GridLayout();
        lLayout.numColumns = 1;
        mShell.setLayout(lLayout);
        mProgressBar = new ProgressBar(mShell, SWT.NONE);
        GridData lData = new GridData(GridData.FILL_HORIZONTAL);
        mProgressBar.setLayoutData(lData);

        Composite lButtonPanel = new Composite(mShell, SWT.NONE);
        RowLayout lRowLayout = new RowLayout();
        lRowLayout.pack = true;
        lButtonPanel.setLayout(lRowLayout);

        Button lOkButton = new Button(lButtonPanel, SWT.PUSH);
        lOkButton.setText(Messages.getString("general.ok"));
        lOkButton.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event arg0) {
                mTask.setCancelled(true);
                mTimer.stop();
                mShell.setVisible(false);
            }
        });

        // Create a timer.
        mTimer = new Timer(20, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        
                        String s = mTask.getMessage();
                        if (s != null) {
                            // CXB TODO
                            // mFeedBackLabel.setText(s);
                        }
                        if (mTask.isDone()) {
                            mProgressBar.setSelection(mTask.getLengthOfTask());
                            mTimer.stop();
                            // mProgressBar.setValue(progressBar.getMinimum());
                            if(!mShell.isDisposed()){
                                mShell.setVisible(false);    
                            }
                        }else{
                            mProgressBar.setSelection(mTask.getCurrent());                            
                        }
                    }
                });
            }
        });
//        mShell.pack();
        mShell.setLocation(DisplayTool.getCenterPosition(mShell.getSize()));
    }

    
    
    
    
    /**
     * When we set the task, the progress is launched automaticly.
     * 
     * @param pTask
     */
    public void setTask(ITask pTask) {
        mShell.setVisible(true);
        mTask = pTask;
        mProgressBar.setMaximum(mTask.getLengthOfTask());
        mTimer.start();
    }

    public void makeModal() {
        Display display = mShell.getParent().getDisplay();
        while (!mShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }
}
