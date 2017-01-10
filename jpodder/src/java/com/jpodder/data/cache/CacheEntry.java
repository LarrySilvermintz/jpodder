package com.jpodder.data.cache;

import com.jpodder.data.configuration.IDataController;

/**
 * Cache track class. Simple class holding an URL of a track.
 * 
 */
public class CacheEntry {

	private String name;

	private String GUID;

	/**
	 * Data Controller to which this Data object belongs to and if null then it
	 * is not managed by it means it is not going to be saved
	 */
	private IDataController mController = new IDataController.NullDataController();

	/**
	 * Constructor.
	 * 
	 * @param url
	 *            URL
	 * @param GUID
	 *            The Global identiy of this cache entry. It corresponds to the
	 *            RSS GUID tag.
	 */
	public CacheEntry(String pName, String GUID) {
		this.GUID = GUID;
		name = pName;
	}

	/**
	 * @return Returns the gUID.
	 */
	public String getGUID() {
		return GUID;
	}

	public String getName() {
		return name;
	}

	/**
	 * Set the data controller this object belong to
	 * 
	 * @param pController
	 *            Data Controller of this instance
	 */
	public void setDataController(IDataController pController) {
		if (pController == null) {
			mController = new IDataController.NullDataController();
		} else {
			mController = pController;
		}
	}

	public boolean equals(String pName) {
		if (pName.equals(name)) {
			return true;
		} else
			return false;
	}

}