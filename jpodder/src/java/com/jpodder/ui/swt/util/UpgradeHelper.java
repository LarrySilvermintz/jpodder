package com.jpodder.ui.swt.util;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;

import org.apache.log4j.Logger;

import com.jpodder.JPodderException;
import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.util.Messages;

/**
 * This class provides the facilities to check the current version and perform
 * an upgrade if necessary.
 * <p>
 * The default configuration directory is:
 * <p>
 * On win32: {user.home}\Application Data\jpodder
 * <p>
 * On nix: {user.home}\jpodder
 * <p>
 * The upgrade depends from which version we are changing, this could be the
 * change or removed properties. The version is file name is changed to the new
 * version.
 * 
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @since 0.9
 * @version 1.1
 */
public class UpgradeHelper {

	private static final String VERSION_FILE_PREFIX = "version.";

	private static final String VERSION_FILE_SUFFIX = ".txt";

	// private static String CURRENT = VERSION_FILE_PREFIX + CURRENT_MAJOR
	// + CURRENT_MINOR + VERSION_FILE_SUFFIX;

	private static Logger sLog = Logger
			.getLogger(UpgradeHelper.class.getName());

	public static void upgrade(ConfigurationLogic lConfig) {

		if (!lConfig.getUserFolder().exists()) {
			createLastestConfiguration(lConfig.getUserFolder(),
					lConfig.getMajor(), lConfig.getMinor());
		} else {
			// Get the version file we want to know which version is running
			// to perform an upgrade.

			String lVersionFile = getInstalledVersion(lConfig
					.getUserFolder());

			String lCurrentMajor = lConfig.getPreviousMajor();
			String lCurrentMinor = lConfig.getPreviousMinor();

			if (lVersionFile.length() <= VERSION_FILE_PREFIX.length()
					+ VERSION_FILE_SUFFIX.length()) {

				// Print error message and throw an exception
				// As TODO: do it Should create a new UpgradeException.

			} else {
				// Strip the pre and suffix. What is left is <major>[-<minor>]
				String lVersion = lVersionFile.substring(VERSION_FILE_PREFIX
						.length(), lVersionFile.length()
						- VERSION_FILE_SUFFIX.length());
				// Now we can parse it <major>[-<minor>]
				int lIndex = lVersion.indexOf("-");
				if (lIndex < 0) {
					lCurrentMajor = lVersion;
				} else {
					if (lIndex > 0) {
						lCurrentMajor = lVersion.substring(0, lIndex);
					}
					if (lIndex < lVersion.length() - 1) {
						lCurrentMinor = lVersion.substring(lIndex + 1);
					}
				}
			}

			boolean lAlsoNext = false;
			String lNewVersion = null;
			if ("0.8".equals(lCurrentMajor)) {
				lAlsoNext = true;
				upgradeFrom08(lConfig.getUserFolder());
			} else if ("0.9".equals(lCurrentMajor) || lAlsoNext) {
				lAlsoNext = true;
				upgradeFrom09(lConfig.getUserFolder());
			} else if ("1.0".equals(lCurrentMajor) || lAlsoNext) {
				lAlsoNext = true;
				// No specific upgrade for 1.0
			}

			// ADD NEW REVISIONS HERE.
			if (lAlsoNext) {
				// Now upgrade the version file
				File lOldVersionFile = new File(lVersionFile);
				if (lOldVersionFile.exists()) {
					lOldVersionFile.delete();
				}
				// CB TODO Add code to write the minor version.
				writeFile(new File(lConfig.getUserFolder(),
						VERSION_FILE_PREFIX + lConfig.getMajor() + VERSION_FILE_SUFFIX),
						"");
			}
		}
	}

