package com.jpodder.data.feeds;

import java.net.URL;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public interface IXEnclosure {
    
	String ENCLOSURE_DETACHED = "The RSS Enclosure model is not set";

    /**
     * Set the parent Item to attach 
     * the enclosure to an RSS Item. 
     * @param pItem
     */
    public void setItem(IXItem pItem);
    
    /**
     * Get the attached parent Item.
     * @return
     * @throws XEnclosureException
     */
    public IXItem getItem() throws XEnclosureException;
    
    /**
     * Set the enclosure URL.
     * @param url URL
     * @throws XEnclosureException
     */
    public abstract void setURL(URL url) throws XEnclosureException;

    /**
     * Set the enclosure MIME type. MIME is generally loosly defined. See: For
     * MIME types.
     * 
     * @param type String
     * @throws XEnclosureException
     */
    public abstract void setType(String type) throws XEnclosureException;

    /**
     * Get the enclosure type.
     * 
     * @return String
     * @throws XEnclosureException
     */
    public abstract String getType() throws XEnclosureException;

    /**
     * Get the enclosure Length
     * 
     * @return
     * @throws XEnclosureException
     */
    public abstract long getLength() throws XEnclosureException;

    /**
     * Set the enclosure length.
     * 
     * @param pLength
     * @throws XEnclosureException
     */
    public abstract void setLength(long pLength) throws XEnclosureException;

    /**
     * Get the enclosure URL.
     * 
     * @return URL
     * @throws XEnclosureException
     */
    public abstract URL getURL() throws XEnclosureException;

    /**
     * Return the URL decoded file name
     * 
     * @return String
     */
    public abstract String getName();
    
}