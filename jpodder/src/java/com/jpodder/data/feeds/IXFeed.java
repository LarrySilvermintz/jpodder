package com.jpodder.data.feeds;

import java.io.File;
import java.util.Iterator;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public interface IXFeed extends IXChannel {

	// Define Java Active Relations
	// ActiveRelation: hasOne Channel.

	// ActiveRelation: hasAndBelongsToMany Items.
	public abstract int indexOfItem(IXItem pItem) throws XFeedException;

	public abstract void removeItem(int pIndex) throws XFeedException;

	public abstract Iterator getItemIterator() throws XFeedException;

	public abstract Object[] getItemArray() throws XFeedException;

	public abstract File getFile();

	/**
	 * @param file
	 *            The file to set.
	 */
	public abstract void setFile(File file);

	/**
	 * Parse the model from file. ( Assuming it's RSS). 
	 * 
	 * @param this
	 * @param pItem
	 */
	public abstract boolean read();

	/**
	 * Write the XML model to file.
	 * @throws XFeedException
	 */
	public abstract void write() throws XFeedException;

	/**
	 * Release is the model. (Including sub models).
	 *  
	 * @throws XFeedException
	 */
	public abstract void release() throws XFeedException;

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