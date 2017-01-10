package com.jpodder.data.m3u;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.JPodderException;
import com.jpodder.data.feeds.IXFile;
import com.jpodder.data.id3.ID3Wrapper;
import com.jpodder.data.id3.MP3Info;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */

// M3U is a media queue format, also generally known to humans as a playlist. 
// It is the default playlist save format of WinAMP and most other media programs. 
// It allows multiple files to be queued in a program in a specific format. 
// The actual format is really simple, though; not complicated at all. 
// A sample M3U list could be: 
//
//#EXTM3U
//#EXTINF:111,3rd Bass - Al z A-B-Cee z
//mp3/3rd Bass/3rd bass - Al z A-B-Cee z.mp3
//#EXTINF:462,Apoptygma Berzerk - Kathy´s song (VNV Nation rmx)
//mp3/Apoptygma Berzerk/Apoptygma Berzerk - Kathy's Song (Victoria Mix by VNV Nation).mp3
//#EXTINF:394,Apoptygma Berzerk - Kathy's Song
//mp3/Apoptygma Berzerk/Apoptygma Berzerk - Kathy's Song.mp3
//#EXTINF:307,Apoptygma Bezerk - Starsign
//mp3/Apoptygma Berzerk/Apoptygma Berzerk - Starsign.mp3
//#EXTINF:282,Various_Artists - Butthole Surfers: They Came In
//mp3/Butthole_Surfers-They_Came_In.mp3
//
//
// The First line, "#EXTM3U" is the format descriptor, in this case M3U 
// (or Extended M3U as it can be called). It does not change, it's always this. 
// The second and third operate in a pair. The second begins "#EXTINF:" 
// which serves as the record marker. The "#EXTINF" is unchanging. After the colon is 
// a number: this number is the length of the track in whole seconds 
// (not minutes:seconds or anything else. Then comes a comma and the name of the tune 
// (not the FILE NAME). A good list generator will suck this data from the ID3 tag 
// if there is one, and if not it will take the file name with the extension chopped 
// off. 
//
// The second line of this pair (the third line) is the actual file name of the media 
// in question. In my example they aren't fully qualified because I run this list by 
// typing "noatun foo.m3u" in my home directory and my music is in ~/mp3, so it just 
// follows the paths as relative from the path of invocation. 
/**
 * Provide logic for parsing and generating M3U format.
 * 
 */
public class M3ULogic {

    private Logger mLog = Logger.getLogger(getClass().getName());

    private static M3ULogic sSelf;

    public static final String M3U_DESCRIPTOR = "#EXTM3U";

    public static final String M3U_INFO = "#EXTINF:";

    public static final String M3U_EXTENSION = "m3u";

    public static M3ULogic getInstance() {
        if (sSelf == null) {
            sSelf = new M3ULogic();
        }
        return sSelf;
    }

    public M3ULogic() {

    }

    public List readPlaylist(File pFile) throws JPodderException {
        ArrayList mM3UList = null;

        if (pFile.exists()) {
            FileReader lReader = null;
            boolean lSucceeded = true;
            try {
                lReader = new FileReader(pFile);
                LineNumberReader lNumberReader = new LineNumberReader(lReader);
                String lLineIn;
                M3U lEntry = null;
                while ((lLineIn = lNumberReader.readLine()) != null
                        && lSucceeded) {
                    int lLineNumber = lNumberReader.getLineNumber();
                    if (lLineNumber == 1) {
                        if (!lLineIn.equals(M3U_DESCRIPTOR)) {
                            lSucceeded = false;
                        } else {
                            mM3UList = new ArrayList();
                        }
                    }
                    if (lLineNumber > 1) {
                        int seconds;
                        String lDescription;

                        String lTemp;
                        if (lLineIn.startsWith(M3U_INFO)) {
                            // We can create a new object here.
                            int lIndex = lLineIn.indexOf(',');
                            lTemp = lLineIn.substring(8, lIndex);
                            Integer lInt = new Integer(lTemp);
                            seconds = lInt.intValue();
                            lTemp = lLineIn.substring(lIndex + 1, lLineIn
                                    .length());
                            lDescription = lTemp;
                            lEntry = new M3U();
                            lEntry.setName(lDescription);
                        } else {
                            String lPath = lLineIn;
                            if (lEntry == null) {
                                lSucceeded = false; // when reaching here, we
                                // can't be null.
                            } else {
                                lEntry.setPath(lPath);
                                if (mM3UList != null) {
                                    mM3UList.add(lEntry);
                                    lEntry = null;
                                } else {
                                    lSucceeded = false;
                                }
                            }
                        }
                    }
                }
                lReader.close();
            } catch (FileNotFoundException e) {
                mLog.warn(e);
                lSucceeded = false;
            } catch (IOException e) {
                mLog.warn(e);
                lSucceeded = false;
            } finally {
                if (lSucceeded) {
                    mLog.info("Parsing succeeded: " + mM3UList.toArray());
                } else {
                    throw new JPodderException("M3U parsing failed");
                }
            }
        } else {
            throw new JPodderException("M3U file doesn't exist");
        }
        return mM3UList;
    }

