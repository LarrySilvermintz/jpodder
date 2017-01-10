package com.jpodder.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.jpodder.JPodderClassLoader;
import com.jpodder.util.Debug;
import com.jpodder.util.Logger;
import com.jpodder.util.PersistentObject;

/**
 * This class is responsible for loading plugins. Plug-ins should be stored in
 * the folder named plugin.
 * 
 * @see Plugin
 */
public class PluginLoader {

	private static final String PLUGIN_DESCRIPTOR_NAME = "plugin.xml";

	private ArrayList mPluginFiles = new ArrayList();

	private ArrayList mPlugins = new ArrayList();

	private File mDirectory;

	private Logger mLog = Logger.getLogger(getClass().getName());

	/**
	 * @param pPluginDirectory
	 *            The directory where plugin are placed into (this directory
	 *            must exists and be readable)
	 */
	public PluginLoader(File pPluginDirectory) {
		mDirectory = pPluginDirectory;
	}

	/**
	 * Scan the plugin folder, for custom player interfaces.
	 */
	public void scan() {
		mPlugins.clear();
		mLog.info("Plugin Directory: " + mDirectory + ", exists: "
				+ mDirectory.exists() + ", is directory: "
				+ mDirectory.isDirectory());

		if (mDirectory.exists() && mDirectory.isDirectory()) {
			File[] lArchiveFiles = mDirectory.listFiles();
			for (int i = 0; i < lArchiveFiles.length; i++) {
				mLog.info("Plugin archive found: " + lArchiveFiles[i]);
				if (!lArchiveFiles[i].isDirectory()) {
					try {
						PluginFile lFile = findFile(lArchiveFiles[i].toString());
						if (lFile != null
								&& lFile.getLastModified() == lArchiveFiles[i]
										.lastModified()) {
							// Already loaded so ignore
							continue;
						}
						if (lFile != null) {
							Iterator j = lFile.getPluginIterator();
							while (j.hasNext()) {
								mPlugins.remove((Plugin) j.next());
							}
							mPluginFiles.remove(lFile);
						}
						lFile = new PluginFile(lArchiveFiles[i].toString(),
								lArchiveFiles[i].lastModified());
						JPodderClassLoader lLocalClassLoader = new JPodderClassLoader(
								new URL[] { lArchiveFiles[i].toURL() },
								getClass().getClassLoader());
						// Open JAR File and read the plugin.xml descriptor
						if (Debug.WITH_DEV_DEBUG) {
							mLog.devDebug("scan(), plugin archive: "
									+ lArchiveFiles[i]);
						}
						JarFile lPluginArchive = new JarFile(lArchiveFiles[i]);
						if (Debug.WITH_DEV_DEBUG) {
							mLog.devDebug("scan(), jar-plugin archive: "
									+ lPluginArchive);
						}
						// Get plugin.xml
						JarEntry lDescriptorEntry = lPluginArchive
								.getJarEntry(PLUGIN_DESCRIPTOR_NAME);
						if (Debug.WITH_DEV_DEBUG) {
							mLog.devDebug("scan(), descriptor entry: "
									+ lDescriptorEntry);
						}
						InputStreamReader lReader = new InputStreamReader(
								lPluginArchive.getInputStream(lDescriptorEntry));
						PluginsDocument lDocument = PluginsDocument.Factory
								.parse(lReader);
						TPlugins lRoot = lDocument.getPlugins();
						if (lRoot.isSetArchives()) {
							TArchives lArchives = lRoot.getArchives();
							TArchive[] lArchiveArray = lArchives
									.getArchiveArray();
							for (int j = 0; j < lArchiveArray.length; j++) {
								TArchive lArchive = lArchiveArray[j];
								String lType = "java";
								if (lArchive.isSetType()) {
									lType = lArchive.getType();
								}
								String lScope = "global";
								if (lArchive.isSetScope()) {
									lScope = lArchive.getScope();
								}
								String lArchiveName = lArchive.getStringValue();
								PersistentObject lLoader = new PersistentObject(
										"jpodder.class.loader");

								mLog.info("archive entry found: '"
										+ lArchiveName + "'");
								if (lArchiveName != null
										&& lArchiveName.length() > 0) {
									JarEntry lArchiveEntry = lPluginArchive
											.getJarEntry(lArchiveName);
									mLog.info("JAR archive entry found: "
											+ lArchiveEntry);
									if (lArchiveEntry != null) {
										// Archive found now extract it and add
										// it
										// to either the class loader or load
										// the
										// library
										BufferedInputStream lInput = new BufferedInputStream(
												lPluginArchive
														.getInputStream(lArchiveEntry));

										File lOutputDir = new File(
												mDirectory,
												"temp"
														+ File.separator
														+ System
																.currentTimeMillis());
										if (!lOutputDir.exists()) {
											lOutputDir.mkdirs();
										}
										File lOutputFile = new File(lOutputDir,
												lArchiveName);
										mLog.info("inner archive copied to: "
												+ lOutputFile);
										BufferedOutputStream lOutput = new BufferedOutputStream(
												new FileOutputStream(
														lOutputFile));
										byte[] lBuffer = new byte[1024];
										int lLength = 0;
										while ((lLength = lInput.read(lBuffer)) >= 0) {
											lOutput.write(lBuffer, 0, lLength);
										}
										lInput.close();
										lOutput.close();

										if ("java".equalsIgnoreCase(lType)) {
											if ("global"
													.equalsIgnoreCase(lScope)) {
												mLog
														.info("inner archive added to application class loader: "
																+ lArchiveName);
												lLoader
														.invoke(
																"addURL",
																new Object[] { lOutputFile
																		.toURL() });
												lFile.setLoadedGlobally(true);
											} else {
												mLog
														.info("inner archive added to local class loader: "
																+ lLocalClassLoader
																+ ", url: "
																+ lOutputFile
																		.toURL());
												lLocalClassLoader
														.addURL(lOutputFile
																.toURL());
											}
										} else { // This is a native library.
											lLoader
													.invoke(
															"addLibrary",
															new Object[] { lOutputFile });
											mLog
													.info("Inner archive loaded as library: "
															+ lArchiveName
															+ ", current class loader: "
															+ getClass()
																	.getClassLoader());
											ClassLoader lCheckLoader = getClass()
													.getClassLoader();

										}

										// Now we delete the temp file again.
										lOutputFile.deleteOnExit();
									}
								}
							}
						}

						TPlugin[] lPlugins = lRoot.getPluginArray();
						for (int j = 0; j < lPlugins.length; j++) {
							TPlugin lTPlugin = lPlugins[j];
							String lClassName = lTPlugin.getClass1();
							String lName = lClassName;
							if (lTPlugin.isSetName()) {
								lName = lTPlugin.getName();
							}
							// Instantiate plugin here.
							
							mLog.info("plugin entry found: " + lName
									+ ", class: " + lClassName);
							Class lClass = null;
							try {
								lClass = lLocalClassLoader
										.loadClass(lClassName);
								Object lInstance = lClass.newInstance();
								Plugin lPlugin = new Plugin(lName, lClass,
										lInstance);
								mPlugins.add(lPlugin);
								lFile.addPlugin(lPlugin);
								mLog.info("Loaded plugin class: " + lClass);
							} catch (ClassNotFoundException cnfe) {
								mLog.warn("Plugin class not found", cnfe);
								// Could not load this class ??
							} catch (NoClassDefFoundError ncdfe) {
								mLog.warn("Plugin class not found", ncdfe);
							}
						}
					} catch (IOException ioe) {
						mLog.info("Plugin class file problem", ioe);
					} catch (Exception e) {
						mLog.info("Plugin class file problem", e);
					}
				}
			}
		}
	}

