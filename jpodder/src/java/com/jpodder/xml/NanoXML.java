package com.jpodder.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;
import net.n3.nanoxml.XMLWriter;

import org.apache.log4j.Logger;

import com.jpodder.JPodderException;

/**
 * Convenience class for parsing a reader. the XML root node is returned.
 */
public class NanoXML {

    private static Logger sLog = Logger.getLogger(NanoXML.class.getName());

    /**
     * nano XML parsing of the feed.
     * 
     * @param in
     *            Reader
     * @return IXMLElement
     * @throws Exception
     *             An XML parsing error.
     */
    public static IXMLElement parseNanoXML(Reader in) throws Exception {

        IXMLElement xml = null;
        try {
            IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
            IXMLReader reader = new StdXMLReader(in);
            parser.setReader(reader);
            xml = (IXMLElement) parser.parse();
            in.close();
        } catch (Exception e) {
            sLog.error("Error parsing XML", e);
            throw new JPodderException(e.getMessage());
        }
        return xml;
    }

    /**
     * nano XML parsing of the feed.
     * 
     * @param in
     *            Reader
     * @return IXMLElement
     * @throws Exception
     *             An XML parsing error.
     */
    public static IXMLElement parseNanoXML(File in) throws Exception {
        IXMLElement xml = null;
        try {
            IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
            FileReader fr = new FileReader(in);

            IXMLReader reader = new StdXMLReader(new BufferedReader(fr));

            parser.setReader(reader);
            xml = (IXMLElement) parser.parse();
            fr.close();
        } catch (Exception e) {
            sLog.error("Error parsing XML", e);
            throw new JPodderException(e.getMessage());
        }
        return xml;
    }

    public static void writeNanoXML(File pFile, IXMLElement pElement) throws JPodderException {
        IXMLElement xml = null;
        try {
            XMLWriter lWriter = new XMLWriter(new FileWriter(pFile));
            lWriter.write(pElement);
        } catch (Exception e) {
            sLog.error("Error parsing XML", e);
            throw new JPodderException(e.getMessage());
        }
    }
}
