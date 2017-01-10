package com.jpodder.plugin.player.itunes;

import org.apache.log4j.Logger;
import org.jawin.COMException;
import org.jawin.DispatchPtr;
import org.jawin.win32.Ole32;

import com.jpodder.JPodderException;
import com.jpodder.data.content.Content;
import com.jpodder.data.content.ContentException;
import com.jpodder.data.content.IContent;
import com.jpodder.data.player.IPlayer;
import com.jpodder.data.player.PlayerException;
import com.jpodder.util.Util;

/**
 * iTunes COM Class. This class, provides, basic COM access to the iTunes
 * application. It can launch the iTunes application also playlists and tracks
 * in playlists can be added. It can be extended to support more iTunes
 * features.
 */
public class ITunesPlayer implements IPlayer {

	public static final String COM_MODULE_NAME = "[COM]";

	private DispatchPtr player = null;

	private DispatchPtr library = null;

	private DispatchPtr playLists = null;

	private boolean iTunes = false;

	private String author = "jPodder development team 2004.";

	private String description = "This player supports all the player function for the apple iTunes media player";

	private Logger mLog = Logger.getLogger(getClass().getName());

	/**
	 * Constuctor. Initializes the COM interface.
	 */
	public ITunesPlayer() {
	}

	public String getName() {
		return "iTunes";
	}

	public void initialize() {
		forkNew();
	}

	/**
	 * Returns true, if iTunes is installed.
	 * 
	 * @return boolean
	 */
	public boolean isInstalled() {
		return iTunes;
	}

	/**
	 * fork a new iTunes application. This method is mandatory, prior to using
	 * other methods in this class.
	 */
	private void forkNew() {
		try {
			Ole32.CoInitialize();
			player = new DispatchPtr("iTunes.Application");
			iTunes = true;
			
			Long lMajor = new Long(0);
			Long lMinor = new Long(0);
			player.invoke("CheckVersion", lMajor, lMinor);
			library = (DispatchPtr) player.get("LibrarySource");
			playLists = (DispatchPtr) library.get("Playlists");
		} catch (COMException ce) {
			mLog.error("iTunes is not installed", ce);
		}catch (UnsatisfiedLinkError ule){
			mLog.error("COM dll not loaded", ule);
		}
		
	}

	/**
	 * Add a playslist to the Library Source. This will only occur if no
	 * playlist exists with the same name.
	 * 
	 * @param name
	 *            String
	 */
	public void addPlaylist(String name) {
		if (!iTunes) {
			return;
		}
		try {
			Object o = playLists.get("ItemByName", name);
			DispatchPtr playList = null;
			;
			if (o != null)
				playList = (DispatchPtr) o;

			o = playList.get("Tracks");
			DispatchPtr trackCollection = null;
			if (o != null) {
				trackCollection = (DispatchPtr) o;
			}
			int count = ((Integer) trackCollection.get("Count")).intValue();
			mLog.info("Found " + count + " tracks in:" + name);
		} catch (COMException ce) {
			mLog.debug("Failed to create a playlist for tracks:  " + name, ce);
			try {
				player.invoke("CreatePlaylist", name);
			} catch (COMException ce1) {
				mLog.error("Failed to create a playlist:  " + name, ce1);
			}
		}
	}

	/**
	 * Remove a playlist from the player.
	 * 
	 * @param name
	 */
	public void removePlayList(String name) {
		int count = 0;
		try {
			DispatchPtr playList = (DispatchPtr) playLists.get("ItemByName",
					name);
			if (playList != null) {
				DispatchPtr trackCollection = (DispatchPtr) playList
						.get("Tracks");
				count = ((Integer) trackCollection.get("Count")).intValue();
				// DispatchPtr track;
				// for (int index = 1; index <= count; index++) {
				// track = (DispatchPtr) trackCollection.get("Item",
				// new Integer(index));
				// track.invoke("Delete");
				// deleteCount++;
				// }
				playList.invoke("Delete");
			} else {
				// null returned?
			}
		} catch (COMException ce) {
			if (count > 0) {
				// Playlist doesn't exist.
				mLog.debug("Playlist:  " + name + " does not exist", ce);
			}
		} finally {
			if (count > 0) {
				mLog.debug("Playlist:  " + name + " does not exist");
			} else {
				mLog.debug("Can't deleted non-existent playlist: " + name);
			}
		}

	}

	/**
	 * Get a track Collection from
	 * 
	 * @param playListName
	 *            String
	 * @return DispatchPtr
	 */
	public DispatchPtr getTrackCollection(String playListName) {
		if (!iTunes) {
			return null;
		}
		DispatchPtr trackCollection = null;
		try {

			DispatchPtr playList = (DispatchPtr) playLists.get("ItemByName",
					playListName);
			trackCollection = (DispatchPtr) playList.get("Tracks");
			Integer c = (Integer) trackCollection.get("Count");
			mLog.info("...playList contains " + c + " tracks");
		} catch (COMException ce) {
			mLog.error("Failed to get track collection for playlist: "
					+ playListName, ce);
		}
		return trackCollection;
	}