	public Iterator getPluginIterator() {
		return mPlugins.iterator();
	}

	private PluginFile findFile(String pFileName) {
		Iterator i = mPluginFiles.iterator();
		if (pFileName != null && pFileName.trim().length() > 0) {
			while (i.hasNext()) {
				PluginFile lFile = (PluginFile) i.next();
				if (pFileName.equals(lFile.getFileName())) {
					return lFile;
				}
			}
		}
		return null;
	}

	public Plugin findPlugin(String pPluginName) {
		if (pPluginName != null && pPluginName.trim().length() > 0) {
			Iterator i = mPlugins.iterator();
			while (i.hasNext()) {
				Plugin lPlugin = (Plugin) i.next();
				if (pPluginName.equals(lPlugin.getName())) {
					return lPlugin;
				}
			}
		}
		return null;
	}

	public Object findPluginInstance(String pPluginName) {
		if (pPluginName != null && pPluginName.trim().length() > 0) {
			Iterator i = mPlugins.iterator();
			while (i.hasNext()) {
				Plugin lPlugin = (Plugin) i.next();
				if (pPluginName.equals(lPlugin.getName())) {
					return lPlugin.getInstance();
				}
			}
		}
		return null;
	}

	public List findPluginsByType(Class pType) {
		List lReturn = new ArrayList();
		if (pType != null) {
			Iterator i = mPlugins.iterator();
			while (i.hasNext()) {
				Plugin lPlugin = (Plugin) i.next();
				if (pType.isAssignableFrom(lPlugin.getClass1())) {
					lReturn.add(lPlugin);
				}
			}
		}
		return lReturn;
	}

	public void cleanTempPlugin() {
		File lTempDir = new File(mDirectory, "temp");
		if (lTempDir.exists()) {
			lTempDir.delete();
		}
	}

	public static class PluginFile {
		private String mFileName;

		private long mModified;

		private boolean mLoadedGlobally;

		private List mPlugins = new ArrayList();

		public PluginFile(String pFileName, long pLastModified) {
			mFileName = pFileName;
			mModified = pLastModified;
		}

		/**
		 * @return the value of FileName.
		 */
		public String getFileName() {
			return mFileName;
		}

		/**
		 * @return the value of Modified.
		 */
		public long getLastModified() {
			return mModified;
		}

		/**
		 * @return the value of LoadedGlobally.
		 */
		public boolean getLoadedGlobally() {
			return mLoadedGlobally;
		}

		/**
		 * Sets the value of LoadedGlobally.
		 * 
		 * @param pLoadedGlobally
		 *            The value to assign to mLoadedGlobally.
		 */
		public void setLoadedGlobally(boolean pLoadedGlobally) {
			mLoadedGlobally = pLoadedGlobally;
		}

		/** @return Iterator over the list of plugins for this file * */
		public Iterator getPluginIterator() {
			return mPlugins.iterator();
		}

		/**
		 * Adds a new plugin to the file
		 * 
		 * @param pPlugin
		 *            Plugin to be added that must not be null
		 */
		public void addPlugin(Plugin pPlugin) {
			mPlugins.add(pPlugin);
		}
	}

	public static class Plugin {
		private String mName;

		private Class mClass;

		// AS ??
		private Object mInstance;

		public Plugin(String pName, Class pClass, Object pInstance) {
			mName = pName;
			mClass = pClass;
			mInstance = pInstance;
		}

		public String getName() {
			return mName;
		}

		public Class getClass1() {
			return mClass;
		}

		public Object getInstance() {
			return mInstance;
		}
	}
}