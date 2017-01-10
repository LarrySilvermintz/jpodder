package com.jpodder.data.feeds.xmlbeans;

import java.util.Date;

import com.jpodder.data.feeds.IXEnclosure;
import com.jpodder.data.feeds.IXFeed;
import com.jpodder.data.feeds.IXItem;
import com.jpodder.data.feeds.XFeedException;
import com.jpodder.data.feeds.XItemException;
import com.jpodder.rss20.TEnclosure;
import com.jpodder.rss20.TGuid;
import com.jpodder.rss20.TItem;
import com.jpodder.util.Util;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */

/**
 * This class wraps an Item model for temporary use. It is not a replication of
 * the model. So we either take en Item model from the Feed or we create one.
 * The Item model is there for always present.
 */
public class XItem implements IXItem, IXItemData {

	protected TItem mDataItem;

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
			TItem[] lRSSItems;
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
					mDataItem = pFeed.getRss().getChannel().addNewItem();
					TGuid guid = mDataItem.addNewGuid();
					guid.setIsPermaLink(false); // No idea what this means.
					guid.setStringValue(""); // TODO Need to hash from unique
					// field.
					mDataItem.setCategory("podcast");
					String pubDate = Util.formatDate(new Date(System
							.currentTimeMillis()));
					mDataItem.setPubDate(pubDate);
					mDataItem.addNewEnclosure();
					pFeed.modified = true;
				}
			} catch (XFeedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setDataSource(Object pDataSource) {
		if (!(pDataSource instanceof TItem)) {
			throw new IllegalArgumentException("Wrong data source type"
					+ pDataSource);
		} else {
			mDataItem = (TItem) pDataSource;
		}
	}

	public TItem getDataSource() throws XItemException {
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
		getDataSource().setPubDate(pPubDate);
		mFeed.setModified(true);
	}

	/**
	 * @return Returns the pubDate.
	 */
	public String getPubDate() throws XItemException {
		if (getDataSource().getPubDate() != null) {
			return getDataSource().getPubDate();
		} else {
			throw new XItemException(ITEM_TAG_NOT_DEFINED);
		}
	}

	/**
	 * Set the item description
	 * 
	 * @param description
	 *            The description to set.
	 * @throws XItemException
	 */
	public void setDescription(String pDescription) throws XItemException {
		getDataSource().setDescription(pDescription);
		mFeed.setModified(true);
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() throws XItemException {
		if (getDataSource().getDescription() != null) {
			return getDataSource().getDescription();
		} else {
			throw new XItemException(ITEM_TAG_NOT_DEFINED);
		}
	}

	/**
	 * @param title
	 *            The title to set.
	 * @throws XItemException
	 */
	public void setTitle(String pTitle) throws XItemException {
		getDataSource().setTitle(pTitle);
		mFeed.setModified(true);
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() throws XItemException {
		if (getDataSource().getTitle() != null) {
			return getDataSource().getTitle();
		} else {
			throw new XItemException(ITEM_TAG_NOT_DEFINED);
		}
	}

	/**
	 * @param author
	 *            The author to set.
	 * @throws XItemException
	 */
	public void setAuthor(String pAuthor) throws XItemException {
		getDataSource().setAuthor(pAuthor);
		mFeed.setModified(true);
	}

	/**
	 * @return Returns the author.
	 */
	public String getAuthor() throws XItemException {
		if (getDataSource().getAuthor() != null) {
			return getDataSource().getAuthor();
		} else {
			throw new XItemException(ITEM_TAG_NOT_DEFINED);
		}
	}

	/**
	 * @param link
	 *            The link to set.
	 * @throws XItemException
	 */
	public void setLink(String pLink) throws XItemException {
		getDataSource().setLink(pLink);
		mFeed.setModified(true);
	}

	/**
	 * @return Returns the link.
	 */
	public String getLink() throws XItemException {

		if (getDataSource().getLink() != null) {
			return getDataSource().getLink();
		} else {
			throw new XItemException(ITEM_TAG_NOT_DEFINED);
		}

	}

	/**
	 * Create a new public Enclosure object from this item.
	 * 
	 * @return XEnclosure the enclosure or <code>null</code> if this item
	 *         doesn't contain enclosures.
	 */
	public IXEnclosure getEnclosure() throws XItemException {
		if( getRSSEnclosure() != null){
			XEnclosure mEnclosure = new XEnclosure(mFeed, this);
			mEnclosure.setDataSource(getRSSEnclosure());
			return mEnclosure;
		}else{
			throw new XItemException("No Enclosure for this item");
		}
	}

	// Rethink, why we need this?
	// Access to the sub-model is always through it's parent.

	/**
	 * Access a nested model.
	 * 
	 * @return
	 * @throws XItemException
	 */
	protected TEnclosure getRSSEnclosure() throws XItemException {
		return getDataSource().getEnclosure();
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
			if (getRSSEnclosure() != null) {
				return true;
			} else {
				return false;
			}
	}

	public void setEnclosure(IXEnclosure pEnclosure) {
		// CB TODO, Should copy in the sub elements when setting an enclosure. 
		// Cloning function. 
		//		IXEnclosure lEnclosure = getEnclosure();
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
		// TODO Auto-generated method stub
		
	}
}