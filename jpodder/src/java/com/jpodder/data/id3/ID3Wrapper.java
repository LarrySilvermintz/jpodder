package com.jpodder.data.id3;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.vdheide.mp3.ID3v2Frame;
import de.vdheide.mp3.ID3v2NoSuchFrameException;
import de.vdheide.mp3.MP3File;
import de.vdheide.mp3.NoID3v2TagException;
import de.vdheide.mp3.TagContent;

/**
 * Wrapper class to hide some of the ID3 methods and to provide some convinience
 * methods.
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class ID3Wrapper {
	
	private static Logger sLog = Logger.getLogger(ID3Wrapper.class.getName());
    /**
     * Tries to find the Getter method for the tag
     * 
     * @param pTagName
     *            Name of the tag
     * 
     * @return Return the Getter method or null if not found
     */
    public static Method getGetterMethod(String pTagName) {
        try {
            return MP3File.class.getMethod("get" + pTagName, null);
        } catch (NoSuchMethodException nsme) {
        	sLog.warn("get" + pTagName + ": ID3 method unknown");
            return null;
        }
    }

    /**
     * Tries to find hte Setter method for the tag
     * 
     * @param pTagName
     *            Name of the tag
     * 
     * @return Return the Setter method or null if not found
     */
    public static Method getSetterMethod(String pTagName) {
        try {
            return MP3File.class.getMethod("set" + pTagName,
                    new Class[] { TagContent.class });
        } catch (NoSuchMethodException nsme) {
        	sLog.warn("set" + pTagName + ": ID3 method unknown");
            return null;
        }
    }

    private MP3File mMP3;
    
    
    public ID3Wrapper(File pMP3File) {
        this(pMP3File.getParentFile(), pMP3File);
    }
    
    /**
     * Constructor to wrap an MP3 file
     * 
     * @param pDirectory
     *            Directory where the MP3 file is placed into and must not be
     *            null
     * @param pMP3File
     *            MP3 File and must not be null
     * 
     * @throws IllegalArgumentException
     *             If one of the parameter is null
     */
    public ID3Wrapper(File pDirectory, File pMP3File) {
        if (pDirectory == null) {
            throw new IllegalArgumentException(
                    "Directory of the MP3 File must not be null");
        }
        if (pMP3File == null) {
            throw new IllegalArgumentException("The MP3 File must not be null");
        }
        try {
            mMP3 = new MP3File(pDirectory, pMP3File.getName());
            mMP3.setWriteID3v2(true);
            mMP3.setWriteID3(false);
            mMP3.setUseCompression(false);
            mMP3.setUseUnsynchronization(false);
            mMP3.setUsePadding(true); // Avoids rewritting to a new file if
            mMP3.setUseExtendedHeader(false);
            // possible.
            mMP3.convert();
        } catch (Exception e) {
            IllegalArgumentException lException = new IllegalArgumentException(
                    "Could not create MP3 File");
            lException.initCause(e);
            throw lException;
        }
    }

    /**
     * Tries to obtain the text content of a tag
     * 
     * @param pTagName
     *            Name of the Tag the content is to be obtained
     * 
     * @return The content of the tag or null if not found or if it is empty
     */
    public String getContent(String pTagName) {
        try {
            TagContent lTagContent = getTagContent(pTagName);
            return lTagContent.getTextContent();
        } catch (Exception e) {
        	sLog.warn("getContent(Tag):" + pTagName + "not defined");
            return null;
        }
    }

    /**
     * Tries to set the Content of a Tag
     * 
     * @param pTagName
     *            Name of the Tag the content is to be set
     * @param pValue
     *            Value to be set on the Tag
     */
    public void setContent(String pTagName, String pValue) {
        try {
            TagContent lTag = getTagContent(pTagName);
            lTag.setContent(pValue);
            Method lSetTagMethod = MP3File.class.getMethod("set" + pTagName,
                    new Class[] { TagContent.class });
            lSetTagMethod.invoke(mMP3, new Object[] { lTag });
        } catch (Exception e) {
        	sLog.warn("setContent(Tag, Value):" + pTagName + "not defined");
            e.printStackTrace();
        }
    }

    /**
     * Removes a frame.
     */
    public void removeContent(ID3TagRewrite pTag) {
        try {
            mMP3.getId3v2().removeFrame(pTag.getId());
        } catch (NoID3v2TagException e) {
        	
            e.printStackTrace();
        } catch (ID3v2NoSuchFrameException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the new contents on the underlying MP3
     */
    public void update() {
        try {
            mMP3.setWriteID3(false);
            mMP3.setWriteID3v2(true);
            mMP3.update();
        } catch (Exception e) {
        	sLog.warn("update() update of " + mMP3.getAbsolutePath() + "failed");
            throw new RuntimeException("MP3 Update Failed: " + e, e);
        }
    }

    /**
     * We get a tag content from a tagname by pre-pending "get" to the tagname
     * and invoking the method.
     * 
     * 
     * @param pTagName
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private TagContent getTagContent(String pTagName)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        Method lGetTagMethod = MP3File.class.getMethod("get" + pTagName, null);
        Object lObj = lGetTagMethod.invoke(mMP3, null);
        if(lObj instanceof TagContent){
        	return (TagContent)lObj;
        }else{
        	sLog.warn("getTagContent(TagName) Content for:" + pTagName+ ", unexpected type:" + lObj.getClass().getName());
        }
        return null;
    }

    /**
     * Return an array of tagcontents for all the known ID3v2 frames for this
     * file.
     * 
     * @return
     * @throws NoID3v2TagException
     */
    public List getAllContents() throws NoID3v2TagException {
        
        // CB TODO, fitering of double frames doesn't work yet. Remove when filtering works.
    	// Vector lFrames = this.filterDoubles(mMP3.getId3v2().getFrames());
        Vector lFrames = mMP3.getId3v2().getFrames();
        TagContent[] contents = new TagContent[lFrames.size()];
        int i = 0;
        for (Iterator iter = lFrames.iterator(); iter.hasNext();) {
            try {
                ID3v2Frame element = (ID3v2Frame) iter.next();
                String id = element.getID();
                ID3Tag tag = ID3Tag.getTag(id);
                if (tag != null) {
                    TagContent tc = getTagContent(tag.getName());
                    contents[i++] = tc;
                }
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }
        
        return Arrays.asList(contents);
    }
    
    
    
    
    public Vector filterDoubles(Vector lFrames){
        
//        Vector lFramesCopy = new Vector(lFrames);
        
        Iterator it = lFrames.iterator();
        while(it.hasNext()){
            ID3v2Frame lFrame = (ID3v2Frame)it.next();
            Iterator it1 = lFrames.iterator();
            int index = 0;
            int count = 0;
            while(it1.hasNext()){
                ID3v2Frame lFrame1 = (ID3v2Frame)it1.next();
                if(lFrame.getID().equals(lFrame1.getID())){
                    count++;
                    if(count > 1 ){
                        // Remove from the copy list.
                        lFrames.remove(index);
                        
                    }
                }
                index++;
            }
        }
        return lFrames;
    }
    

    /**
     * @return Returns the mMP3.
     */
    public MP3File getMP3() {
        return mMP3;
    }    
}