	public boolean hasTrack(String playListName, String trackPath)
			throws PlayerException {
		if (!iTunes) {
			throw new PlayerException("iTunes, not installed.");
		}
		DispatchPtr playList = null;
		boolean trackExists = false;
		try {
			playList = (DispatchPtr) playLists.get("ItemByName", playListName);
			DispatchPtr trackCollection = (DispatchPtr) playList.get("Tracks");
			int count = ((Integer) trackCollection.get("Count")).intValue();
			DispatchPtr track;
			String trackName = Util.getName(trackPath);
			for (int index = 1; index <= count; index++) {
				track = (DispatchPtr) trackCollection.get("Item", new Integer(
						index));
				String loc = (String) track.get("Location");

				if (loc != null) { // The track could exist, but the file is
					// gone!
					loc = Util.getName(loc);
					if (loc.compareToIgnoreCase(trackName) == 0) {
						trackExists = true;
						break;
					}
				} else {
					// Just skip this file.
				}
			}
		} catch (COMException ce) {
			// The playlist doesn't exist.
			mLog.debug("Unknown playlist: " + playListName);
		} finally {

		}

		return trackExists;

	}

	/**
	 * Add a track to a playlist. The track is compared to the existing tracks
	 * in the playlist. It's only added if no similar track (Path to the actual
	 * file), exists.
	 * 
	 * @param playListName
	 *            DispatchPtr
	 * @param trackPath
	 *            String
	 * @throws JPodderException
	 */
	public void addTrack(String playListName, String trackPath)
			throws PlayerException {
		if (!iTunes) {
			return;
		}
		DispatchPtr playList = null;
		try {
			playList = (DispatchPtr) playLists.get("ItemByName", playListName);
			DispatchPtr trackCollection = (DispatchPtr) playList.get("Tracks");
			int count = ((Integer) trackCollection.get("Count")).intValue();
			DispatchPtr track;
			boolean trackExists = false;
			String trackName = Util.getName(trackPath);
			for (int index = 1; index <= count; index++) {
				track = (DispatchPtr) trackCollection.get("Item", new Integer(
						index));
				String loc = (String) track.get("Location");
				if (loc != null) { // The track could exist, but the file is
					// gone!
					loc = Util.getName(loc);
					if (loc.compareToIgnoreCase(trackName) == 0) {
						trackExists = true;
						break;
					}
				} else {
					// Just skip this file.
				}
			}
			if (!trackExists) {
				playList.invoke("addFile", trackPath);
				mLog.info("...adding track " + trackPath);
			}
		} catch (COMException ce) {
			mLog.error("Error adding track: " + trackPath, ce);
			throw new PlayerException(ce.getMessage());
		}
	}

	/**
	 * Unitialize the COM Interface.
	 */
	public void finalize() {
		try {
			Ole32.CoUninitialize();
		} catch (COMException ce) {
			mLog.error("Error uninitializing", ce);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jpodder.Player#deleteTrack(java.lang.String, java.lang.String)
	 */
	public void deleteTrack(String playListName, String trackPath)
			throws PlayerException {
		if (!iTunes) {
			throw new PlayerException("iTunes, not installed.");
		}
		DispatchPtr playList = null;
		boolean trackExists = false;
		try {
			playList = (DispatchPtr) playLists.get("ItemByName", playListName);
			DispatchPtr trackCollection = (DispatchPtr) playList.get("Tracks");
			int count = ((Integer) trackCollection.get("Count")).intValue();
			DispatchPtr track;
			String trackName = Util.getName(trackPath);
			for (int index = 1; index <= count; index++) {
				track = (DispatchPtr) trackCollection.get("Item", new Integer(
						index));
				String loc = (String) track.get("Location");

				if (loc != null) { // The track could exist, but the file is
					// gone!
					loc = Util.getName(loc);
					if (loc.compareToIgnoreCase(trackName) == 0) {
						trackExists = true;
						mLog.info("...deleting track " + trackPath);
						track.invoke("Delete");
						break;

					}
				} else {
					// Just skip this file.
				}
			}
			if (trackExists) {
				// 

			}

		} catch (COMException ce) {
			// The playlist doesn't exist.
			mLog.info("Failed to delete track: " + playListName, ce);
		} finally {

		}

	}

	/*
	 * Start playing a file in the standard player.
	 */
	public void play(String playListName, String trackPath) {

		if (!iTunes) {
			return;
		}
		DispatchPtr playList = null;
		// boolean trackExists = false;
		try {
			playList = (DispatchPtr) playLists.get("ItemByName", playListName);
			DispatchPtr trackCollection = (DispatchPtr) playList.get("Tracks");
			// int count = ((Integer) trackCollection.get("Count")).intValue();
			// DispatchPtr track = (DispatchPtr)
			// trackCollection.get("ItemByName",
			// trackName);
			int count = ((Integer) trackCollection.get("Count")).intValue();
			DispatchPtr track = null;
			String trackName = Util.getName(trackPath);
			for (int index = 1; index <= count; index++) {
				track = (DispatchPtr) trackCollection.get("Item", new Integer(
						index));
				String loc = (String) track.get("Location");

				if (loc != null) { // The track could exist, but the file is
					// gone!
					loc = Util.getName(loc);
					if (loc.compareToIgnoreCase(trackName) == 0) {
						break;
					}
				} else {
					// Just skip this file.
				}
			}

			if (track != null)
				track.invoke("Play");
		} catch (COMException ce) {
			// The playlist doesn't exist.
			mLog.info("Can't play: " + trackPath, ce);
		}
	}

	public String getAuthor() {
		return author;
	}

	/**
	 * Get the description for this player.
	 * 
	 * @see jpodder.player.IPlayer#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see jpodder.player.IPlayer#getMIMETypes()
	 */
	public Content[] getMIMETypes() {
		try {
			return new Content[] { new Content(IContent.MIME_MPEG),
					new Content(IContent.MIME_AAC),
					new Content(IContent.MIME_MP4V) };
		} catch (ContentException e) {
			return new Content[] {};
		}
	}

	public void stop(String playlistName, String trackPath) throws PlayerException {
	}

	public void pauze(String playlistName, String trackPath) throws PlayerException {
	}
}