package com.jpodder.data.feeds.nano;

import net.n3.nanoxml.IXMLElement;

import com.jpodder.data.feeds.XItemException;


public interface IXItemData {
	
	public void setDataSource(Object pDataSource);
	public void releaseDataSource();
	public IXMLElement getDataSource() throws XItemException;
	
}
