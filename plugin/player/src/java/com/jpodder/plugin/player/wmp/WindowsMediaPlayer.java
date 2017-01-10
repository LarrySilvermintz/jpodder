package com.jpodder.plugin.player.wmp;

import java.io.File;

import org.apache.log4j.Logger;

import org.jawin.COMException;
import org.jawin.win32.Ole32;

import com.jpodder.JPodderException;
import com.jpodder.data.content.Content;
import com.jpodder.data.content.ContentException;
import com.jpodder.data.content.IContent;
import com.jpodder.data.player.IPlayer;
import com.jpodder.data.player.PlayerException;
import com.jpodder.util.Util;

public class WindowsMediaPlayer extends WindowsMediaPlayerAdapter implements
		IPlayer {

	private static boolean _LOG_SYSTEM = true;

	Player mPlayerAdapter;

	boolean wmp = false;

	private Logger mLog = Logger.getLogger(getClass().getName());

	public WindowsMediaPlayer() {
	}

	public String getName() {
		return "Windows Media Player";
	}

	/**
	 * 
	 */
	public void initialize() {
		try {
			mPlayerAdapter = new Player();
			wmp = true;
			mLog.debug("WMP initialized correctly");
		} catch (PlayerException e) {
			mLog.warn(e.getMessage());
		}
	}

	/**
	 * Unitialize the COM Interface.
	 */
	public void finalize() {
		try {
			Ole32.CoUninitialize();
		} catch (COMException ce) {
		}
	}

	/**
	 * Returns if the player has a specified track. The Play list name is
	 * required for some players.
	 * 
	 * @param playListName
	 *            String
	 * @param trackPath
	 * @return boolean
	 * @throws JPodderException
	 */
	public boolean hasTrack(String playListName, String trackPath)
			throws PlayerException {
		boolean trackExists = false;
		if (!wmp) {
			return false;
		}
		String trackName = Util.getName(trackPath);
		
//		Playlist lPlaylist = mPlayerAdapter.getMediaCollection().getByName(trackName);
//		mLog.info(lPlaylist.getName());
//		return true;
		
		PlaylistCollection lCollection = mPlayerAdapter.getPlaylistCollection();
		Playlist[] lPlaylists = lCollection.getPlayListsByName(playListName);
		mLog.debug("hasTrack(), playlist retrieved " + lPlaylists);
		if (lPlaylists.length >= 1) {
			Playlist lPlaylist = lPlaylists[0]; // get first list;
			// setCurrentPlaylist(player, lPlaylist);
			for (int index = 0; index < lPlaylist.getCount(); index++) {
				Media lMedia = lPlaylist.getItem(index);
				mLog.debug("hasTrack(), got media" + index);
				String lSourceURL = lMedia.getSourceURL();
				String name = Util.getName(lSourceURL);
				if (name.compareToIgnoreCase(trackName) == 0) {
					mLog.debug("Track " + trackName + " found!, in "
							+ playListName);
					trackExists = true;
					break;
				}
			}
			if (!trackExists) {
				mLog.debug("Track " + trackName + " NOT found!, in "
						+ playListName);
			}
		} else {
			mLog.debug("hasTrack(), Playlist:" + playListName
					+ " doesn't exist");
		}
		return trackExists;
	}

	/**
	 * Returns if the player is installed on this system.
	 * 
	 * @return boolean
	 */
	public boolean isInstalled() {
		return wmp;
	}

	/**
	 * Add a track name to a specified playlist.
	 * 
	 * @param playListName
	 *            String
	 * @param trackName
	 *            String
	 * @throws JPodderException
	 */
	public void addTrack(String playListName, String trackName)
			throws PlayerException {

		if (!wmp && hasTrack(playListName, trackName))
			return;

		File trackFile = new File(trackName);
		String trackPath = trackFile.toString();

		MediaCollection lMediaCollection = mPlayerAdapter.getMediaCollection();
		Media lMedia = lMediaCollection.add(trackPath);
		mLog.debug("New media added " + trackName);

		PlaylistCollection lCollection = mPlayerAdapter.getPlaylistCollection();
		Playlist[] lPlaylists = lCollection.getPlayListsByName(playListName);

		if (lPlaylists.length >= 1) { // Found a playlist.
			Playlist lPlaylist = lPlaylists[0];
			lPlaylist.insertItem(0, lMedia);
		} else {
			throw new PlayerException("Playlist non existent");
		}
	}

	/**
	 * Add a new playlist to the player. The player is queried on the name first
	 * to avoid duplicate playlists.
	 * 
	 * @param playListName
	 *            String
	 */
	public void addPlaylist(String playListName) throws PlayerException {

		if (!wmp) {
			return;
		}
		PlaylistCollection lCollection = mPlayerAdapter.getPlaylistCollection();
		Playlist[] lPlaylists = lCollection.getPlayListsByName(playListName);

		if (lPlaylists.length >= 1) {
			mLog.info(playListName + " already exists, won't add it");
		} else {
			lCollection.newPlayList(playListName);
			mLog.debug("Succesfully added playlist " + playListName);
		}
	}

	/**
	 * Delete a track.
	 */
	public void deleteTrack(String playListName, String trackPath)
			throws PlayerException {

		MediaCollection lMediaCollection = mPlayerAdapter.getMediaCollection();

		PlaylistCollection lCollection = mPlayerAdapter.getPlaylistCollection();
		Playlist[] lPlaylists = lCollection.getPlayListsByName(playListName);

		if (lPlaylists.length >= 1) {
			Playlist lPlaylist = lPlaylists[0];
			for (int i = 0; i < lPlaylist.getCount(); i++) {
				Media lMedia = lPlaylist.getItem(i);
				String lPath = lMedia.getSourceURL();

				if (lPath.compareToIgnoreCase(trackPath) == 0) {
					lMediaCollection.remove(lMedia);
					break;
				}
			}
		} else {
			throw new PlayerException(playListName + " non existent");
		}
	}

	/**
	 * Remove a playlist form WMP. This method also removes the Media from the
	 * Media library.
	 * 
	 * @param playListName
	 *            String the Name of the playlist in the player.
	 */
	public void removePlayList(String playListName) throws PlayerException {

		MediaCollection lMediaCollection = mPlayerAdapter.getMediaCollection();
		PlaylistCollection collection = mPlayerAdapter.getPlaylistCollection();
		Playlist[] lPlaylists = collection.getPlayListsByName(playListName);

		if (lPlaylists.length >= 1) {
			Playlist lPlaylist = lPlaylists[0];
			for (int index = 0; index < lPlaylist.getCount(); index++) {
				Media media = lPlaylist.getItem(index);
				lMediaCollection.remove(media);
			}
			lMediaCollection.remove(lPlaylist);
		} else {
			// This playlist doesn't exist.
			throw new PlayerException(playListName + " is non existent");
		}
	}

	/**
	 * 
	 */
	public void play(String pPlayListName, String trackPath)
			throws PlayerException {

		String trackName = Util.getName(trackPath);
		PlaylistCollection collection = mPlayerAdapter.getPlaylistCollection();
		Playlist[] lPlayLists = collection.getPlayListsByName(pPlayListName);
		Media lMedia = null;
		if (lPlayLists.length >= 1) {
			Playlist lPlaylist = lPlayLists[0];
			for (int i = 0; i < lPlaylist.getCount(); i++) {
				lMedia = lPlaylist.getItem(i);
				String lPath = lMedia.getSourceURL();
				if (lPath.compareToIgnoreCase(trackPath) == 0) {
					break;
				}
			}
		}
		if (lMedia != null) {
			// player.put("currentMedia", media);
			// player.put("currentPlaylist", playlist);

			Controls lControls = mPlayerAdapter.getControls();
			int state = mPlayerAdapter.getPlayerState();
			if (state == Playing) {
				lControls.stop();
			}
			if (state == Ready) {
				lControls.play(lMedia);
			}
		}
	}

	public void stop(String pPlayListName, String trackPath)
			throws PlayerException {
		String trackName = Util.getName(trackPath);
		PlaylistCollection collection = mPlayerAdapter.getPlaylistCollection();
		Playlist[] lPlayLists = collection.getPlayListsByName(pPlayListName);
		Media lMedia = null;
		if (lPlayLists.length >= 1) {
			Playlist lPlaylist = lPlayLists[0];
			for (int i = 0; i < lPlaylist.getCount(); i++) {
				lMedia = lPlaylist.getItem(i);
				String lPath = lMedia.getSourceURL();
				if (lPath.compareToIgnoreCase(trackPath) == 0) {
					break;
				}
			}
		}
		if (lMedia != null) {
			// player.put("currentMedia", media);
			// player.put("currentPlaylist", playlist);

			Controls lControls = mPlayerAdapter.getControls();
			int state = mPlayerAdapter.getPlayerState();
			if (state == Playing) {
				lControls.stop();
			}
		}
	}

	public void pauze(String playlistName, String trackPath)
			throws PlayerException {

	}

	/**
	 * Get the author of this player.
	 * 
	 * @see jpodder.player.IPlayer#getAuthor()
	 */
	public String getAuthor() {
		return "jPodder development team 2004.";
	}

	/**
	 * 
	 * @see jpodder.player.IPlayer#getDescription()
	 */
	public String getDescription() {
		return "Windows Media Player support is limited."
				+ "Tracks can be added to the media library, "
				+ " the player is not launched automaticly.";
	}

	/**
	 * Support .mp3, .wma
	 * 
	 * @see jpodder.player.IPlayer#getMIMETypes()
	 */
	public Content[] getMIMETypes() {
		try {
			return new Content[] { new Content(IContent.MIME_MPEG),
					new Content(IContent.MIME_WMA) };
		} catch (ContentException e) {
			return new Content[] {};
		}
	}

}