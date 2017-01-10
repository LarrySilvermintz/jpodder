package com.jpodder.data.content;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.jpodder.data.player.IPlayer;
import com.jpodder.plugin.PluginLoader.Plugin;
import com.jpodder.util.Logger;
import com.jpodder.util.PersistentObject;

/**
 * The content Logic can return a plugin application for a certain MIME type. It
 * creates a map of applications and MIME types. Various convenience methods are
 * available to build, query, print etc the MIME type map.
 * 
 * @author <a href="mailto:christophe@kualasoft.com" >Christophe Bouhier </a>
 * @version 1.1
 */
public class ContentLogic {

	private Logger mLog = Logger.getLogger(getClass().getName());

	private Hashtable mMIMEToAppMap = new Hashtable();

	private static ContentLogic sSelf;

	private PersistentObject mPluginLoader;

	public static ContentLogic getInstance() {
		if (sSelf == null) {
			sSelf = new ContentLogic();
		}
		return sSelf;
	}

	public ContentLogic() {
		try {
			if (com.jpodder.util.Debug.WITH_DEV_DEBUG) {
				mLog.devDebug("<init>, Create Plugin Loader");
			}
			mPluginLoader = new PersistentObject("jpodder.plugin.loader");
			if (com.jpodder.util.Debug.WITH_DEV_DEBUG) {
				mLog.devDebug("<init>, Plugin Loader: " + mPluginLoader);
			}
		} catch (Exception e) {
			mLog.error("could not create persistent object for plugin loader",
					e);
		}
		buildApplicationMap();
	}

	/**
	 * Get a map of which MIME types, are supported with which player. This will
	 * be the default setting for the application map. <br>
	 * 
	 * MIME types could be overriden to be supported by external applications
	 * instead of plugins.
	 * 
	 */
	public void buildApplicationMap() {
		// Iterate through the MIME types and associate a player.
		// If multiple players support the MIME type, the preffered
		// player is set, otherwise any other matching player.
		for (int i = 0; i < IContent.BASE_TYPES.length; i++) {
			String lBaseType = IContent.BASE_TYPES[i];
			ArrayList mimeSupportList = new ArrayList();
			if (com.jpodder.util.Debug.WITH_DEV_DEBUG) {
				mLog.devDebug("Plugin Loader: " + mPluginLoader);
			}
			if (com.jpodder.util.Debug.WITH_DEV_DEBUG) {
				mLog.devDebug("List of player plugins: "
						+ mPluginLoader.invoke("findPluginsByType",
								new Object[] { IPlayer.class }));
			}
			Iterator j = ((List) mPluginLoader.invoke("findPluginsByType",
					new Object[] { IPlayer.class })).iterator();
			while (j.hasNext()) {
				Plugin lPlugin = (Plugin) j.next();
				if (lPlugin.getInstance() instanceof IPlayer) {
					IPlayer lPlayer = (IPlayer) lPlugin.getInstance();
					IContent[] lPlayerTypes = lPlayer.getMIMETypes();
					if (lPlayerTypes != null) {
						for (int k = 0; k < lPlayerTypes.length; k++) {
							IContent lType = lPlayerTypes[k];
							if (lType.getName().equals(lBaseType)) {
								// A MIME type name/string is matched.
								// We will map the name to the player, depending
								// on the default player.
								mimeSupportList.add(lPlayer.getName());
							}
						}// Checked all MIME types for this player.
					}// This player doesn't have any MIME types?
				}// No of type player so ignore it
			} // Checked All players.
			// Add the list of players to the MIME type only if the list
			// contains at least 1 player.
			mMIMEToAppMap.put(lBaseType, mimeSupportList);
			// Add also an entry for the variants.
		}
	}

