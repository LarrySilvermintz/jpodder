package com.jpodder.data.opml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;

import org.apache.log4j.Logger;

import com.jpodder.JPodderException;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.directory.DirectoryEntry;
import com.jpodder.directory.DirectoryList;
import com.jpodder.net.NetTask;
import com.jpodder.tasks.ITask;
import com.jpodder.util.Util;
import com.jpodder.xml.NanoXML;
import com.jpodder.xml.XMLUtil;

/**
 * Parse opml
 */
public class OPMLParser {

    private static Logger sLog = Logger.getLogger(OPMLParser.class.getName());

    public int crawlIterations = 0;
    protected int level = 0;
    int lRSSCount = 0;
    boolean mCategoriesSwitch = false;
    protected ITask mTask;
    protected static OPMLParser mSelf;

    public static OPMLParser getInstance() {
        if (mSelf == null) {
            mSelf = new OPMLParser();
        }
        return mSelf;
    }

    /**
     * Constructor.
     * 
     * @param opmlURL
     *            URL
     * @param task
     *            TaskOPML
     */
    public void getOPMLTree(URL opmlURL, ITask pTask) {
        getOPMLTree(opmlURL, pTask, null);
    }

    /**
     * Constructor.
     * 
     * @param opmlURL
     *            URL
     * @param task
     *            TaskOPML
     * @param query
     *            String
     */
    public void getOPMLTree(URL opmlURL, ITask pTask, List lCurrentList) {
        mTask = pTask;
        try {
            Reader stream = null;
            boolean checkURL = false;
            crawlOPML(opmlURL);
        } catch (Exception ie) {
            sLog.warn(ie.getMessage());
        }
        mTask.setDone(true);
    }

    public List getOPML(File pFile, ITask pTask) {
        mTask = pTask;
        return getOPML(pFile);
    }

    public List getOPML(URL pLink, ITask pTask) {
        mTask = pTask;
        return getOPML(pLink);
    }

    private static final short OPML_DOC = 501;
    private static final short RSS_DOC = 502;
    private static final short OTHER_DOC = 503;
    private static final short BAD_DOC = 504;

    private short crawlOPML(URL opmlLink, String searchString) {
        return -1;
    }

    /**
     * Crawl through the OPML tree.
     * 
     * @param opmlLink
     *            URL
     * @return short The document type.
     * @deprecated
     */

