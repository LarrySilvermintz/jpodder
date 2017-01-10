package com.jpodder.data.feeds.nano;

import net.n3.nanoxml.IXMLElement;

import com.jpodder.data.feeds.IXEnclosure;
import com.jpodder.data.feeds.IXFeed;
import com.jpodder.data.feeds.IXItem;
import com.jpodder.data.feeds.XFeedException;
import com.jpodder.data.feeds.XItemException;

/**
 * 
 * <pre>
 * <xs:element type="xs:string" name="title" xmlns:xs="http://www.w3.org/2001/XMLSchema"/>
 * <xs:element type="xs:anyURI" name="link" xmlns:xs="http://www.w3.org/2001/XMLSchema"/>
 * <xs:element type="xs:string" name="description" xmlns:xs="http://www.w3.org/2001/XMLSchema"/>
 * <xs:element type="xs:string" name="author" xmlns:xs="http://www.w3.org/2001/XMLSchema"/>
 * <xs:element type="xs:string" name="pubDate" xmlns:xs="http://www.w3.org/2001/XMLSchema"/>
 * <xs:element type="xs:string" name="category" xmlns:xs="http://www.w3.org/2001/XMLSchema"/>
 * <xs:element type="xs:anyURI" name="comments" xmlns:xs="http://www.w3.org/2001/XMLSchema"/>
 * 
 * </br>
 * iTunes elements.
 * <xs:element ref="pod:author" xmlns:pod="http://www.itunes.com/dtds/podcast-1.0.dtd"/>
 * <xs:element ref="pod:subtitle" xmlns:pod="http://www.itunes.com/dtds/podcast-1.0.dtd"/>
 * <xs:element ref="pod:summary" xmlns:pod="http://www.itunes.com/dtds/podcast-1.0.dtd"/>
 * <xs:element ref="pod:explicit" xmlns:pod="http://www.itunes.com/dtds/podcast-1.0.dtd"/>
 * <xs:element ref="pod:duration" xmlns:pod="http://www.itunes.com/dtds/podcast-1.0.dtd"/>
 * </pre>
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */

/**
 * This class wraps an Item model for temporary use. It is not a replication of
 * the model. So we either take en Item model from the Feed or we create one.
 * The Item model is there for always present.
 */
public class XItem implements IXItem, IXItemData {

	String RSS_ITEM_PUBDATE = "pubDate";

	String RSS_ITEM_TITLE = "title";

	String RSS_ITEM_DESCRIPTION = "description";

	String RSS_ITEM_LINK = "link";

	String RSS_ITEM_AUTHOR = "author";

	String RSS_ITEM_CAT = "category";

	String RSS_ITEM_COM = "comments";

	String RSS_ITEM_ENCL = "enclosure";

	String RSS_ITEM_IT_SUMMRY = "summary";

	String RSS_ITEM_IT_AUTHOR = "author";

	String RSS_ITEM_IT_explicit = "explicit";

	String RSS_ITEM_IT_SUB = "subtitle";

	String RSS_ITEM_IT_DURATION = "duration";

	protected IXMLElement mDataItem;

	protected IXFeed mFeed;

	protected IXEnclosure mEnclosure;

	public XItem(XFeed pFeed) {
		this(pFeed, -1);
	}

