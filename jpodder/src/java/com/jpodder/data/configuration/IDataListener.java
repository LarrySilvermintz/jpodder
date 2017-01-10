package com.jpodder.data.configuration;


/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @version 1.1
 */
public interface IDataListener {
	
	public void dataRemoved(DataEvent pEvent);
	public void dataWillBeRemoved(DataEvent pEvent);
	
}
