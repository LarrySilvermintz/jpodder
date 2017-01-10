package com.jpodder.ui.swt.util;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 */
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;

import javax.swing.border.BevelBorder;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jpodder.data.configuration.IDataHandler;
import com.jpodder.util.Logger;
import com.jpodder.util.Messages;

/**
 * Dialog to let the user select a backup file for recovery or
 * to stop the bootstraping to recover by hand
 **/
public class RecoverDialog
        extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2141505075020768879L;

	private Logger mLog = Logger.getLogger(getClass().getName());

    private static final int PANEL_HEIGHT = 400;
    private static final int PANEL_WIDTH = 500;

    private static final int INIT = 0;
    private static final int FIRST_SELECTION_PANEL = 10;
    private static final int SECOND_SELECTION_PANEL = 20;
    private static final int THIRD_SELECTION_PANEL = 30;

    private ActionSelectionPanel mActionSelectionPanel = new ActionSelectionPanel();
    private DefaultFilePanel mDefaultFilePanel = new DefaultFilePanel();
    private ListBackupPanel mListBackupFilePanel = new ListBackupPanel();
    private EditFilePanel mEditFilePanel = new EditFilePanel();
    private JPanel mButtonPanel = new JPanel();
    private JPanel mContentPanel = new JPanel();

    private ActionController mActionController = new ActionController();

    private JButton mBackButton = new JButton();
    private JButton mNextButton = new JButton();

    private File mSelectedFile;
    private int mState = INIT;

    public RecoverDialog( JFrame pFrame ) {
        super( pFrame );

        mContentPanel.setLayout( new BorderLayout() );

        mBackButton.addActionListener( mActionController );
        mNextButton.addActionListener( mActionController );

        JPanel lButtonPanel = new JPanel();
        mBackButton.setText( Messages.getString( "general.back" ) );
        lButtonPanel.add( mBackButton );
        mNextButton.setText( Messages.getString( "general.next" ) );
        lButtonPanel.add( mNextButton );

        setContentPanel( mActionSelectionPanel );

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( mContentPanel, BorderLayout.CENTER );
        getContentPane().add( lButtonPanel, BorderLayout.SOUTH );
        getRootPane().setDefaultButton( mNextButton );

        setTitle( Messages.getString( "recover.dialog.title" ) );
//AS        setModal( false );
        setModal( true );
        setSize( PANEL_HEIGHT, PANEL_WIDTH );
        setLocationRelativeTo( pFrame );
//        urlText.requestFocusInWindow();
    }

    public void setContentPanel( AbstractContentPanel pPanel ) {
        mContentPanel.removeAll();
        mContentPanel.add( pPanel, BorderLayout.CENTER );
        pPanel.activate();
        repaint();
    }

    public void show( String pCorruptFile, List pBackupFiles, IDataHandler pDataHandler ) {
        mEditFilePanel.setFileContent( pCorruptFile );
        mListBackupFilePanel.setFiles( pBackupFiles );
        mActionController.setDataHandler( pDataHandler );
        // Initialize the dialog
        mSelectedFile = null;
        mState = INIT;
        setContentPanel( mActionSelectionPanel );
        super.show();
    }

    /** @return Either the fixed file through editing, a selected backup file or null if default should be used **/
    public File getFile() {
        return mSelectedFile;
    }

    private class ActionSelectionPanel
            extends AbstractContentPanel {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1260954522563858427L;
		private JRadioButton mDefaultFile;
        private JRadioButton mBackupFile;
        private JRadioButton mEditFile;

        public ActionSelectionPanel() {
            super( "recover.action.title", INIT );
            mDefaultFile = new JRadioButton(
                Messages.getString( "recover.action.default.label" )
            );
            mBackupFile = new JRadioButton(
                Messages.getString( "recover.action.backup.label" )
            );
            mEditFile = new JRadioButton(
                Messages.getString( "recover.action.edit.label" )
            );
            ButtonGroup lGroup = new ButtonGroup();
            lGroup.add( mDefaultFile );
            lGroup.add( mBackupFile );
            lGroup.add( mEditFile );

            JPanel lPanel = new JPanel( new GridLayout( 3, 1 ) );
            lPanel.add( mDefaultFile );
            lPanel.add( mBackupFile );
            lPanel.add( mEditFile );
            
            add( lPanel );
        }

        public int getSelection() {
            if( mDefaultFile.isSelected() ) {
                return FIRST_SELECTION_PANEL;
            } else if( mBackupFile.isSelected() ) {
                return SECOND_SELECTION_PANEL;
            } else if( mEditFile.isSelected() ) {
                return THIRD_SELECTION_PANEL;
            } else {
                return -1;
            }
        }

        public void activate() {
            super.activate();
            mNextButton.setText( Messages.getString( "general.next" ) );
            mNextButton.setEnabled( true );
            mBackButton.setEnabled( false );
        }
    }

    private class DefaultFilePanel
            extends AbstractContentPanel {

        /**
		 * 
		 */
		private static final long serialVersionUID = 8541479239466968604L;

		public DefaultFilePanel() {
            super( "recover.default.title", FIRST_SELECTION_PANEL );
            setLayout( new BorderLayout() );
            add(
                new JLabel(
                    "<html><body>" +
                    Messages.getString( "recover.default.explanation.html" ) +
                    "</body></html>"
                ),
                BorderLayout.NORTH
            );
        }

        public void activate() {
            super.activate();
            mNextButton.setEnabled( true );
            mBackButton.setEnabled( true );
            mNextButton.setText( Messages.getString( "general.ok" ) );
        }
    }

    private class ListBackupPanel
            extends AbstractContentPanel {

        /**
		 * 
		 */
		private static final long serialVersionUID = -8062227336196703919L;
		private JList mFileList;
        private List mFiles;

        public ListBackupPanel() {
            super( "recover.backup.title", SECOND_SELECTION_PANEL );
            setLayout( new BorderLayout() );
            add(
                new JLabel(
                    "<html><body>" +
                    Messages.getString( "recover.backup.explanation.html" ) +
                    "</body></html>"
                ),
                BorderLayout.NORTH
            );
            mFileList = new JList();
            mFileList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            mFileList.addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged( ListSelectionEvent pEvent ) {
                        if( !pEvent.getValueIsAdjusting() ) {
                            JList lList = (JList) pEvent.getSource();
                            int lSelectedIndex = lList.getMinSelectionIndex();
                            mNextButton.setEnabled( lSelectedIndex >= 0 );
                        }
                    }
                }
            );
            JScrollPane lScroller = new JScrollPane( mFileList );
            add( lScroller, BorderLayout.CENTER );
        }

        public void setFiles( List pFiles ) {
            mFiles = pFiles;
            String[] lFiles = new String[ pFiles.size() ];
            for( int i = 0; i < lFiles.length; i++ ) {
                File lFile = (File) pFiles.get( i );
                lFiles[ i ] = lFile.getName();
            }
            mFileList.setListData( lFiles );
        }

        public File getSelection() {
            File lReturn = null;
            int lSelectedIndex = mFileList.getMinSelectionIndex();
            if( lSelectedIndex >= 0 && lSelectedIndex < mFiles.size() ) {
                lReturn = (File) mFiles.get( lSelectedIndex );
            }
            return lReturn;
        }

        public void activate() {
            super.activate();
            mNextButton.setEnabled( false );
            mBackButton.setEnabled( true );
            mNextButton.setText( Messages.getString( "general.ok" ) );
        }
    }

    private class EditFilePanel
            extends AbstractContentPanel {

        /**
		 * 
		 */
		private static final long serialVersionUID = 5504499490958644053L;
		private JTextArea mArea;

        public EditFilePanel() {
            super( "recover.edit.title", THIRD_SELECTION_PANEL );
            setLayout( new BorderLayout() );
            add(
                new JLabel(
                    "<html><body>" +
                    Messages.getString( "recover.edit.explanation.html" ) +
                    "</body></html>"
                ),
                BorderLayout.NORTH
            );
            mArea = new JTextArea();
            JScrollPane lScroller = new JScrollPane( mArea );
            add( lScroller, BorderLayout.CENTER );
        }

        public String getFileContent() {
            return mArea.getText();
        }

        public void setFileContent( String pFile ) {
            mArea.setText( pFile );
        }

        public void activate() {
            super.activate();
            mNextButton.setEnabled( true );
            mBackButton.setEnabled( true );
            mNextButton.setText( Messages.getString( "general.ok" ) );
        }
    }

    private abstract class AbstractContentPanel
            extends JPanel {

        private int mMyState;

        public AbstractContentPanel( String pTitleKey, int pState ) {
            setBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createBevelBorder( BevelBorder.LOWERED ),
                    Messages.getString( pTitleKey )
                )
            );
            mMyState = pState;
        }

        public void activate() {
            mState = mMyState;
        }
    }

    private class ActionController
            implements ActionListener {

        public ActionController() {
        }

        public void actionPerformed( ActionEvent pEvent ) {
            // Check the button pressed
            Object lSource = pEvent.getSource();
            if( com.jpodder.util.Debug.WITH_DEV_DEBUG ) { mLog.devDebug( "actionPerformed(), state: " + mState ); }
            if( lSource == mBackButton ) {
                switch( mState ) {
                    case INIT:
                        // Ignore it (nothing to go back to)
                        break;
                    case FIRST_SELECTION_PANEL:
                    case SECOND_SELECTION_PANEL:
                    case THIRD_SELECTION_PANEL:
                        // Go back to the init panel
                        setContentPanel( mActionSelectionPanel );
                        break;
                }
            } else if( lSource == mNextButton ) {
                switch( mState ) {
                    case INIT:
                        // Check the selection and set the content panel accordingly
                        switch( mActionSelectionPanel.getSelection() ) {
                            case FIRST_SELECTION_PANEL:
                                setContentPanel( mDefaultFilePanel );
                                break;
                            case SECOND_SELECTION_PANEL:
                                setContentPanel( mListBackupFilePanel );
                                break;
                            case THIRD_SELECTION_PANEL:
                                setContentPanel( mEditFilePanel );
                                break;
                        }
                        break;
                    case FIRST_SELECTION_PANEL:
                        mSelectedFile = null;
                        hide();
                        break;
                    case SECOND_SELECTION_PANEL:
                        mSelectedFile = mListBackupFilePanel.getSelection();
                        hide();
                        break;
                    case THIRD_SELECTION_PANEL:
                        String lContent = mEditFilePanel.getFileContent();
                        try {
                            mSelectedFile = File.createTempFile( "jpodder.config.", "xml" );
                        } catch( Exception e ) {
                            if( com.jpodder.util.Debug.WITH_DEV_DEBUG ) { mLog.devDebug( "actionPerformed(), create temp file failed", e ); }
                        }
                        hide();
                        break;
                }
            }
        }

        public void setDataHandler( IDataHandler pDataHandler ) {
        }
    }
}