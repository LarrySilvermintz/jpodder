package com.jpodder.remote;

import org.jdesktop.jdic.filetypes.Action;
import org.jdesktop.jdic.filetypes.Association;
import org.jdesktop.jdic.filetypes.AssociationAlreadyRegisteredException;
import org.jdesktop.jdic.filetypes.AssociationNotRegisteredException;
import org.jdesktop.jdic.filetypes.AssociationService;
import org.jdesktop.jdic.filetypes.RegisterFailedException;

import com.jpodder.Main;
import com.jpodder.os.OS;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class OneClick2 {

	String MR_ONE_CLICK_APP = "jPodder.one.click.jar";

	String ONE_CLICK_MIME = "application/rss+xml";

	public String getRSSAssociation() {

		AssociationService assocService = new AssociationService();
		Association assoc = assocService.getFileExtensionAssociation(".rss");
		if (assoc != null) {
			StringBuffer lBuffer = new StringBuffer();
			lBuffer.append("Description: " + assoc.getDescription() + "\n");
			lBuffer.append("MIME Type: " + assoc.getMimeType() + "\n");
			Action lAction = assoc.getActionByVerb("Open");
			if (lAction != null) {
				lBuffer.append("Path: " + lAction.getCommand() + "\n");
			}
			return lBuffer.toString();
		} else {
			return "";
		}
	}

	/**
	 * @param pPath
	 * @return <code>true</code> If the association is set correctly.
	 */
	public boolean setRSSAssociation(String pPath) {

		if (pPath == null) {
			pPath = "";
		}

		String command = "javaw -jar \"" + pPath + "/" + MR_ONE_CLICK_APP
				+ "\" \"%1\"";

		Association assoc = new Association();
		AssociationService assocService = new AssociationService();
		// assoc = assocService.getFileExtensionAssociation(".rss");
		assoc.setDescription("jPodder");
		assoc.setMimeType(ONE_CLICK_MIME);
		assoc.setName("rssfile");
		assoc.addFileExtension(".rss");

		Action lAction = new Action("open", command);
		lAction.setDescription("One Click");
		assoc.addAction(lAction);

		try {
			assocService.registerSystemAssociation(assoc);
			return true;
		} catch (AssociationAlreadyRegisteredException e) {
			String exMessage = e.getMessage();
		} catch (RegisterFailedException e) {
			String exMessage = e.getMessage();
		}
		return false;
	}

	/**
	 * @param pPath
	 * @return <code>true</code> If the association is set correctly.
	 */
	public boolean setProtocolAssociation(String pPath) {

		// CB TODO, store perhaps what we overwrite.

		if (pPath == null) {
			pPath = "";
		}
		String command = "javaw -jar \"" + pPath + "/" + MR_ONE_CLICK_APP
				+ "\" \"%1\"";

		OS.getWin32Registry().reg(
				OS.Win32Registry.REG_ADD_UTIL,
				OS.Win32Registry.HKCR + OS.Win32Registry.PODCAST,
				OS.Win32Registry.OPTION_DEFAULT + OS.Win32Registry.OPTION_FORCE
						+ OS.Win32Registry.OPTION_DATA,
				OS.Win32Registry.URL + Main.APP_TITLE);

		OS.getWin32Registry().reg(OS.Win32Registry.REG_ADD_UTIL,
				OS.Win32Registry.HKCR + OS.Win32Registry.PODCAST,
				OS.Win32Registry.OPTION_FORCE + OS.Win32Registry.OPTION_VALUE,
				OS.Win32Registry.PROTOCOL);

		OS.getWin32Registry().reg(
				OS.Win32Registry.REG_ADD_UTIL,
				OS.Win32Registry.HKCR + OS.Win32Registry.PODCAST
						+ OS.Win32Registry.SHELL + OS.Win32Registry.OPEN
						+ OS.Win32Registry.COMMAND,
				OS.Win32Registry.OPTION_DEFAULT + OS.Win32Registry.OPTION_FORCE
						+ OS.Win32Registry.OPTION_DATA, command);

		return false;
	}

	/**
	 * @param pPath
	 * @return <code>true</code> If the association is set correctly.
	 */
	public boolean getProtocolAssociation() {

		String pResult = OS.getWin32Registry().reg(
				OS.Win32Registry.REG_QUERY_UTIL,
				OS.Win32Registry.HKCR + OS.Win32Registry.PODCAST);

		if (pResult.length() == 0 || OS.getWin32Registry().isError(pResult)) { // If an error, there is
			// no key.
			return false;
		} else {
			return true;
		}
	}

	public void removeProtocolAssocitation() {

		OS.getWin32Registry().reg(OS.Win32Registry.REG_DELETE_UTIL,
				OS.Win32Registry.HKCR + OS.Win32Registry.PODCAST,
				OS.Win32Registry.OPTION_VALUE_ALL);
	}

	/**
	 * 
	 * @return <code>true</code> if the removal was succesfull.
	 */
	public boolean removeRSSAssociation() {
		AssociationService assocService = new AssociationService();
		Association assoc = assocService.getMimeTypeAssociation(ONE_CLICK_MIME);
		if (assoc == null) {
			assoc = assocService.getFileExtensionAssociation(".rss");
		}
		if (assoc != null) {
			try {
				assocService.unregisterSystemAssociation(assoc);
				return true;
			} catch (RegisterFailedException e) {
				e.printStackTrace();
			} catch (AssociationNotRegisteredException e) {
				// OK, it's not registered. }
			}
		}
		return false;
	}
}