	/**
	 * Get the installed version. The version of the installation is stored as
	 * an empty text file. The file name is the installed version.
	 * <p>
	 * The format is: version.&ltmajor&gt[-&ltminor&gt].txt
	 * <p>
	 * Example: For major version 0.9 and minor version 1 this is
	 * version.0.9-1.txt The minor version can also contain the text RC which
	 * stands for Release Candidate.
	 * 
	 * @param pConfigurationDirectory
	 * @return
	 */
	public static String getInstalledVersion(File pConfigurationDirectory) {

		String lVersionFile = "";
		String[] lVersionFiles = pConfigurationDirectory
				.list(new FilenameFilter() {
					public boolean accept(File pDirectory, String pFileName) {
						return pFileName.startsWith(VERSION_FILE_PREFIX)
								&& pFileName.endsWith(".txt");
					}
				});
		if (lVersionFiles.length == 0) {
			// No file? We assume it's the current.
			// CB TODO
			// lVersionFile = ;
		} else if (lVersionFiles.length == 1) {
			lVersionFile = lVersionFiles[0];
		} else {
			// Get the latest file (Modified date) and delete the other
			// files.
			String lLatestVersionString = null;
			for (int i = 0; i < lVersionFiles.length; i++) {
				String lTempVersionString = lVersionFiles[i];
				if (lLatestVersionString == null) {
					lLatestVersionString = lTempVersionString;
				} else {
					File lTempFile = new File(lTempVersionString);
					File lLatestFile = new File(lLatestVersionString);
					Date lTempDate = new Date(lTempFile.lastModified());
					Date lLatestDate = new Date(lLatestFile.lastModified());
					if (lLatestDate.compareTo(lTempDate) <= 0) {
						lLatestVersionString = lTempVersionString;
					} else {
						lTempFile.delete();
					}
				}
			}
			lVersionFile = lLatestVersionString;
		}
		return lVersionFile;
	}

	private static void createLastestConfiguration(
			File pConfigurationDirectory, String pMajor, String pMinor) {
		createGenericFile(pConfigurationDirectory);
	}

	private static void upgradeFrom08(File pConfigurationDirectory) {
		sLog.info(Messages.getString("upgradehelper.upgrade.message", "0.8",
				"0.9"));
		// Create the generic tag rewriting file
		createGenericFile(pConfigurationDirectory);
	}

	private static void upgradeFrom09(File pConfigurationDirectory) {
		// Convert the properties.xml file
		Configuration lConfiguration = new Configuration();
		File lConfigurationFile = new File(pConfigurationDirectory,
				ConfigurationLogic.PROPS_FILE_NAME);
		if (lConfigurationFile.exists()) {
			try {
				IXMLElement lRoot = readXmlFile(new File(
						pConfigurationDirectory, ConfigurationLogic.PROPS_FILE_NAME));
				lConfiguration.setFolder(getXmlString(lRoot, "folder", ""));
				lConfiguration.setSound(getXmlBoolean(lRoot, "sound", false));
				lConfiguration.setAuto(getXmlBoolean(lRoot, "auto", false));
				lConfiguration.setDelay(getXmlInt(lRoot, "delay", -1));
				lConfiguration.setPlayer(getXmlString(lRoot, "player", ""));
				String lValue = getXmlString(lRoot, "opml_url", null);
				if (lValue != null && lValue.trim().length() > 0) {
					lConfiguration.setOpmlUrl(new URL(lValue));
				}
				lConfiguration.setAutoPreview(getXmlBoolean(lRoot,
						"auto_preview", false));
				lConfiguration.setTorrentDefault(getXmlBoolean(lRoot,
						"torrent_default", false));
				lConfiguration.setCacheLearn(getXmlBoolean(lRoot,
						"cache_learn", false));
				lConfiguration.setMarkMax(getXmlBoolean(lRoot, "mark_max",
						false));

				IXMLElement lXmlConnection = lRoot
						.getFirstChildNamed("connection");
				Configuration.Connection lConnection = lConfiguration
						.getConnection();
				lConnection
						.setTimeout(getXmlInt(lXmlConnection, "timeout", -1));
				boolean lBooleanValue = getXmlBoolean(lRoot, "proxy_enabled",
						false);
				lConnection.setProxyEnabled(lBooleanValue);
				if (lBooleanValue) {
					lValue = getXmlString(lRoot, "proxy", null);
					if (lValue != null && lValue.trim().length() > 0) {
						lConnection.setProxy(new URL(lValue));
					}
					lConnection.setUserName(getXmlString(lRoot, "proxy_user",
							""));
					lConnection.setPassword(getXmlString(lRoot,
							"proxy_password", ""));
				}

				IXMLElement lXmlGui = lRoot.getFirstChildNamed("gui");
				Configuration.Gui lGui = lConfiguration.getGui();
				lGui.setIconified(getXmlBoolean(lXmlGui, "iconified", false));
				lGui.setMaximized(getXmlBoolean(lXmlGui, "maximized", false));
				lGui.setHeight(getXmlInt(lXmlConnection, "height", 250));
				lGui.setWidth(getXmlInt(lXmlConnection, "width", 400));
				lGui.setVisible(getXmlBoolean(lXmlGui, "visible", false));
				lGui.setX(getXmlInt(lXmlConnection, "x", 100));
				lGui.setY(getXmlInt(lXmlConnection, "y", 100));
				lGui.setDirectory(getXmlBoolean(lXmlGui, "directory", false));
				lGui.setLog(getXmlBoolean(lXmlGui, "log", false));
				lGui.setProduction(getXmlBoolean(lXmlGui, "production", false));
				lGui.setDownload(getXmlBoolean(lXmlGui, "download", false));
				lGui.setHelp(getXmlBoolean(lXmlGui, "help", false));

				IXMLElement lXmlScheduling = lRoot
						.getFirstChildNamed("scheduling");
				Configuration.Scheduling lScheduling = lConfiguration
						.getScheduling();
				lValue = getXmlString(lXmlScheduling, "type", "time");
				int lTypeIndex = Configuration.SCHEDULING_TYPE_INTERVAL;
				if ("time".equals(lValue)) {
					lTypeIndex = Configuration.SCHEDULING_TYPE_TIMER;
				}
				lScheduling.setType(lTypeIndex);
				lScheduling.setInterval(getXmlInt(lXmlScheduling, "y", 0));
				lValue = getXmlString(lXmlScheduling, "time1", null);
				if (lValue != null && lValue.trim().length() > 0) {
					lScheduling.addTimer(lValue);
				}
				lValue = getXmlString(lXmlScheduling, "time2", null);
				if (lValue != null && lValue.trim().length() > 0) {
					lScheduling.addTimer(lValue);
				}
				lValue = getXmlString(lXmlScheduling, "time3", null);
				if (lValue != null && lValue.trim().length() > 0) {
					lScheduling.addTimer(lValue);
				}
			} catch (Exception e) {
				sLog.warn("setContent(), failed", e);
			}
			ConfigurationLogic.getInstance().save(lConfiguration.getHandler());
		}
		// Read the feeds.xml file and drop any attribute that is empty
		// otherwise XmlBeans cannot read the XML file
	}