	/**
	 */
	public XItem(XFeed pFeed, int pIndex) {
		if (pFeed == null) {
			throw new IllegalArgumentException();
		} else {
			mFeed = pFeed;
			IXMLElement[] lRSSItems;
			try {
				lRSSItems = pFeed.getRSSItems();
				if (pIndex >= 0 && pIndex < lRSSItems.length) {
					mDataItem = lRSSItems[pIndex];
					setDataSource(mDataItem);
					try {
						mEnclosure = getEnclosure();
					} catch (XItemException e) {
					}
				} else {
					// CB TODO, Add an item.
					// mDataItem = pFeed.getRss().getChannel().addNewItem();
					// TGuid guid = mDataItem.addNewGuid();
					// guid.setIsPermaLink(false); // No idea what this means.
					// guid.setStringValue(""); // TODO Need to hash from unique
					// // field.
					// mDataItem.setCategory("podcast");
					// String pubDate = Util.formatDate(new Date(System
					// .currentTimeMillis()));
					// mDataItem.setPubDate(pubDate);
					// mDataItem.addNewEnclosure();
					// pFeed.modified = true;
				}
			} catch (XFeedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setDataSource(Object pDataSource) {
		if (!(pDataSource instanceof IXMLElement)) {
			throw new IllegalArgumentException("Wrong data source type"
					+ pDataSource);
		} else {
			mDataItem = (IXMLElement) pDataSource;
		}
	}

	public IXMLElement getDataSource() throws XItemException {
		if (mDataItem == null) {
			throw new XItemException(ITEM_DETACHED);
		}
		return mDataItem;
	}

	/**
	 * Set the item publication date.
	 * 
	 * @param pubDate
	 *            The pubDate to set.
	 * @throws XItemException
	 */
	public void setPubDate(String pPubDate) throws XItemException {

		IXMLElement lSrcObj = getDataSource();
		IXMLElement lPubDateObj = lSrcObj.getFirstChildNamed(RSS_ITEM_PUBDATE);
		if (lPubDateObj != null) {
			lPubDateObj.setContent(pPubDate);
			mFeed.setModified(true);
		}
	}

	/**
	 * @return Returns the pubDate.
	 */
	public String getPubDate() throws XItemException {
		IXMLElement lSrcObj = getDataSource();
		IXMLElement lPubDateObj = lSrcObj.getFirstChildNamed(RSS_ITEM_PUBDATE);
		if (lPubDateObj != null) {
			return lPubDateObj.getContent();
		}
		throw new XItemException();
	}

	/**
	 * Set the item description
	 * 
	 * @param description
	 *            The description to set.
	 * @throws XItemException
	 */
	public void setDescription(String pDescription) throws XItemException {
		IXMLElement lSrcObj = getDataSource();
		IXMLElement lDescriptionObj = lSrcObj
				.getFirstChildNamed(RSS_ITEM_DESCRIPTION);
		if (lDescriptionObj != null) {
			lDescriptionObj.setContent(pDescription);
			mFeed.setModified(true);
		}
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() throws XItemException {
		IXMLElement lSrcObj = getDataSource();
		IXMLElement lDescriptionObj = lSrcObj
				.getFirstChildNamed(RSS_ITEM_DESCRIPTION);
		if (lDescriptionObj != null) {
			return lDescriptionObj.getContent();
		}
		throw new XItemException();
	}

	/**
	 * @param title
	 *            The title to set.
	 * @throws XItemException
	 */
	public void setTitle(String pTitle) throws XItemException {
		IXMLElement lSrcObj = getDataSource();
		IXMLElement lTitleObj = lSrcObj.getFirstChildNamed(RSS_ITEM_TITLE);
		if (lTitleObj != null) {
			lTitleObj.setContent(pTitle);
			mFeed.setModified(true);
		}
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() throws XItemException {
		IXMLElement lSrcObj = getDataSource();
		IXMLElement lTitleObj = lSrcObj.getFirstChildNamed(RSS_ITEM_TITLE);
		if (lTitleObj != null) {
			return lTitleObj.getContent();
		}
		throw new XItemException();
	}

	/**
	 * @param author
	 *            The author to set.
	 * @throws XItemException
	 */
	public void setAuthor(String pAuthor) throws XItemException {
		IXMLElement lSrcObj = getDataSource();
		IXMLElement lAuthorObj = lSrcObj.getFirstChildNamed(RSS_ITEM_AUTHOR);
		if (lAuthorObj != null) {
			lAuthorObj.setContent(pAuthor);
			mFeed.setModified(true);
		}
	}

	/**
	 * @return Returns the author.
	 */
	public String getAuthor() throws XItemException {
		IXMLElement lSrcObj = getDataSource();
		IXMLElement lAuthorObj = lSrcObj.getFirstChildNamed(RSS_ITEM_AUTHOR);
		if (lAuthorObj != null) {
			return lAuthorObj.getContent();
		}
		throw new XItemException();
	}

	/**
	 * @param link
	 *            The link to set.
	 * @throws XItemException
	 */
	public void setLink(String pLink) throws XItemException {
		IXMLElement lSrcObj = getDataSource();
		IXMLElement lLinkObj = lSrcObj.getFirstChildNamed(RSS_ITEM_LINK);
		if (lLinkObj != null) {
			lLinkObj.setContent(pLink);
			mFeed.setModified(true);
		}
	}

	/**
	 * @return Returns the link.
	 */
	public String getLink() throws XItemException {
		IXMLElement lSrcObj = getDataSource();
		IXMLElement lLinkObj = lSrcObj.getFirstChildNamed(RSS_ITEM_LINK);
		if (lLinkObj != null) {
			return lLinkObj.getContent();
		}
		throw new XItemException();
	}

	/**
	 * Create a new public Enclosure object from this item.
	 * 
	 * @return XEnclosure the enclosure or <code>null</code> if this item
	 *         doesn't contain enclosures.
	 */
	public IXEnclosure getEnclosure() throws XItemException {

		IXMLElement lSrcObj = getDataSource();
		IXMLElement lEnclObj = lSrcObj.getFirstChildNamed(RSS_ITEM_ENCL);
		if (lEnclObj != null) {
			XEnclosure mEnclosure = new XEnclosure(mFeed, this);
			mEnclosure.setDataSource(lEnclObj);
			return mEnclosure;
		} else {
			throw new XItemException("No Enclosure for this item");
		}
	}

	public String toString() {
		try {
			return getTitle();
		} catch (XItemException e) {
			return e.getMessage();
		}
	}

	/**
	 * Return if the Item has an enclosure.
	 * 
	 * @return boolean Return <code>true</code> if the item has an enclosure.
	 */
	public boolean hasEnclosure() throws XItemException {

		IXMLElement lSrcObj = getDataSource();
		IXMLElement lEnclObj = lSrcObj.getFirstChildNamed(RSS_ITEM_ENCL);

		if (lEnclObj != null) {
			return true;
		} else {
			return false;
		}
	}

	public void setEnclosure(IXEnclosure pEnclosure) {
		// CB TODO, Should copy in the sub elements when setting an enclosure.
		// Cloning function.
		// IXEnclosure lEnclosure = getEnclosure();
	}

	public boolean equals(IXItem pItem) {

		try {
			if (pItem.getTitle() != null && getTitle() != null
					&& pItem.getTitle().equals(getTitle())) {
				return true;
			} else {
				return false;
			}
		} catch (XItemException e) {
			return false;
		}
	}

	public void releaseDataSource() {
		mDataItem = null;
	}
}