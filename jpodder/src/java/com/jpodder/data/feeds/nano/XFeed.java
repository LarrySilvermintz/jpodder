package com.jpodder.data.feeds.nano;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import net.n3.nanoxml.IXMLElement;

import com.jpodder.data.feeds.IXFeed;
import com.jpodder.data.feeds.IXItem;
import com.jpodder.data.feeds.XFeedException;
import com.jpodder.data.feeds.XItemException;
import com.jpodder.xml.NanoXML;

/**
 * <br/>RSS 2.0 elements
 * 
 * <pre>
 *                 &lt;xs:element type=&quot;xs:string&quot; name=&quot;title&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *                 &lt;xs:element type=&quot;xs:anyURI&quot; name=&quot;link&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *                 &lt;xs:element type=&quot;xs:string&quot; name=&quot;description&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *                 &lt;xs:element type=&quot;xs:string&quot; name=&quot;generator&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *                 &lt;xs:element type=&quot;xs:anyURI&quot; name=&quot;docs&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *                 &lt;xs:element type=&quot;xs:string&quot; name=&quot;language&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *                 &lt;xs:element type=&quot;xs:string&quot; name=&quot;copyright&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *                 &lt;xs:element type=&quot;xs:string&quot; name=&quot;managingEditor&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *               &lt;xs:element type=&quot;xs:string&quot; name=&quot;webMaster&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *     			&lt;xs:element type=&quot;xs:string&quot; name=&quot;pubDate&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *                    &lt;xs:element type=&quot;xs:string&quot; name=&quot;lastBuildDate&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *                    &lt;xs:element type=&quot;xs:string&quot; name=&quot;category&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *                    &lt;xs:element type=&quot;xs:short&quot; name=&quot;ttl&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *                   &lt;xs:element type=&quot;xs:anyURI&quot; name=&quot;url&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *             
 *             &lt;xs:element type=&quot;xs:string&quot; name=&quot;title&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *             &lt;xs:element type=&quot;xs:anyURI&quot; name=&quot;link&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *             &lt;xs:element type=&quot;xs:short&quot; name=&quot;width&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *             &lt;xs:element type=&quot;xs:short&quot; name=&quot;height&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;/&gt;
 *            
 * </pre>
 * 
 * <br/>ITunes elements
 * 
 * <pre>
 *                     &lt;xs:element ref=&quot;pod:author&quot; xmlns:pod=&quot;http://www.itunes.com/dtds/podcast-1.0.dtd&quot;/&gt;
 *                     &lt;xs:element ref=&quot;pod:subtitle&quot; xmlns:pod=&quot;http://www.itunes.com/dtds/podcast-1.0.dtd&quot;/&gt;
 *                     &lt;xs:element ref=&quot;pod:summary&quot; xmlns:pod=&quot;http://www.itunes.com/dtds/podcast-1.0.dtd&quot;/&gt;
 *                     &lt;xs:element ref=&quot;pod:keywords&quot; xmlns:pod=&quot;http://www.itunes.com/dtds/podcast-1.0.dtd&quot;/&gt;
 *                     &lt;xs:element ref=&quot;pod:explicit&quot; xmlns:pod=&quot;http://www.itunes.com/dtds/podcast-1.0.dtd&quot;/&gt;
 *                     &lt;xs:element ref=&quot;pod:image&quot; xmlns:pod=&quot;http://www.itunes.com/dtds/podcast-1.0.dtd&quot;/&gt;
 *                     &lt;xs:element ref=&quot;pod:owner&quot; xmlns:pod=&quot;http://www.itunes.com/dtds/podcast-1.0.dtd&quot;/&gt;
 *                     &lt;xs:element ref=&quot;pod:block&quot; xmlns:pod=&quot;http://www.itunes.com/dtds/podcast-1.0.dtd&quot;/&gt;
 *                     &lt;xs:element ref=&quot;pod:category&quot; xmlns:pod=&quot;http://www.itunes.com/dtds/podcast-1.0.dtd&quot;/&gt;
 * </pre>
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class XFeed implements IXFeed {

	private File mFile;

	String RSS_CHANNEL = "channel";

	String RSS_CHANNEL_TITLE = "title";

	String RSS_CHANNEL_LINK = "link";

	String RSS_CHANNEL_DESCRIPTION = "description";

	String RSS_CHANNEL_GEN = "generator";

	String RSS_CHANNEL_DOCS = "docs";

	String RSS_CHANNEL_LANGUAGE = "language";

	String RSS_CHANNEL_COPYRIGHT = "copyright";

	String RSS_CHANNEL_WEBMASTER = "webMaster";

	String RSS_CHANNEL_BUILD = "lastBuildDate";

	String RSS_CHANNEL_PUBDATE = "pubDate";

	String RSS_CHANNEL_EDITOR = "managingEditor";

	String RSS_CHANNEL_CATEGORY = "category";

	String RSS_CHANNEL_TTL = "ttl";

	String RSS_CHANNEL_ITEM = "item";

	String RSS_CHANNEL_IMAGE = "image";

	String RSS_IMAGE_URL = "url";

	// ITunes tags.
	String ITUNES_NS = "http://www.itunes.com/dtds/podcast-1.0.dtd";

	String RSS_CHANNEL_IT_IMAGE = "image";

	String RSS_CHANNEL_IT_AUTHOR = "author";

	String RSS_CHANNEL_IT_SUMMARY = "summary";

	String RSS_CHANNEL_IT_EXPLICIT = "explicit";

	String RSS_CHANNEL_IT_SUB = "subtitle";

	String RSS_CHANNEL_IT_CATEGORY = "category";

	String RSS_CHANNEL_IT_IMAGE_HREF = "href";

	String RSS_CHANNEL_IT_IMAGE_TYPE = "type";

	String RSS_CHANNEL_IT_IMAGE_REL = "rel";

	protected IXMLElement mRSSDocument;

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
		// mRssDocument = RssDocument.Factory.newInstance();
		// TRss rss = mRssDocument.addNewRss();
		// rss.setVersion(2.0f);
		// TChannel channel = rss.addNewChannel();
		// channel.setGenerator("");
		// // CB TODO, Should we set the date here? this is the creation date,
		// // not publication.
		// channel.setPubDate(Util
		// .formatDate(new Date(System.currentTimeMillis())));
		// modified = true;
	}

	private IXMLElement getChannel() throws XFeedException {
		
		if(mRSSDocument == null){
			throw new XFeedException("RSS Data model disconnected");
		}
		
		IXMLElement lChannel = mRSSDocument.getFirstChildNamed(RSS_CHANNEL);
		if (lChannel != null) {
			return lChannel;
		}
		throw new XFeedException();
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
	protected IXMLElement getRSSDocument() throws XFeedException {
		if (mRSSDocument == null) {
			throw new XFeedException("Document Unknown");
		}
		return mRSSDocument;
	}

	/**
	 * @param rss
	 *            The rss from the feed to set.
	 */
	protected void setRSSDocument(XFeed pFeed) {
		mRSSDocument = pFeed.mRSSDocument;
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
	protected IXMLElement[] getRSSItems() throws XFeedException {
		Vector lItems = getChannel().getChildrenNamed(RSS_CHANNEL_ITEM);
		IXMLElement[] lItemsArray = new IXMLElement[lItems.size()];
		lItems.copyInto(lItemsArray);
		return lItemsArray;
	}

	/**
	 * Wrapper method for RSS Item array. (We convert from List to Array).
	 * 
	 * @param items
	 *            The items to set.
	 * @throws XFeedException
	 */
	protected void setRSSItems(ArrayList items) throws XFeedException {
		// CB TODO, Set Items.
		// TRss rss = getRss();
		// TChannel channel = rss.getChannel();
		// channel.setItemArray((TItem[]) items.toArray());

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
		IXMLElement channel = getChannel();
		IXMLElement lPubDateObj = channel
				.getFirstChildNamed(RSS_CHANNEL_PUBDATE);
		if (lPubDateObj != null) {
			lPubDateObj.setContent(pPubDate);
			modified = true;
		}
	}

	public String getPubDate() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lPubDateObj = channel
				.getFirstChildNamed(RSS_CHANNEL_PUBDATE);
		if (lPubDateObj != null) {
			return lPubDateObj.getContent();
		}
		throw new XFeedException();

	}

	/**
	 * @param pEditor
	 *            The Editor to set.
	 * @throws XFeedException
	 */
	public void setGenerator(String pGenerator) throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lGeneratorObj = channel.getFirstChildNamed(RSS_CHANNEL_GEN);
		if (lGeneratorObj != null) {
			lGeneratorObj.setContent(pGenerator);
			modified = true;
		}
	}

	public String getGenerator() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lGenObj = channel.getFirstChildNamed(RSS_CHANNEL_GEN);
		if (lGenObj != null) {
			return lGenObj.getContent();
		}
		throw new XFeedException();
	}

	/**
	 * @param pEditor
	 *            The Editor to set.
	 * @throws XFeedException
	 */
	public void setCategory(String pCategory) throws XFeedException {
		// TODO.
	}

	public String getCategory() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lCategoryObj = channel
				.getFirstChildNamed(RSS_CHANNEL_CATEGORY);
		if (lCategoryObj != null) {
			return lCategoryObj.getContent();
		}
		throw new XFeedException();
	}

	/**
	 * @param pEditor
	 *            The Editor to set.
	 * @throws XFeedException
	 */
	public void setEditor(String pEditor) throws XFeedException {
		// TODO
	}

	public String getEditor() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lEditorObj = channel.getFirstChildNamed(RSS_CHANNEL_EDITOR);
		if (lEditorObj != null) {
			return lEditorObj.getContent();
		}
		throw new XFeedException();
	}

	/**
	 * Convenience method.
	 * 
	 * @return Returns the title.
	 * @throws XFeedException
	 */
	public String getTitle() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lTitleObj = channel.getFirstChildNamed(RSS_CHANNEL_TITLE);
		if (lTitleObj != null) {
			return lTitleObj.getContent();
		}
		throw new XFeedException();

	}

	/**
	 * @param title
	 *            The title to set.
	 * @throws XFeedException
	 */
	public void setTitle(String pTitle) throws XFeedException {
		// TODO.
	}

	/**
	 * @return Returns the link.
	 * @throws XFeedException
	 */
	public String getLink() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lLinkObj = channel.getFirstChildNamed(RSS_CHANNEL_LINK);
		if (lLinkObj != null) {
			return lLinkObj.getContent();
		}
		throw new XFeedException();
	}

	/**
	 * @param link
	 *            The link to set.
	 * @throws XFeedException
	 */
	public void setLink(String pLink) throws XFeedException {
		// TODO.
	}

	/**
	 * @return Returns the webmaster.
	 * @throws XFeedException
	 */
	public String getWebmaster() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lWebMasterObj = channel
				.getFirstChildNamed(this.RSS_CHANNEL_WEBMASTER);
		if (lWebMasterObj != null) {
			return lWebMasterObj.getContent();
		}
		throw new XFeedException();

	}

	/**
	 * @param link
	 *            The webmaster to set.
	 * @throws XFeedException
	 */
	public void setWebmaster(String pWebmaster) throws XFeedException {
		// TODO.
	}

	/**
	 * @return Returns the description.
	 * @throws XFeedException
	 */
	public String getDescription() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lDescriptionObj = channel
				.getFirstChildNamed(RSS_CHANNEL_DESCRIPTION);
		if (lDescriptionObj != null) {
			return lDescriptionObj.getContent();
		}
		throw new XFeedException();
	}

	/**
	 * @param description
	 *            The description to set.
	 * @throws XFeedException
	 */
	public void setDescription(String pDescription) throws XFeedException {
		// TODO.
	}

	/**
	 * @param pCopyright
	 *            The copyright to set.
	 * @throws XFeedException
	 */
	public void setCopyright(String pCopyright) throws XFeedException {
		// TODO.
	}

	/**
	 * @return Returns the description.
	 * @throws XFeedException
	 */
	public String getCopyright() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lCopyrightObj = channel
				.getFirstChildNamed(RSS_CHANNEL_COPYRIGHT);
		if (lCopyrightObj != null) {
			return lCopyrightObj.getContent();
		}
		throw new XFeedException();
	}

	/**
	 * Get an image URL as a String. Note: Other Image meta data is not
	 * retrieved. TODO, Extract Image as an object.
	 */
	public String getImage() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lImageObj = channel.getFirstChildNamed(RSS_CHANNEL_IMAGE);
		if (lImageObj != null) {
			IXMLElement lImageUrl = lImageObj.getFirstChildNamed(RSS_IMAGE_URL);
			if (lImageUrl != null) {
				return lImageUrl.getContent();
			}
		}
		return null;
	}

	// ------------------ iToons Tags

	public String getIToonsImage() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lImageObj = channel.getFirstChildNamed(
				RSS_CHANNEL_IT_IMAGE, ITUNES_NS);
		if (lImageObj != null) {
			IXMLElement lHref = lImageObj
					.getFirstChildNamed(RSS_CHANNEL_IT_IMAGE_HREF, ITUNES_NS);
			if (lHref != null) {
				return lHref.getContent();
			}
		}
		return null;
	}

	public String getIToonsCategory() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lCategoryObj = channel.getFirstChildNamed(
				RSS_CHANNEL_IT_CATEGORY, ITUNES_NS);
		if (lCategoryObj != null) {
			return lCategoryObj.getContent();
		}
		throw new XFeedException();
	}

	public String getIToonsSummary() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lSummaryObj = channel.getFirstChildNamed(
				RSS_CHANNEL_IT_SUMMARY, ITUNES_NS);
		if (lSummaryObj != null) {
			return lSummaryObj.getContent();
		}
		throw new XFeedException();
	}

	public String getIToonsExplicit() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lExplicitObj = channel.getFirstChildNamed(
				RSS_CHANNEL_IT_EXPLICIT, ITUNES_NS);
		if (lExplicitObj != null) {
			return lExplicitObj.getContent();
		}
		throw new XFeedException();
	}

	public String getIToonsSubtitle() throws XFeedException {
		IXMLElement channel = getChannel();
		IXMLElement lSubtitleObj = channel.getFirstChildNamed(
				RSS_CHANNEL_IT_SUB, ITUNES_NS);
		if (lSubtitleObj != null) {
			return lSubtitleObj.getContent();
		}
		throw new XFeedException();
	}

	public int indexOfItem(IXItem pItem) throws XFeedException {
		IXMLElement[] lItems = getRSSItems();
		for (int i = 0; i < lItems.length; i++) {
			try {
				if (lItems[i].equals(((IXItemData)pItem).getDataSource())) {
					return i;
				}
			} catch (XItemException e) {
				// Something is horribly wrong here.
			}
		}
		return -1;
	}

	public void removeItem(int pIndex) throws XFeedException {
		IXMLElement[] lItems = getRSSItems();
		if (pIndex >= 0 && pIndex < lItems.length) {
			IXMLElement lChannel = getChannel();
			lChannel.removeChild(lItems[pIndex]);
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
	public boolean read() {
		File lFile = getFile();
		if (lFile != null) {
			try {
				mRSSDocument = NanoXML.parseNanoXML(lFile);
			} catch (Exception e) {

			}
			if (mRSSDocument != null) {
				return true;
			}
		}
		return false;
	}

	public void write() throws XFeedException {
		File lFile = getFile();
		if (lFile != null) {
			try {
				NanoXML.writeNanoXML(lFile, mRSSDocument);
			} catch (Exception e) {
			}
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
		ArrayList<IXItem> lList = new ArrayList<IXItem>();
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
		ArrayList<IXItem> lList = new ArrayList<IXItem>();
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
		mRSSDocument = null;
	}
}