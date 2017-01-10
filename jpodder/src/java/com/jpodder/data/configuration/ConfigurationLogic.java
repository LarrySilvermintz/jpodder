package com.jpodder.data.configuration;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.jpodder.FileHandler;
import com.jpodder.JPodderException;
import com.jpodder.os.OS;
import com.jpodder.tasks.AbstractTaskWorker;
import com.jpodder.tasks.TaskLogic;
import com.jpodder.ui.swt.comp.RadioButtonDialog;
import com.jpodder.ui.swt.util.UpgradeHelper;
import com.jpodder.util.Messages;

public class ConfigurationLogic {

	Logger mLog = Logger.getLogger(this.getClass().getName());

	private static final String CONFIGURATION_FILE_NAME = "jPodder.properties";

	public static final String PROPS_FILE_NAME = "properties.xml";

	public static final String FEED_FILE_NAME = "feeds.xml";

	public static final String DIR_FILE_NAME = "directory.xml";

	public static final String CACHE_FILE_NAME = "cache.xml";

	public static final String PLUGIN_PROPS_FILE_NAME = "plugin-properties.xml";

	public static final String GENERIC_FILE_NAME = "generic.xml";

	public static final String THEME_FILE_NAME = "theme.css";

	private File mUserFolder = null;

	private static ConfigurationLogic sSelf;

	// Load the jPodder properties to see if there is a need for a
	// configuration directory suffix
	static Properties sAppProps = new Properties();

	public static ConfigurationLogic getInstance() {
		if (sSelf == null) {
			sSelf = new ConfigurationLogic();
		}
		return sSelf;
	}

	/**
	 * Initialize the User configuration handler. The configuration file
	 * contains the properties:
	 * <p>
	 * 
	 * <li>configuration.directory.suffix A suffix, which is used for
	 * pre-release versions</li>
	 * <li>major.version The Major version of jPodder</li>
	 * <li>minor.version The Minor version of jPodder</li>
	 * <p>
	 * 
	 * If a suffix exists, this is an indication that this is a pre-release of
	 * jPodder. If not upgraded yet, we will ask to user what to do with the
	 * configuration.
	 * 
	 * @param pUserFolder
	 *            If provided this directory will be used as configuration
	 *            directory instead of the default
	 */

	public String initialize(String pProposedUserFolder) {

		String lNewUserFolder = pProposedUserFolder;

		// When the configuration directory is specified by the user we take
		// it
		// as is ( We don't try to figure out a new one).
		if (lNewUserFolder == null || lNewUserFolder.length() == 0) {

			String userDir = System.getProperty("user.home");
			if (OS.isWindows())
				// CB FIXME We actually want to try in the user.home
				// because
				// of I18n of windows.
				lNewUserFolder = userDir + File.separator + "Application Data"
						+ File.separator + "jPodder";
			else {
				lNewUserFolder = userDir + File.separator + "jPodder";
			}
			lNewUserFolder = doVersionCheck(lNewUserFolder);
		}
		
		mLog.info("User folder set to: " + lNewUserFolder);
		mUserFolder = new File(lNewUserFolder);
		if (!mUserFolder.exists()) {
			if (mUserFolder.mkdir()) {
				mLog.info(mUserFolder.getPath() + " created");
			} else {
				mLog.error("Can not create " + mUserFolder.getPath());
			}
		} else {
			FileHandler.deleteDirectory(new File(mUserFolder, "temp"));
		}
		return null;
	}

