package com.jpodder.ui.swt.conf.panel.language;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.jpodder.JPodderException;
import com.jpodder.data.language.LanguageLogic;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.comp.DisplayTool;
import com.jpodder.ui.swt.conf.panel.LanguagePanel;
import com.jpodder.ui.swt.conf.panel.language.TranslationModel.TranslationEntry;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com" >Christophe Bouhier</a>
 * @version 1.1
 */
public class TranslationDialog implements TranslationListener {

	private final LanguagePanel mPanel;

	protected Button mOKButton;

	protected Button mSaveButton;

	protected Button mCancelButton;

	protected Button mSaveAsButton;

	protected Label mFileNameLabel;

	protected Label mTotalKeysLabel;

	protected Label mIdenticalValuesLabel;

	protected Label mTodoValuesLabel;

	protected Button mShowEmpty;

	protected File mResourceFile = null;

	protected TranslationTable mTable;

	protected TranslationModel mModel;

	protected ShowEmptyFilter mEmptyFilter = new ShowEmptyFilter();
	
	protected Shell mSelf;

	public Shell getShell() {
		return mSelf;
	}

	public TranslationDialog(LanguagePanel pPanel, Shell lShell) {
		mPanel = pPanel;
		mSelf = new Shell(lShell, SWT.DIALOG_TRIM);

		GridLayout lLayout = new GridLayout();
		lLayout.numColumns = 1;
		mSelf.setLayout(lLayout);

		Group lInfoGroup = new Group(mSelf, SWT.NONE);
		GridData lData = new GridData(GridData.FILL_HORIZONTAL);
		lInfoGroup.setLayoutData(lData);

		GridLayout lInfoLayout = new GridLayout();
		lInfoLayout.numColumns = 3;
		lInfoGroup.setLayout(lInfoLayout);

		mFileNameLabel = new Label(lInfoGroup, SWT.NONE);
		lData = new GridData();
		lData.horizontalSpan = 3;
		mFileNameLabel.setLayoutData(lData);

		final String lEmptyString = Messages.getString("languagepanel.edit.showempty");
		final String lAllString = Messages.getString("languagepanel.edit.showall");

		mShowEmpty = new Button(lInfoGroup, SWT.PUSH);
		mShowEmpty.setText(lEmptyString);
		
		
		mShowEmpty.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				String lText = mModel.getShowEmptyOnly() ?  lEmptyString  : lAllString;
				mShowEmpty.setText(lText);
				
				mModel.setShowEmptyOnly(!mModel.getShowEmptyOnly());
				
				// Filter is only applied on the first column.
				if(mModel.getShowEmptyOnly()){
					mTable.getTableViewer().addFilter(mEmptyFilter);	
				}else{
					mTable.getTableViewer().removeFilter(mEmptyFilter);
				}
			}
		});

		mTotalKeysLabel = new Label(lInfoGroup, SWT.NONE);
