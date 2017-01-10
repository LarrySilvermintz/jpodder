package com.jpodder.plugin.player.wmp;

import java.util.ArrayList;

import org.jawin.COMException;
import org.jawin.DispatchPtr;
import org.jawin.win32.Ole32;

import com.jpodder.data.player.PlayerException;

/**
 * 
 * @link http://msdn.microsoft.com/library/default.asp?url=/library/en-us/wmpsdk11/mmp_sdk/kwindowsmediaplayer11sdk.asp
 *       <p>
 *       It seems SDK for version 9 and 10 is not available anymore.
 *       </p>
 */
public class WindowsMediaPlayerAdapter {

	int Undefined = 0; // Windows Media Player is in an undefined state.

	int Stopped = 1; // Playback of the current media item is stopped.

	int Paused = 2; // Playback of the current media item is paused. When a

	// media item is paused, resuming playback begins from the
	// same location.

	int Playing = 3; // The current media item is playing.

	int ScanForward = 4; // The current media item is fast forwarding.

	int ScanReverse = 5; // The current media item is fast rewinding.

	int Buffering = 6; // The current media item is getting additional data

	// from the server.

	int Waiting = 7; // Connection is established, but the server is not

	// sending data. Waiting for session to begin.

	int MediaEnded = 8;// Media item has completed playback.

	int Transitioning = 9; // Preparing new media item.

	int Ready = 10; // Ready to begin playing.

	int Reconnecting = 11; // Reconnecting to stream.

	public class Player {

		private DispatchPtr mPlayer;

		public Player() throws PlayerException {
			forkNew();
		}

		private void forkNew() throws PlayerException {

			try {
				Ole32.CoInitialize();
				mPlayer = new DispatchPtr("WMPlayer.OCX");
				// String status = (String) player.get("status");

			} catch (COMException ce) {
				try {
					DispatchPtr error = (DispatchPtr) mPlayer.get("error");
					// int count = ((Integer)
					// error.get("errorCount")).intValue();
				} catch (COMException ce1) {
					throw new PlayerException("Failed to start player: "
							+ ce1.getMessage());
				}
				throw new PlayerException("Failed to start player: "
						+ ce.getMessage());
			}
		}

		public Controls getControls() throws PlayerException {
			return new Controls(mPlayer);
		}

		/**
		 * Convenience method to get a playlist collection object.
		 * 
		 * @param pPlayer
		 * @return
		 * @throws PlayerException
		 */
		public PlaylistCollection getPlaylistCollection()
				throws PlayerException {
			return new PlaylistCollection(mPlayer);
		}

