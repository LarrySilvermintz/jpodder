package com.jpodder.data.language;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.jpodder.FileHandler;
import com.jpodder.JPodderException;
import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class LanguageLogic implements IConfigurationListener {

	private static Logger sLog = Logger
			.getLogger(LanguageLogic.class.getName());

	private ArrayList<ILanguageListener> mListeners = new ArrayList<ILanguageListener>();

	public void addListener(ILanguageListener pListener) {
		if (!mListeners.contains(pListener)) {
			mListeners.add(pListener);
		}
	}

	public void removeListener(ILanguageListener pListener) {
		if (mListeners.contains(pListener)) {
			mListeners.remove(pListener);
		}
	}

	public void fireLanguageChanged(LanguageEvent pEvent) {
		Iterator iter = mListeners.iterator();
		while (iter.hasNext()) {
			ILanguageListener listener = (ILanguageListener) iter.next();
			listener.languageChanged(pEvent);
		}
	}

	private static LanguageLogic sSelf;

	public static LanguageLogic getInstance() {
		if (sSelf == null) {
			sSelf = new LanguageLogic();
		}
		return sSelf;
	}

	/**
	 * The display name is according to the Locals display names.
	 * 
	 * @param pDisplayName
	 * @return
	 */
	public Locale getLocale(String pDisplayName) {
		Locale[] lLocals = Locale.getAvailableLocales();
		for (int j = 0; j < lLocals.length; j++) {
			Locale lLocale = lLocals[j];
			String lName = lLocale.getDisplayName();
			if (lName.endsWith(pDisplayName)) {
				// we have a match;
				return lLocale;
			}
		}
		return null; // No Locale for this name.
	}

	/**
	 * Get all locales on this system.
	 * 
	 * @return List A sorted collection of Locales.
	 */
	public List getLocals() {
		ArrayList<String> lLocalsList = new ArrayList<String>();
		Locale[] lLocals = Locale.getAvailableLocales();
		for (int j = 0; j < lLocals.length; j++) {

			Locale lLocale = lLocals[j];
			// String lCountry = lLocale.getCountry();
			// String lLanguage = lLocale.getLanguage();
			// We filter out all locales which are dialects.
			if (isMainLanguage(lLocale)) {
				lLocalsList.add(lLocals[j].getDisplayName());
			}
		}

		Collections.sort(lLocalsList, new Comparator() {
			public int compare(Object o1, Object o2) {
				String lName1 = (String) o1;
				String lName2 = (String) o2;
				return lName1.compareTo(lName2);
			}
		});

		// CB REMOVE LATER.
		// Collections.sort(lLocalsList, new Comparator() {
		// public int compare(Object o1, Object o2) {
		// Locale lLocale1 = (Locale) o1;
		// Locale lLocale2 = (Locale) o2;
		// String lLanguage1 = lLocale1.getDisplayLanguage();
		// String lLanguage2 = lLocale2.getDisplayLanguage();
		// return lLanguage1.compareTo(lLanguage2);
		// }
		// });
		return lLocalsList;
	}

	public boolean isCurrent(Locale pLocale) {
		Locale mCurrent = Messages.getInstance().getLocale();
		if (pLocale.equals(mCurrent)) {
			return true;
		} else {
			// Fails for US_EN local while it should.
			if (pLocale.equals(Locale.ENGLISH)) {
				pLocale = Messages.getMasterBundle().getLocale();
			}

			if (pLocale.equals(mCurrent)) {
				return true;
			}
			return false;
		}
	}

	/**
	 * The purpose of this method is to check if the provided locale is not a
	 * dialect/variant of a language.
	 * 
	 * @return
	 */
	public boolean isMainLanguage(Locale pLocale) {

		String lCountry = pLocale.getCountry();
		if (lCountry.length() > 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Get if a resource (Language) bundle is available for a locale.
	 * 
	 * @param pLocale
	 * @return
	 */
	public boolean isSupported(Locale pLocale) {
		// We use only main locales, this means that for our
		// resource bundles we should add the default country.
		try {
			Messages.getInstance().getResourceBundle(pLocale);
			return true;
		} catch (JPodderException e) {
			return false;
		}
	}

	/**
	 * Create a path out of a locale (Use default application file name.
	 * <code>MASTER_RESOURCE</code>).
	 * <p>
	 * Save the key/value collection to the file.
	 * 
	 * @param pLocale
	 * @param lMap
	 */
	public void saveFile(Locale pLocale, TreeMap lMap) {
		File lFile = new File(FileHandler.sLibDirectory,
				Messages.MASTER_RESOURCE + "_" + pLocale.getLanguage() + "_"
						+ pLocale.getCountry() + ".properties");
		saveFile(lFile, lMap);
	}

	/**
	 * Save a sorted collection of keys and values into the specified file.
	 * </br>
	 * 
	 * @param pFile
	 * @param lMap
	 */
	public void saveFile(File pFile, TreeMap lMap) {

		if (pFile == null) {
			throw new IllegalArgumentException("File is null");
		}
		if (!pFile.exists()) {
			try {
				pFile.createNewFile();
			} catch (IOException e) {
				sLog.warn("Can not create file " + pFile.getAbsolutePath());
				return;
			}
		} else {
			// this is a temporary file.
			// Move it to the new destination.

		}
		try {

			FileWriter lWriter = new FileWriter(pFile);
			BufferedWriter lBufferedWriter = new BufferedWriter(lWriter);
			Iterator it = lMap.keySet().iterator();
			while (it.hasNext()) {
				Object lObj = it.next();
				if (lObj instanceof String) {
					String lKey = (String) lObj;
					sLog.info("Key = " + lObj);
					lBufferedWriter.write(lKey + "=" + (String) lMap.get(lKey));
					lBufferedWriter.newLine();
				}
			}
			lBufferedWriter.close();
			lWriter.close();

		} catch (FileNotFoundException e) {
			sLog.warn("Can not create file " + pFile.getAbsolutePath());
			return;
		} catch (IOException e) {
			sLog.warn("Can not access file " + pFile.getAbsolutePath());
			return;
		}
	}

	/**
	 * Apply a locale (Language), getting the resource bundle. Will only work
	 * when a resource bundle exists.</br> Should call:
	 * <code>isSupported()</code> first.
	 * 
	 * @param pLocale
	 */
	public void setLanguage(Locale pLocale) {
		try {
			ResourceBundle lBundle = Messages.getInstance().getResourceBundle(
					pLocale);
			sLog.info("setLanguage() Bundle retrieved"
					+ lBundle.getLocale().getDisplayLanguage() + " "
					+ lBundle.getLocale().getDisplayCountry());
			Messages.getInstance().setResourceBundle(lBundle);
			// Restart the UI here.
			fireLanguageChanged(new LanguageEvent(this));
		} catch (JPodderException e) {
			sLog.info("Locale not available for: "
					+ pLocale.getDisplayLanguage());
			// When a locale is not available, the default local will be used.
			// this is English / US.
			sLog.info("Language is English / US: ");
		}
	}

	/**
	 * Listen to properies change of the selected Locale. If the configuration
	 * locale is not already set, we apply the new locale (Calls
	 * <code>setLanguage()</code>) and restart the UI. (To load the new
	 * messages).
	 * 
	 * @param event
	 *            PropertyEvent.
	 */
	public void configurationChanged(ConfigurationEvent event) {

		if (!event.getSource().equals(ConfigurationLogic.class)) {
			return;
		}

		String lName = Configuration.getInstance().getLanguage();
		Locale lLocale = LanguageLogic.getInstance().getLocale(lName);
		if (lLocale == null) {
			return;
		}
		// Check to see if this is the current locale.
		sLog.debug("propertiesChanged(), local: " + lLocale
				+ ", message instance: " + Messages.getInstance());

		if (isCurrent(lLocale)) {
			return;
		}

		if (isSupported(lLocale)) {
			sLog.info("Changing language to: " + lLocale.getDisplayName());
			if (!UILauncher.getInstance().getRebooting()) {
				setLanguage(lLocale);
			}
		} else {
			// It's not the current and it's not supported, we change it
			// back to
			// the default.
			Locale lCurrentLocale = Messages.getInstance().getLocale();
			lName = lCurrentLocale.getDisplayName();
			Configuration.getInstance().setLanguage(lName);
			ConfigurationLogic.getInstance()
					.fireConfigurationChanged(
							new ConfigurationEvent(this,
									Configuration.CONFIG_LANGUAGE));
		}

	}
}
