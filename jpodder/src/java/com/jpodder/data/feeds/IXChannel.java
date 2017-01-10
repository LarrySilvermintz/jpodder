package com.jpodder.data.feeds;

import java.util.Iterator;

import com.jpodder.data.feeds.xmlbeans.XItem;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public interface IXChannel {

    /**
     * @param pEditor
     *            The Editor to set.
     * @throws XFeedException
     */
    public abstract void setPubDate(String pPubDate) throws XFeedException;

    public abstract String getPubDate() throws XFeedException;

    /**
     * @param pEditor
     *            The Editor to set.
     * @throws XFeedException
     */
    public abstract void setGenerator(String pGenerator) throws XFeedException;

    public abstract String getGenerator() throws XFeedException;

    /**
     * @param pEditor
     *            The Editor to set.
     * @throws XFeedException
     */
    public abstract void setCategory(String pCategory) throws XFeedException;

    public abstract String getCategory() throws XFeedException;

    /**
     * @param pEditor
     *            The Editor to set.
     * @throws XFeedException
     */
    public abstract void setEditor(String pEditor) throws XFeedException;

    public abstract String getEditor() throws XFeedException;

    /**
     * Convenience method.
     * 
     * @return Returns the title.
     * @throws XFeedException
     */
    public abstract String getTitle() throws XFeedException;

    /**
     * @param title
     *            The title to set.
     * @throws XFeedException
     */
    public abstract void setTitle(String pTitle) throws XFeedException;

    /**
     * @return Returns the link.
     * @throws XFeedException
     */
    public abstract String getLink() throws XFeedException;

    /**
     * @param link
     *            The link to set.
     * @throws XFeedException
     */
    public abstract void setLink(String pLink) throws XFeedException;

    /**
     * @return Returns the webmaster.
     * @throws XFeedException
     */
    public abstract String getWebmaster() throws XFeedException;

    /**
     * @param link
     *            The webmaster to set.
     * @throws XFeedException
     */
    public abstract void setWebmaster(String pWebmaster) throws XFeedException;

    /**
     * @return Returns the description.
     * @throws XFeedException
     */
    public abstract String getDescription() throws XFeedException;

    /**
     * @param description
     *            The description to set.
     * @throws XFeedException
     */
    public abstract void setDescription(String pDescription)
            throws XFeedException;

    /**
     * @param pCopyright
     *            The copyright to set.
     * @throws XFeedException
     */
    public abstract void setCopyright(String pCopyright) throws XFeedException;

    /**
     * @return Returns the copyright.
     * @throws XFeedException
     */
    public abstract String getCopyright() throws XFeedException;

    public abstract String getImage() throws XFeedException;
    
    /**
     * iToons specific tags.
     * 
     * @return
     * @throws XFeedException
     */
    public String getIToonsImage() throws XFeedException;
    public String getIToonsSummary() throws XFeedException;
    public String getIToonsExplicit() throws XFeedException;
    public String getIToonsSubtitle() throws XFeedException;
    public String getIToonsCategory() throws XFeedException;
    
}