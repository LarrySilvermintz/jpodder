package com.jpodder.data.player;

import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.content.Content;
import com.jpodder.data.content.ContentException;
import com.jpodder.data.content.ContentLogic;
import com.jpodder.data.feeds.IXFile;
import com.jpodder.data.feeds.IXPersonalEnclosure;
import com.jpodder.plugin.PluginLogic;
import com.jpodder.plugin.PluginLoader.Plugin;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class PlayerLogic implements IConfigurationListener {

    private Logger mLog = Logger.getLogger(getClass().getName());

    private static PlayerLogic sSelf;

    private IPlayer mDefaultPlayer = null;

    public static PlayerLogic getInstance() {
        if (sSelf == null) {
            sSelf = new PlayerLogic();
            sSelf.loadPlayers();
        }
        return sSelf;
    }

    public void scanAndLoad() {
        PluginLogic.getInstance().scanPluginFolder();
        loadPlayers();
    }

    /**
     * Get the plugin names, and compare with the default player. If we don't
     * have a plugin for the default player we set to &quotNo Player&quot.
     */
    public String[] loadPlayers() {
        List lPlayers = PluginLogic.getInstance()
                .getPluginByClass(IPlayer.class);
        int lSize = lPlayers.size();
        // We need one extra player for "No Player"
        String[] lPlayerNames = new String[lSize + 1];
        mDefaultPlayer = new NoPlayer();
        lPlayerNames[0] = mDefaultPlayer.getName();

        boolean lDefaultFound = false;
        for (int i = 0; i < lSize; i++) {
            lPlayerNames[i + 1] = ((IPlayer) ((Plugin) lPlayers.get(i))
                    .getInstance()).getName();
            if (lPlayerNames[i + 1].equals(mDefaultPlayer.getName())) {
                lDefaultFound = true;
            }
        }

        // if the default player is not in the list of players
        // any more, we should override the default selection.
        if (!lDefaultFound) {
            // The default player doesn't exist anymore.
            mLog.info("The default player: " + mDefaultPlayer.getName()
                    + " is not in \\plugin folder");
        }
        return lPlayerNames;
    }

    /**
     * Store the file in the player.
     * 
     * @param pFileWrapper
     */
    public void storeInPlayer(IXFile pFileWrapper) {

        if (pFileWrapper.isLocal()) {
            String lTitle = "";
            lTitle = pFileWrapper.getFeed().getTitle();
            if (addPlayList(lTitle)) {
            }
            if (addTrack(lTitle, pFileWrapper)) {
                pFileWrapper.setInPlayer(true);
            }
        }
    }

    public boolean playTrack(IXFile pFileWrapper) {
        boolean lSuccess = false;

        IPlayer lPlayer = getBestPlayer(pFileWrapper);
        if (lPlayer instanceof NoPlayer || !initPlayer(lPlayer)) {
            return false;
        }
        String lTrackPath = pFileWrapper.getFile().getAbsolutePath();
        try {
            lPlayer.play(pFileWrapper.getFeed().getTitle(), lTrackPath);
            lSuccess = true;
            mLog.info("Succesfully playing: " + lTrackPath + "in "
                    + lPlayer.getName());
        } catch (Exception e1) {
            mLog.warn("Failed to play: " + lTrackPath + "from "
                    + lPlayer.getName());
            mLog.debug(e1);
        } finally {
            lPlayer.finalize();
        }
        return lSuccess;
    }

    public boolean deleteTrack(IXFile pFileWrapper) {
        boolean lSuccess = false;

        IPlayer lPlayer = getBestPlayer(pFileWrapper);
        if (lPlayer instanceof NoPlayer || !initPlayer(lPlayer)) {
            return false;
        }
        String lTrackPath = pFileWrapper.getFile().getAbsolutePath();
        try {
            lPlayer.deleteTrack(pFileWrapper.getFeed().getTitle(), lTrackPath);
            lSuccess = true;
            mLog.info("Succesfully deleted: " + lTrackPath + "from "
                    + lPlayer.getName());
        } catch (Exception e1) {
            mLog.warn("Failed to delete: " + lTrackPath + "from "
                    + lPlayer.getName());
            mLog.debug(e1);
        } finally {
            lPlayer.finalize();
        }
        return lSuccess;
    }

    public boolean addTrack(String pPlayList, IXFile pFileWrapper) {
        boolean lSuccess = false;

        if (pFileWrapper.getInPlayer()) {
            return false; // Sheez don't call us!
        }

        IPlayer lPlayer = getBestPlayer(pFileWrapper);
        if (lPlayer instanceof NoPlayer || !initPlayer(lPlayer)) {
            return false;
        }

        String lTrackPath = pFileWrapper.getFile().getAbsolutePath();

        if (pFileWrapper instanceof IXPersonalEnclosure) {
            if (((IXPersonalEnclosure) pFileWrapper).isTorrent()) {
                lTrackPath = ((IXPersonalEnclosure) pFileWrapper)
                        .getTorrentFile().getPath();
                mLog.info("The file to be added is a .torrent");
            }
        }

        try {
            lPlayer.addPlaylist(pPlayList);
            lPlayer.addTrack(pPlayList, lTrackPath);
            if (lPlayer.hasTrack(pPlayList, lTrackPath)) {
                lSuccess = true;
                mLog.info("Succesfully added: " + lTrackPath + "to "
                        + lPlayer.getName());
            }
        } catch (Exception e1) {
            mLog.warn("Failed to add: " + lTrackPath + "to "
                    + lPlayer.getName());
            mLog.debug(e1);
        } finally {
            lPlayer.finalize();
        } 
        return lSuccess;
    }

    public boolean hasTrack(String pPlayList, IXFile pFileWrapper) {
        boolean lSuccess = false;

        IPlayer lPlayer = getBestPlayer(pFileWrapper);
        if (lPlayer instanceof NoPlayer || !initPlayer(lPlayer)) {
            return false;
        }
        
        
        String lTrackPath = pFileWrapper.getFile().getAbsolutePath();

        try {
            lSuccess = lPlayer.hasTrack(pPlayList, lTrackPath);
        } catch (Exception ie) {
            mLog.warn("Failed to check if: " + lTrackPath + "is stored in "
                    + lPlayer.getName());
            mLog.debug(ie);
        } finally {
            lPlayer.finalize();
        }
        return lSuccess;
    }

    public boolean removePlayList(String pPlayList) {
        boolean lSuccess = false;

        IPlayer lPlayer = mDefaultPlayer;
        if (lPlayer instanceof NoPlayer || !initPlayer(lPlayer)) {
            return false;
        }
        try {
            lPlayer.removePlayList(pPlayList);
            lSuccess = true;
            mLog.info("Succesfully removed: " + pPlayList + "from "
                    + lPlayer.getName());
        } catch (Exception e1) {
            mLog.warn("Failed to remove: " + pPlayList + "from "
                    + lPlayer.getName());
            mLog.debug(e1);
        } finally {
            lPlayer.finalize();
        }

        return lSuccess;
    }

    public boolean addPlayList(String pPlayList) {
        boolean lSuccess = false;

        IPlayer lPlayer = mDefaultPlayer;
        if (lPlayer instanceof NoPlayer || !initPlayer(lPlayer)) {
            return false;
        }
        
        
        try {
            lPlayer.addPlaylist(pPlayList);
            lSuccess = true;
            mLog.info("Succesfully add: " + pPlayList + "to "
                    + lPlayer.getName());
        } catch (Exception e1) {
            mLog
                    .warn("Failed to add: " + pPlayList + "to "
                            + lPlayer.getName());
            mLog.debug(e1);
        } finally {
            lPlayer.finalize();
        }
        return lSuccess;
    }

    public boolean initPlayer(IPlayer lPlayer) {
        try {
            lPlayer.initialize();
            return true;
        } catch (Exception e) {
            mLog.warn("Attempt to access a player, which was not loaded properly");
            mLog.warn("See the \\bin\\jPodder.log for details of the failure");
            return false;
        }
    }

    /**
     * Satisfies the property interface. We want to know the selected player We
     * update the status bar.
     * 
     * @param e
     *            PropertyEvent
     */

    public void configurationChanged(ConfigurationEvent e) {
        if (!e.getSource().equals(ConfigurationLogic.class)) {
            return;
        }
        String lPlayerName = Configuration.getInstance().getPlayer();
        mDefaultPlayer = (IPlayer) PluginLogic.getInstance().getPluginByName(
                lPlayerName);
        if (mDefaultPlayer == null) {
            mDefaultPlayer = new NoPlayer();
            // We could fire an event as player type is changed by the logic.
            Configuration.getInstance().setPlayer(mDefaultPlayer.getName());
            ConfigurationLogic.getInstance().fireConfigurationChanged(
                    new ConfigurationEvent(this, Configuration.CONFIG_PLAYER));
        }
        mLog.info("Player set to: " + mDefaultPlayer.getName());
    }

    /**
     * Get the MIME for this file. this is either a defined type or translated
     * from the file extension. If the default is not <code>NoPlayer</code> we
     * grab an application which supports this MIME type. If none is found, we
     * return the <code>NoPlayer</code>
     * 
     * @param pFileWrapper
     * @return
     */
    public IPlayer getBestPlayer(IXFile pFileWrapper) {
        String lMimeType = pFileWrapper.getFileType();
        if (lMimeType == null || lMimeType.length() == 0) {
            lMimeType = ContentLogic.getContentFromFileName(pFileWrapper.getFile().getName());
        }

        IPlayer lPlayer = mDefaultPlayer;
        if (lMimeType != null) {
            if (!ContentLogic.getInstance().supportsContent(lMimeType, lPlayer)) {
                if (!(mDefaultPlayer instanceof NoPlayer)) {                	
                	try{
                    lPlayer = ContentLogic.getInstance().getPlayer(new Content(
                            lMimeType));
                	}catch(ContentException ce){
                		// Mime type is not recognized.
                	}
                    if (lPlayer == null) {
                        lPlayer = new NoPlayer();
                    }
                }
            }
        }
        return lPlayer;
    }

    public IPlayer getDefaultPlayer() {
        return mDefaultPlayer;
    }
}