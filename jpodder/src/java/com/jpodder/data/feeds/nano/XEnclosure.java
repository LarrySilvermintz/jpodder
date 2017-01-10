package com.jpodder.data.feeds.nano;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @version 1.1
 */
import java.net.MalformedURLException;
import java.net.URL;

import net.n3.nanoxml.IXMLElement;

import com.jpodder.FileException;
import com.jpodder.FileHandler;
import com.jpodder.data.content.Content;
import com.jpodder.data.content.ContentException;
import com.jpodder.data.feeds.IXEnclosure;
import com.jpodder.data.feeds.IXFeed;
import com.jpodder.data.feeds.IXItem;
import com.jpodder.data.feeds.XEnclosureException;
import com.jpodder.data.feeds.XItemException;

/**
 * This class wraps an enclosure model for temporary use. It is not a
 * replication of the model. So we either take en enclosure model from the item,
 * we create one or we don't specify one.
 * <p>
 * An enclosure which doesn't have the item set is considered to be detached
 * from the RSS model. Calls to methods in a dettached enclosure will result in
 * the <code>XEnclosureException</code> being thrown.
 * 
 * <pre>
 *       &lt;xs:attribute type=&quot;xs:anyURI&quot; name=&quot;url&quot; use=&quot;optional&quot;/&gt;
 *       &lt;xs:attribute type=&quot;xs:int&quot; name=&quot;length&quot; use=&quot;optional&quot;/&gt;
 *       &lt;xs:attribute type=&quot;xs:string&quot; name=&quot;type&quot; use=&quot;optional&quot;/&gt;
 * </pre>
 */
public class XEnclosure implements IXEnclosure, IXEnclosureData {

	String RSS_ENCL_URL = "url";

	String RSS_ENCL_LEN = "length";

	String RSS_ENCL_TYPE = "type";

	private String GUID = null;

	protected IXFeed mFeed;

	private IXItem mItem;

	protected IXMLElement mDataSource;

	public XEnclosure(IXFeed pFeed) {
		mFeed = pFeed;
	}

	/**
	 * If the provided RSS Item model doesn't contain and enclosure, we create
	 * one.
	 * 
	 * @param pItem
	 */
	public XEnclosure(IXFeed pFeed, IXItem pItem) {
		mItem = pItem;
		mFeed = pFeed;
	}

	/**
	 * Set the item for this enclosure.
	 */
	public void setItem(IXItem pItem) {
		mItem = pItem;
	}

	public void setDataSource(Object pDataSource) {
		if (!(pDataSource instanceof IXMLElement)) {
			throw new IllegalArgumentException("Wrong data source type"
					+ pDataSource);
		}
		mDataSource = (IXMLElement) pDataSource;
	}
	
	public void releaseDataSource(){
		mDataSource = null;
	}
	
	public IXMLElement getDataSource() throws XEnclosureException {
		if (mDataSource != null) {
			return mDataSource;
		} else {
			throw new XEnclosureException(ENCLOSURE_DETACHED);
		}
	}

	public IXItem getItem() throws XEnclosureException {
		if (mItem != null) {
			return mItem;
		} else {
			throw new XEnclosureException(ENCLOSURE_DETACHED);
		}
	}

	/**
	 * Set the enclosure URL.
	 * 
	 * @param url
	 *            URL
	 * @throws XEnclosureException
	 */
	public void setURL(URL url) throws XEnclosureException {
		IXMLElement lEnclObj = getDataSource();
		IXMLElement lUrlObj = lEnclObj.getFirstChildNamed(RSS_ENCL_URL);
		if (lUrlObj != null) {
			lUrlObj.setContent(url.toExternalForm());
			mFeed.setModified(true);
		}
	}

	/**
	 * Get the enclosure URL. Return <code>http://</code> as URL, if the
	 * source URL is not well formed.
	 * 
	 * @return URL
	 * @throws XEnclosureException
	 */
	public URL getURL() throws XEnclosureException {
		try {
			IXMLElement lEnclObj = getDataSource();
			String lUrl = lEnclObj.getAttribute(RSS_ENCL_URL, null);
			return new URL(lUrl);

			//			IXMLElement lUrlObj = lEnclObj.getFirstChildNamed(RSS_ENCL_URL);
//			if (lUrlObj != null) {
//			}
		} catch (MalformedURLException mue) {
			try {
				return new URL("http://");
			} catch (MalformedURLException e) {
			}
		}
		throw new XEnclosureException();
	}

	/**
	 * Set the enclosure MIME type. MIME is generally loosly defined. See: For
	 * MIME types.
	 * 
	 * @param pType
	 *            String
	 * @throws XEnclosureException
	 */
	public void setType(String pType) throws XEnclosureException {
		IXMLElement lEnclObj = getDataSource();
		IXMLElement lTypeObj = lEnclObj.getFirstChildNamed(RSS_ENCL_TYPE);
		if (lTypeObj != null) {
			lTypeObj.setContent(pType);
			mFeed.setModified(true);
		}
	}

	/**
	 * Get the enclosure type.
	 * 
	 * @return String
	 * @throws XEnclosureException
	 */
	public String getType() throws XEnclosureException {
		IXMLElement lEnclObj = getDataSource();
		String lType = lEnclObj.getAttribute(RSS_ENCL_TYPE, null);
		if( lType != null){
			return lType;
		}
		throw new XEnclosureException();
	}

	/**
	 * Get the enclosure Length
	 * 
	 * @return
	 * @throws XEnclosureException
	 */
	public long getLength() throws XEnclosureException {

		IXMLElement lEnclObj = getDataSource();
		String lLen = lEnclObj.getAttribute(RSS_ENCL_LEN, null);
		if( lLen != null){
			return new Long(lLen).longValue();
		}
		throw new XEnclosureException();
	}

	public void setLength(long pLength) throws XEnclosureException {
		IXMLElement lEnclObj = getDataSource();
		IXMLElement lLengthObj = lEnclObj.getFirstChildNamed(RSS_ENCL_TYPE);
		if (lLengthObj != null) {
			lLengthObj.setContent(new Long(pLength).toString());
			mFeed.setModified(true);
		}
	}

	/**
	 * Return the URL decoded file name
	 * 
	 * @return String
	 */
	public String getName() {

		URL lUrl;
		Content lContent = null;
		try {
			lContent = new Content(getType());
		} catch (ContentException e1) {
		} catch (XEnclosureException e) {
		}

		try {
			lUrl = getURL();
			return FileHandler.getUrlFileName(lUrl);
		} catch (XEnclosureException e) {
			return null;
		} catch (FileException e) {
			try {
				String lTitle = getItem().getTitle();
				lTitle = FileHandler.makeFSName(lTitle);
				if (lContent != null) {
					lTitle += "." + lContent.getExtension();
				}
				return lTitle;
			} catch (XItemException e1) {
			} catch (XEnclosureException e1) {
			}
			return new Long(System.currentTimeMillis()).toString();
		}
	}

	public String toString() {
		try {
			return (getURL().toExternalForm() + ", type=" + getType());
		} catch (XEnclosureException e) {
			return "";
		}
	}

	/**
	 * Check if enclosures are equal. For now we compare the filename only.
	 * 
	 * @param pEnclosure
	 * @return
	 * @throws XEnclosureException
	 */
	public boolean equals(IXEnclosure pEnclosure) throws XEnclosureException {
		return pEnclosure.getURL().getFile().equals(getURL().getFile());
	}

}