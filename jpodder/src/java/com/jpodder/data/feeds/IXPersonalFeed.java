package com.jpodder.data.feeds;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.jpodder.data.feeds.XPersonalFeed.FeedCredentials;
import com.jpodder.data.feeds.stats.XFeedEventHistory;
import com.jpodder.data.feeds.stats.XFeedInstruction;
import com.jpodder.data.id3.ID3TagRewrite;
import com.jpodder.net.NetHEADInfo;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public interface IXPersonalFeed extends IXFeed {
    /**
     * Delivers the enclosure with the given index if available
     * 
     * @param pIndex
     *            Index of the Enclosure in the list
     * 
     * @return Enclosure if found
     */
    public abstract IXPersonalEnclosure getEnclosure(int pIndex) throws XFeedException;

    /**
     * Delivers the index with the given enclosure if available
     * 
     * @return pIndex Index of the Enclosure in the list
     */
    public abstract int indexOf(IXPersonalEnclosure pEnclosure);

    public abstract int indexOf(IXFile pEnclosure);
    
    /**
     * Adds the given Enclosure to our list of Enclosures
     * 
     * @param aEnclosure
     *            A new enclosure to be added at the end of the list
     */
    public abstract void addEnclosure(IXPersonalEnclosure pEnclosure);

    /**
     * Get the number of enclosures.
     * @return
     */
    public abstract int getEnclosureSize();

    /**
     * Get an iterator containing enclosures.
     * @return
     */
    public abstract Iterator getEnclosureIterator();
    
    /**
     * Get an Array containing the enclosures.
     * @return
     */
    public abstract Object[] getEnclosureArray();
    
    
    /**
     * Get the title from the RSS model or an 
     * 
     * @return
     */
    public abstract String getTitle();
    
    /**
     * Get the personal title of this feed.
     * @return
     */
    public String getPersonalTitle();
    
    /**
     * Set the personal title of this feed. 
     * @param pTitle
     */
    public void setPersonalTitle(String pTitle);

    
    /**
     * Replaces the current list enclosure with the given list. If will first
     * clear the
     * 
     * @param pEnclosures
     *            List of enclosure to replace the current list and must not be
     *            null
     */
    // CB TODO, We can not set the list of enclosures without attaching
    // the RSS model to the enclosure. 
//    public abstract void setEnclosures(List pEnclosures);
    
    /**
     * Merge the enclosures from an RSS file with the altready defined 
     * enclosures.
     * @param pFile
     * @return
     */
    public int updateEnclosures(File pFile) throws XFeedException ;
 
    /**
     * Release the enclosures (Allow garbage collection of the enclosure data).
     * 
     * @return
     * @throws XFeedException
     */
    public void releaseEnclosures() throws XFeedException ;
    
    /**
     * Get the local files in the podcast folder. The file folder is parsed and
     * returned as a collection. The collection take also a temporary state
     * which can be modified. (Files beeing added and removed). The application
     * is responsible for synchroning the collection with the directory state.
     * 
     * @param refresh
     *            boolean Rescans the files directory if requested.
     * @return Vector
     */
    public abstract List getFiles(boolean refresh);

    /**
     * Returns the total size of the files in the folder.
     * 
     * @return String total size of this feed's folder space excluding
     *         enclosures.
     */
    public abstract String getAccumulatedFolderSize(boolean pRefresh);

    /**
     * Returns the total size of the folder.
     * 
     * @return String total size of this feed's folder space
     */
    public abstract String getAccumulatedMergedSize();

    /**
     * Get all the marked files.
     * 
     * @return int Get the number of files which are marked.
     */
    public abstract int getMarkedFilesCount();

    /**
     * Get the number of files or enclosures for this feed.
     * 
     * @param onlyLocal
     *            Specify if local or all files/enclosures should be counted.
     *            Locals are files on the local hard disc.
     * 
     * @return Get the number of files or enclosures for this feed.
     */
    public abstract int getMergedCount(boolean onlyLocal);
    
    
    /**
     * Returns the status of the given index
     * 
     * @param pIndex
     *            The index of the related feed.
     * @return Either -1 indicating it is a local file (or no file available) or
     *         the status as sum of: 2: indicating a downloadable candidate 4:
     *         indicating a incomplete download
     */
    public abstract int getEnclosureStatus(int pIndex);

    /**
     * Get the number of download candidates. A candidate is marked and fits
     * within the limit of maximum downloadable enclosures from this feed.
     * 
     * @return in Get the number of files which are a candidate to be
     *         downloaded.
     */
    public abstract int getCandidatesCount();

    /**
     * Set the number of download candidates.
     * 
     * @param candidates
     */
    public abstract void setCandidatesCount(int candidates);

    /**
     * Get the number of files which are on the local disc including enclosures.
     * 
     * @return int Get the number of files / enclosures which are available on
     *         the local hard disc.
     */
    public abstract int getOnDiscFileCount();

    /**
     * Merges the enclosures and the local files. Remerging occures when the
     * refresh argument is <code>true</code>. Merging adds all enclosures and
     * the the non-duplicate files.
     * 
     * @param refresh
     *            When <code>true<code>, the files and enclosures for this feef
     * will be re-merged.
     * 
     * @return Vector
     */
    public abstract List getMerged(boolean refresh);
    
    public abstract Object[] getMergedArray(boolean refresh);
    /**
     * Return all enclosures which have status local + all 
     * folder files. 
     * 
     * @return Array of objects.
     */
    public abstract Object[] getLocals();
    
    /**
     * Set the maximum number of downloadable enclosures for this feed(index).
     * 
     * @param pMaxDownloads
     *            int
     */
    public abstract void setMaxDownloads(int pMaxDownloads);

    /**
     * Set the folder of this feed.
     * 
     * @param pLocalFolder
     */
    public abstract void setFolder(String pLocalFolder);

    /**
     * Get the folder of this feed.
     * 
     * @return String Get the folder associated with this feed.
     */
    public abstract String getFolder();

    /**
     * Get a description of this feed to be used in a ToolTip text.
     * 
     * @return String.
     */
    public abstract String getToolTipDescription();

    /**
     * Get the maximum number of downloadable enclosures from this feed(index).
     * This is a wrapper for a tablemodel.
     * 
     * @return int
     */
    public abstract int getMaxDownloads();

    /**
     * Get the URL Head information.
     * 
     * @return NetHEADInfo
     * @see NetHEADInfo
     */
    public abstract NetHEADInfo getHEADInfo();

    /**
     * Set the header information.
     * 
     * @param head
     */
    public abstract void setHEADInfo(NetHEADInfo pHeaderInformation);

    /**
     * Get the quality of this feed. The quality can be;
     * <code>GOOD_QUALITY</code>,<code>MEDIUM_QUALITY</code>,
     * <code>BAD_QUALITY</code> or <code>UNKNOW_QUALITY</code>.
     * 
     * @return Integer
     */
    public abstract int getQuality();

    /**
     * Set the quality of this feed.
     * 
     * @param pQuality
     *            int
     */
    public abstract void setQuality(int pQuality);

    public abstract URL getURL();

    /**
     * Set the feed URL.
     * 
     * @param url
     *            URL
     */
    public abstract void setURL(URL pUrl);

    
    /**
     * Get the HTTP Redirect URL.
     * @return
     */
    public URL getRedirectURL();

    /**
     * Set the feed URL.
     * 
     * @param url
     *            URL
     */
    public void setRedirectURL(URL pRedirecturl);

    
    /**
     * Gets if the feed should be polled. This is a wrapper for a tablemodel.
     * 
     * @return boolean
     */
    public abstract boolean getPoll();

    /**
     * Set if the feed should be polled.
     * 
     * @param value
     *            boolean
     */
    public abstract void setSubscribed(boolean value);

    /**
     * Set the instruction for this feed.
     * 
     * @param src
     * 
     * @param pCollectFeed
     *            boolean
     * @param pCollectEncl
     *            boolean
     * @param pMarkEncl
     *            boolean
     * @param pInspectEncl
     *            boolean
     * @param pDownloadEncl
     *            boolean
     * @param store
     *            boolean
     * @see FeedInstruction
     */
    public abstract void setInstruction(Object src, boolean pCollectFeed,
            boolean pCollectEncl, boolean pMarkEncl, boolean pInspectEncl, boolean pDownloadEncl,
            boolean store);

    /**
     * Get the current instruction. Instructions are volatile. This method
     * returns only the current instruction.
     * 
     * @return FeedInstruction.
     */
    public abstract XFeedInstruction getInstruction();

    /**
     * Get if the inspection has been ordered before. This is to avoid
     * un-necessary resource insentive inspection.
     * 
     * @return
     */
    public abstract boolean getInspectInstructed();
    
    
    
    /**
     * * This methods flags the enclosures in a feed, which are a candidate for
     * downloading. The first # of enclosures are marked according to the
     * maximum number of downloads for the feed.
     * 
     * @return int The number of candidates
     */
    public abstract int updateAllCandidates(boolean pUpdateMax);

    /**
     * Update a single candidate.
     * @param encl
     * @param pUpdateMax
     */
    public void updateSingleCandidate(IXPersonalEnclosure encl,
            boolean pUpdateMax);

    /**
     * @return Returns the credentials.
     */
    public abstract FeedCredentials getCredentials();

    /**
     * @param _credentials
     *            The credentials to set.
     */
    public abstract void setCredentials(FeedCredentials _credentials);

    /**
     * @return Returns the history.
     */
    public abstract XFeedEventHistory getHistory();

    /**
     * @param history
     *            The history to set.
     */
    public abstract void setHistory(XFeedEventHistory history);

    /**
     * Add an id3Tag.
     * 
     * @param tag
     */
    public abstract void addId3Tag(ID3TagRewrite tag);

    public abstract void removeId3Tag(ID3TagRewrite tag);

    /**
     * get an id3Tag.
     * 
     * @param name
     * @return ID3TagRewrite Returns <code>null</code> if no tag is found
     *         whith this name.
     */
    public abstract ID3TagRewrite getId3Tag(String name);

    /**
     * @return Iterator on the Tags for Rewrite List
     */
    public abstract Iterator getTagListIterator();

    /**
     * @return Rewrite List
     */
    public abstract void setTagList(List pTagsForRewriteList);

    /**
     * @return Rewrite List
     */
    public abstract List getTagList();

    /**
     * @return Returns the qualityDescription.
     */
    public abstract String getQualityDescription();

    /**
     * @param qualityDescription
     *            The qualityDescription to set.
     */
    public abstract void setQualityDescription(String qualityDescription);
    

}