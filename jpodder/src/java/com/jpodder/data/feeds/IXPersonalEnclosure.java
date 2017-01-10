// CB TODO CLEAN HEADINFO OBJECT FROM ENCLOSURE, ONLY KEEP: 
// Length + Date of the Url.

package com.jpodder.data.feeds;

import java.io.File;
import java.net.URL;

import com.jpodder.JPodderException;

//import com.jpodder.net.NetHEADInfo;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public interface IXPersonalEnclosure extends IXEnclosure, IXFile {

	/**
	 * The size of the content.
	 * 
	 * @return
	 */
	public int getContentSize();

	/**
	 * Set the size of the content. (this is is not the size as retrieved from
	 * RSS, but as returned from the HTTP request).
	 * 
	 * @param pSize
	 */
	public void setContentSize(int pSize);

	/**
	 * Set the date of the content (this is is not the date as retrieved from
	 * RSS, but as returned from the HTTP request).
	 * @param pDate
	 */
	public void setContentDate(long pDate);

	/**
	 * The Content date.
	 * @return
	 */
	public long getContentDate();

	/**
	 * Get the personal enclosure URL.
	 * 
	 * @return URL
	 */
	public URL getPersonalURL();

	/**
	 * set the personal enclosure URL.
	 * 
	 * @return URL
	 */
	public void setPersonalURL(URL pUrl);

	/**
	 * Set candidate for download. (Used by highlighter).
	 * 
	 * @param candidate
	 *            boolean
	 */
	public void setCandidate(boolean pCandidate);

	/**
	 * Get if candidate for download. (Used by highlighter).
	 * 
	 * @return boolean
	 */
	public boolean isCandidate();

	/**
	 * Set the HTTP HEAD Information. The HEAD Information contains valuable
	 * data from the HTTP Server. These are the modified date etc.
	 * 
	 * @param head
	 * @see NetHEADInfo
	 */
	// public void setHEADInfo(NetHEADInfo head);
	/**
	 * Get the HTTP HEAD information.
	 * 
	 * @return NetHEADInfo
	 * @see NetHEADInfo
	 */
	// public NetHEADInfo getHEADInfo();
	/**
	 * Get if this enclosure is a torrent file.
	 * 
	 * @return boolean
	 */
	public boolean isTorrent();

	/**
	 * Set this enclosure as torrentFile.
	 */
	public void setTorrent();

	/**
	 * Get the file to which the torrent enclosure will be/has been downloaded.
	 * 
	 * @param torrentFile
	 *            File
	 */
	public void setTorrentFile(File pTorrentFile);

	/**
	 * Get the file to which the torrent enclosure will be/has been downloaded.
	 * 
	 * @return File the bitTorrent file.
	 */
	public File getTorrentFile();

	/**
	 * Return if the file is incomplete. This method compares the file length on
	 * the local storage with the length in the HTTP Header. An exact match of
	 * the size in bytes is needed.
	 * 
	 * @deprecated
	 * @throws Exception
	 * @return boolean
	 * @throws JPodderException
	 */
	public boolean isIncomplete() throws JPodderException;

	/**
	 * Set the completion status for a downloaded enclosure. This flag is set by
	 * the Network engine, when an end of a stream is reached. If reading is
	 * abrupted prematurly, this flag is off.
	 * 
	 * @param pCompleted
	 */
	public void setDownloadCompleted(boolean pCompleted);

	/**
	 * Get the completion status for a downloaded enclosure.
	 * 
	 * @return
	 */
	public boolean isDownloadCompleted();

}