		/**
		 * Convenience Method to get a MediaCollection wrapper.
		 * 
		 * @param pPlayer
		 * @return
		 * @throws PlayerException
		 */
		public MediaCollection getMediaCollection() throws PlayerException {

			try {
				DispatchPtr collection = (DispatchPtr) mPlayer
						.get("mediaCollection");
				return new MediaCollection(collection);
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		/**
		 * 
		 * 
		 * 
		 * @param pPlayer
		 * @return
		 * @throws PlayerException
		 */
		public int getPlayerState() throws PlayerException {
			try {
				return ((Integer) mPlayer.get("playState")).intValue();
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public void setCurrentPlaylist(Playlist pPlaylist)
				throws PlayerException {
			try {
				mPlayer.put("currentPlaylist", pPlaylist.mPlaylist);
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

	} // PLAYER

	/**
	 * 
	 * 
	 */
	public class PlaylistCollection {

		DispatchPtr mPlaylistCollection;

		public PlaylistCollection(DispatchPtr pPlayer) throws PlayerException {
			try {
				mPlaylistCollection = (DispatchPtr) pPlayer
						.get("playlistCollection");
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public Playlist[] getAllPlayLists() throws PlayerException {
			DispatchPtr playListArray;
			ArrayList<Playlist> lList = new ArrayList<Playlist>();
			try {
				playListArray = (DispatchPtr) mPlaylistCollection
						.invoke("getAll");
				int count = getCount(playListArray);
				Playlist[] lPlaylists = new Playlist[count];
				for (int i = 0; i < count; i++) {
					DispatchPtr playList = getItem(playListArray, i);
					Playlist lPlayList = new Playlist(playList);
					lPlaylists[i] = lPlayList;
				}
				return lPlaylists;
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public Playlist[] getPlayListsByName(String pName)
				throws PlayerException {
			try {
				DispatchPtr playListArray = (DispatchPtr) mPlaylistCollection
						.invoke("getByName", pName);
				int count = getCount(playListArray);
				Playlist[] lPlaylists = new Playlist[count];
				for (int i = 0; i < count; i++) {
					DispatchPtr playList = getItem(playListArray, i);
					Playlist lPlaylist = new Playlist(playList);
					System.out.println(lPlaylist.getName());
					lPlaylists[i] = lPlaylist;
				}
				return lPlaylists;
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		/**
		 * Applies to:
		 * <ul>
		 * <li>PlayListArray <a
		 * href="http://windowssdk.msdn.microsoft.com/en-us/library/aa392452(VS.80).aspx">API</a></li>
		 * <li>PlayList <a
		 * href="http://windowssdk.msdn.microsoft.com/en-us/library/aa392587(VS.80).aspx">API</a></li>
		 * </ul>
		 * 
		 * @param playlistArray
		 * @return The number of playlists in the provided array.
		 * @throws PlayerException
		 */
		private int getCount(DispatchPtr playlistArray) throws PlayerException {
			try {
				return ((Integer) playlistArray.get("count")).intValue();
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		private DispatchPtr getItem(DispatchPtr playlistArray, int pIndex)
				throws PlayerException {
			try {
				return (DispatchPtr) playlistArray.invoke("item", new Integer(
						pIndex));
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public void removePlayList(Playlist pPlaylist) throws PlayerException {
			DispatchPtr collection;
			try {
				mPlaylistCollection.invoke("remove", pPlaylist.mPlaylist);
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public Playlist newPlayList(String pName) throws PlayerException {
			try {
				DispatchPtr lPlaylist = (DispatchPtr) mPlaylistCollection
						.invoke("newPlaylist", pName);
				return new Playlist(lPlaylist);
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}
	}

	/**
	 * WMP Media Object</br> Warning! Does not implement all methods and
	 * parameters <a
	 * href="http://windowssdk.msdn.microsoft.com/en-us/library/aa391998(VS.80).aspx">API</a>
	 */

	public class MediaCollection {
		DispatchPtr mMediaCollection;

		public MediaCollection(DispatchPtr pMediaCollection) {
			mMediaCollection = pMediaCollection;
		}

		public Media add(String pPath) throws PlayerException {
			try {
				DispatchPtr lMedia = (DispatchPtr) mMediaCollection.invoke(
						"add", pPath);
				return new Media(lMedia);
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public void remove(Media pMedia) throws PlayerException {
			try {
				mMediaCollection.invoke("remove", pMedia.mMedia, new Boolean(
						true));
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public void remove(Playlist pPlaylist) throws PlayerException {
		}

		public Playlist getByName(String pName) throws PlayerException {
			try {
				DispatchPtr playlist = (DispatchPtr) mMediaCollection.invoke(
						"getByName", pName);
				Playlist lPlayList = new Playlist(playlist);
				return lPlayList;
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}
	}

	/**
	 * WMP Playlist object. <br>
	 * <a
	 * href="http://windowssdk.msdn.microsoft.com/en-us/library/aa392587(VS.80).aspx">API</a>
	 */
	public class Playlist {

		DispatchPtr mPlaylist;

		public Playlist(DispatchPtr pPlaylist) {
			mPlaylist = pPlaylist;
		}

		public String getName() throws PlayerException {
			try {
				return (String) mPlaylist.get("name");
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		/**
		 * @param pPlaylist
		 * @return The number of playlists in the provided array.
		 * @throws PlayerException
		 */
		public int getCount() throws PlayerException {
			try {
				return ((Integer) mPlaylist.get("count")).intValue();
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public Media getItem(int pIndex) throws PlayerException {
			try {
				DispatchPtr lMedia = (DispatchPtr) mPlaylist.invoke("item",
						new Long(pIndex));
				return new Media(lMedia);
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public void removeItem(Media pMedia) throws PlayerException {
			try {
				mPlaylist.invoke("removeItem", pMedia.mMedia);
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public void appendItem(Media pMedia) throws PlayerException {
			try {
				mPlaylist.invoke("appendItem", pMedia.mMedia);
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public void insertItem(int pIndex, Media pMedia) throws PlayerException {
			try {
				mPlaylist.invoke("insertItem", new Integer(pIndex),
						pMedia.mMedia);
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		// public Media getMedia(String pMediaPath) throws PlayerException {
		// DispatchPtr media = null;
		// int count;
		// try {
		// count = getCount();
		//
		// for (int index = 0; index < count; index++) {
		// media = (DispatchPtr) mPlaylist.get("item", new Integer(
		// index));
		// String name = (String) media.get("sourceURL");
		// name = Util.getName(name);
		// if (name.compareToIgnoreCase(pMediaPath) == 0) {
		// // track found.
		// break;
		// }
		// }
		// return new Media(media);
		// } catch (COMException e) {
		// throw new PlayerException(e.getMessage());
		// }
		// }
	}

	/**
	 * WMP Media Object</br> Warning! Does not implement all methods and
	 * parameters <a
	 * href="http://windowssdk.msdn.microsoft.com/en-us/library/aa391998(VS.80).aspx">API</a>
	 */
	public class Media {

		public DispatchPtr mMedia;

		public Media(DispatchPtr pMedia) {
			mMedia = pMedia;
		}

		public String getSourceURL() throws PlayerException {
			try {
				String name = (String) mMedia.get("sourceURL");
				return name;
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public boolean isMemberOf(Playlist pPlaylist) throws PlayerException {
			try {
				Boolean b = (Boolean) mMedia.invoke("isMemberOf");
				return b.booleanValue();
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}
	}

	/**
	 * WMP Controls Object </br> Warning! Does not implement all methods and
	 * parameters <a
	 * href="http://windowssdk.msdn.microsoft.com/en-us/library/aa386443(VS.80).aspx">API</a>
	 */
	public class Controls {

		DispatchPtr mControls;

		public Controls(DispatchPtr pPlayer) throws PlayerException {
			try {
				mControls = (DispatchPtr) pPlayer.get("controls");
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public void play(Media pMedia) throws PlayerException {
			if (mControls == null) {
				return;
			}
			try {
				mControls.invoke("play", pMedia.mMedia);
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

		public void stop() throws PlayerException {
			if (mControls == null) {
				return;
			}
			try {
				mControls.invoke("stop");
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}

		}

		public void pause() throws PlayerException {
			if (mControls == null) {
				return;
			}
			try {
				mControls.invoke("pause");
			} catch (COMException e) {
				throw new PlayerException(e.getMessage());
			}
		}

	}

}