package com.jpodder.ui.swt.properties.panel;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

import com.jpodder.FileHandler;
import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.os.OS;
import com.jpodder.remote.OneClick2;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.comp.BaseAction;
import com.jpodder.util.Messages;

/**
 * This class is only activated once on system install, and and on request by
 * the user. It creates an association on the OS, between the RSS MIME type and
 * the jPodder application. This class is OS agnostic.
 * 
 * If an association already exists, the user is asked if it should be
 * overriden.
 * 
 */
public class OneClickController implements IConfigurationListener {

	public BaseAction mCheckOneClick;

	public BaseAction mSetOneClick;

	public BaseAction mRemoveOneClick;

	private String mPath;

	private OneClickPanel mView;

	public OneClickController() {
		initialize();
	}

	public void setView(OneClickPanel pView) {
		mView = pView;
		initializeUI();
	}

	public void initialize() {
		File lBinFile = FileHandler.sBinDirectory;
		if (lBinFile != null) {
			mPath = lBinFile.getAbsolutePath();
		}
	}

	public void initializeUI() {

		mCheckOneClick = new CheckOneClick();
		mView.mCheckRegistrationButton.setText(mCheckOneClick.getName());

		mSetOneClick = new SetOneClick();
		mView.mSetRegistrationButton.setText(mSetOneClick.getName());

		mRemoveOneClick = new RemoveOneClick();
		mView.mRemoveRegistrationButton.setText(mRemoveOneClick.getName());

		mView.mCheckRegistrationButton.addListener(SWT.Selection,
				mCheckOneClick);
		mView.mSetRegistrationButton.addListener(SWT.Selection, mSetOneClick);
		mView.mRemoveRegistrationButton.addListener(SWT.Selection,
				mRemoveOneClick);
	}

	public class CheckOneClick extends BaseAction {
		public CheckOneClick() {
			init(Messages.getString("oneclickpanel.check"), true);
		}

		public void handleEvent(Event e) {

			OneClick2 lOneClick = new OneClick2();
			String lResult = lOneClick.getRSSAssociation();
			lResult += "\n" + lOneClick.getProtocolAssociation();

			if (lResult.length() > 0) {
				MessageDialog.openInformation(UILauncher.getInstance()
						.getShell(), Messages
						.getString("oneclickpanel.check.result"), lResult);
			} else {
				MessageDialog.openInformation(UILauncher.getInstance()
						.getShell(), Messages
						.getString("oneclickpanel.check.result"), Messages
						.getString("oneclickpanel.check.notset"));

			}
		}
	}

	public class SetOneClick extends BaseAction {
		public SetOneClick() {
			init(Messages.getString("oneclickpanel.set"), true);
		}

		public void handleEvent(Event e) {
			OneClick2 lOneClick = new OneClick2();
			boolean lRSSOK = false;
			boolean lProtocolOK = false;

			if (lOneClick.getRSSAssociation().length() > 0) {
				if (lOneClick.setRSSAssociation(mPath)) {
					lRSSOK = true;
				}
			}

			if (OS.isWindows()) {
				if (!lOneClick.getProtocolAssociation()) {
					lOneClick.setProtocolAssociation(mPath);
					lProtocolOK = true;
				}
			}else{
				lProtocolOK = true;
			}
			
			if (lProtocolOK || lRSSOK) {
				MessageDialog.openInformation(UILauncher.getInstance()
						.getShell(), Messages
						.getString("oneclickpanel.check.result"), Messages
						.getString("oneclickpanel.set.success"));

			}else{
				MessageDialog.openError(UILauncher.getInstance().getShell(),
						Messages.getString("oneclickpanel.set"), Messages
								.getString("oneclickpanel.set.failed"));
			}
		}
	}

	public class RemoveOneClick extends BaseAction {
		public RemoveOneClick() {
			init(Messages.getString("oneclickpanel.remove"), true);
		}

		public void handleEvent(Event e) {
			OneClick2 lOneClick = new OneClick2();
			if (lOneClick.removeRSSAssociation()) {
				MessageDialog.openInformation(UILauncher.getInstance()
						.getShell(), Messages
						.getString("oneclickpanel.check.result"), Messages
						.getString("oneclickpanel.remove.success"));
			} else {
				MessageDialog.openError(UILauncher.getInstance().getShell(),
						Messages.getString("oneclickpanel.remove"), Messages
								.getString("oneclickpanel.remove.failed"));
			}

		}
	}

	public void configurationChanged(ConfigurationEvent event) {
		// We need to know if the one-click check was performed.
		// 1. Access configuration
		// 2. Set the local flag for the value.
	}
}