    /**
     * 
     * 
     * @param pFile
     * @param pLocals
     *            Should be of type FileWrapper.
     * @throws JPodderException
     * @throws IOException
     */
    public void writePlaylist(File pFile, Object[] pLocals)
            throws JPodderException, IOException {
        writePlaylist(pFile, getM3UEntries(pLocals));
    }

    public void writePlaylist(File pFile, M3U[] pEntries)
            throws JPodderException, IOException {

        if (!pFile.exists()) {
            if (!pFile.createNewFile()) {
                throw new JPodderException("Can not create file");
            }
        }
        if (pEntries != null) {
            String lSep = System.getProperty("line.separator");
            FileWriter lWriter = new FileWriter(pFile);

            lWriter.write(M3U_DESCRIPTOR + lSep);

            for (int i = 0; i < pEntries.length; i++) {
                StringBuffer lBuf = new StringBuffer();
                M3U lEntry = pEntries[i];
                lEntry.getName();
                Integer lSeconds = new Integer(lEntry.getSeconds());
                lBuf.append(M3U_INFO);
                lBuf.append(lSeconds.toString() + ',');
                lBuf.append(lEntry.getName() + lSep);
                lBuf.append(lEntry.getPath() + lSep);
                lWriter.write(lBuf.toString());
            }
            lWriter.close();
            mLog.info("M3U file created: " + pFile.getName() + " with "
                    + pEntries.length);
        } else {
            mLog.warn("M3U file, nothing to write ");
        }
    }

    private M3U[] getM3UEntries(Object[] lFiles) {
        M3U[] lEntries = new M3U[lFiles.length];

        for (int i = 0; i < lFiles.length; i++) {
            IXFile lFile = (IXFile) lFiles[i];
            if (lFile.getFile() != null) { // File is null until it's downloaded.
                // Now we have to ID3 a bit.
            	File lLocalFile = lFile.getFile();
                ID3Wrapper lWrapper = new ID3Wrapper(lLocalFile);
                String lArtist = lWrapper.getContent("Artist");
                String lAlbum = lWrapper.getContent("Album");
                String lTitle = lWrapper.getContent("Title");

                MP3Info lInfo = new MP3Info(lLocalFile);
                long lSeconds = lInfo.getMPInfo().getLength();
                M3U lEntry = new M3U();
                lEntry.setName((lArtist != null ? lArtist : "") + "--"
                        + (lAlbum != null ? lAlbum : "") + "--"
                        + (lTitle != null ? lTitle : ""));
                lEntry.setPath(lLocalFile.getAbsolutePath());
                lEntry.setSeconds(new Long(lSeconds).intValue());
                lEntries[i] = lEntry;
            }
        }
        return lEntries;
    }

    private class M3U {

        /**
         * @return Returns the seconds.
         */
        public int getSeconds() {
            return seconds;
        }

        /**
         * @param seconds
         *            The seconds to set.
         */
        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        protected String path;

        protected String name;

        protected int seconds;

        public M3U() {

        }

        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }

        /**
         * @param name
         *            The name to set.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return Returns the path.
         */
        public String getPath() {
            return path;
        }

        /**
         * @param path
         *            The path to set.
         */
        public void setPath(String path) {
            this.path = path;
        }
    }

}