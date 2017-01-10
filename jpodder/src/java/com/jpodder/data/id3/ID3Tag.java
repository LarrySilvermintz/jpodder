package com.jpodder.data.id3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;

import org.apache.log4j.Logger;

import com.jpodder.xml.XMLUtil;

/**
 * This class holds all know existing ID3 frames in a static list.
 * The ID3 name and description of the frame are stored in 
 * XML format in a file named id3.xml. The file is parsed
 * when loading this class. 
 * <p>
 * It provides convenience classes for retrieving all ID3 frames
 * or only text based frames. (Starting with a T). 
 * <p>
 * See a list of all ID3 frame on http://www.id3.org
 * <P>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public class ID3Tag {

    private static Logger sLog = Logger.getLogger( ID3Tag.class.getName() );

    static List mID3TagList;

    static {
        mID3TagList = new ArrayList();
        if (com.jpodder.FileHandler.sID3AllFile != null) {
            try {
                IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
                IXMLReader reader = StdXMLReader.fileReader(
                    com.jpodder.FileHandler.sID3AllFile.toURL().getPath().toString()
                );
                parser.setReader(reader);
                IXMLElement xml = (IXMLElement) parser.parse();
                Iterator i = XMLUtil.getElementNamed(xml, "tag").iterator();
                while (i.hasNext()) {
                    IXMLElement tag = (IXMLElement) i.next();
                    String id = "";
                    String name = "";
                    String description = "";
                    String value = "";
                    
                    if (tag.hasAttribute("id")) {
                        id = tag.getAttribute("id", "no-exist");
                    }
                    if (tag.hasAttribute("name")) {
                        name = tag.getAttribute("name", "no-exist");
                    }
                    if (tag.hasAttribute("description")) {
                        description = tag.getAttribute("description", "no-exist");
                    }
                    if (tag.hasAttribute("value")) {
                        value = tag.getAttribute("value", "no-exist");
                    }
                    mID3TagList.add(
                        new ID3Tag(
                                id,
                            name,
                            description,
                            value
                        )
                    );
                }
            } catch (Exception e) {
                sLog.error( "Error reading the ID3 file: " + com.jpodder.FileHandler.sID3AllFile, e );
            }
        }
    }

    public static List getID3TagList() {
        return mID3TagList;
    }
    
    /**
     * A list of text based ID3 frames is returned. 
     * @return
     */
    public static List getID3TextTagList(){
        ArrayList lTextTagList = new ArrayList();
        Iterator it = mID3TagList.iterator();
        for(;it.hasNext();){
           ID3Tag lTag = (ID3Tag)it.next();
           if(lTag.getId().startsWith("T")){
               lTextTagList.add(lTag);
           }
        }
        return lTextTagList;
    }
    
        
    private String mId;
    
    private String mName;

    private String mDescription;

    private String mValue;

    /**
     * Basic constructor
     *
     * @param pName Name of the Tag which must not be null
     */
    public ID3Tag(String pName) {
        mName = pName;
    }
 
    /**
     * Regular Constructor for a completely set up Tag
     *
     * @param pName Name of the Tag which must not be null
     * @param pDescription Description of the tag
     * @param pValue Value of the Tag
     */
    public ID3Tag(String pId, String pName, String pDescription, String pValue) {
        mId = pId;
        mName = pName;
        mDescription = pDescription;
        mValue = pValue;
    }

    /**
     * @return Tag Name
     */
    public String getName() {
        return mName;
    }
    
    /**
     * Get the short name for a tag, from it's frame ID.
     * @return Tag Name
     */
    public static ID3Tag getTag(String pId) {
        
        for (Iterator it = mID3TagList.iterator(); it.hasNext();) {
            ID3Tag element = (ID3Tag) it.next();
            if( element.getId().equals(pId)){
                return element;
            }
        }
        return null;
    }
    

    /**
     * @return Tag Description
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * @param pValue The new description to be set
     */
    public void setDescription(String pValue) {
        mDescription = pValue;
    }

    /** @return Tag Value **/
    public String getValue() {
        return mValue;
    }

    /**
     * @param pValue Tag Value to be set
     */
    public void setValue(String pValue) {
        mValue = pValue;
    }

    public String toString() {
        return mName + ", " + mDescription;
    }

    public boolean equals( Object pTest ) {
        boolean lReturn = false;
        if( pTest instanceof ID3Tag ) {
            ID3Tag lOther = (ID3Tag) pTest;
            if( mName != null ) {
                lReturn = mName.equals( lOther.mName );
            }
        }
        return lReturn;
    }

    public int hashCode() {
        if( mName == null ) {
            return 37;
        } else {
            return mName.hashCode();
        }
    }
    /**
     * @return Returns the mId.
     */
    public String getId() {
        return mId;
    }
}