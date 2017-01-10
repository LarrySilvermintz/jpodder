package com.jpodder.data.feeds;

import java.io.File;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public interface IXFeedDataSource {

	/**
	 * @return get the file.
	 */

	public abstract File getFile();

	/**
	 * @param file The file to set.
	 */
	public abstract void setFile(File file);

	public abstract boolean read();

	public abstract void write() throws XFeedException;

	/**
	 * Get if the feed is modified.
	 * 
	 */
	public abstract boolean isModified();

	/**
	 * Set the feed to modified.
	 * 
	 * @param pModified
	 */
	public abstract void setModified(boolean pModified);

	public boolean isParsed();
}