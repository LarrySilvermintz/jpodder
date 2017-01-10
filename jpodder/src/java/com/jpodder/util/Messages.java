package com.jpodder.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

import com.jpodder.FileHandler;
import com.jpodder.JPodderException;
import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.language.LanguageLogic;

/**
 * <u>Notes: </u> <BR>
 * To test different locales, using the following VM parameters: <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;java -jar jPodder.jar
 * -Duser.language=&lt;language&gt; -Duser.region=&lt;country&gt; <BR>
 * <BR>
 * For example, to use English for language and Great Britain for Country, the
 * startup would be: <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;java -jar jPodder.jar -Duser.language=en
 * -Duser.region=GB <BR>
 * <BR>
 * Language codes can be found <a
 * href="http://ftp.ics.uci.edu/pub/ietf/http/related/iso639.txt">here </a> <BR>
 * Country codes can be found <a
 * href="http://userpage.chemie.fu-berlin.de/diverse/doc/ISO_3166.html">here
 * </a> <BR>
 * <BR The getString() method is overloaded and supports variable replacement
 * for up to 3 fields. The replacement is noted with the following strings: <BR>
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;%1 <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;%2 <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;%3 <BR>
 * 
 * <BR>
 * For example, the resources string <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;feedcontrol.addedFeeds=Added %1 feeds <BR>
 * would produce the output
 * 
 * "Added 5 feeds"
 * 
 * If it were called as <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;ipMessages.getString("feedcontrol.addedFeeds",5);
 * 
 */

public class Messages {

	public static final String MASTER_RESOURCE = "jPodderResources";

	private static ResourceBundle sDefaultBundle = null;

	private static Locale sCurrent = null;

	private static Logger sLog = Logger.getLogger(Messages.class.getName());

	private static Messages sSelf;

	public static Messages getInstance() {
		if (sSelf == null) {
			sSelf = new Messages();
		}
		return sSelf;
	}

	static {
		String lName = Configuration.getInstance().getLanguage();
		Locale lLocale;
		if (lName != null) {
			lLocale = LanguageLogic.getInstance().getLocale(lName);
			if (lLocale != null) {

				try {

					sLog.info("OS Locale is: " + lLocale.getDisplayLanguage()
							+ " " + lLocale.getDisplayCountry());
					// We rather check if a bundle exists in the \lib folder.
					// for english we make sure that the country is US.

					File lSourceFile = getFile(lLocale);
					if (lSourceFile == null || lSourceFile.length() == 0) {
						sDefaultBundle = ResourceBundle.getBundle(
								MASTER_RESOURCE, lLocale);
					} else {
						sDefaultBundle = getResourceBundle(lLocale, lSourceFile);
					}

					sLog.info("Resource file " + MASTER_RESOURCE
							+ " loaded for locale: " + lLocale.getLanguage()
							+ "_" + lLocale.getCountry());
				} catch (Exception e) {
					sLog.error("Unable to load resource: " + MASTER_RESOURCE
							+ lLocale.getLanguage() + "_"
							+ lLocale.getCountry());
					sLog.error("Loading default resource instead");
					sDefaultBundle = getMasterBundle();
				}
			} else {
				sDefaultBundle = getMasterBundle();
			}
		} else {
			sDefaultBundle = getMasterBundle();
		}
	}

	public Messages() {

	}

	public Locale getLocale() {
		
		if(sDefaultBundle.getLocale()== null && sCurrent != null){
			return sCurrent;
		}else{
			return sDefaultBundle.getLocale();
		}
	}

	public static ResourceBundle getMasterBundle() {
		ResourceBundle lBundle = ResourceBundle.getBundle(MASTER_RESOURCE,
				Locale.US);
		sLog.info("getMasterBundle() Master bundle is:"
				+ lBundle.getLocale().getDisplayLanguage() + " "
				+ lBundle.getLocale().getDisplayCountry());
		return lBundle;
	}

	/**
	 * Set the resource bundle based on a Locale
	 * 
	 * @param pLocal
	 * @throws JPodderException
	 */
	public ResourceBundle getResourceBundle(Locale pLocale)
			throws JPodderException {

		if (pLocale != null && pLocale.equals(Locale.ENGLISH)) {
			return getMasterBundle();
		}

		ResourceBundle lBundle = null;
		try {
			lBundle = ResourceBundle.getBundle(MASTER_RESOURCE, pLocale);
		} catch (MissingResourceException mre) {
			sLog.warn(mre.getMessage());
			throw new JPodderException(mre.getMessage());
		}

		if (lBundle != null) {
			Locale lLocale = lBundle.getLocale();
			if (lLocale.equals(pLocale)) {
				sLog.info("Found bundle for : " + MASTER_RESOURCE + "_"
						+ pLocale.getLanguage() + "_" + pLocale.getCountry());
				return lBundle;
			} else {
				throw new JPodderException("Locale, does not match");
			}
		} else {
			throw new JPodderException("Locale, does not match");
		}
	}

	/**
	 * Get the resource bundle based on a file (Perform a check with the locale.
	 * 
	 * @param pLocal
	 * @param pFile
	 * @throws JPodderException
	 */
	public static ResourceBundle getResourceBundle(Locale pLocale, File pFile)
			throws JPodderException {
		sLog.info("Trying to open : " + pFile.getAbsolutePath());
		try {
			FileInputStream lStream = new FileInputStream(pFile);
			PropertyResourceBundle lBundle = new PropertyResourceBundle(lStream);
			// Note the local is not set for this bundle, so we keep track in
			// another field.
			sCurrent = pLocale;
			lStream.close();
			return lBundle;
		} catch (MissingResourceException mre) {
			sLog.warn(mre.getMessage());
			throw new JPodderException(mre.getMessage());
		} catch (FileNotFoundException e) {
			sLog.warn(e.getMessage());
			throw new JPodderException(e.getMessage());
		} catch (IOException e) {
			sLog.warn(e.getMessage());
			throw new JPodderException(e.getMessage());
		}
	}

