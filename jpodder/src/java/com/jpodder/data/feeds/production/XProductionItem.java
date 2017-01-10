package com.jpodder.data.feeds.production;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;

import com.jpodder.data.feeds.IXEnclosure;
import com.jpodder.data.feeds.XEnclosureException;
import com.jpodder.data.feeds.XItemException;
import com.jpodder.data.feeds.xmlbeans.XItem;
import com.jpodder.util.Util;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */

/**
 * Extends XItem to add a file object, which is a recording.
 */
public class XProductionItem extends XItem {

	private static Logger sLog = Logger.getLogger(XItem.class.getName());

	private File mRecording;

	private String mFolder;

	public XProductionItem(XProduction pProduction) {
		this(pProduction, null, -1);
	}

	public XProductionItem(XProduction pProduction, int pIndex) {
		this(pProduction, null, pIndex);
	}

	/**
	 * @param localMP3
	 * @param url
	 */
	public XProductionItem(XProduction pProduction, File pRecording, int pIndex) {
		super(pProduction, pIndex);

		mFolder = pProduction.getFolderFile().getAbsolutePath();
		if (pRecording == null) {
			mRecording = getRecordingFromURL();
		} else {
			mRecording = pRecording;
		}
	}

	/**
	 * Parse a URL file and try to create a file object from it.
	 * 
	 * @return
	 */
	public File getRecordingFromURL() {
		File lTempFile = null;

		URL url = null;
		try {
			IXEnclosure lEnclosure = getEnclosure();
			if (lEnclosure != null) {
				url = lEnclosure.getURL();
			}
		} catch (XEnclosureException e) {
			return lTempFile;
		} catch (XItemException e) {
			return lTempFile;
		}

		if (url != null) {
			// CB TODO for some URL's the name should be further
			// parsed to remove the query.
			String fileName = Util.getName(url);
			if (!"".equals(mFolder)) {
				lTempFile = new File(mFolder, fileName);
			}
			if (lTempFile != null && lTempFile.exists()) {
				sLog.info("getRecordingFromURL() succesfull for : "
						+ lTempFile.getAbsolutePath());
			}
		}
		return lTempFile;
	}

	/**
	 * @return Returns the localMP3.
	 */
	public File getRecording() {
		return mRecording;
	}

	/**
	 * @param pRecording
	 *            The recording for this item.
	 */
	public void setRecording(File pRecording) {
		mRecording = pRecording;
		try {
			IXEnclosure lEnclosure = getEnclosure();
			if (lEnclosure != null) {
				lEnclosure.setLength(new Long(pRecording.length()).intValue());
				// CB TODO, resolve the MIME type.
				lEnclosure.setType("audio/mpeg");
			}
		} catch (XEnclosureException e) {
		} catch (XItemException e) {
		}

	}

	public String toString() {
		StringBuffer lBuffer = new StringBuffer();

		if (mRecording != null) {
			lBuffer.append(mRecording.getName());
		}
		try {
			IXEnclosure lEnclosure = getEnclosure();
			if (lEnclosure != null) {

				try {
					lBuffer.append(getTitle() + " "
							+ lEnclosure.getURL().toExternalForm());
				} catch (XItemException e) {
				}

			} else {
				lBuffer.append(" (No Enclosure)");
			}
		} catch (XEnclosureException e) {
			lBuffer.append("(No Enclosure)");
		} catch (XItemException e1) {
			lBuffer.append("(No Enclosure)");
		}
		return lBuffer.toString();
	}
}