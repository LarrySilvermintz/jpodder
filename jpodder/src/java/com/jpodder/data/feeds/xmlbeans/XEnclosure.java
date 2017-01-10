package com.jpodder.data.feeds.xmlbeans;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 */
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;

import com.jpodder.FileException;
import com.jpodder.FileHandler;
import com.jpodder.data.content.Content;
import com.jpodder.data.content.ContentException;
import com.jpodder.data.feeds.IXEnclosure;
import com.jpodder.data.feeds.IXFeed;
import com.jpodder.data.feeds.IXItem;
import com.jpodder.data.feeds.XEnclosureException;
import com.jpodder.data.feeds.XItemException;
import com.jpodder.rss20.TEnclosure;

/**
 * This class wraps an enclosure model for temporary use. It is not a
 * replication of the model. So we either take en enclosure model from the item,
 * we create one or we don't specify one.
 * <p>
 * An enclosure which doesn't have the item set is considered to be detached
 * from the RSS model. Calls to methods in a dettached enclosure will result in
 * the <code>XEnclosureException</code> being thrown.
 */
public class XEnclosure implements IXEnclosure, IXEnclosureData {

	private String GUID = null;

	protected IXFeed mFeed;
	private IXItem mItem;
	protected TEnclosure mDataSource;
	
	public XEnclosure(IXFeed pFeed) {
		mFeed = pFeed;
	}

	/**
	 * If the provided RSS Item model doesn't contain and enclosure, we create
	 * one.
	 * 
	 * @param pItem
	 */
	public XEnclosure(IXFeed pFeed, IXItem pItem) {
		mItem = pItem;
		mFeed = pFeed;
	}

	/**
	 * Set the item for this enclosure.
	 */
	public void setItem(IXItem pItem) {
		mItem = pItem;
	}

	public void setDataSource(Object pDataSource){
		if( !( pDataSource instanceof TEnclosure)){
			throw new IllegalArgumentException("Wrong data source type" + pDataSource);
		} 
		mDataSource = (TEnclosure)pDataSource;
	}
	
	public TEnclosure getDataSource() throws XEnclosureException {
		if(mDataSource != null){
			return mDataSource;
		}else{
			throw new XEnclosureException(ENCLOSURE_DETACHED);
		}
		
	}

	public void releaseDataSource() {
		mDataSource = null;
	}


	
	public IXItem getItem() throws XEnclosureException {
		if (mItem != null) {
			return mItem;
		} else {
			throw new XEnclosureException(ENCLOSURE_DETACHED);
		}
	}

	/**
	 * Set the enclosure URL.
	 * 
	 * @param url
	 *            URL
	 * @throws XEnclosureException
	 */
	public void setURL(URL url) throws XEnclosureException {
		getDataSource().setUrl(url.toExternalForm());
		mFeed.setModified(true);
	}
	
	/**
	 * Set the enclosure MIME type. MIME is generally loosly defined. See: For
	 * MIME types.
	 * 
	 * @param type
	 *            String
	 * @throws XEnclosureException
	 */
	public void setType(String type) throws XEnclosureException {
		getDataSource().setType(type);
		mFeed.setModified(true);
	}

	/**
	 * Get the enclosure type.
	 * 
	 * @return String
	 * @throws XEnclosureException
	 */
	public String getType() throws XEnclosureException {
		return getDataSource().getType();
	}

