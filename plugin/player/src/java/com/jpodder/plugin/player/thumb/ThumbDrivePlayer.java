package com.jpodder.plugin.player.thumb;

import java.io.File;
import java.io.IOException;

import com.jpodder.FileHandler;
import com.jpodder.JPodderException;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IDataHandler;
import com.jpodder.data.content.Content;
import com.jpodder.data.content.ContentException;
import com.jpodder.data.content.IContent;
import com.jpodder.data.player.IPlayer;
import com.jpodder.data.player.PlayerException;
import com.jpodder.plugin.PluginLogic;
import com.jpodder.plugin.PluginRegistryEntry;
import com.jpodder.tasks.ITaskListener;
import com.jpodder.tasks.TaskEvent;
import com.jpodder.tasks.TaskLogic;
import com.jpodder.util.Util;

/**
 * This is a generic thumb drive player. It also add a UI panel the
 * configuration.
 * 
 */
public class ThumbDrivePlayer implements IPlayer, ITaskListener {

	private static boolean lThumbDriveAvailable = false;

	protected static String sCacheFolder = "";

	public ThumbDrivePlayer() {
		TaskLogic.getInstance().addListener(this);
		lThumbDriveAvailable = true;
	}

	// We first need to check if we have some info in the plugin registry.
	public void taskCompleted(TaskEvent e) {
		Object lSrc = e.getSource();
		if (lSrc instanceof IDataHandler) {
			if (((IDataHandler) lSrc).getIndex() == ConfigurationLogic.PLUGIN_PROPERTIES_INDEX) {
				PluginRegistryEntry lEntry = PluginLogic.getInstance()
						.getRegistryEntry("thumbdrive");
				if (lEntry != null) {
					String lLocation = lEntry.getValue();

					if (lLocation == null || lLocation.length() == 0) {
						// PluginLogic.getInstance().setRegistryEntry("thumbdrive",
						// sCacheFile);
						lThumbDriveAvailable = false;
					} else {
						sCacheFolder = lLocation;
						setData();
						lThumbDriveAvailable = true;
					}
				}

			}
		}
	}

	public void taskAborted(TaskEvent e) {

	}

	public void taskFailed(TaskEvent e) {

	}

	public String getName() {
		return "Thumb Drive Player";
	}

	/**
	 * 
	 */
	public void initialize() {
	}

	/**
	 * Extendable parameter. Can be used by plugin UI functions to talk to a
	 * player plugin.
	 * 
	 * @param pData
	 */
	public static void setData() {
	}

	/**
	 * Unitialize the COM Interface.
	 */
	public void finalize() {
	}

	/**
	 * Returns if the player has a specified track. The Play list name is
	 * required for some players.
	 * 
	 * @param playListName
	 *            String
	 * @param pTrackPath
	 * @return boolean
	 * @throws JPodderException
	 */
	public boolean hasTrack(String playListName, String pTrackPath)
			throws PlayerException {
		boolean trackExists = false;
		if (lThumbDriveAvailable) {
			File lTrackFile = getThumbTrack(FileHandler
					.makeFSName(playListName), pTrackPath);
			if (lTrackFile.exists()) {
				trackExists = true;
			}
		}
		return trackExists;
	}

	/**
	 * Returns if the player is installed on this system.
	 * 
	 * @return boolean
	 */
	public boolean isInstalled() {
		return lThumbDriveAvailable;
	}

	/**
	 * Add a track name to a specified playlist.
	 * 
	 * @param playListName
	 *            String
	 * @param pTrackPath
	 *            String
	 * @throws JPodderException
	 */
	public void addTrack(String playListName, String pTrackPath)
			throws PlayerException {

		playListName = FileHandler.makeFSName(playListName);
		if (lThumbDriveAvailable && hasTrack(playListName, pTrackPath))
			return;

		File lDestFile = getThumbTrack(playListName, pTrackPath);
		if (lDestFile.exists()) {
			return;
		} else {
			try {
				addPlaylist(playListName);
				if (lDestFile.createNewFile()) {
					File lSourceFile = new File(pTrackPath);
					FileHandler.copyFile(lSourceFile, lDestFile);
					// sCache.addTrack(pTrackPath);
				}
			} catch (IOException e) {
				throw new PlayerException(e.getMessage());
			} catch (JPodderException e) {
				throw new PlayerException(e.getMessage());
			}
		}
	}

	private File getThumbTrack(String playListName, String pTrackPath) {
		String lFileName = Util.getName(pTrackPath);
		return new File(sCacheFolder + File.separator + playListName
				+ File.separator + lFileName);

	}

	/**
	 * Add a new playlist to the player. The player is queried on the name first
	 * to avoid duplicate playlists.
	 * 
	 * @param pPlayListName
	 *            String
	 */
	public void addPlaylist(String pPlayListName) {
		if (!lThumbDriveAvailable) {
			return;
		}
		// We need to make sure the playlist can be created.
		pPlayListName = FileHandler.makeFSName(pPlayListName);

		File lCacheFolderFile = new File(sCacheFolder + File.separator
				+ pPlayListName);
		if (!lCacheFolderFile.exists()) {
			if (lCacheFolderFile.mkdir()) {
				return;
			}
		}
	}

	/**
	 * Delete a track from the player.
	 */
	public void deleteTrack(String playListName, String trackPath)
			throws PlayerException {
		File lTrackFile = getThumbTrack(FileHandler.makeFSName(playListName),
				trackPath);
		if (lTrackFile.exists()) {
			lTrackFile.delete();
		}
	}

	public void removePlayList(String playListName) {
	}

	/**
	 * Not supported in this plugin
	 * 
	 * @see com.jpodder.data.player.IPlayer#play(java.lang.String,
	 *      java.lang.String)
	 */
	public void play(String playListName, String trackPath) {
		return;
	}

	/**
	 * Get the author of this player.
	 * 
	 * @see jpodder.player.IPlayer#getAuthor()
	 */
	public String getAuthor() {
		return "Christophe Bouhier 2006.";
	}

	/**
	 * 
	 * @see jpodder.player.IPlayer#getDescription()
	 */
	public String getDescription() {
		return "A generic thumb drive plugin.";
	}

	/**
	 */
	public Content[] getMIMETypes() {
		try {
			// CB TODO Shoud support all file types.
			return new Content[] { new Content(IContent.MIME_MPEG),
					new Content(IContent.MIME_WMA),
					new Content(IContent.MIME_MP4V) };
		} catch (ContentException e) {
			return new Content[] {};
		}
	}

	public void stop(String playlistName, String trackPath)
			throws PlayerException {
		return; // not supported.
	}

	public void pauze(String playlistName, String trackPath)
			throws PlayerException {
		return; // not supported.
	}

}