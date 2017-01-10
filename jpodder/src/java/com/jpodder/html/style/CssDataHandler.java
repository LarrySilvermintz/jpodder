package com.jpodder.html.style;

import java.io.File;

import org.apache.log4j.Logger;

import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IDataHandler;

/**
 * Data Handler implementation for the Feeds.
 * 
 * @version 1.1
 */
public class CssDataHandler implements IDataHandler {

	protected Logger mLog = Logger.getLogger(getClass().getName());

	private boolean mIsModified = false; // Always false;
	
	private Css mTheme;
	private File mFile;

	public CssDataHandler(Css pTheme) {
		mTheme = pTheme;
	}

	public int getIndex() {
		return ConfigurationLogic.THEME_INDEX;
	}

	public boolean isModified() {
		return mIsModified;
	}

	public String getContent() throws Exception {
		mLog.info("getContent(), return the content of the feed list");
		return ""; // We don't need to save the theme.
	}

	public void setContent(String pContent) throws Exception {
		mLog.debug("setContent(), content to be set: " + pContent);
		// We don't use the content.
		if(getPersistentFile() != null ){
			mTheme.parse(getPersistentFile());	
		}
		
	}

	public boolean validate(String pContent, boolean pCompare) {
		return true; // validation is implicit when setting content.
	}

	public void setPersistentFile(File pFile) {
		mFile = pFile;
	}

	public File getPersistentFile() {
		return mFile;
	}

}