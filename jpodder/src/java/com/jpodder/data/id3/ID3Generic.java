package com.jpodder.data.id3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLParserFactory;
import net.n3.nanoxml.XMLWriter;

import org.apache.log4j.Logger;

import com.jpodder.JPodderException;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.xml.XMLUtil;

/**
 * The generic ID3 class reads and writes ID3 tags used
 * to rewrite ID3 tag. 
 * 
 * TODO We don't use the generic data handler but nano-xml. 
 * This could possibly be XMLBeans with background loading.
 * 
 */
public class ID3Generic {

    private static Logger sLog = Logger
            .getLogger(ID3Generic.class.getName());

    protected static ID3Generic self;

    /**
     * A list of id3 tags. Comment for <code>list</code>
     */
    private static List<ID3TagRewrite> mRewriteTagsList = new ArrayList<ID3TagRewrite>();
    
    static{
        try {
        	
            if(ConfigurationLogic.getInstance().getGenericID3File().exists()){
                IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
                IXMLReader reader = StdXMLReader.fileReader(ConfigurationLogic.getInstance().getGenericID3File().toURL()
                        .getPath().toString());
                parser.setReader(reader);
                IXMLElement xml = (IXMLElement) parser.parse();
                
                Vector v = XMLUtil.getElementNamed(xml, "tag");
                Iterator it = v.iterator();
                while (it.hasNext()) {
                    IXMLElement tag = (IXMLElement) it.next();
                    mRewriteTagsList.add(new ID3TagRewrite(tag.getAttribute("name",
                            "no-exist"), tag.getAttribute("value", "no-exist")));
                }
            }else{
                throw new Exception();
            }
        } catch (Exception e1) {
            sLog.info("ID3 XML file for generic tags is not available, will be created when needed");
        }
    }
    
    /**
     * Return single instance of this class. Releaves cascading of classes.
     * 
     * @return ID3Generic
     */
    public static ID3Generic getInstance() {
        if (self == null) {
            self = new ID3Generic();
        }
        return self;
    }

    /**
     * Add an id3Tag.
     * 
     * @param tag
     */
    public void addId3Tag(ID3TagRewrite tag) {
        if (!mRewriteTagsList.contains(tag)) {
            mRewriteTagsList.add(tag);
            Collections.sort(mRewriteTagsList, new Comparator() {
                public int compare(Object pFirst, Object pSecond) {
                    ID3TagRewrite lFirst = (ID3TagRewrite) pFirst;
                    ID3TagRewrite lSecond = (ID3TagRewrite) pSecond;
                    return lFirst.getName().compareTo(lSecond.getName());
                }
                public boolean equals(Object pTest) {
                    return pTest == this;
                }
            });
        }
    }

    public void removeId3Tag(ID3TagRewrite tag) {
        if (mRewriteTagsList.contains(tag)) {
            mRewriteTagsList.remove(tag);
        }
    }

    /**
     * get an id3Tag.
     * 
     * @param name
     * @return ID3TagRewrite Returns <code>null</code> if no tag is found
     *         whith this name.
     */
    public ID3TagRewrite getId3Tag(String name) {
        for (Iterator iter = mRewriteTagsList.iterator(); iter.hasNext();) {
            ID3TagRewrite element = (ID3TagRewrite) iter.next();
            if (element.getName().equals(name)) {
                return element;
            }
        }
        return null;
    }
    
    /**
     * @return the Rewrite List
     */
    public List getTagList() {
        return mRewriteTagsList;
    }
    
    public void setTagList(List<ID3TagRewrite> pTagList){
        mRewriteTagsList = pTagList;
    }
    
    /**
     * @return Iterator on the Tags for Rewrite List
     */
    public Iterator getTagListIterator() {
        return mRewriteTagsList.iterator();
    }

    
    
    public void writeResource() throws JPodderException {
    	writeResource(ConfigurationLogic.getInstance().getGenericID3File());
    }
    	
    /**
     * @param pFile
     * @throws Exception
     * @see com.jpodder.ui.resources.Resource#writeResource(java.io.File)
     */
    private void writeResource(File pFile) throws JPodderException {
    	
    if(pFile == null){
    		throw new IllegalArgumentException();
    	}
    	sLog.info("writeResource(), file: " + pFile);
        IXMLElement xml = (IXMLElement) new XMLElement("id3");
        Iterator it = mRewriteTagsList.iterator();
        while (it.hasNext()) {
            ID3TagRewrite lTag = (ID3TagRewrite) it.next();
            IXMLElement elem = xml.createElement("tag");
            elem.setAttribute("name", lTag.getName());
            elem.setAttribute("value", lTag.getValue());
            xml.addChild(elem);
        }
        try {
            if(!pFile.exists()){
                pFile.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(pFile);
            XMLWriter xmlwriter = new XMLWriter(fileWriter);
            xmlwriter.write(xml);
        } catch (IOException ioe) {
            sLog.error( "...problems writing id3 file", ioe );
            throw new JPodderException(ioe.getMessage());
        }
    }

    public String getName() {
        return null;
    }

    public static String giveGetMethod(ID3TagRewrite target) {
        StringBuffer result = new StringBuffer();
        result.append("get");
        result.append(target.getName());
        return result.toString();
    }

    public static String giveSetMethod(ID3TagRewrite target) {
        StringBuffer result = new StringBuffer();
        result.append("set");
        result.append(target.getName());
        return result.toString();
    }
}