	private String doVersionCheck(String pNewUserFolder) {

		try {
			sAppProps.load(ConfigurationLogic.class.getClassLoader()
					.getResourceAsStream(CONFIGURATION_FILE_NAME));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String lSuffix = sAppProps.getProperty(
				"configuration.directory.suffix", "");

		// LOGGING
		if (mLog.isDebugEnabled()) {
			mLog
					.debug("initialize(), Check for pre-release configurations (2), directory suffix: "
							+ lSuffix + ", config dir: " + pNewUserFolder);
		}

		if (lSuffix != null && !"".equals(lSuffix.trim())) {

			// Check if the existing configuration folder was not
			// already upgraded
			// else continue with the default configuration.
			// a version.txt file is stored in the conf. folder.
			File lDefaultUserFolder = new File(pNewUserFolder);
			if (lDefaultUserFolder.exists() && lDefaultUserFolder.isDirectory()) {

				String lVersionString = UpgradeHelper
						.getInstalledVersion(lDefaultUserFolder);

				if (lVersionString.length() == 0) {
					File lSuffixConfigDir = new File(pNewUserFolder + lSuffix);
					if (mLog.isDebugEnabled()) {
						mLog
								.debug("initialize(), suffix configuration directory: "
										+ lSuffixConfigDir
										+ ", does exists: "
										+ lSuffixConfigDir.exists());
					}
					if (!lSuffixConfigDir.exists()) {

						// Before going on we check if the directory
						// exists
						// and if not ask the user what to do with the
						// existing config
						// We have to set the configuration folder, as
						// invocation of
						// the i18n in the dialog below. will initialize
						// the
						// Configuration
						// data handler., the configuration folder is
						// set
						// according
						// to the user choice further on.

						// FileHandler.sPropsFile = new File(lNewUserFolder
						// + File.separator + PROPS_FILE_NAME);

						int lResult = new RadioButtonDialog()
								.showConfirmDialog(
										Messages
												.getString("filehandler.dialog.configuation.title"),
										Messages
												.getString("filehandler.dialog.configuation.message")
												+ lSuffixConfigDir
														.getAbsolutePath(),
										new String[] {
												Messages
														.getString("filehandler.dialog.configuation.option.new"),
												Messages
														.getString("filehandler.dialog.configuation.option.copy"),
												Messages
														.getString("filehandler.dialog.configuation.option.use"), });

						mLog.info("User selected the following action: "
								+ lResult);
						switch (lResult) {
						case -1:
						break;
						case 0:
							pNewUserFolder = pNewUserFolder + lSuffix;
							break;
						case 1:
							String lNewConfigDir = pNewUserFolder + lSuffix;
							File lNewConfig = new File(lNewConfigDir);
							lNewConfig.mkdirs();
							// Copy the directory
							File[] lFiles = new File(pNewUserFolder)
									.listFiles(new FileFilter() {
										public boolean accept(File lTest) {
											return !lTest.isDirectory()
													&& lTest.canRead();
										}
									});
							for (int i = 0; i < lFiles.length; i++) {
								String lFileName = lFiles[i].getName();
								File lNewFile = new File(lNewConfig, lFileName);
								if (!lNewFile.exists()) {
									mLog.info("File to copy: " + lFiles[i]
											+ ", target file: " + lNewFile);
									try {
										FileHandler.copyFile(lFiles[i],
												lNewFile);
									} catch (Exception e) {
										mLog.error("Could not copy the file: "
												+ lFiles[i] + ", to: "
												+ lNewFile, e);
									}
								}
							}
							pNewUserFolder = lNewConfigDir;
							break;
						case 2:
							// Mark the configuration to be converted to
							// this version so that the system does not
							// ask
							// again

							StringBuffer lVersion = new StringBuffer();
							lVersion.append("version." + getMajor());
							if (getMinor() != null && getMinor().length() > 0) {
								lVersion.append("." + getMinor());
							}
							File lVersionFile = new File(pNewUserFolder,
									lVersion.toString() + ".txt");
							try {
								lVersionFile.createNewFile();
							} catch (Exception e) {
								mLog.warn("Can not create version file");
							}
							break;
						}
					} else {
						// If the directory already exists there is
						// nothing
						// to be done except using it
						pNewUserFolder = pNewUserFolder + lSuffix;
					}
				}
			}
		}
		return pNewUserFolder;
	}

	public File getFeedFile() {
		return new File(mUserFolder, FEED_FILE_NAME);
	}

	public File getCacheFile() {
		return new File(mUserFolder, CACHE_FILE_NAME);
	}

	public File getPropsFile() {
		return new File(mUserFolder, PROPS_FILE_NAME);
	}

	public File getDirectoryFile() {
		return new File(mUserFolder, DIR_FILE_NAME);
	}

	public File getPluginFile() {
		return new File(mUserFolder, PLUGIN_PROPS_FILE_NAME);
	}

	public File getGenericID3File() {
		return new File(mUserFolder, GENERIC_FILE_NAME);
	}

	public File getUserFolder() {
		return mUserFolder;
	}

	public File getCssFile() {
		return new File(mUserFolder, THEME_FILE_NAME);
	}

	// -----------Application version handling (Upgrade helper.

	public String getMajor() {
		return sAppProps.getProperty("major.version", "");
	}

	public String getMinor() {
		return sAppProps.getProperty("minor.version", "");
	}

	public String getPreviousMajor() {
		return sAppProps.getProperty("previous.major.version");
	}

	public String getPreviousMinor() {
		return sAppProps.getProperty("previous.minor.version");
	}

	// ----------- Configuration Notification handling

	private CopyOnWriteArrayList<IConfigurationListener> mListenerList = new CopyOnWriteArrayList<IConfigurationListener>();

	public void addConfigListener(IConfigurationListener pListener) {
		if (!mListenerList.contains(pListener)) {
			mListenerList.add(pListener);
		}
	}

	public void removeConfigListener(IConfigurationListener pListener) {
		if (mListenerList.contains(pListener)) {
			mListenerList.remove(pListener);
		}
	}

	/**
	 * Fire a properties changed event. All listeners will be notified. All
	 * properties are communicated, it is the listeners responsibility to
	 * extract the applicable propety.
	 * <p>
	 * 
	 * @param event
	 *            PropertyEvent
	 */
	public void fireConfigurationChanged(ConfigurationEvent event) {

		synchronized (mListenerList) {
			Iterator it = mListenerList.iterator();
			while (it.hasNext()) {
				IConfigurationListener next = (IConfigurationListener) it
						.next();
				next.configurationChanged(event);
			}
		}
		
		// we log the number of notifications to detect problems
		mLog.info("Configuration Changed: " + event.getSource().toString());
	}

	// ---------- DATA HANDLING

	private static int sMaximumBackupFiles = 5;

	private static ArrayList<IDataHandler> mHandlerList = new ArrayList<IDataHandler>();

	public static final int FEED_INDEX = 0;

	public static final int CACHE_INDEX = 1;

	public static final int DIRECTORY_INDEX = 2;

	public static final int CONFIGURATION_INDEX = 3;

	public static final int PLUGIN_PROPERTIES_INDEX = 4;

	public static final int THEME_INDEX = 5;

	private static final String TEMP_EXTENSION = ".temp";

	private static final String BACKUP_EXTENSION = ".back";

	private static final String ERROR_BACKUP_EXTENSION = ".err-back";

	private static final int FILE_NOT_FOUND = -1;

	public void addDataHandler(IDataHandler pHandler, boolean pConcurrent)
			throws JPodderException {
		addDataHandler(pHandler, null, pConcurrent);
	}

	public void addDataHandler(IDataHandler pHandler) throws JPodderException {
		addDataHandler(pHandler, null, true);
	}

	public void addDataHandler(IDataHandler pHandler, File pFile)
			throws JPodderException {
		addDataHandler(pHandler, pFile, true);
	}

	/**
	 * Register a data handler with the file handler and if the data handler was
	 * not registered so far it will load the data for it
	 * 
	 * @param pHandler
	 *            Data Handler to be registered
	 * 
	 * @param JPodderException
	 *            If reading the file failed
	 */
	public void addDataHandler(IDataHandler pHandler, File pFile,
			boolean pConcurrent) throws JPodderException {

		mLog.debug("addDataHandler(), add data handler (index: "
				+ pHandler.getIndex() + "), handler: " + pHandler);
		if (pHandler != null && !mHandlerList.contains(pHandler)) {
			mHandlerList.add(pHandler);
			if (pFile == null) {
				pFile = getFile(pHandler);
			}
			pHandler.setPersistentFile(pFile);
			DataTask lTask = new DataTask(pHandler, pConcurrent);

		}
	}

	/**
	 * A task which executes data loading in the background. 
	 */
	class DataTask extends AbstractTaskWorker {
		IDataHandler mHandler;

		public DataTask(final IDataHandler pHandler, boolean pConcurrent) {
			if (pConcurrent) {
				mHandler = pHandler;
				TaskLogic.getInstance().add(this, null, null);
				start();
			} else {
				addHandler(pHandler);
			}
		}

		public void finished() {
			super.finished();
			TaskLogic.getInstance().fireTaskCompleted(mHandler, null);
			TaskLogic.getInstance().remove(this, null, null);
		}

		public Object construct() {
			addHandler(mHandler);
			return null;
		}
	}

	private Object addHandler(IDataHandler pHandler) {
		try {
			File lFile = pHandler.getPersistentFile();
			boolean lDone = false;
			while (!lDone) {
				try {
					mLog.info("addDataHandler(), file: " + lFile);
					String pContent = FileHandler.readFileContent(lFile);
					mLog.debug("addDataHandler(), content: " + pContent);
					pHandler.setContent(pContent);
					lDone = true;
				} catch (JPodderException jpe) {
					switch (jpe.getType()) {
					case IDataHandler.CONTENT_EMPTY:
					case FILE_NOT_FOUND:
						// Copy the default file and try again
						switch (pHandler.getIndex()) {
						case FEED_INDEX:
							FileHandler.copyFile(new File(
									FileHandler.sDefaultDirectory,
									ConfigurationLogic.FEED_FILE_NAME), lFile);
							break;
						case CACHE_INDEX:
							FileHandler.copyFile(new File(
									FileHandler.sDefaultDirectory,
									ConfigurationLogic.CACHE_FILE_NAME), lFile);
							break;
						case CONFIGURATION_INDEX:
							FileHandler.copyFile(new File(
									FileHandler.sDefaultDirectory,
									ConfigurationLogic.PROPS_FILE_NAME), lFile);
							break;
						case DIRECTORY_INDEX:
							FileHandler.copyFile(new File(
									FileHandler.sDefaultDirectory,
									ConfigurationLogic.DIR_FILE_NAME), lFile);
							break;
						case PLUGIN_PROPERTIES_INDEX:
							FileHandler.copyFile(new File(
									FileHandler.sDefaultDirectory,
									ConfigurationLogic.PLUGIN_PROPS_FILE_NAME),
									lFile);
							break;
						case THEME_INDEX:
							FileHandler.copyFile(new File(
									FileHandler.sDefaultDirectory,
									ConfigurationLogic.THEME_FILE_NAME), lFile);
							break;
						}

					default: {
						// For non default files we simply skip. The
						// file
						// will be created when writting the
						// content.
					}
						break;
					case IDataHandler.CONTENT_CORRUPT:
						mLog.info("addDataHandler(), corrupt file" + lFile
								+ ", parent dir: " + lFile.getParentFile());
						// Check if there are backup files
						final File lCorruptFile = lFile;
						File[] lFiles = lCorruptFile.getParentFile().listFiles(
								new FilenameFilter() {
									public boolean accept(File pDirectory,
											String pName) {
										return pName.startsWith(lCorruptFile
												.getName()
												+ BACKUP_EXTENSION);
									}
								});
						mLog.info("addDataHandler(), backup files: "
								+ java.util.Arrays.asList(lFiles));
						// CB FIXME, migrate Recover dialog.

						// RecoverDialog lDialog = new RecoverDialog(null);
						// lDialog.show(lFileContent,
						// java.util.Arrays
						// .asList(lFiles), pHandler);
						// lFile = lDialog.getFile();

						// AS lDone = true;
						// Let the user choose the next steps
						break;
					}
				}
			}
		} catch (Exception e) {
			mLog.warn(e.getMessage());
		}
		return null;

	}

	public File getFile(IDataHandler pHandler) {
		switch (pHandler.getIndex()) {
		case FEED_INDEX:
			return ConfigurationLogic.getInstance().getFeedFile();
		case CACHE_INDEX:
			return ConfigurationLogic.getInstance().getCacheFile();
		case CONFIGURATION_INDEX:
			return ConfigurationLogic.getInstance().getPropsFile();
		case DIRECTORY_INDEX:
			return ConfigurationLogic.getInstance().getDirectoryFile();
		case PLUGIN_PROPERTIES_INDEX:
			return ConfigurationLogic.getInstance().getPluginFile();
		case THEME_INDEX:
			return ConfigurationLogic.getInstance().getCssFile();
		default:
			return null;
		}
	}

	/**
	 * Checks all the registered Data Handlers if there is some modified content
	 * and if take the content and save it to the appropriate file
	 */
	public void save() {
		Iterator i = mHandlerList.iterator();
		while (i.hasNext()) {
			IDataHandler lHandler = (IDataHandler) i.next();
			save(lHandler);
		}
	}

	/**
	 * Saves the given Data Handler if it is registered or not
	 */
	public void save(IDataHandler pHandler) {
		save(pHandler, pHandler.getPersistentFile());
	}

	/**
	 * Saves the given Data Handler if it is registered or not
	 */
	public void save(IDataHandler pHandler, File pFile) {

		if (pHandler.isModified()) {
			try {
				String lFileContent = pHandler.getContent();
				if (pFile == null) {
					pFile = getFile(pHandler);
				}
				saveContentToFile(pFile, lFileContent);
			} catch (Exception e) {
				mLog.fatal("Could not obtain the content of data handler: "
						+ pHandler, e);
			}
		}
	}

	/**
	 * Save a modified registered Data Handlers;
	 */
	public void save(int pIndex, File pFile) {
		Iterator i = mHandlerList.iterator();
		while (i.hasNext()) {
			IDataHandler lHandler = (IDataHandler) i.next();
			if (lHandler.getIndex() == pIndex && lHandler.isModified()) {
				try {
					String lFileContent = lHandler.getContent();
					saveContentToFile(pFile, lFileContent);
				} catch (Exception e) {
					mLog.fatal("Could not obtain the content of data handler: "
							+ lHandler, e);
				}
			}
		}
	}

	/**
	 * Saves the given content to a file and also creates the necessary backups
	 * to undo changes and to recover from crashes
	 * 
	 * Some resource hold their own model, some don't When calling this method,
	 * this should be knows and the list argument should be provided.
	 * 
	 * @param pFile
	 *            File to which the content is to be saved to
	 * @param pContent
	 *            Content to be of the file
	 */
	private void saveContentToFile(File pFile, String pContent) {
		if (pFile == null) {
			mLog.error("Could not store content: '" + pContent
					+ "' because given file is not defined");
		} else {
			if (pContent == null) {
				mLog.warn("Could not save to file: " + pFile
						+ " because given content is not defined");
			} else {
				String lFileName = pFile.toString();
				File lTemp = new File(lFileName + TEMP_EXTENSION);
				// Check if the file exists and if then rename it and if that
				// fails, too, then use the
				// next available indexed temp file instead
				if (lTemp.exists()) {
					for (int i = 1; i <= 5; i++) {
						File lTemp2 = new File(lTemp + "." + i);
						if (!lTemp2.exists()) {
							if (!lTemp.renameTo(lTemp2)) {
								mLog
										.warn("Could not rename existing temp. file: '"
												+ lTemp
												+ "' to '"
												+ lTemp2
												+ "'"
												+ " -> use the second file as temporary file instead");
								lTemp = lTemp2;
							} else {
								mLog.info("rename success from " + lTemp + "to"
										+ lTemp2);
								break;
							}
						}
					}
				}
				// Write the content of the resource to the temp file
				boolean lWriteOk = false;
				FileWriter lWriter = null;
				try {
					lWriter = new FileWriter(lTemp);
					lWriter.write(pContent, 0, pContent.length());
					lWriteOk = true;
				} catch (Exception e) {
					mLog.fatal("Failed to write resource to file: " + lTemp
							+ " -> exit", e);
				} finally {
					if (lWriter != null) {
						try {
							lWriter.close();
						} catch (Exception e) {
						}
					}
				}
				if (lWriteOk) {
					// Delete the backup file with the maximum index, move all
					// the other backup files an index up
					// and then rename the current file to the first backup file
					File lDelete = new File(lFileName + BACKUP_EXTENSION + "."
							+ sMaximumBackupFiles);
					boolean lMoveOk = true;
					if (lDelete.exists()) {
						if (!lDelete.delete()) {
							mLog.debug("Eelete of max backup file: " + lDelete
									+ " failed");
							lMoveOk = false;
						}
					}
					if (lMoveOk) {
						for (int i = sMaximumBackupFiles - 1; i > 0; i--) {
							File lToRename = new File(lFileName
									+ BACKUP_EXTENSION + "." + i);
							if (lToRename.exists()) {
								File lRenameTo = new File(lFileName
										+ BACKUP_EXTENSION + "." + (i + 1));
								if (!lToRename.renameTo(lRenameTo)) {
									mLog.debug("Move of backup file: "
											+ lToRename + " to: " + lRenameTo
											+ " failed");
									lMoveOk = false;
									break;
								}
							}
						}
					}
					if (lMoveOk) {
						// If the move went well so fare we rename the current
						// file to the first backup file
						File lRenameTo = new File(lFileName + BACKUP_EXTENSION
								+ "." + 1);
						if (!pFile.renameTo(lRenameTo)) {
							mLog.warn("Could not move current file: " + pFile
									+ " to first backup file");
							lMoveOk = false;
						}
					}
					if (!lMoveOk) {
						// If any of this failes rename the current file to the
						// next availalbe index backup error file
						// to provide a copy
						int i = 0;
						File lErrorBackupFile = null;
						while (true) {
							lErrorBackupFile = new File(lFileName
									+ ERROR_BACKUP_EXTENSION + "." + i);
							if (!lErrorBackupFile.exists()) {
								break;
							}
						}
						if (!pFile.renameTo(lErrorBackupFile)) {
							// Now this fails so we exit in an error
							// constellataion given the user
							// as chance to recover by hand
							mLog.fatal("Failed to rename the current file: "
									+ pFile + " to an error backup file: "
									+ lErrorBackupFile);
						} else {
							mLog.debug("Move of current file: " + pFile
									+ " to error backup file: "
									+ lErrorBackupFile + " failed");
							lMoveOk = true;
						}
					}
					if (lMoveOk) {
						// Now we finally rename the temporary file to the name
						// of the current file to activate its content
						mLog.debug("Move of temp file: " + lTemp
								+ " that exists: " + lTemp.exists()
								+ " to target file: " + pFile
								+ " that exists: " + pFile.exists());
						if (!lTemp.renameTo(pFile)) {
							mLog.fatal("Failed to rename the temporary file: "
									+ lTemp + " to the target file: " + pFile);
						}
					}
				}
			}
		}
	}
}