package com.jpodder.data.feeds;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.1
 */
import java.io.File;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;

import com.jpodder.FileHandler;

// CHANGE THE MODEL IMPORTS TO USE EITHER XMLBEANS OR NANO
import com.jpodder.data.feeds.xmlbeans.IXEnclosureData;
import com.jpodder.data.feeds.xmlbeans.XEnclosure;

 
//import com.jpodder.data.feeds.nano.IXEnclosureData;
//import com.jpodder.data.feeds.nano.XEnclosure;

// import com.jpodder.net.NetHEADInfo;

/**
 * This class wraps an enclosure model for temporary use. It is not a
 * replication of the model. So we either take en enclosure model from the item
 * or we create one. The enclosure model is not necessary present when we can
 * create an empty enclosure. The model will throw exceptions if the enclosure
 * model is not present.
 */
public class XPersonalEnclosure extends XEnclosure implements
		IXPersonalEnclosure {

	// CB TODO, We can store the status in a single byte. (8 bits).
	private int mStatus = 0x00;

	static final byte MARKED = 0x01;

	static final byte LOCAL = 0x02;

	static final byte PLAYER = 0x04;

	static final byte CACHED = 0x08;

	static final byte TORRENT = 0x10;

	static final byte INSPECTED = 0x20;

	static final byte COMPLETED = 0x40;

	static final byte CANDIDATE = (byte) 0x80;

	private int mSize = 0;

	private long mDate = 0;

	private String mFile = null;

	private File mTorrentFile;

	// -------- XPersonalEnclosure

	// private NetHEADInfo mHeaderInformation;

	private IXPersonalFeed mFeed;

	private URL mPersonalUrl;

	static final Logger sLog = Logger.getLogger(XPersonalEnclosure.class
			.getName());

	/**
	 * Constructor without an Item object. The enclosure is there for detached
	 * from the RSS model.
	 * 
	 * @param pFeed
	 */
	public XPersonalEnclosure(IXPersonalFeed pFeed) {
		super(pFeed, null);
		mFeed = pFeed;

	}

	/**
	 * Constructor with an Item object. The enclosure is derived from the item
	 * and attached to the personal enclosure.
	 * 
	 * @param pItem
	 */
	public XPersonalEnclosure(IXPersonalFeed pFeed, IXItem pItem) {
		super(pFeed, pItem);
		mFeed = pFeed;

	}

	public XPersonalEnclosure(XPersonalFeed pFeed, IXItem pItem,
			IXEnclosure pEnclosure) {
		super(pFeed, pItem);
		if (pEnclosure instanceof IXEnclosureData) {
			try {
				super.setDataSource(((IXEnclosureData) pEnclosure)
						.getDataSource());
			} catch (XEnclosureException e) {
				sLog.warn("Setting datasource failed " + this);
			}
		}
		mFeed = pFeed;
	}

	/**
	 * Get the enclosure URL.
	 * 
	 * @return URL
	 */
	public URL getPersonalURL() {
		return mPersonalUrl;
	}

	/**
	 * set the personal enclosure URL.
	 * 
	 * @return URL
	 */
	public void setPersonalURL(URL pUrl) {
		mPersonalUrl = pUrl;
	}

	public void setItem(IXItem pItem) {
		super.setItem(pItem);
	}

	/**
	 * Return the URL decoded file name
	 * 
	 * @return String
	 */
	public String getName() {
		return getFile().getName();
	}

	public String toString() {
		String lUrl = "";
		try {
			lUrl = getURL().toExternalForm();
		} catch (XEnclosureException e) {
		}
		return (lUrl + ", type=" + getFileType());
	}

	/**
	 * Check if enclosures are equal. For now we compare the filename only.
	 * 
	 * 
	 * @param pEnclosure
	 * @return
	 */
	public boolean equals(IXPersonalEnclosure pEnclosure) {
		try {
			return pEnclosure.getURL().getFile().equals(getURL().getFile());
		} catch (XEnclosureException e) {
			return false;
		}
	}

	public boolean isMarked() {
		return ((mStatus & MARKED) == 0) ? false : true;
	}

	public void setMarked(boolean pMarked) {
		mStatus = (pMarked) ? mStatus | MARKED : mStatus & ~MARKED;
		mFeed.setModified(true);
	}

	public boolean isCached() {
		return ((mStatus & CACHED) == 0) ? false : true;
	}

	public void setCached(boolean pCached) {
		mStatus = (pCached) ? mStatus | CACHED : mStatus & ~CACHED;
		mFeed.setModified(true);
	}

	public boolean getInPlayer() {
		return ((mStatus & PLAYER) == 0) ? false : true;
	}

	public void setInPlayer(boolean pPlayer) {
		mStatus = (pPlayer) ? mStatus | PLAYER : mStatus & ~PLAYER;
		mFeed.setModified(true);
	}

	public boolean isLocal() {
		return ((mStatus & LOCAL) == 0) ? false : true;
	}

	public void setLocal(boolean pLocal) {
		mStatus = (pLocal) ? mStatus | LOCAL : mStatus & ~LOCAL;
		mFeed.setModified(true);
	}

	public void setCandidate(boolean pCandidate) {
		mStatus = (pCandidate) ? mStatus | CANDIDATE : mStatus & ~CANDIDATE;
		mFeed.setModified(true);
	}

	public boolean isCandidate() {
		return ((mStatus & CANDIDATE) == 0) ? false : true;
	}

	/**
	 * Set that the enclosure is now inspected.
	 */
	public void setInspected() {
		if (isInspected()) {
			throw new IllegalArgumentException();
		} else {
			mStatus |= INSPECTED;
		}
	}

	public boolean isInspected() {
		return ((mStatus & INSPECTED) == 0) ? false : true;
	}

	public void setDownloadCompleted(boolean pCompleted) {
		mStatus = (pCompleted) ? mStatus | COMPLETED : mStatus & ~COMPLETED;
		mFeed.setModified(true);
	}

	public boolean isDownloadCompleted() {
		return ((mStatus & COMPLETED) == 0) ? false : true;
	}

	/**
	 * Get if this enclosure is a torrent file.
	 * 
	 * @return boolean
	 */
	public boolean isTorrent() {
		return ((mStatus & TORRENT) == 0) ? false : true;
	}

	/**
	 * Set this enclosure as torrentFile.
	 */
	public void setTorrent() {
		mStatus |= TORRENT;
	}

	public Date getDate() {
		return new Date(getContentDate());
		// String dS = getHEADInfo().getModifiedString();
		// if (dS != null) {
		// try {
		// return DateFormat.getDateInstance().parse(dS);
		// } catch (java.text.ParseException pe) {
		// // Can not parse this date string.
		// }
		// }
		// return null;
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

	public int getFileLength() {
		return mSize;
	}

	public File getFile() {
		return getFile(false);
	}

	/**
	 * Overrides the XFile method.
	 */
	public File getFile(boolean pRefresh) {
		String lFolder = getFeed().getFolder();
		if (mFile == null || mFile.length() == 0 || mFile.equals("null")
				|| pRefresh) {
			mFile = super.getName();
			if (mFile == null) {
				return null;
			}
		}
		return FileHandler.getLocalEnclosureFile(mFile, lFolder);
	}

	public void setFile(File pFile) {
		// Should not be called, satisfy interface only.
		throw new IllegalArgumentException();
	}

	public void setFileName(String pFile) {
		mFile = pFile;
	}

	/**
	 * Get the file to which the torrent enclosure will be/has been downloaded.
	 * 
	 * @param torrentFile
	 *            File
	 */
	public void setTorrentFile(File pTorrentFile) {
		mTorrentFile = pTorrentFile;
	}

	/**
	 * Get the file to which the torrent enclosure will be/has been downloaded.
	 * 
	 * @return File the bitTorrent file.
	 */
	public File getTorrentFile() {
		return mTorrentFile;
	}

	/**
	 * Return if the file is incomplete. This method performs several checks on
	 * the file. It compares file length on the local storage with the length in
	 * the HTTP Header. An exact match of the size in bytes is needed. It also
	 * compares the file length with the length as provided in the RSS tag.
	 * 
	 * @throws Exception
	 * @return boolean
	 */
	public boolean isIncomplete() {

		if (getFile() != null) {

			long lFileLength = getFile().length();
			if (lFileLength == 0 || isDownloadCompleted()) {
				return false;
			}
			// We perform an additional check as the downloadCompleted flag
			// was implemented in a later release. Previous stored enclosures
			// will not have this flag set.

			// CB FIXME. Unfortunatly the HEAD information is not always
			// correct for certain feed. The difference is not "visible"
			// when rounding off with the size formatters.
			// We could set an incomplete flag, when we start the download
			// and then clear the flag wehn the download completes.
			// The incomplete flag, would need to be stored persistenly, in the
			// configuration.

			long lContentSize = getContentSize();

			// mLog.info( lFileLength + "=" + lHEADLength);
			if (getFile().length() != 0 && lContentSize != 0 && getFile().length() >= lContentSize) {
				return false;
				// Note that the file could be bigger (or smaller) than
				// indicated by
				// the server. ID3 rewriting could add some bytes like new
				// tags or padding to the file
			}

			// Another check using the RSS length of the file.
			try {
				long lRSSLength = getLength();
				if (getFile().length() != 0 && getFile().length() >= lRSSLength) {
					return false;
				}
			} catch (XEnclosureException e) {
				// We simply ignore this assertion.
			}

			// we have to assume this file is not complete.
			return true;

		} else {
			return false;
		}
	}

	/**
	 * Compare two objects on the date.
	 * 
	 * @param o
	 *            Object
	 * @return int
	 */
	public int compareTo(Object o) {
		IXFile f = (IXFile) o;
		int cmpResult = -1;
		if (f.getDate() != null && getDate() != null) {
			cmpResult = f.getDate().compareTo(getDate());
		}
		return cmpResult;
	}

	public void setURL(URL url) {
		try {
			super.setURL(url);
		} catch (XEnclosureException e) {
		}
	}

	public String getFileType() {
		try {
			return getType();
		} catch (XEnclosureException e) {
			return "";
		}
	}

	public void setFeed(IXPersonalFeed pFeed) {
		mFeed = pFeed;
	}

	public IXPersonalFeed getFeed() {
		return mFeed;
	}

	public URL getFileURL() {
		return getPersonalURL();
	}

	public void setFileURL(URL url) {
		// satisfy the interface.
	}

	public int getContentSize() {
		return mSize;
	}

	public void setContentSize(int pSize) {
		mSize = pSize;
	}

	public void setContentDate(long pDate) {
		mDate = pDate;
	}

	public long getContentDate() {
		return mDate;
	}

}