	public void setResourceBundle(ResourceBundle pBundle) {
		if (pBundle != null) {
			sDefaultBundle = pBundle;
		}
	}

	/**
	 * Inspect the availability of a bundle for a resource.
	 * 
	 * @param pLocale
	 * @return
	 */
	public boolean hasResourceBundle(Locale pLocale) {
		try {
			getResourceBundle(pLocale);
			return true;
		} catch (JPodderException e) {
			return false;
		}
	}

	/**
	 * Uses Message Format to convert the message according to the pattern
	 * provided as an argument.
	 * 
	 * @param pMessage
	 *            The actual Mesage.
	 * @param pArguments
	 *            List of arguments to be formatted into the message pattern
	 * 
	 * @return Formatted message or a message indicating the problem and the
	 *         message
	 */
	public static String getFormatedMessage(String pMessage, Object[] pArguments) {
		return MessageFormat.format(pMessage, pArguments);
	}

	/**
	 * 
	 * @param label
	 * @param var1
	 * @return String The message
	 */
	public static String getString(String label) {
		return getMessage(label, new String[] {});
	}

	/**
	 * 
	 * @param label
	 * @param var1
	 * @return String The message
	 */
	public static String getString(String label, String var1) {
		return getMessage(label, new String[] { var1 });
	}

	/**
	 * 
	 * @param label
	 * @param var1
	 * @return String The message
	 */
	public static String getString(String label, int var1) {
		return getMessage(label, new String[] { new Integer(var1).toString() });
	}

	/**
	 * 
	 * @param label
	 * @param var1
	 * @param var2
	 * @return String The message
	 */
	public static String getString(String label, int var1, int var2) {
		return getMessage(label, new String[] { new Integer(var1).toString(),
				new Integer(var2).toString() });
	}

	/**
	 * 
	 * @param label
	 * @param var1
	 * @param var2
	 * @return String The message
	 */
	public static String getString(String label, String var1, String var2) {
		return getMessage(label, new String[] { var1, var2 });
	}

	/**
	 * 
	 * @param label
	 * @param var1
	 * @param var2
	 * @param var3
	 * @return String The message
	 */
	public static String getString(String label, String var1, String var2,
			String var3) {
		return getMessage(label, new String[] { var1, var2, var3 });
	}

	/**
	 * Looks up the message in the resource bundle and uses Message Format to
	 * convert the message according to the pattern delivered by the resource
	 * bundle according ot the label
	 * 
	 * @param pMessageLabel
	 *            Label of the message pattern to be found by the resource
	 *            bundle
	 * @param pArguments
	 *            List of arguments to be formatted into the message pattern
	 * 
	 * @return Formatted message or a message indicating the problem and the
	 *         message
	 */
	public static String getMessage(String pMessageLabel, Object[] pArguments) {
		if (sDefaultBundle != null) {
			try {
				String lMessage = sDefaultBundle.getString(pMessageLabel);
				return MessageFormat.format(lMessage, pArguments);
			} catch (Exception e) {
				return "Message not found for '"
						+ pMessageLabel
						+ "', arguments: "
						+ (pArguments == null ? "[]" : java.util.Arrays.asList(
								pArguments).toString());
			}
		} else {
			return "?: '"
					+ pMessageLabel
					+ "', arguments: "
					+ (pArguments == null ? "[]" : java.util.Arrays.asList(
							pArguments).toString());
		}
	}

	/**
	 * Get the file for a locale. A resource file is created when none exists in
	 * the classpath. The opened, extracted or created file will be the
	 * returned. (In this priority order.
	 * 
	 * @param pLocale
	 * @return
	 */
	public static File getFile(Locale pLocale) {

		// CB Added some code which checks if the country is known.
		String lCountry = pLocale.getCountry();

		String lResource = Messages.MASTER_RESOURCE + "_"
				+ pLocale.getLanguage()
				+ (lCountry.length() > 0 ? "_" + lCountry : "") + ".properties";

		// We look in the lib folder for the resource file, if we don't find
		// it we look in the jPodder.jar file. If not found again we create
		// the file.

		File lOutputFile = new File(FileHandler.sLibDirectory, lResource);
		if (!lOutputFile.exists()) {
			try {
				JarFile jPodderArchive = new JarFile(new File(
						FileHandler.sLibDirectory, "jPodder.jar"));
				JarEntry lResourceEntry = jPodderArchive.getJarEntry(lResource);
				if (lResourceEntry != null) {
					BufferedInputStream lInput = new BufferedInputStream(
							jPodderArchive.getInputStream(lResourceEntry));
					sLog.info("inner archive copied to: " + lOutputFile);
					BufferedOutputStream lOutput = new BufferedOutputStream(
							new FileOutputStream(lOutputFile));
					byte[] lBuffer = new byte[1024];
					int lLength = 0;
					while ((lLength = lInput.read(lBuffer)) >= 0) {
						lOutput.write(lBuffer, 0, lLength);
					}
					lInput.close();
					lOutput.close();
					return lOutputFile;
				} else {
					lOutputFile.createNewFile();
				}
				return lOutputFile;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return lOutputFile;
		}
		return null;
	}

}