//		mIdenticalValuesLabel = new Label(lInfoGroup, SWT.NONE);
		mTodoValuesLabel = new Label(lInfoGroup, SWT.NONE);

		mTable = new TranslationTable(mSelf);

		Composite lButtonPanel = new Composite(mSelf, SWT.NONE);
		lData = new GridData(GridData.FILL_HORIZONTAL);
		lData.horizontalSpan = 3;
		lData.horizontalAlignment = GridData.CENTER;
		lButtonPanel.setLayoutData(lData);

		RowLayout lButtonLayout = new RowLayout();
		lButtonPanel.setLayout(lButtonLayout);

		mOKButton = new Button(lButtonPanel, SWT.PUSH);
		mOKButton.setText(Messages.getString("general.ok"));
		mOKButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				mSelf.close();
			}
		});

		mSaveButton = new Button(lButtonPanel, SWT.PUSH);
		mSaveButton.setText(Messages.getString("general.save"));
		mSaveButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {

				TreeMap lEdited = mModel.getTranslationMap();
				LanguageLogic.getInstance().saveFile(mResourceFile, lEdited);
				
				MessageDialog.openInformation(UILauncher.getInstance().getShell(),
						Messages.getString("languagepanel.edit.send.title"),
						Messages.getString(
								"languagepanel.edit.send.description",
								mResourceFile.getAbsolutePath()));
				int lEmptyKeys = mModel.translationCount();
				mTodoValuesLabel.setText(Messages
						.getString("languagepanel.edit.open")
						+ lEmptyKeys);
				mSaveButton.setEnabled(false);
			}
		});

		mCancelButton = new Button(lButtonPanel, SWT.PUSH);
		mCancelButton.setText(Messages.getString("general.cancel"));
		mCancelButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {
				mSelf.close();
			}
		});
		lButtonPanel.pack();
		mSelf.setDefaultButton(mCancelButton);
		mSelf.setLocation(DisplayTool.getCenterPosition(mSelf.getSize()));
	}
	
	
	
	class ShowEmptyFilter extends ViewerFilter{

		public boolean select(Viewer pViewer, Object pParent, Object pElement) {
				if(pElement instanceof TranslationEntry){
					
				String pString = ((TranslationEntry)pElement).getTranslation();
				if(pString.length() == 0 || pString.equals("<String>")){
					return true;
				}
			}
			return false;
		}
    	
    }

	
	/**
	 * Fill the language editor. First try to load the existing file as a
	 * resource bundle. If this fails we load the bundle from the classloader.
	 * If this also fails we use a new file in the default location. (\lib). We
	 * set the model for the table and reset action status for saving an edited
	 * language key/value.
	 * <p>
	 * 
	 * @param pLocale
	 */
	public void fillDialog(Locale pLocale) {
		ResourceBundle lEditBundle = null;
		// We get the file for this locale, and open it
		// as a bundle if it contains something.
		mResourceFile = Messages.getFile(pLocale);
		boolean tryClassLoader = true;
		try {
			if (mResourceFile.length() > 0) {
				lEditBundle = Messages.getResourceBundle(pLocale,
						mResourceFile);
				tryClassLoader = false;
			}
		} catch (JPodderException e) {
			// Bundle doesn't exit.
//			lPanel.mLog.warn("Failure to load language bundle for: "
//					+ mResourceFile.getAbsolutePath());
			tryClassLoader = true;
		}
		if (tryClassLoader) {
			try {
				lEditBundle = Messages.getInstance().getResourceBundle(pLocale);
			} catch (JPodderException e1) {
//				lPanel.mLog.warn("Failure to load language bundle for: "
//						+ pLocale.getDisplayLanguage());
			}
		}

		if (lEditBundle == null) {
//			lPanel.mLog.warn("Loading new language bundle in editor for:"
//					+ pLocale.getDisplayLanguage());
		}

		ResourceBundle lMasterBundle = Messages.getMasterBundle();

		mModel = new TranslationModel(lMasterBundle, lEditBundle);

		mModel.addListener(this);

		mTable.getTableViewer().setLabelProvider(
				new TranslationLabelProvider(mModel));
		
		mTable.getTableViewer().setInput(mModel);

		if (mResourceFile != null) {
			mFileNameLabel.setText(mResourceFile.getAbsolutePath());
		}

		int lTotalKeys = mModel.keyCount();
		int lEmptyKeys = mModel.translationCount();

		mTotalKeysLabel.setText(Messages.getString("languagepanel.edit.total")
				+ lTotalKeys);
		mTodoValuesLabel.setText(Messages.getString("languagepanel.edit.open")
				+ lEmptyKeys);

		mSelf.setText(Messages.getString("languagepanel.edit.dialog", pLocale
				.getDisplayLanguage()));
		mSaveButton.setEnabled(false);
	}

	public void translationOccured(TranslationEvent pEvent) {
		mSaveButton.setEnabled(true);
	}
}