	private static void createGenericFile(File pConfigurationDirectory) {
		File lGenericFile = new File(pConfigurationDirectory,
				ConfigurationLogic.GENERIC_FILE_NAME);
		if (!lGenericFile.exists()) {
			writeFile(lGenericFile, "<id3></id3>");
		}
	}

	private static void writeFile(File pFile, String pContent) {
		try {
			FileWriter lWriter = new FileWriter(pFile);
			lWriter.write(pContent);
			lWriter.close();
		} catch (IOException ioe) {
			sLog.error(Messages.getString("upgradehelper.file.creation.failed",
					pFile + ""), ioe);
		}
	}

	private static IXMLElement readXmlFile(File pFile) throws JPodderException {
		IXMLElement lReturn = null;
		try {
			IXMLParser lParser = XMLParserFactory.createDefaultXMLParser();
			IXMLReader lReader = StdXMLReader.fileReader(pFile + "");
			lParser.setReader(lReader);
			lReturn = (IXMLElement) lParser.parse();
		} catch (Exception e) {
			throw new JPodderException("Failed to read Xml File: " + pFile, e);
		}
		return lReturn;
	}

	private static String getXmlString(IXMLElement pElement, String pAttribute,
			String pDefault) {
		String lReturn = pElement.getAttribute(pAttribute, pDefault);
		if (lReturn == null) {
			lReturn = pDefault;
		}
		return lReturn;
	}

	private static boolean getXmlBoolean(IXMLElement pElement,
			String pAttribute, boolean pDefault) {
		boolean lReturn = pDefault;
		String lValue = getXmlString(pElement, pAttribute, pDefault + "");
		if (lValue != null) {
			lReturn = new Boolean(lValue).booleanValue();
		}
		return lReturn;
	}

	private static int getXmlInt(IXMLElement pElement, String pAttribute,
			int pDefault) {
		int lReturn = pDefault;
		String lValue = getXmlString(pElement, pAttribute, pDefault + "");
		if (lValue != null) {
			lReturn = new Integer(lValue).intValue();
		}
		return lReturn;
	}
}