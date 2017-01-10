package com.jpodder.data.id3;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.jpodder.data.content.ContentLogic;

import de.vdheide.mp3.MP3Properties;
import de.vdheide.mp3.NoMP3FrameException;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class MP3Info {
    
    private Logger mLog = Logger.getLogger(getClass().getName());
    private MP3Properties mp3Props = null;
    public MP3Info(File file) {
        boolean lIsMP3 = ContentLogic.isMP3(file.getAbsolutePath());
        if (lIsMP3) {
            try {
                mp3Props = new MP3Properties(file);
            } catch (IOException e1) {
                mLog.info("IO Error getting MP3 info for " + file.getName());
                return;
            } catch (NoMP3FrameException e) {
                mLog.info("MP3 frame error getting MP3 info for " + file.getName());
                return;
            }
        }else{
            mLog.info("Extension is not .MP3 getting MP3 infor for " + file.getName());
        }
    }
    
    public MP3Properties getMPInfo(){
        return mp3Props;
    }   
}