    private short crawlOPML(URL opmlLink) {

        crawlIterations++;
        level++;

        short docType = OTHER_DOC;
        IXMLElement node = null;

        sLog.info("OPML level: " + level + " url=" + opmlLink.toExternalForm());
        // Get the URL in a reader and transform to XML object model.
        Reader stream = null;
        try {
            stream = NetTask.getReader(opmlLink);
            node = NanoXML.parseNanoXML(stream);
            // sLog.info("URL :" + opmlLink.toExternalForm() + " OK");
        } catch (Exception e) {
            return BAD_DOC;
        }

        // Check if this an OPML document. Looking for tag opml.
        // Should also investigate the version.

        if (node.getName().equals("opml")) { // This is likely an OPML
            // document.
            docType = OPML_DOC;
            sLog.info("Node is OPML");
            // OK, this is OPML, loop through the "Outline" list.

            Vector outlineElements = XMLUtil.getElementNamed(node, "outline");
            int size = outlineElements.size();
            int counter = 0;
            sLog.info("OPML files has: " + size + "<outline> elements");
            Iterator it = outlineElements.iterator();
            IXMLElement olElem;
            while (it.hasNext()) {
                olElem = (IXMLElement) it.next();
                String lText = null;
                counter++;
                // Get the url tag
                if (olElem.hasAttribute("text")) {
                    lText = olElem.getAttribute("text", "non-exist");
                    sLog.info("<outline>  " + counter + "(" + size + ") "
                            + lText);
                }

                if (level == 1) {
                    if (lText != null && lText.equals("Business")) {
                        mCategoriesSwitch = true;
                    }
                    // CB TODO Force interuption of parsing.
                    if (lText != null && lText.equals("Bicycles")) {
                        mCategoriesSwitch = false;
                    }

                }
                if (mCategoriesSwitch && olElem.hasAttribute("url")) {
                    String olURL = olElem.getAttribute("url", "non-exist");
                    try {
                        URL url = new URL(olURL);
                        // Check if this ends on OPML, all URL's should be
                        // parsed anyway.

                        String lUrlPath = url.getPath();
                        String lExt = Util.stripExtension(lUrlPath);
                        // Calls itself to go through the next link.
                        DirectoryList lList = DirectoryList.getInstance();
                        short nextDocType = crawlOPML(url);
                        level--;
                        if (level == 2) {
                        	ConfigurationLogic.getInstance().save(ConfigurationLogic.DIRECTORY_INDEX,
                            		ConfigurationLogic.getInstance().getDirectoryFile());
                        }

                        if (nextDocType != BAD_DOC) {
                        } else {
                            sLog.info("Invalide Node (Neither OPML/RSS)"
                                    + url.toExternalForm());
                        }
                        switch (nextDocType) {
                            case RSS_DOC: {
                            }
                                break;
                            case OPML_DOC: {
                                // CB save the feeds on a branch basis.
                            }
                                break;
                        }
                        // }
                    } catch (MalformedURLException mue) {
                        sLog.info("URL is malformed : " + olURL);
                    }
                } // No URL , just skip.
            } // end of outline loop.
        }
        // Check if this an RSS document. Looking for tag rss.
        if (node.getName().equals("rss")) { // This is likely an RSS document.
            docType = RSS_DOC;
            sLog.info("Node is RSS");
            if (opmlLink != null) {
                // Do we have this feed?
                DirectoryList lList = DirectoryList.getInstance();
                DirectoryEntry lEntry = lList
                        .getFeed(opmlLink.toExternalForm());
                if (lEntry == null) {
                    lEntry = new DirectoryEntry();
                    lEntry.setURL(opmlLink.toExternalForm());
                    lList.addFeed(lEntry);
                    sLog.info("New directory entry #" + ++lRSSCount + " url="
                            + opmlLink.toExternalForm());
                } else {
                    IXMLElement lChannel = (IXMLElement) node.getChildrenNamed(
                            "channel").get(0);
                    node = (IXMLElement) lChannel.getChildrenNamed(
                            "description").get(0);
                    String description = node.getContent();
                    node = (IXMLElement) lChannel.getChildrenNamed("title")
                            .get(0);
                    String title = node.getContent();
                    lEntry.setTitle(title);
                    lEntry.setDescription(description);
                    sLog.info("Directory entry exists: url="
                            + opmlLink.toExternalForm());
                }
            }
        }
        return docType;
    }

    private List getOPML(File pFile) {
        sLog.info("OPML parsing file=" + pFile.getAbsolutePath());
        Reader stream;
        try {
            stream = new FileReader(pFile);
            return getOPML(stream);
        } catch (IOException ioe) {
            sLog.warn("getOPML() Error retrieving" + pFile.getAbsolutePath()
                    + " " + ioe.getMessage());
        }
        return null;
    }

    private List getOPML(URL pLink) {
        sLog.info("OPML parsing url=" + pLink.toExternalForm());
        Reader stream;
        try {
            stream = NetTask.getReader(pLink);
            return getOPML(stream);
        } catch (JPodderException e) {
            sLog.warn("getOPML() Error retrieving" + pLink.toExternalForm()
                    + " " + e.getMessage());
        }
        return null;
    }

