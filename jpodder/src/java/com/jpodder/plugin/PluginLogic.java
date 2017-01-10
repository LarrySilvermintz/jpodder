package com.jpodder.plugin;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.FileHandler;
import com.jpodder.JPodderException;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.util.PersistentObject;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class PluginLogic {

	private static PluginLogic sSelf;

	private static PersistentObject sPluginLoader;

	private PluginRegistryHandler mHandler;
	
	private Logger mLog = Logger.getLogger(getClass().getName());
	
	public static PluginLogic getInstance() {
		if (sSelf == null) {
			sSelf = new PluginLogic();
		}
		return sSelf;
	}

	public PluginLogic() {
		sPluginLoader = new PersistentObject("jpodder.plugin.loader", PluginLogic.class
				.getClassLoader(), "com.jpodder.plugin.PluginLoader",
				new Object[] { FileHandler.sPluginDirectory });

		mHandler = new PluginRegistryHandler();
		try {
			ConfigurationLogic.getInstance().addDataHandler(mHandler);
		} catch (JPodderException e) {
			mLog.warn("Loading of datahandler failed: " + e.getMessage());
		}
	}

	public PluginRegistryEntry getRegistryEntry(String pName) {
		Iterator lIter = mHandler.mRegistryList.iterator();
		while (lIter.hasNext()) {
			PluginRegistryEntry lEntry = (PluginRegistryEntry) lIter.next();
			if (lEntry.getName().equals(pName)) {
				return lEntry;
			}
		}
		return null;
	}

	public void setRegistryEntry(String pName, String pValue) {
		
		PluginRegistryEntry lEntry = getRegistryEntry(pName);
		if(lEntry == null){
			mHandler.mRegistryList.add(new PluginRegistryEntry(pName, pValue));
		}else{
			lEntry.setValue(pValue);
		}
		mHandler.mModified = true;
	}

	public void scanPluginFolder() {
		sPluginLoader.invoke("scan", null);
	}

	public List getPluginByClass(Class pClass) {
		List lPlugin = (List) sPluginLoader.invoke("findPluginsByType",
				new Object[] { pClass });
		return lPlugin;
	}

	public Object getPluginByName(String pName) {
		return sPluginLoader.invoke("findPluginInstance",
				new Object[] { pName }, new Class[] { String.class });
	}
	
	public void finalize(){
		sPluginLoader.invoke("cleanTempPlugin", null);
	}
}
