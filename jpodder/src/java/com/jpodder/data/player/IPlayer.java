package com.jpodder.data.player;

import com.jpodder.data.content.Content;

/**
 * A generic player interface.
 * @version 1.1 
 */
public interface IPlayer {

    /**
     * Comment for <code>PLAYER_ITUNES</code>
     */
    public static final String PLAYER_ITUNES = "iTunes";

    /**
     * Comment for <code>PLAYER_WMP</code>
     */
    public static final String PLAYER_WMP = "wmp";

    /**
     * Comment for <code>PLAYER_OFF</code>
     */
    public static final String PLAYER_OFF = "noPlayer";

    /**
     * Returns if the player has a specified track. The Play list name is
     * required for some players.
     * 
     * @param playListName
     *            String
     * @param trackPath
     *            String
     * @return boolean
     * @throws Exception
     */
    public boolean hasTrack(String playListName, String trackPath)
            throws PlayerException;

    /**
     * Launch (Fork) a new player.
     */
    //  private void forkNew();
    /**
     * Returns if the player is installed on this system.
     * 
     * @return boolean
     */
    public boolean isInstalled();

    /**
     * Add a track name to a specified playlist.
     * 
     * @param playListName
     *            String
     * @param trackPath
     *            String
     * @throws Exception
     */
    public void addTrack(String playListName, String trackPath)
            throws PlayerException;

    /**
     * Deleted a track to a playlist in the player.
     * 
     * @param playListName
     * @param trackPath
     * @throws Exception
     */
    public void deleteTrack(String playListName, String trackPath)
            throws PlayerException;

    /**
     * Add a new playlist to the player.
     * 
     * @param playListName
     *            String
     * @throws Exception
     */
    public void addPlaylist(String playListName) throws PlayerException;

    /**
     * Remove a playlist from the player.
     * 
     * @param playListName
     * @throws Exception
     */
    public void removePlayList(String playListName) throws PlayerException;

    /**
     * Get the player name.
     * 
     * @return String
     */
    public String getName();

    /**
     * Initialize the connection to the player.
     */
    public void initialize();

    /**
     * Finalize the connection to the player
     */
    public void finalize();

    /**
     * Play A track.
     * 
     * @param playlistName
     * @param trackPath
     * @throws Exception
     */
    public void play(String playlistName, String trackPath) throws PlayerException;
    
    /**
     * Stop a track
     * 
     * @param playlistName
     * @param trackPath
     * @throws Exception
     */
    public void stop(String playlistName, String trackPath) throws PlayerException;
    
    /**
     * Pauze a track
     * 
     * @param playlistName
     * @param trackPath
     * @throws Exception
     */
    public void pauze(String playlistName, String trackPath) throws PlayerException;
    
    /**
     * Get the author of this player.
     * 
     * @return String
     */
    public String getAuthor();

    /**
     * Get the description of this player.
     * 
     * @return String
     */
    public String getDescription();

    /**
     * Get an array of mime types supported for this player.
     * 
     * @return MIMEType An array of MIME types.
     */
    public Content[] getMIMETypes();

}