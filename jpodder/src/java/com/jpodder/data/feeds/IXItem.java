package com.jpodder.data.feeds;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public interface IXItem {
    
	String ITEM_DETACHED = "The RSS Item model is not set";
	String ITEM_TAG_NOT_DEFINED = "The request tag in item is not defined.";
	
	/**
     * Set the item publication date.
     * 
     * @param pubDate
     *            The pubDate to set.
	 * @throws XItemException 
     */
    public abstract void setPubDate(String pPubDate) throws XItemException;

    /**
     * @return Returns the pubDate.
     */
    public abstract String getPubDate() throws XItemException;

    /**
     * Set the item description
     * 
     * @param description
     *            The description to set.
     * @throws XItemException 
     */
    public abstract void setDescription(String pDescription) throws XItemException;

    /**
     * @return Returns the description.
     */
    public abstract String getDescription() throws XItemException;

    /**
     * @param title
     *            The title to set.
     * @throws XItemException 
     */
    public abstract void setTitle(String pTitle) throws XItemException;

    /**
     * @return Returns the title.
     */
    public abstract String getTitle() throws XItemException;

    /**
     * @param author
     *            The author to set.
     * @throws XItemException 
     */
    public abstract void setAuthor(String pAuthor) throws XItemException;

    /**
     * @return Returns the author.
     */
    public abstract String getAuthor() throws XItemException;

    /**
     * @param link
     *            The link to set.
     * @throws XItemException 
     */
    public abstract void setLink(String pLink) throws XItemException;

    /**
     * @return Returns the link.
     */
    public abstract String getLink() throws XItemException;

    
    /**
     * Get the encosure 
     * 
     * @return IXEnclosure
     * @throws XItemException 
     */
    public abstract IXEnclosure getEnclosure() throws XItemException;
    
    /**
     * Set a the enclosure for this Item.
     * 
     * @param pEnclosure
     */
    public abstract void setEnclosure(IXEnclosure pEnclosure);    
    
    public abstract boolean hasEnclosure() throws XItemException;
    
    public boolean equals(IXItem pItem);
    
}