package com.jpodder.os;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */

/**
 * An extended OS detection class. It parses the version number for the OS and
 * create human readable Strings of the OS. Provides conveniences classes for
 * querying what kind of OS we are using.
 * 
 * Additionally this class supports the setting OS entries. For Windows32, it
 * uses the reg.exe command for querying or setting win32 registry entries.
 * 
 * @link http://forum.java.sun.com/thread.jspa?forumID=256&threadID=454268
 * @link http://lopica.sourceforge.net/os.html
 * @link http://tolstoy.com/samizdat/sysprops-mac.html
 */
public class OS {

	private static Logger sLog = Logger.getLogger(OS.class.getName());

	private static String osName;

	// Microsoft Windows OS.
	private static String WIN95 = "Windows 95";

	private static String WIN98 = "Windows 98";

	private static String WINME = "Windows ME";

	private static String WIN9x = "Windows 9x (Unknown)";

	private static String WINNT3_5 = "Windows NT 3.5";

	private static String WINNT2000 = "Windows 2000";

	private static String WINNTXP = "Windows XP";

	private static String WINNT2003 = "Windows 2003";

	private static String WINNTx = "Windows 2003";

	// Apple Max OS.
	private static String MACOSX = "Mac OS X";

	// Linux OS. (Check Architecture).
	private static String LINUX = "Linux";

	static {
		osName = System.getProperty("os.name");
		String osVersion = System.getProperty("os.version");
		String osArchitecture = System.getProperty("os.arch");

		String osMajorVersion = osVersion.substring(0, osVersion.indexOf("."));
		String osMinorVersion = osVersion.substring(osVersion.indexOf(".") + 1,
				osVersion.length());

		int osMajorInt = new Integer(osMajorVersion).intValue();

		int osMinorInt = -1;
		try {
			osMinorInt = new Integer(osMinorVersion).intValue();
		} catch (java.lang.Exception e) {
		}

		sLog.info("OS = " + osName + " " + osMajorVersion + "."
				+ osMinorVersion);

		if (osName.startsWith("Windows")) {
			switch (osMajorInt) {
			// case VER_PLATFORM_WIN32s:
			// sprops.os_name = "Windows 3.1";
			// break;
			case 4: {
				switch (osMinorInt) {
				case 0:
					osName = WIN95;
					break;
				case 10:
					osName = WIN98;
					break;
				case 90:
					osName = WINME;
					break;
				default:
					osName = WIN9x;
				}
			}
				break;
			case 5:
				switch (osMinorInt) {
				case 0:
					osName = WINNT2000;
					break;
				case 1:
					osName = WINNTXP;
					break;
				case 2:
					osName = WINNT2003;
					break;
				default:
					osName = WINNTx;
					break;
				}
				break;
			default:
				break;
			}
		}
	}

	public static Win32Registry getWin32Registry() {
		return new OS().new Win32Registry();
	}

	public static boolean isWindows95() {
		return (osName.equals(WIN95));
	}

	public static boolean isWindows98() {
		return osName.startsWith(WIN98);
	}

	public static boolean isWindowsME() {
		return osName.startsWith(WINME);
	}

	public static boolean isWindows2000() {
		return osName.startsWith(WINNT2000);
	}

	public static boolean isWindowsXP() {
		return osName.startsWith(WINNTXP);
	}

	public static boolean isWindowsNT() {
		return isWindows2000() || isWindowsXP();
	}

	public static boolean isWindows() {
		return osName.startsWith("Windows");
	}

	public static boolean isOSX() {
		return osName.startsWith(MACOSX);
	}

	public static boolean isLinux() {
		return osName.startsWith(LINUX);
	}

	/**
	 * Perform some MAC specific initializtion.
	 */
	public static void initializeMac() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.brushMetalLook", "true");
	}

	/**
	 * Works on winXP
	 */
	public class Win32Registry {

		public static final String REG_QUERY_UTIL = "reg query ";

		public static final String REG_ADD_UTIL = "reg add ";
		
		public static final String REG_DELETE_UTIL = "reg delete ";
		
		public static final String REGSTR_TOKEN = "REG_SZ";

		public static final String REGDWORD_TOKEN = "REG_DWORD";

		public static final String REGNONE_TOKEN = "REG_NONE";

		public static final String HKCU = "HKCU";

		public static final String HKLM = "HKLM";

		public static final String HKCR = "HKCR";

		// query options
		public static final String OPTION_DEFAULT = " /ve "; // Use to set
																// the (Default)
																// value

		public static final String OPTION_VALUE = " /v ";
		
		public static final String OPTION_VALUE_ALL = " /va ";
		
		public static final String OPTION_SUBKEYS = "/s ";

		// ADD options
		public static final String OPTION_FORCE = " /f ";

		public static final String OPTION_DATA = "/d ";

		// Some more common keys.
		// /////// OS FOLDERS
		public static final String WINDOWS = "\\Software\\Microsoft\\Windows\\CurrentVersion";

		public static final String EXPLORER_SHELL_FOLDERS = "\\Explorer\\Shell Folders";

		// STANDARD CLASS KEYS
		public static final String SHELL = "\\shell";

		public static final String OPEN = "\\open";

		public static final String COMMAND = "\\command";

		// STANDARD PODCAST KEYS
		public static final String PCAST = "\\pcast";

		public static final String PODCAST = "\\podcast";

		// PROTOCOL VALUE
		public static final String PROTOCOL = "URL Protocol";

		public static final String URL = "URL:";

		// values, use /v
		public static final String PERSONAL_FOLDER = "Personal";

		public static final String COMMON_APP_DATA = "Common AppData";

		public static final String COMMON_DESKTOP = "Common Desktop";

		String quote(String pToQuote) {
			return "\"" + pToQuote + "\"";
		}

		/**
		 * No option and value is specified.
		 * 
		 * @param pCommand
		 * @param pKey
		 * @return
		 */
		public String reg(String pCommand, String pKey) {
			return reg(pCommand, pKey, null, null);
		}

		public String reg(String pCommand, String pKey, String pOption) {
			return reg(pCommand, pKey, pOption, null);
		}

		public String reg(String pCommand, String pKey, String pOption,
				String pValue) {
			try {
				String pExec = pCommand + quote(pKey);
				if (pOption != null && pValue != null) {
					pExec += pOption + quote(pValue);
				}
				Process process = Runtime.getRuntime().exec(pExec);

				StreamReader reader = new StreamReader(process.getInputStream());

				reader.start();
				process.waitFor();
				reader.join();

				String result = reader.getResult();
				return result;
			} catch (Exception e) {
				return null;
			}
		}
		
		public boolean isError(String pResult){
			return pResult.trim().startsWith("Error");
		}
		
		public String getValue(String pResult){
			int p = pResult.indexOf(REGSTR_TOKEN);

			if (p == -1)
				return null;

			return pResult.substring(p + REGSTR_TOKEN.length()).trim();
			
		}
		
		
		class StreamReader extends Thread {
			private InputStream is;

			private StringWriter sw;

			StreamReader(InputStream is) {
				this.is = is;
				sw = new StringWriter();
			}

			public void run() {
				try {
					int c;
					while ((c = is.read()) != -1)
						sw.write(c);
				} catch (IOException e) {
					;
				}
			}

			String getResult() {
				return sw.toString();
			}
		}
	}
}
 