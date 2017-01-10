package com.jpodder.ui.swt.conf.panel;

/**
 * @author <a href="mailto:christophe@kualasoft.com" >Christophe Bouhier</a>
 * @version 1.1
 */

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.jpodder.JPodderException;
import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.language.LanguageLogic;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.conf.ConfigurationBinder;
import com.jpodder.ui.swt.conf.IConfigurationBinder;
import com.jpodder.ui.swt.conf.panel.language.TranslationDialog;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Messages;

/**
 * The language panel offers a selection of available languages in a combo box.
 * After selecting a language, it can be applied by pressing a button.
 * 
 * The collection of available languages can be applied by calling a
 * <code>fillPanel()</code> method
 */
public class LanguagePanel implements IConfigurationPanel {

	Logger mLog = Logger.getLogger(getClass().getName());

	protected Combo mLanguageCombo;

	protected Label mLanguageDescription;

	protected Label mSupportDescription;

	protected Button mEditButton;

	protected IConfigurationBinder mLanguageBinder;

	protected IConfigurationBinder[] mBinderArray = new IConfigurationBinder[1];

	private String lSelection; // for local use only.

	protected Composite mView;

	public LanguagePanel(Composite pParent) {
		initialize(pParent);
	}

	public LanguagePanel() {
	}

	public Composite getView() {
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
		lGroup.setText(Messages.getString("languagepanel.title"));

		GridLayout lGridLayout = new GridLayout();
		lGridLayout.numColumns = 2;
		lGroup.setLayout(lGridLayout);

		mLanguageDescription = new Label(lGroup, SWT.BORDER | SWT.WRAP);
		mLanguageDescription.setText(Messages
				.getString("languagepanel.description"));
		mLanguageDescription
				.setBackground(UITheme.getInstance().TABLE_ODD_BACKGROUND_COLOR);

		GridData lData = new GridData();
		lData.horizontalSpan = 2;
		lData.widthHint = 400;
		mLanguageDescription.setLayoutData(lData);

		Label lSelectionLabel = new Label(lGroup, SWT.NONE);
		lSelectionLabel.setText(Messages.getString("languagepanel.select"));

		mLanguageCombo = new Combo(lGroup, SWT.BORDER | SWT.READ_ONLY);
		mLanguageCombo.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {
				int lIndex = mLanguageCombo.getSelectionIndex();
				lSelection = mLanguageCombo.getItem(lIndex);
				Locale lLocale = LanguageLogic.getInstance().getLocale(
						lSelection);
				if (LanguageLogic.getInstance().isCurrent(lLocale)) {
					mSupportDescription.setText(Messages
							.getString("languagepanel.current"));
					return;
				}
				if (LanguageLogic.getInstance().isSupported(lLocale)) {
					mSupportDescription.setText(Messages
							.getString("languagepanel.supported"));
				} else {
					mLog.info("Locale not supported: " + lSelection);
					mSupportDescription.setText(Messages
							.getString("languagepanel.notsupported"));
				}
			}
		});

		mSupportDescription = new Label(lGroup, SWT.WRAP);
		
		lData = new GridData();
		lData.horizontalSpan = 2;
		lData.heightHint = 40;
		lData.widthHint = 400;
		mSupportDescription.setLayoutData(lData);

		mEditButton = new Button(lGroup, SWT.PUSH);
		mEditButton.setText(Messages.getString("general.edit"));
		mEditButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {
				TranslationDialog lDialog = new TranslationDialog(LanguagePanel.this, UILauncher.getInstance()
						.getShell());
				lDialog.fillDialog(LanguageLogic.getInstance().getLocale(
						lSelection));
				lDialog.getShell().open();
			}
		});

		// Messages.getString("languagepanel.settings")

		try {
			mLanguageBinder = new ConfigurationBinder(mLanguageCombo,
					IConfigurationBinder.SUB_TYPE_CONFIGURATION,
					Configuration.CONFIG_LANGUAGE);
			mBinderArray[0] = mLanguageBinder;
		} catch (JPodderException e) {
			mLog.warn(e.getMessage());
		}
		fillPanel();
	}

	public void fillPanel() {
		List lList = LanguageLogic.getInstance().getLocals();
		Iterator iter = lList.iterator();
		while (iter.hasNext()) {
			Object lElement = iter.next();
			if (lElement instanceof String) {
				mLanguageCombo.add((String) lElement);
			}
		}
	}

	public IConfigurationBinder[] getBindings() {
		return mBinderArray;
	}
}