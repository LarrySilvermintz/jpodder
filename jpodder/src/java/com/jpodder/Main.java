package com.jpodder;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 */
import java.io.File;
import java.net.URL;

import org.apache.log4j.xml.DOMConfigurator;

import com.jpodder.util.PersistentObject;

/**
 * jPodder bootstrap class
 */
public class Main {

	private static final String CONFIG_DIR_OPTION = "-configuration=";

	public Main(String[] args) {
		// Set some OS specific properties.
		// if (OS.isOSX()) {
		// OS.initializeMac();
		// }

		// Parse all the arguments.
		Arguments lArgs = new Arguments();
		lArgs.parse(args);

		String lConfigDirectoryArgument = "";
		for (int i = 0; i < args.length; i++) {
			String lArgument = args[i];
			if (lArgument == null) {
				continue;
			} else if (lArgument.length() == 0) {
				continue;
			} else {
				if (lArgument.startsWith(CONFIG_DIR_OPTION)) {
					lConfigDirectoryArgument = lArgument
							.substring(CONFIG_DIR_OPTION.length());
				}
			}
		}

		final String lConfigDirectory = lConfigDirectoryArgument;

		try {

			final JPodderClassLoader lLoader = new JPodderClassLoader(
					new URL[] {}, Main.class.getClassLoader());

			// Add all archives in the lib directory to the class loader
			// Find the Lib directory first.

			// CB TODO, we can't load "../lib" if we are not in /bin.

			File[] lArchives = new File("../lib").listFiles();

			if (lArchives != null) {
				for (int i = 0; i < lArchives.length; i++) {
					File lArchive = lArchives[i];
					if (lArchive.toString().endsWith(".jar")) {
						lLoader.addURL(lArchive.toURL());
					}
				}
			}

			File lFile = new File("./log4j.xml");
			if (lFile.exists()) {
				DOMConfigurator.configure("./log4j.xml");
			}

			// This creation is just here to enlist the class loader
			PersistentObject lPeristentLoader = new PersistentObject(
					"jpodder.class.loader", lLoader);

			PersistentObject Logic = new PersistentObject("jpodder.logic",
					lLoader, "com.jpodder.Logic",
					new Object[] { lConfigDirectory },
					new Class[] { String.class });

			launchUI(lConfigDirectory, lLoader);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Main class.
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		new Main(args);
	}

	class Arguments {

		private String LIB = "lib";

		private String[] mOptions = new String[10];

		final int LIB_OPTION = 0;

		private String mLibraryPath;

		Arguments() {
			mOptions[LIB_OPTION] = LIB;
		}

		public void parse(String[] pArgs) {
			for (int i = 0; i < pArgs.length; i++) {
				String lArgument = pArgs[i];

				// Is it an option
				if (lArgument.startsWith("-")) {
					// Now get the tupple for the option.
					lArgument = lArgument.substring(1, lArgument.length()); // strip
					// hyphen
					int option = getOption(lArgument);

					switch (option) {
					case -1: {
						// unrecognized, ignored.
					}
						break;
					case LIB_OPTION: {
						mLibraryPath = getValue(lArgument);
					}
						break;
					}
				}
			}
		}

		public int getOption(String pTupple) {
			for (int i = 0; i < mOptions.length; i++) {
				String lOption = mOptions[i];
				if (lOption == null) {
					continue;
				} else if (pTupple.startsWith(lOption)) {
					return i;
				}
			}
			return -1;
		}

		public String getValue(String pTupple) {
			return null; // TODO FILL IN.
		}

	}

	public static void launchUI(String lConfigDirectory, ClassLoader lLoader) {

		// UILauncher.getInstance(lConfigDirectory);

		PersistentObject lLauncherUI = new PersistentObject(
				"jpodder.main.ui.swt", lLoader,
				"com.jpodder.ui.swt.UILauncher",
				new Object[] { lConfigDirectory }, new Class[] { String.class });

	}

	public static final String APP_TITLE = "jPodder";

	public static final String APP_RELEASE = "v 1.1 RC3";

}