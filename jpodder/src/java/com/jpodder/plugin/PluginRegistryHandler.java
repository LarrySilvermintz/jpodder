package com.jpodder.plugin;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IDataHandler;
import com.jpodder.registry.RegistryDocument;
import com.jpodder.registry.TProperty;
import com.jpodder.registry.TRegistry;

public class PluginRegistryHandler implements IDataHandler {

	protected boolean mModified = false;

	private Logger mLog = Logger.getLogger(getClass().getName());
	protected List<PluginRegistryEntry> mRegistryList = new ArrayList<PluginRegistryEntry>();
	private File mFile;

	public int getIndex() {
		return ConfigurationLogic.PLUGIN_PROPERTIES_INDEX;
	}

	public boolean isModified() {
		return mModified;
	}

	public String getContent() throws Exception {

		mLog.info("getContent(), return the content of the feed list");

		RegistryDocument lDocument = RegistryDocument.Factory.newInstance();
		TRegistry lRoot = lDocument.addNewRegistry();

		Iterator i = mRegistryList.iterator();
		while (i.hasNext()) {
			PluginRegistryEntry lEntry = (PluginRegistryEntry) i.next();
			TProperty lProperty = lRoot.addNewProperty();
			lProperty.setName(lEntry.getName());
			lProperty.setValue(lEntry.getValue());
		}
		StringWriter lOutput = new StringWriter();
		XmlOptions xmlOptions = new XmlOptions();
		xmlOptions.setSavePrettyPrint();
		lDocument.save(lOutput);
		return lOutput.toString();
	}

	public void setContent(String pContent) throws Exception {
		if (mLog.isDebugEnabled()) {
			mLog.info("setContent(), content to be set: '" + pContent + "'");
		}
		if (pContent != null && pContent.trim().length() != 0) {
			RegistryDocument lDocument = RegistryDocument.Factory
					.parse(pContent);
			if (mLog.isDebugEnabled()) {
				mLog.info("Cache Array: "
						+ java.util.Arrays.asList(lDocument.getRegistry()
								.getPropertyArray()));
			}
			ArrayList<PluginRegistryEntry> lRegistryList = new ArrayList<PluginRegistryEntry>();
			TProperty[] lProperties = lDocument.getRegistry()
					.getPropertyArray();
			for (int i = 0; i < lProperties.length; i++) {
				TProperty lProperty = lProperties[i];
				lRegistryList.add(new PluginRegistryEntry(lProperty.getName(),
						lProperty.getValue()));
			}
			mRegistryList.clear();
			mRegistryList.addAll(lRegistryList);
			if (mLog.isDebugEnabled()) {
				mLog.info("setContent(), registry list: " + lRegistryList);
			}
		} else {
			mLog
					.warn("setContent(), given content is empty and it is assumed that there is noregistry at all");
		}
	}

	public void setPersistentFile(File pFile) {
		mFile = pFile;
	}

	public File getPersistentFile() {
		return mFile;
	}

	public boolean validate(String pContent, boolean pCompare) {
		return false;
	}
}
