package com.jpodder.data.feeds;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 */
import java.io.File;
import java.net.URL;
import java.util.Date;

/**
 * A virtual file interface. The file can be an enclosure or a file on the file
 * system.
 */
public interface IXFile {

	/**
	 * The parent feed to which this file belongs.
	 * 
	 * @return Feed
	 */
	public IXPersonalFeed getFeed();

	/**
	 * Set the parent to while this file belongs.
	 * 
	 * @param feed
	 *            Feed
	 */
	public void setFeed(IXPersonalFeed pFeed);

	/**
	 * The length of the file.
	 * 
	 * @return int
	 */
	public int getFileLength();

	/**
	 * Get the URL of the file.
	 * 
	 * @return URL
	 */
	public URL getFileURL();

	/**
	 * Set the URL of the file.
	 * 
	 * @param url
	 *            URL
	 */
	public void setFileURL(URL url);

	/**
	 * Get the file.
	 * 
	 * @return File
	 */
	public File getFile();
	
	
	public File getFile(boolean pRefresh);
	
	/**
	 * Set the file.
	 * 
	 * @param file
	 *            File
	 */
	public void setFile(File file);

	/**
	 * Set the filename of the file. 
	 * The actual file path should be calculated from 
	 * a folder in which the file resides. 
	 * 
	 * @param pName
	 */
	public void setFileName(String pName);

	/**
	 * Marking is context sensitive. For enclosures it has a different meaning
	 * then for files.
	 * 
	 * @return boolean
	 */
	public boolean isMarked();

	/**
	 * 
	 * @param isMarked
	 *            boolean
	 */
	public void setMarked(boolean isMarked);

	/**
	 * 
	 * @return boolean
	 */
	public boolean isCached();

	/**
	 * 
	 * @param inCache
	 *            boolean
	 */
	public void setCached(boolean inCache);

	/**
	 * 
	 * @return boolean
	 */
	public boolean getInPlayer();

	/**
	 * 
	 * @param inPlayer
	 *            boolean
	 */
	public void setInPlayer(boolean inPlayer);

	/**
	 * Get if the file is stored locally.
	 * 
	 * @return boolean
	 */
	public boolean isLocal();

	/**
	 * The if the file is stored on the local storage
	 * 
	 * @param local
	 *            boolean
	 */
	public void setLocal(boolean local);

	/**
	 * Get a date object.
	 * 
	 * @return String
	 */
	public Date getDate();

	/**
	 * Get the file name.
	 * 
	 * @return String
	 */
	public String getName();

	/**
	 * Get the MIME type of the file.
	 * 
	 * @return String The MIME type expressed as a String
	 */
	public String getFileType();

	/**
	 * Compare two objects on the date.
	 * 
	 * @param o
	 *            Object
	 * @return int
	 */
	public int compareTo(Object o);

	/**
	 * Set that the file is now inspected.
	 */
	public void setInspected();

	/**
	 * Check if a file is inspected.
	 * 
	 * @return
	 */
	public boolean isInspected();

}
