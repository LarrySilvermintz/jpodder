package com.jpodder.xml;

/**
 * @author <a href="mailto:christophe.bouhier@kualasoft.com">Christophe Bouhier
 *         </a>
 * @version 1.0
 */
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import com.jpodder.rss20.RssDocument;

public class RSSBinding {

    private static Logger sLog = Logger.getLogger(RSSBinding.class.getName());

    private static String CHARSETS_CP[] = { "ASCII", "BIG5", "CP037", "CP1026",
            "CP1047", "CP1140", "CP1141", "CP1142", "CP1143", "CP1143",
            "CP1144", "CP1145", "CP1146", "CP1147", "CP1148", "CP1250",
            "CP1251", "CP1252", "CP1253", "CP1254", "CP1255", "CP1256",
            "CP1257", "CP1258", "CP273", "CP277", "CP278", "CP280", "CP284",
            "CP285", "CP290", "CP297", "CP420", "CP424", "CP437", "CP500",
            "CP775", "CP775", "CP850", "CP855", "CP857", "CP858", "CP860",
            "CP861", "CP861", "CP862", "CP863", "CP864", "CP865", "CP866",
            "CP868", "CP869" };
    
    private static String CHARSETS_OTHERS[] = {
            "ISO2022CN", "ISO2022KR",
            "ISO8859_1", "ISO8859_2", "ISO8859_3", "ISO8859_4", "ISO8859_5",
            "ISO8859_6", "ISO8859_7", "ISO8859_8", "ISO8859_9", "JIS",
            "JIS0201", "JIS0208", "JIS0212", "KOI8_R", "KSC5601", "MS932",
            "SJIS", "TIS620", "UNICODE", "UNICODEBIG", "UNICODELITTLE", "UTF8"
    };
                                       
    private static String CHARSETS_ISO[] = {
            "ISO8859_1", "ISO8859_2", "ISO8859_3", "ISO8859_4", "ISO8859_5",
            "ISO8859_6", "ISO8859_7", "ISO8859_8", "ISO8859_9", 
            "UNICODE", "UNICODEBIG", "UNICODELITTLE", "UTF8" };

    public RSSBinding() {

    }

    /**
     * Validate and parse a file.
     * 
     * @param file
     * @return
     */
    public static RssDocument parse(File file) {
        int lAttempts = 0;
        RssDocument lDoc = null;
        boolean lGoOn = true;
        while (lGoOn) {
            try {
                XmlOptions lOptions = new XmlOptions();
                lOptions.setLoadStripWhitespace();
                lOptions.setLoadTrimTextBuffer();
                lOptions.setCompileNoValidation();
                if (lAttempts > 0) {
                    lOptions.setCharacterEncoding(CHARSETS_ISO[lAttempts]);
                }
                lDoc = RssDocument.Factory.parse(file, lOptions);
                sLog
                        .info("RSS file: " + file.getName()
                                + "parsing successfull");
                lGoOn = false;
            } catch (XmlException e) {
                lAttempts++;
                sLog.warn("RSS parsing error: " + e.getMessage());
//                sLog.debug("RSS parsing error: " + e.getMessage());
            } catch (IOException e) {
                sLog.warn("RSS parsing IO error: " + e.getMessage());
                sLog.warn("RSS parsing attempts: " + lAttempts);
                lGoOn = false;
            }
        }
        sLog.warn("RSS parsing completed, total attempts: " + lAttempts);
        return lDoc;
    }

    /**
     * Write a RSS document to a file.
     * 
     * @param doc
     * @param file
     */
    public static void write(RssDocument doc, File file) {
        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setSavePrettyPrint();
            xmlOptions.setSavePrettyPrintOffset(4);
            doc.save(file, xmlOptions);
            sLog.info("RSS writting of " + file.getName() + " succesful");
        } catch (IOException e) {
            sLog.warn("RSS writting error: " + e.getMessage());
        }

    }

    /**
     * Validate a file against the RSS 2.0 schema.
     * 
     * @param pFile
     * @throws org.apache.xmlbeans.XmlException
     * @throws java.io.IOException
     * @return boolean <code>true</true> if validation succeeds.
     */
    public static boolean validate(Reader pReader)
            throws org.apache.xmlbeans.XmlException, java.io.IOException {
        RssDocument doc = RssDocument.Factory.parse(pReader);
        ArrayList lErrors = new ArrayList();
        XmlOptions lOptions = new XmlOptions();
        lOptions.setErrorListener(lErrors);

        if (doc.validate(lOptions)) {
            sLog.info("RSS stream is fully valid");
            return true;
        } else {
            sLog.warn("RSS stream is not fully valid");
            Iterator iter = lErrors.iterator();
            while (iter.hasNext()) {
                Object lObject = iter.next();
                sLog.warn(">> " + lObject);
            }
            return false;
        }
    }

    /**
     * Validate a file against the RSS 2.0 schema.
     * 
     * @param pFile
     * @throws org.apache.xmlbeans.XmlException
     * @throws java.io.IOException
     * @return boolean <code>true</true> if validation succeeds.
     */
    public static boolean validate(File pFile)
            throws org.apache.xmlbeans.XmlException, java.io.IOException {
        RssDocument doc = RssDocument.Factory.parse(pFile);

        ArrayList lErrors = new ArrayList();
        XmlOptions lOptions = new XmlOptions();
        lOptions.setErrorListener(lErrors);

        if (doc.validate(lOptions)) {
            sLog.info("RSS file: " + pFile.getName() + " is fully valid");
            return true;
        } else {
            sLog.warn("RSS file: " + pFile.getName() + " is not fully valid");
            Iterator iter = lErrors.iterator();
            while (iter.hasNext()) {
                Object lObject = iter.next();
                sLog.warn(">> " + lObject);
            }
            return false;
        }
    }

}