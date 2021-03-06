package com.jpodder.data.feeds.nano;

import net.n3.nanoxml.IXMLElement;

import com.jpodder.data.feeds.XEnclosureException;


public interface IXEnclosureData {
	
	public void setDataSource(Object pDataSource);
	public void releaseDataSource();
	public IXMLElement getDataSource() throws XEnclosureException;
	
}