    private List getOPML(Reader pReader) {

        ArrayList lList = new ArrayList();
        level++;
        short docType = OTHER_DOC;
        IXMLElement node = null;

        try {
            node = NanoXML.parseNanoXML(pReader);
        } catch (Exception e) {
            sLog.warn("getOPML() Parsing error");
            return null;
        }
        // Check if this an OPML document. Looking for tag opml.
        // Should also investigate the version.
        int lChildren = node.getChildrenCount();

        if (node.getName().equalsIgnoreCase("opml")) { // This is likely an OPML
            // document.
            docType = OPML_DOC;
            sLog.info("File verified, it's OPML");
            // OK, this is OPML, loop through the "Outline" list.

            Vector outlineElements = XMLUtil.getElementNamed(node, "outline");
            int size = outlineElements.size();
            mTask.setLengthOfTask(size);
            int counter = 0;
            sLog.info("OPML file has: " + size + "<outline> elements");
            Iterator it = outlineElements.iterator();
            IXMLElement olElem;
            while (it.hasNext()) {
                mTask.setCurrent(mTask.getCurrent() + 1);
                olElem = (IXMLElement) it.next();
                String lText = null;
                String lUrl = null;

                counter++;
                // Get the url tag
                if (olElem.hasAttribute("text")) {
                    lText = olElem.getAttribute("text", "non-exist");
                    sLog.info("<outline>  " + counter + "(" + size + ") "
                            + lText);
                }

                // Sometimes xmlUrl is used Podnova, iPodder?.
                if (olElem.hasAttribute("xmlUrl")) {
                    lUrl = olElem.getAttribute("xmlUrl", "non-exist");
                }
                if (olElem.hasAttribute("url")) {
                    lUrl = olElem.getAttribute("url", "non-exist");
                }

                if (lUrl != null) {
                    try {
                        Outline lOl = new Outline(lText, new URL(lUrl));
                        lList.add(lOl);
                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        }   
        mTask.setDone(true);
        return lList;
    }

    public void writeOPML(List pList, File pFile) {
        sLog.info("writeOPML() Writing new OPML to: " + pFile.getPath());
        IXMLElement lRootElement = new XMLElement();
        lRootElement.setName("opml");
        IXMLElement lBody = lRootElement.createElement("body");
        lRootElement.addChild(lBody);
        
        Object[] lOutlines = pList.toArray();
        for (int i = 0; i < lOutlines.length; i++) {
            Object object = lOutlines[i];
            if(object instanceof Outline){
                Outline lOIn = (Outline)object;
                IXMLElement lO = new XMLElement();
                lO.setName("outline");
                lO.setAttribute("type", "link");
                lO.setAttribute("text", lOIn.getText());
                lO.setAttribute("url", lOIn.getURL().toExternalForm());
                lBody.addChild(lO);
            }
        }
        
        try {
            NanoXML.writeNanoXML(pFile, lRootElement);
        } catch (Exception e) {
            sLog.warn("getOPML() Parsing error");
        }
    }
}

/*
 * Get the Outline list. @param elem IXMLElement @return Vector
 */

// CB TODO Anything reusable in this method?
// private List getOutLineList(Reader stream, boolean checkURL)
// throws Exception {
//
// ArrayList lFeedList = new ArrayList();
//
// IXMLElement root = NanoXML.parseNanoXML(stream);
// Vector outlineElements = XMLUtil.getElementNamed(root, "outline");
// mTask.lengthOfTask = outlineElements.size();
// Iterator it = outlineElements.iterator();
// IXMLElement olElem;
// while (it.hasNext() && !mTask.canceled) {
// olElem = (IXMLElement) it.next();
// if (olElem.hasAttribute("text")) {
// String text = olElem.getAttribute("text", "non-exist");
// mTask.statMessage = text;
// mTask.current += 1;
// String olURL = olElem.getAttribute("url", "non-exist");
// URL url = null;
// int docType = OTHER_DOC;
// try {
// url = new URL(olURL);
// IXMLElement node = null;
// // Get the URL in a reader and transform to XML object
// // model.
// stream = NetTask.getReader(url);
// node = NanoXML.parseNanoXML(stream);
// // Check if this an OPML document. Looking for tag opml.
// // Should also investigate the version.
// if (node.getName().equals("opml")) { // This is likely
// // an OPML
// // document.
// docType = OPML_DOC;
// }
// // Check if this an RSS document. Looking for tag rss.
// if (node.getName().equals("rss")) { // This is likely an
// // RSS document.
// docType = RSS_DOC;
// }
//
// } catch (MalformedURLException mue) {
// // EXCEPTION HANDLING
// // Whatever, go on.
// } catch (Exception ie) {
// // EXCEPTION HANDLING
// // Whatever, go on.
// }
//
// if (url != null && docType == RSS_DOC) {
// // Store a more comprehensive enclosure object.
// DirectoryEntry lEntry = new DirectoryEntry();
// lEntry.setURL(url.toExternalForm());
// lFeedList.add(lEntry);
// }
// }
// }
// return lFeedList;
// }