	/**
	 * Print the mapping of the mime types and the players.
	 */
	public String printApplicationMap() {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < IContent.BASE_TYPES.length; i++) {
			String type = IContent.BASE_TYPES[i];
			java.util.List list = (java.util.List) mMIMEToAppMap.get(type);
			Iterator it1 = list.iterator();

			s.append("Mapping:" + type + " to:\n");
			while (it1.hasNext()) {
				String name = (String) it1.next();
				// Player pl = (Player)playerMap.get(name);
				s.append(" -" + name + "\n");
			}
		}
		return s.toString();
	}

	/**
	 * Get the player based on the MIME type. If no player exists for this MIME
	 * type, return no player.
	 * 
	 * @param mime
	 * @return Returns the player for a certain mimetype.
	 */
	public IPlayer getPlayer(IContent pContent) {

		IPlayer lPlayer = null;
		// Sequence:
		// 1. Get the MIME type, base type if any.
		// 2. From the list players

		if (pContent == null) {
			return lPlayer;
		}

		// Get the base type for this exotic type.
		if (ContentVariants.hasVariants(pContent)) {
			if (!ContentVariants.isBaseType(pContent)) {
				pContent = ContentVariants.getBaseType(pContent);
				if (pContent == null) {
					return lPlayer;
				}
			}
		}

		List plList = (List) mMIMEToAppMap.get(pContent.getName());
		// We have one or more players select a player from the list.
		if (plList != null && plList.size() > 0) {
			String name = (String) plList.get(0);
			lPlayer = (IPlayer) mPluginLoader.invoke("findPluginInstance",
					new Object[] { name });
		}
		// This MIME type is not supported by any player.
		// Return null.
		return lPlayer;
	}

	public boolean supportsContent(String pMime, IPlayer pPlayer) {
		try {
			return supportsContent(new Content(pMime), pPlayer);
		} catch (ContentException ce) {
			return false;
		}
	}

	public boolean supportsContent(IContent pContent, IPlayer pPlayer) {

		if (ContentVariants.hasVariants(pContent)) {
			// Get the base type for this exotic type.
			if (!ContentVariants.isBaseType(pContent)) {
				pContent = ContentVariants.getBaseType(pContent);
				if (pContent == null) {
					return false;
				}
			}

		}
		IContent[] lList = pPlayer.getMIMETypes();
		if (lList != null) {
			for (int i = 0; i < lList.length; i++) {
				IContent lType = lList[i];
				if (lType.getContent().equals(pContent.getContent())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return Returns the MIME to application MAP.
	 */
	public Hashtable getPlayerMap() {
		return mMIMEToAppMap;
	}

	/**
	 * Iterate through the player map and retrieve all mime types.
	 * 
	 * @return MIMEType[] An array of mime type objects.
	 */
	public IContent[] getPlayerContentArray() {
		ArrayList mimeList = new ArrayList();

		List lPlayers = (List) mPluginLoader.invoke("findPluginsByType",
				new Object[] { IPlayer.class });
		for (Iterator iter = lPlayers.iterator(); iter.hasNext();) {
			Plugin lPlugin = (Plugin) iter.next();
			IPlayer lPlayer = (IPlayer) lPlugin.getInstance();
			IContent[] types = lPlayer.getMIMETypes();
			for (int i = 0; i < types.length; i++) {
				if (!mimeList.contains(types[i])) {
					mimeList.add(types[i]);
				}
			}
		}
		return (IContent[]) mimeList.toArray();
	}

	/**
	 * Return the MIME type based on the extension of the file. Only MP3,
	 * torrent is supported for now.
	 * 
	 * @param file
	 * @return String The MIME type in String format.
	 */
	public static String getContentFromFileName(String file) {
		if (isMP3(file)) {
			return IContent.MIME_MPEG;
		}
		if (isTorrent(file)) {
			return IContent.MIME_BT;
		}
		if (isMP4v(file)) {
			return IContent.MIME_MP4V;
		}
		if (isMP4(file)) {
			return IContent.MIME_AAC;
		}
		return "";
	}
	
	/**
	 * NOT IMPLEMENTED YET.
	 * 
	 * @param pContent
	 * @return
	 */
	public static String getExtensionFromContent(IContent pContent) {
		return "";
	}

	public static boolean isExtension(String pFile, String pExtension) {
		return pFile.lastIndexOf(pExtension) == -1 ? false : true;
	}

	/**
	 * Determines if the extension is .wma.
	 * 
	 * @param file
	 * @return boolean if the specified file ends with .wma
	 */
	public static boolean isWMA(String file) {
		return file.lastIndexOf(".wma") == -1 ? false : true;
	}

	/**
	 * Determines if the extension is .torrent.
	 * 
	 * @param file
	 * @return boolean If the specified file ends with .torrent.
	 */
	public static boolean isTorrent(String file) {
		return isExtension(file, IContent.EXTENSION_BT);
	}

	/**
	 * Determines if the extension is .mp3.
	 * 
	 * @param file
	 *            String
	 * @return boolean
	 */
	public static boolean isMP3(String file) {
		return isExtension(file, IContent.EXTENSION_MP3);
	}

	/**
	 * @param pFile
	 * @return
	 */
	public static boolean isMP4v(String pFile) {
		return isExtension(pFile, IContent.EXTENSION_MP4V);
	}

	/**
	 * @param pFile
	 * @return
	 */
	public static boolean isMP4(String pFile) {
		return isExtension(pFile, IContent.EXTENSION_AAC);
	}

}