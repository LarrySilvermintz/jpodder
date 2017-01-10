package com.jpodder.data.configuration;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An interface that provides the methods of a Data Controller that must be
 * informed by the Data object when Data Object content has changed internally.
 * 
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 */
public interface IDataController {

	/**
	 * Marks the Data Controller as changed
	 */
	public void setModified();

	/**
	 * @return True if the Data controller manages handles modified Data objects
	 */
	public boolean isModified();

	/**
	 * Null Data Controller where there is no data controller set on a data
	 * object but we want to avoid to check every time where it is set or not.
	 * Just use this data controller instance instead
	 */
	public static class NullDataController implements IDataController {
		public void setModified() {
		}

		public boolean isModified() {
			return false;
		}
	}

	/**
	 * This class makes sure that any removal of an entry through the iterator
	 * does mark the feed list as updated so that it can be saved later
	 */
	public static class CallbackList extends ArrayList {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6821781248442333741L;

		private IDataController mContainer;

		public CallbackList(IDataController pContainer) {
			mContainer = pContainer;
		}

		public Object remove(int pIndex) {
			Object lReturn = super.remove(pIndex);
			mContainer.setModified();
			fireDataRemoved(new DataEvent(lReturn));
			return lReturn;
		}

		public void willRemove(Object pObject) {
			fireDataWillBeRemoved(new DataEvent(pObject));
		}

		// ---- MODEL CHANGE HANDLING

		ArrayList<IDataListener> pListenerList = new ArrayList<IDataListener>();

		public void addListener(IDataListener pListener) {
			if (!pListenerList.contains(pListener)) {
				pListenerList.add(pListener);
			}
		}

		public void removeListener(IDataListener pListener) {
			if (pListenerList.contains(pListener)) {
				pListenerList.remove(pListener);
			}
		}

		public void fireDataWillBeRemoved(DataEvent pEvent) {
			Iterator<IDataListener> lIter = pListenerList.iterator();
			while (lIter.hasNext()) {
				lIter.next().dataWillBeRemoved(pEvent);
			}
		}

		public void fireDataRemoved(DataEvent pEvent) {
			Iterator<IDataListener> lIter = pListenerList.iterator();
			while (lIter.hasNext()) {
				lIter.next().dataRemoved(pEvent);
			}
		}

	}
}