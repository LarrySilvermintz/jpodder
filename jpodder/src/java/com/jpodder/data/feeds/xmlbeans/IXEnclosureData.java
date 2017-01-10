package com.jpodder.data.feeds.xmlbeans;

import com.jpodder.data.feeds.XEnclosureException;
import com.jpodder.rss20.TEnclosure;


public interface IXEnclosureData {
	
	public void setDataSource(Object pDataSource);
	public void releaseDataSource();
	public TEnclosure getDataSource() throws XEnclosureException;
	
}
