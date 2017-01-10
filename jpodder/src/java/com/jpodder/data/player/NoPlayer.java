package com.jpodder.data.player;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
import com.jpodder.data.content.Content;
import com.jpodder.util.Messages;

public class NoPlayer implements IPlayer {
	public NoPlayer() {
	}

	/**
	 * Return the player name.
	 * 
	 * @return String
	 */
	public String getName() {
		return "No player";
	}

	/**
	 * Returns if the player has a specified track. The Play list name is
	 * required for some players.
	 * 
	 * @param playListName
	 *            String
	 * @param trackName
	 *            String
	 * @return boolean
	 * @throws Exception
	 */
	public boolean hasTrack(String playListName, String trackName)
			throws PlayerException {
		return false;
	};

	/**
	 * Returns if the player is installed on this system.
	 * 
	 * @return boolean
	 */
	public boolean isInstalled() {
		return false;
	};

	/**
	 * Add a track name to a specified playlist.
	 * 
	 * @param playListName
	 *            String
	 * @param trackName
	 *            String
	 * @throws Exception
	 */
	public void addTrack(String playListName, String trackName)
			throws PlayerException {
	};

	/**
	 * Add a new playlist to the player.
	 * 
	 * @param playListName
	 *            String
	 */
	public void addPlaylist(String playListName) {
	};

	/**
	 * Nothing to init when there is no player.
	 */
	public void initialize() {

	}

	/**
	 * Unitialize the COM interface. (This should be moved to a seperate
	 * interface.)
	 */
	public void finalize() {
	}

	/**
	 * @see com.jpodder.ui.IPlayer#deleteTrack(java.lang.String,
	 *      java.lang.String)
	 */
	public void deleteTrack(String playListName, String trackName)
			throws PlayerException {
	}

	/**
	 * @see com.jpodder.data.player.IPlayer#removePlayList(java.lang.String)
	 */
	public void removePlayList(String playListName) {
	}

	/**
	 * @see com.jpodder.data.player.IPlayer#play()
	 */
	public void play(String playListName, String trackPath) {
	}

	/**
	 * @see com.jpodder.data.player.IPlayer#getAuthor()
	 */
	public String getAuthor() {
		return "jPodder production team";
	}

	/**
	 * Return the description for this player.
	 * 
	 * @see com.jpodder.data.player.IPlayer#getDescription()
	 */
	public String getDescription() {
		return Messages.getString("noplayer.description");
	}

	/**
	 * No Player does not support any MIME types.
	 * 
	 * @see com.jpodder.data.player.IPlayer#getMIMETypes()
	 */
	public Content[] getMIMETypes() {
		return null;
	}

	public void stop(String playlistName, String trackPath) throws PlayerException {

	}

	public void pauze(String playlistName, String trackPath) throws PlayerException {

	};

}
