package com.jpodder.plugin;

public class PluginRegistryEntry {
	private String mName;
	private String mValue;
	
	public PluginRegistryEntry(String pName, String pValue){
		mValue = pValue;
		mName = pName;
	}
	
	public String getName(){
		return mName;
	};
	
	public String getValue(){
		return mValue;
	}
	
	public void setValue(String pValue){
		mValue = pValue;
	}
}
