package com.jpodder.data.feeds.production;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jpodder.data.feeds.XFeedException;
import com.jpodder.data.feeds.xmlbeans.XFeed;


/**
 * The production class is a simple holder of an Rss content tree. Additionally
 * the production stage is maintained.
 * 
 * @see ProductionStage 
 * TODO This type is holder for a Feed object and an RSS
 *      object + individual members of an RSS Item tab.
 */
public class XProduction extends XFeed {

    /**
     * The local folder file. Handy for storing the rss file and associated
     * local productions. Comment for <code>folderFile</code>
     */
    private File folderFile;

    private URL mFtpFeedUrl;

    private URL mFtpEnclosureUrl;

    private ProductionStage currentStage = null;
    
    private ArrayList lItems = new ArrayList();
    
    public XProduction(boolean pCreate) {
        this(null, pCreate);
    }

    public XProduction(File pFile, boolean pCreate) {
        super(pFile, pCreate);
        if(pFile != null){
            setFolderFile(pFile.getParentFile());    
        }
        
        try {
            for (int i = 0; i < super.getRSSItems().length; i++) {
                lItems.add(new XProductionItem(this, i));
            }
        } catch (XFeedException e) {
            // Rss object is not available.
        }
    }
    
    public List getProductionItems(){
        return lItems;
    }
    
    public void removeItem(XProductionItem pItem){        
        lItems.remove(pItem);
        try {
            removeItem(indexOfItem(pItem));
        } catch (XFeedException e) {
            e.printStackTrace();
        }
    }
    
    public XProductionItem getProductionItem(int pIndex) {
        return (XProductionItem)lItems.get(pIndex);
    }
    
    /**
     * @return Returns the currentStage.
     */
    public ProductionStage getCurrentStage() {
        return currentStage;
    }

    /**
     * @param currentStage
     *            The currentStage to set.
     */
    public void setCurrentStage(ProductionStage currentStage) {
        this.currentStage = currentStage;
    }

    /**
     * @return Returns the folderFile.
     */
    public File getFolderFile() {
        return folderFile;
    }

    /**
     * @param folderFile
     *            The folderFile to set.
     */
    public void setFolderFile(File folderFile) {
        this.folderFile = folderFile;
        // We will also reparse the items so that correct file
        // object can be created.
        
    }
    
    public void extracRecordings(){
        // CB TODO, We don't need to do this every time.
        // Only when loading from 
        Iterator iter = getProductionItems().iterator();
        while (iter.hasNext()) {
            XProductionItem lItem = (XProductionItem) iter.next();
            File lRecording = lItem.getRecordingFromURL();
            lItem.setRecording(lRecording);
        }
    }
    
    

    /**
     * @param pItem
     *            A production item.
     * @return boolean <code>true</code> if the provided MP3 file exits in the
     *         provided production.
     */
    public boolean hasRecording(XProductionItem pItem) {
        
        List l = getProductionItems();
        Iterator it = l.iterator();
        while (it.hasNext()) {
            XProductionItem lRecording = (XProductionItem) it.next();
            if (pItem.getRecording().getName().equals(
                    lRecording.getRecording().getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Returns the mFtpEnclosureUrl.
     */
    public URL getFtpEnclosureUrl() {
        return mFtpEnclosureUrl;
    }

    /**
     * @param ftpEnclosureUrl
     *            The mFtpEnclosureUrl to set.
     */
    public void setFtpEnclosureUrl(URL ftpEnclosureUrl) {
        mFtpEnclosureUrl = ftpEnclosureUrl;
    }

    /**
     * @return Returns the mFtpFeedUrl.
     */
    public URL getFtpFeedUrl() {
        return mFtpFeedUrl;
    }

    /**
     * @param ftpFeedUrl
     *            The mFtpFeedUrl to set.
     */
    public void setFtpFeedUrl(URL ftpFeedUrl) {
        mFtpFeedUrl = ftpFeedUrl;
    }
}