	//    
	// org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException
	// at
	// org.apache.xmlbeans.impl.va
	// lues.JavaIntHolder.set_text(JavaIntHolder.java:42)
	// at
	// org.apache.xmlbeans.impl.values.XmlObjectBase.update_from_wscanon_text(XmlObjectBase.java:1069)
	// at
	// org.apache.xmlbeans.impl.values.XmlObjectBase.check_dated(XmlObjectBase.java:1207)
	// at
	// org.apache.xmlbeans.impl.values.JavaIntHolder.intValue(JavaIntHolder.java:52)
	// at
	// org.apache.xmlbeans.impl.values.XmlObjectBase.getIntValue(XmlObjectBase.java:1425)
	// at com.jpodder.rss20.impl.TEnclosureImpl.getLength(Unknown Source)
	// at
	// com.jpodder.data.feeds.XEnclosureImpl.getLength(XEnclosureImpl.java:123)
	// at
	// com.jpodder.data.feeds.XPersonalEnclosureImpl.isIncomplete(XPersonalEnclosureImpl.java:345)
	// at
	// com.jpodder.data.feeds.XPersonalFeedImpl.getEnclosureStatus(XPersonalFeedImpl.java:791)
	// at
	// com.jpodder.ui.swt.file.MediaLabelProvider.getBackground(MediaLabelProvider.java:94)
	// at
	// org.eclipse.jface.viewers.TableViewer$TableColorAndFontCollector.setFontsAndColors(Unknown
	// Source)
	// at org.eclipse.jface.viewers.TableViewer.doUpdateItem(Unknown Source)
	// at
	// org.eclipse.jface.viewers.StructuredViewer$UpdateItemSafeRunnable.run(Unknown
	// Source)
	// at org.eclipse.jface.util.SafeRunnable$1.run(Unknown Source)
	// at org.eclipse.jface.util.SafeRunnable.run(Unknown Source)
	// at org.eclipse.jface.viewers.StructuredViewer.updateItem(Unknown Source)
	// at org.eclipse.jface.viewers.TableViewer.internalRefreshAll(Unknown
	// Source)
	// at org.eclipse.jface.viewers.TableViewer.internalRefresh(Unknown Source)
	// at org.eclipse.jface.viewers.TableViewer.internalRefresh(Unknown Source)
	// at org.eclipse.jface.viewers.StructuredViewer$7.run(Unknown Source)
	// at org.eclipse.jface.viewers.StructuredViewer.preservingSelection(Unknown
	// Source)
	// at org.eclipse.jface.viewers.StructuredViewer.refresh(Unknown Source)
	// at org.eclipse.jface.viewers.StructuredViewer.refresh(Unknown Source)
	// at org.eclipse.jface.viewers.TableViewer.inputChanged(Unknown Source)
	// at org.eclipse.jface.viewers.ContentViewer.setInput(Unknown Source)
	// at org.eclipse.jface.viewers.StructuredViewer.setInput(Unknown Source)
	// at com.jpodder.ui.swt.file.FilesView.setInput(FilesView.java:329)
	// at
	// com.jpodder.ui.swt.file.FileController.setFileView(FileController.java:277)
	// at
	// com.jpodder.ui.swt.feeds.FeedController.updateSelection(FeedController.java:901)
	// at
	// com.jpodder.ui.swt.feeds.FeedController.cellSelected(FeedController.java:869)
	// at de.kupzog.ktable.KTable.fireCellSelection(KTable.java:2424)
	// at de.kupzog.ktable.KTable.focusCell(KTable.java:1587)
	// at de.kupzog.ktable.KTable.onMouseDown(KTable.java:1776)
	// at de.kupzog.ktable.KTable$5.mouseDown(KTable.java:269)
	// at
	// org.eclipse.swt.widgets.TypedListener.handleEvent(TypedListener.java:133)
	// at org.eclipse.swt.widgets.EventTable.sendEvent(EventTable.java:66)
	// at org.eclipse.swt.widgets.Widget.sendEvent(Widget.java:896)
	// at org.eclipse.swt.widgets.Display.runDeferredEvents(Display.java:3236)
	// at org.eclipse.swt.widgets.Display.readAndDispatch(Display.java:2856)
	// at com.jpodder.ui.swt.main.MainUI.dispatch(MainUI.java:225)
	// at com.jpodder.ui.swt.UILauncher.<init>(UILauncher.java:59)
	// at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	// at sun.reflect.NativeConstructorAccessorImpl.newInstance(Unknown Source)
	// at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(Unknown
	// Source)
	// at java.lang.reflect.Constructor.newInstance(Unknown Source)
	// at com.jpodder.util.DynamicObject.<init>(DynamicObject.java:93)
	// at com.jpodder.util.PersistentObject.<init>(PersistentObject.java:137)
	// at com.jpodder.Main.main(Main.java:78)

	/**
	 * Get the enclosure Length
	 * 
	 * @return
	 * @throws XEnclosureException
	 */
	public long getLength() throws XEnclosureException {
		try {
			return getDataSource().getLength();
		} catch (XmlValueOutOfRangeException xmle) {
			return 0;
		}
	}

	public void setLength(long pLength) throws XEnclosureException {
		getDataSource().setLength(new Long(pLength).intValue());
		mFeed.setModified(true);
	}

	/**
	 * Get the enclosure URL.
	 * 
	 * @return URL
	 * @throws XEnclosureException
	 */
	public URL getURL() throws XEnclosureException {

		try {
			String lUrl = getDataSource().getUrl();
			return new URL(mDataSource.getUrl());
		} catch (MalformedURLException mue) {
			try {
				return new URL("http://");
			} catch (MalformedURLException e) {
				return null;
			}
		}
	}

	/**
	 * Return the URL decoded file name
	 * @return String
	 */
	public String getName() {

		URL lUrl;
		Content lContent = null;
		try {
			lContent = new Content(getType());
		} catch (ContentException e1) {
		} catch (XEnclosureException e) {
		}

		try {
			lUrl = getURL();
			return FileHandler.getUrlFileName(lUrl);
		} catch (XEnclosureException e) {
			return null;
		} catch (FileException e) {
			try {
				String lTitle = getItem().getTitle();
				lTitle = FileHandler.makeFSName(lTitle);
				if(lContent != null){
					lTitle += "." + lContent.getExtension();
				}
				return lTitle;
			} catch (XItemException e1) {
			} catch (XEnclosureException e1) {
			}
			return new Long(System.currentTimeMillis()).toString();
		}
	}

	public String toString() {
		try {
			return (getURL().toExternalForm() + ", type=" + getType());
		} catch (XEnclosureException e) {
			return "";
		}
	}

	/**
	 * Check if enclosures are equal. For now we compare the filename only.
	 * 
	 * @param pEnclosure
	 * @return
	 * @throws XEnclosureException
	 */
	public boolean equals(IXEnclosure pEnclosure) throws XEnclosureException {
		return pEnclosure.getURL().getFile().equals(getURL().getFile());
	}

}