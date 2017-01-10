package com.jpodder.ui.swt.conf.panel.language;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
 * Simple model holding a master and slave resource bundle. The keys from the
 * master bundle are used to generate a list for edited values.
 */
public class TranslationModel {

	ArrayList<TranslationEntry> mTranslations = new ArrayList<TranslationEntry>();
	
	protected ResourceBundle mMasterBundle;

	protected ResourceBundle mTranslationBundle;

	Logger mLog = Logger.getLogger(getClass().getName());

	protected boolean mModified = false;

	protected List<TranslationListener> mListeners = new ArrayList<TranslationListener>();

	protected boolean mShowEmptyOnly = false;
	
	
	public TranslationModel(ResourceBundle pMasterBundle,
			ResourceBundle pTranslationBundle) {
		mapModel(pMasterBundle, pTranslationBundle);
	}

	/**
	 * Binds the source data (A master and translation resource bundle) to
	 * a an array of Translation Entries
	 * 
	 * @param pMasterBundle
	 * @param pTranslationBundle
	 */
	private void mapModel(ResourceBundle pMasterBundle,
			ResourceBundle pTranslationBundle) {
		mMasterBundle = pMasterBundle;
		mTranslationBundle = pTranslationBundle;

		Enumeration<String> lEnum = pMasterBundle.getKeys();
		while (lEnum.hasMoreElements()) {
			String lKey = lEnum.nextElement();
			String lTranslation = "";
			try {
				if (pTranslationBundle != null){ 
					lTranslation = pTranslationBundle.getString(lKey);
				}
			} catch (MissingResourceException mre) {
				mLog.warn("No resource for:" + lKey + " in "
						+ pTranslationBundle.toString());
			}
			String lMaster = pMasterBundle.getString(lKey);
			mTranslations.add(new TranslationEntry(lKey, lMaster, lTranslation));
		}
		Collections.sort(mTranslations);
	}
	
	public void setShowEmptyOnly(boolean pSet){
		mShowEmptyOnly = pSet;
	}
	
	public boolean getShowEmptyOnly(){
		return mShowEmptyOnly;
	}
	
	
	public void addListener(TranslationListener lListener) {
		if (!mListeners.contains(lListener)) {
			mListeners.add(lListener);
		}
	}

	public void removeListener(TranslationListener lListener) {
		if (mListeners.contains(lListener)) {
			mListeners.remove(lListener);
		}
	}

	public void fireLanguageEditModified(TranslationEvent pEvent) {
		Iterator lIt = mListeners.iterator();
		while (lIt.hasNext()) {
			((TranslationListener) lIt.next()).translationOccured(pEvent);
		}
	}

	public boolean getModified() {
		return this.mModified;
	}

	public int indexOf(String pKey) {
		
		int lTranslations = 0;
		Iterator<TranslationEntry> lIterator = mTranslations.iterator();
		int i = 0;
		while (lIterator.hasNext()) {
			TranslationEntry lEntry = lIterator.next();			
			if(lEntry.getKey().equals(pKey)){
			return i;
			}
			i++;
		}
		return lTranslations;
	}

	public int translationCount() {
		
		int lTranslations = 0;
		Iterator<TranslationEntry> lIterator = mTranslations.iterator();
		while (lIterator.hasNext()) {
			TranslationEntry lEntry = lIterator.next();
			if(lEntry.getTranslation().length() != 0){
				lTranslations ++;
			}
		}
		return lTranslations;
	}

	public int keyCount() {
		return mTranslations.size();
	}

public void setTransLation(String pKey, String pValue) {
		mTranslations.get(indexOf(pKey)).mTranslation = pValue;
		mModified = true;
		fireLanguageEditModified(new TranslationEvent(this));
				
}
	
	
	/**
	 * An entry containing the Key, Master text (EN_UK) and translation (if any). 
	 * Some simple data validation is performed on the translation. 
	 */
	public class TranslationEntry implements Comparable {
		private String mKey;
		private String mMaster;
		private String mTranslation;
		
		public TranslationEntry(String pKey, String pMaster, String pTranslation){
			mKey = pKey;
			mMaster = pMaster;

			if (pTranslation == null || pTranslation.length() == 0) {
				mTranslation = "";
			}else{
				mTranslation = pTranslation;
			}
		}
		
		public String getKey(){
			return mKey;
		}
		
		public String getMaster(){
			return mMaster;
		}
		
		public String getTranslation(){
			return mTranslation;
		}

		public void setKey(String pKey){
			mKey = pKey;
		}
		
		public void setMaster(String pMaster){
			mMaster = pMaster;
		}
		
		public void setTranslation(String pTranslation){
			mTranslation = pTranslation;
		}
		
		public int compareTo(Object arg0) {
			TranslationEntry lEntry0 = (TranslationEntry)arg0;
			return getKey().compareTo(lEntry0.getKey());
			
		}
	}
	
	public TreeMap getTranslationMap(){
		TreeMap<String,String> lMap = new TreeMap<String,String>();
		Iterator<TranslationEntry> lIt = mTranslations.iterator();
		while( lIt.hasNext()){
			TranslationEntry lEntry = lIt.next();
			lMap.put(lEntry.getKey(), lEntry.getTranslation());
		}
		return lMap;
	}
	
	
}
