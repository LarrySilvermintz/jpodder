package com.jpodder.data.feeds.xmlbeans;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.jpodder.data.feeds.IXFeed;
import com.jpodder.data.feeds.IXItem;
import com.jpodder.data.feeds.XFeedException;
import com.jpodder.data.feeds.XItemException;
import com.jpodder.rss20.ImageType;
import com.jpodder.rss20.RssDocument;
import com.jpodder.rss20.TImage;
import com.jpodder.rss20.TRss;
import com.jpodder.rss20.TChannel;
import com.jpodder.rss20.TItem;
import com.jpodder.rss20.CategoryType;

import com.jpodder.util.Util;
import com.jpodder.xml.RSSBinding;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class XFeed implements IXFeed {

	private File mFile;

	/**
	 * The production title. Comment for <code>title</code>
	 */
	protected RssDocument mRssDocument;

	protected boolean modified = false;

	public XFeed(boolean pCreate) {
		this(null, pCreate);
	}

	public XFeed(File pFile, boolean pCreate) {
		if (pFile != null) {
			mFile = pFile;
		}
		if (pCreate) {
			create();
		} else {
			read();
		}
	}

	/**
	 * Create an RSS Object. The Generator and publication date are set.
	 */
	protected void create() {
		mRssDocument = RssDocument.Factory.newInstance();
		TRss rss = mRssDocument.addNewRss();
		rss.setVersion(2.0f);
		TChannel channel = rss.addNewChannel();
		channel.setGenerator("");
		// CB TODO, Should we set the date here? this is the creation date,
		// not publication.
		channel.setPubDate(Util
				.formatDate(new Date(System.currentTimeMillis())));
		modified = true;
	}

	/**
	 * Get the RSS object.
	 * 
	 * @return
	 * @throws XFeedException
	 */
	protected TRss getRss() throws XFeedException {
		TRss rss = getRSSDocument().getRss();
		if (rss != null) {
			return rss;
		} else {
			throw new XFeedException("RSS tag missing");
		}
	}

	public File getFile() {
		return mFile;
	}

	/**
	 * @param file
	 *            The file to set.
	 */
	public void setFile(File pFile) {
		mFile = pFile;
	}

	/**
	 * @return Returns the rss.
	 * @throws XFeedException
	 */
	protected RssDocument getRSSDocument() throws XFeedException {
		if (mRssDocument == null) {
			throw new XFeedException("Document Unknown");
		}
		return mRssDocument;
	}

	/**
	 * @param rss
	 *            The rss from the feed to set.
	 */
	protected void setRSSDocument(XFeed pFeed) {
		mRssDocument = pFeed.mRssDocument;
	}

	protected IXItem getItem(int pIndex) throws XFeedException {
		if (pIndex >= 0 && pIndex < getRSSItems().length) {
			return new XItem(this, pIndex);
		}
		return null;
	}

	/**
	 * Get the items for this feed
	 * 
	 * @return Returns the items.
	 * @throws XFeedException
	 */
	protected TItem[] getRSSItems() throws XFeedException {
		TChannel channel = getRss().getChannel();
		return channel.getItemArray();
	}

	/**
	 * Wrapper method for RSS Item array. (We convert from List to Array).
	 * 
	 * @param items
	 *            The items to set.
	 * @throws XFeedException
	 */
	protected void setRSSItems(ArrayList items) throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		channel.setItemArray((TItem[]) items.toArray());
	}

	/**
	 * Retrun <code>true</code> if the feed file has been parsed and is
	 * available.
	 */
	public boolean isParsed() {
		try {
			getRSSDocument();
			return true;
		} catch (XFeedException e) {
			return false;
		}

	}

	/**
	 * @param pEditor
	 *            The Editor to set.
	 * @throws XFeedException
	 */
	public void setPubDate(String pPubDate) throws XFeedException {
		TRss rss = getRss();
		if (rss != null) {
			TChannel channel = rss.getChannel();
			channel.setPubDate(pPubDate);
			modified = true;
		}
	}

	public String getPubDate() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getPubDate();
	}

	/**
	 * @param pEditor
	 *            The Editor to set.
	 * @throws XFeedException
	 */
	public void setGenerator(String pGenerator) throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		channel.setGenerator(pGenerator);
		modified = true;
	}

	public String getGenerator() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getGenerator();
	}

	/**
	 * @param pEditor
	 *            The Editor to set.
	 * @throws XFeedException
	 */
	public void setCategory(String pCategory) throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		channel.setCategory(pCategory);
		modified = true;
	}

	public String getCategory() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getCategory();
	}

	/**
	 * @param pEditor
	 *            The Editor to set.
	 * @throws XFeedException
	 */
	public void setEditor(String pEditor) throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		channel.setManagingEditor(pEditor);
		modified = true;
	}

	public String getEditor() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getManagingEditor();
	}

	/**
	 * Convenience method.
	 * 
	 * @return Returns the title.
	 * @throws XFeedException
	 */
	public String getTitle() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getTitle();
	}

	/**
	 * @param title
	 *            The title to set.
	 * @throws XFeedException
	 */
	public void setTitle(String pTitle) throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		channel.setTitle(pTitle);
		modified = true;
	}

	/**
	 * @return Returns the link.
	 * @throws XFeedException
	 */
	public String getLink() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getLink();
	}

	/**
	 * @param link
	 *            The link to set.
	 * @throws XFeedException
	 */
	public void setLink(String pLink) throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		channel.setLink(pLink);
		modified = true;
	}

	/**
	 * @return Returns the webmaster.
	 * @throws XFeedException
	 */
	public String getWebmaster() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getWebMaster();
	}

	/**
	 * @param link
	 *            The webmaster to set.
	 * @throws XFeedException
	 */
	public void setWebmaster(String pWebmaster) throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		channel.setWebMaster(pWebmaster);
		modified = true;
	}

	/**
	 * @return Returns the description.
	 * @throws XFeedException
	 */
	public String getDescription() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getDescription();
	}

	/**
	 * @param description
	 *            The description to set.
	 * @throws XFeedException
	 */
	public void setDescription(String pDescription) throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		channel.setDescription(pDescription);
		modified = true;
	}

	/**
	 * @param pCopyright
	 *            The copyright to set.
	 * @throws XFeedException
	 */
	public void setCopyright(String pCopyright) throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		channel.setCopyright(pCopyright);
		modified = true;
	}

	/**
	 * @return Returns the description.
	 * @throws XFeedException
	 */
	public String getCopyright() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getCopyright();
	}

	public String getImage() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		TImage image = channel.getImage();
		if (image != null) {
			return image.getUrl();
		} else {
			return null;
		}
	}

	// ------------------ iToons Tags

	public String getIToonsImage() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		ImageType lImageType = channel.getImage2();
		if (lImageType != null) {
			return lImageType.getHref();
		}
		return null;
	}

	public String getIToonsCategory() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		CategoryType lCategory = channel.getCategory2();
		if (lCategory != null) {
			return lCategory.getText();
		}
		return null;

	}

	public String getIToonsSummary() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getSummary();
	}

	public String getIToonsExplicit() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getExplicit();
	}

	public String getIToonsSubtitle() throws XFeedException {
		TRss rss = getRss();
		TChannel channel = rss.getChannel();
		return channel.getSubtitle();
	}

	public int indexOfItem(IXItem pItem) throws XFeedException {

		TItem[] lRSSItems = getRSSItems();
		for (int i = 0; i < lRSSItems.length; i++) {
			try {
				if (lRSSItems[i].equals(((IXItemData) pItem).getDataSource())) {
					return i;
				}
			} catch (XItemException e) {
				// Something is horribly wrong here.
			}
		}
		return -1;
	}

	public void removeItem(int pIndex) throws XFeedException {
		TItem[] lItems = getRSSItems();
		if (pIndex >= 0 && pIndex < lItems.length) {
			getRss().getChannel().removeItem(pIndex);
			modified = true;
		}

	}

	/**
	 * Add an Item to the RSS Object. The underlying RSS Item & enclosure are
	 * created. The returned wrapper provides easy access to all required
	 * methods.
	 * 
	 * @param this
	 * @param pItem
	 */
	// CB TODO look further if we need this convenience method.
	// public XItemImpl addItem(URL pUrl, String pTitle,
	// String pLink, String pDescription, String pAuthor, int pLength, MIMEType
	// pMIMEType) {
	//        
	// XItemImpl lItem = new XItemImpl(this);
	// lItem.setAuthor(pAuthor);
	// lItem.setTitle(pTitle);
	// lItem.setLink(pLink);
	// lItem.setDescription(pDescription);
	// XEnclosureImpl lEnclosure = lItem.getEnclosure();
	// lEnclosure.setURL(pUrl);
	// lEnclosure.setLength(pLength);
	// lEnclosure.setType(pMIMEType.getMimeType());
	// return lItem;
	// }
	public boolean read() {
		File lFile = getFile();
		if (lFile != null) {
			mRssDocument = RSSBinding.parse(lFile);
			if (mRssDocument != null) {
				return true;
			}
		}
		return false;
	}

	public void write() throws XFeedException {
		File lFile = getFile();
		if (lFile != null) {
			RSSBinding.write(getRSSDocument(), lFile);
			modified = false;
		}
	}

	/**
	 * Get if the feed is modified.
	 * 
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Set the feed to modified.
	 * 
	 * @param pModified
	 */
	public void setModified(boolean pModified) {
		modified = pModified;
	}

	/**
	 * Wraps the internal RSS items into a conveninient iterator. The list is
	 * not saved, but generated every time. TODO potentially save the item list
	 * to avoid recreation.
	 */
	public Iterator getItemIterator() throws XFeedException {
		ArrayList lList = new ArrayList();
		for (int i = 0; i < getRSSItems().length; i++) {
			lList.add(new XItem(this, i));
		}
		return lList.iterator();
	}

	/**
	 * Wraps the internal RSS items into a conveninient
	 * 
	 */
	public Object[] getItemArray() throws XFeedException {
		ArrayList lList = new ArrayList();
		for (int i = 0; i < getRSSItems().length; i++) {
			lList.add(new XItem(this, i));
		}
		return lList.toArray();
	}

	public String toString() {
		try {
			return getTitle() + " : " + getDescription();
		} catch (XFeedException e) {
			return "No title";
		}
	}

	public void release() throws XFeedException {
		mRssDocument = null;
	}

}