package com.jpodder.data.feeds.xmlbeans;

import com.jpodder.data.feeds.XItemException;
import com.jpodder.rss20.TItem;


public interface IXItemData {
	
	public void setDataSource(Object pDataSource);
	public void releaseDataSource();
	public TItem getDataSource() throws XItemException;
}
