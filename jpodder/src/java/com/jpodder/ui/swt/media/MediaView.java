package com.jpodder.ui.swt.media;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.jpodder.data.content.ContentAssociation;
import com.jpodder.data.feeds.IXFile;
import com.jpodder.data.feeds.IXItem;
import com.jpodder.data.feeds.IXPersonalEnclosure;
import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.XEnclosureException;
import com.jpodder.html.HTMLLogic;
import com.jpodder.ui.swt.IView;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.util.Messages;
import com.jpodder.util.Util;

/**
 * This class sets up the File View and interacts with the main application
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class MediaView implements IView {

	protected Menu mFilePopupMenu;

	protected MenuItem mFileDownloadPopupMenu;

	// -- Plugin popup UI
	protected MenuItem mFilePlayPopupMenu;

	protected MenuItem mFileStopPopupMenu;

	protected MenuItem mFileDeleteSelectedPopupMenu;

	protected MenuItem mFileAddSelectedToPlayerPopupMenu;

	protected MenuItem mToolsSyncMenu;

	// -- Program association UI
	protected MenuItem mFileOpenSelectedPopupMenu;

	// -- ID3 popup UI
	protected MenuItem mFileID3ViewPopupMenu;

	protected MenuItem mFileID3RewritePopupMenu;

	// Marking popup UI
	protected MenuItem mFileMarkingMenu;

	protected MenuItem mFileMarkAllPopupMenu;

	protected MenuItem mFileClearMarkingPopupMenu;

	protected MenuItem mFileDeleteMarkedPopupMenu;

	protected MenuItem mFileAddMarkedToPlayerPopupMenu;

	protected MenuItem mFileMenu;

	protected MenuItem mFileDownloadMenu;

	protected MenuItem mFilePlayMenu;

	protected MenuItem mFileStopMenu;

	protected MenuItem mFileDeleteSelectedMenu;

	protected MenuItem mFileAddSelectedToPlayerMenu;

	protected MenuItem mFileID3ViewMenu;

	protected MenuItem mFileID3RewriteMenu;

	protected MenuItem mFileAddMarkedToPlayerMenu;

	protected MenuItem mFileMarkAllMenu;

	protected MenuItem mFileClearMarkingMenu;

	protected MenuItem mFileDeleteMarkedMenu;

	// -- View UI.
	protected MenuItem mFileFilterMenu;

	protected MenuItem mFileViewItemsMenu;

	private Logger mLog = Logger.getLogger(getClass().getName());

	/**
	 * The Item and Media viewer is now created, but should be dynamic in the
	 * future. Some listeners are set in the controller. Need to rethink on how
	 * the selection listeners can access the controller actions when viewer is
	 * loaded dynamicly.
	 */
	protected MediaTreeView mItemViewer;

	protected MediaTableView mMediaViewer;

	protected MediaItemView mItemInfoView;

	/**
	 * The selection can be an XItem or an XPersonalEnclosure depending on the
	 * selected row.
	 */
	protected Object mSelection;

	/**
	 * The selection can be an XItem or an XPersonalEnclosure depending on the
	 * selected row.
	 */
	protected Object[] mSelectionArray;

	public static final int ITEM_VIEW = 401;

	public static final int MEDIA_VIEW = 402;

	protected int mCurrentView;

	protected Composite mParent;

	protected Composite mComposite;

	protected ModelPreparator mPreparator;

	public Composite getView() {
		return mComposite;
	}

	public ModelPreparator getModelPreparator() {
		return mPreparator;
	}

	/**
	 * Constructor. Defines the actions in this controller.
	 */
	public MediaView(Composite pParent) {
		mLog.info("<init>");
		mParent = pParent;

		// --- File Popup Menu ---------------------------------------------

		mFilePopupMenu = new Menu(UILauncher.getInstance().getShell(),
				SWT.POP_UP);
		mFileDownloadPopupMenu = new MenuItem(mFilePopupMenu, SWT.PUSH);
		new MenuItem(mFilePopupMenu, SWT.SEPARATOR);
		mFilePlayPopupMenu = new MenuItem(mFilePopupMenu, SWT.PUSH);
		mFileAddSelectedToPlayerPopupMenu = new MenuItem(mFilePopupMenu,
				SWT.PUSH);
		mFileDeleteSelectedPopupMenu = new MenuItem(mFilePopupMenu, SWT.PUSH);
		new MenuItem(mFilePopupMenu, SWT.SEPARATOR);
		mFileOpenSelectedPopupMenu = new MenuItem(mFilePopupMenu, SWT.PUSH);
		new MenuItem(mFilePopupMenu, SWT.SEPARATOR);
		// mFileID3EditPopupMenu = new MenuItem(mFilePopupMenu, SWT.PUSH);
		mFileID3ViewPopupMenu = new MenuItem(mFilePopupMenu, SWT.PUSH);
		mFileID3RewritePopupMenu = new MenuItem(mFilePopupMenu, SWT.PUSH);

		// --- File Menu ---------------------------------------------

		mFileMenu = new MenuItem(UILauncher.lWindowUI.lMainMenu, SWT.CASCADE, 3);
		mFileMenu.setText(Messages.getString("gui.menu.enclosures"));
		mFileMenu.setAccelerator(SWT.CTRL + 'e');
		mFileMenu.setMenu(new Menu(UILauncher.getInstance().getShell(),
				SWT.DROP_DOWN));

		mFileDownloadMenu = new MenuItem(mFileMenu.getMenu(), SWT.PUSH);
		mFilePlayMenu = new MenuItem(mFileMenu.getMenu(), SWT.PUSH);

		new MenuItem(mFileMenu.getMenu(), SWT.SEPARATOR);
		mFileAddSelectedToPlayerMenu = new MenuItem(mFileMenu.getMenu(),
				SWT.PUSH);
		mFileDeleteSelectedMenu = new MenuItem(mFileMenu.getMenu(), SWT.PUSH);
		mToolsSyncMenu = new MenuItem(mFileMenu.getMenu(), SWT.PUSH);

		new MenuItem(mFileMenu.getMenu(), SWT.SEPARATOR);
		// mFileID3EditMenu = new MenuItem(mFileMenu.getMenu(), SWT.PUSH);
		mFileID3ViewMenu = new MenuItem(mFileMenu.getMenu(), SWT.PUSH);
		mFileID3RewriteMenu = new MenuItem(mFileMenu.getMenu(), SWT.PUSH);

		new MenuItem(mFileMenu.getMenu(), SWT.SEPARATOR);

		// --- Marking Menu ---------------------------------------------

		mFileMarkingMenu = new MenuItem(mFileMenu.getMenu(), SWT.CASCADE);
		mFileMarkingMenu.setMenu(new Menu(UILauncher.getInstance().getShell(),
				SWT.DROP_DOWN));
		mFileMarkAllMenu = new MenuItem(mFileMarkingMenu.getMenu(), SWT.PUSH);
		mFileClearMarkingMenu = new MenuItem(mFileMarkingMenu.getMenu(),
				SWT.PUSH);
		mFileDeleteMarkedMenu = new MenuItem(mFileMarkingMenu.getMenu(),
				SWT.PUSH);
		mFileAddMarkedToPlayerMenu = new MenuItem(mFileMarkingMenu.getMenu(),
				SWT.PUSH);

		// ---- Filter Menu
		mFileFilterMenu = new MenuItem(mFileMenu.getMenu(), SWT.CASCADE);
		mFileFilterMenu.setMenu(new Menu(UILauncher.getInstance().getShell(),
				SWT.DROP_DOWN));
		mFileViewItemsMenu = new MenuItem(mFileFilterMenu.getMenu(), SWT.CHECK);
	}

	public void setViewer(int pView) {
		if (mCurrentView == pView) {
			return;
		}

		if (mComposite != null) {
			mComposite.dispose();
		}
		mComposite = new Composite(mParent, SWT.NONE);
		GridLayout lGridLayout = new GridLayout();
		lGridLayout.marginLeft = 0;
		lGridLayout.marginRight = 0;
		lGridLayout.marginHeight = 0;
		lGridLayout.marginWidth = 0;
		mComposite.setLayout(lGridLayout);

		if (pView == ITEM_VIEW) {

			mItemViewer = getItemViewer(mComposite);
			mLog.info("setViewer() Changed view to Item view ");
		}
		if (pView == MEDIA_VIEW) {
			mMediaViewer = getMediaViewer(mComposite);
			mLog.info("setViewer() Changed view to Media view ");
		}
		mCurrentView = pView;
		mParent.layout();
	}

	public MediaTreeView getItemViewer(Composite pParent) {

		SashForm lItemSash = new SashForm(pParent, SWT.VERTICAL);
		GridData lData = new GridData(GridData.FILL_BOTH);
		lItemSash.setLayoutData(lData);

		mItemViewer = new MediaTreeView(lItemSash);
		mItemInfoView = new MediaItemView(lItemSash);
		mItemViewer.mTree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				if (mSelection != null) {
					if (mSelection instanceof IXItem) {
						mItemInfoView.formatItem((IXItem) mSelection,
								HTMLLogic.STYLE_SHEET);
					}
				}
			}
		});
		mItemViewer.mTree.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				mFilePopupMenu.setLocation(event.x, event.y);
				mFilePopupMenu.setVisible(true);
				while (!mFilePopupMenu.isDisposed()
						&& mFilePopupMenu.isVisible()) {
					if (!Display.getDefault().readAndDispatch())
						Display.getDefault().sleep();
				}
			}
		});
		return mItemViewer;
	}

	public MediaTableView getMediaViewer(Composite pParent) {
		MediaTableView lMediaViewer = new MediaTableView(pParent);
		lMediaViewer.mTable.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				mLog.info("Menuevent at: " + event.x + event.y);
				mFilePopupMenu.setLocation(event.x, event.y);
				mFilePopupMenu.setVisible(true);
				while (!mFilePopupMenu.isDisposed()
						&& mFilePopupMenu.isVisible()) {
					if (!Display.getDefault().readAndDispatch())
						Display.getDefault().sleep();
				}
			}
		});
		return lMediaViewer;
	}

	/**
	 * Asynchronously update the object in the current active view.
	 * 
	 * @param pFile
	 */
	// CB TODO Doesn't work on delete (of what?)!
	public void update(final IXFile pFile) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {

				switch (mCurrentView) {
				case ITEM_VIEW: {
					if (mItemViewer.mTreeViewer.getTree().isDisposed()) {
						return;
					}
					mItemViewer.mTreeViewer.refresh(pFile);
				}
					break;
				case MEDIA_VIEW: {
					if (mMediaViewer.mTableViewer.getTable().isDisposed()) {
						return;
					}					
					mMediaViewer.mTableViewer.refresh(pFile);
//					mMediaViewer.mTableViewer.update(pFile, null);
				}
					break;
				}
			}
		});
	}

	public void updateDownload(final IXFile pFile) {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {

				switch (mCurrentView) {
				case ITEM_VIEW: {
					if (mItemViewer.mTreeViewer.getTree().isDisposed()) {
						return;
					}
					mItemViewer.mTreeViewer.update(pFile, null);
				}
					break;
				case MEDIA_VIEW: {
					if (mMediaViewer.mTableViewer.getTable().isDisposed()) {
						return;
					}
					if (pFile instanceof IXPersonalEnclosure) {
						// Only update the column representing the
						// file/enclosure size.
						String[] lProperties = new String[] { mMediaViewer.mColumnNames[3] };
						mMediaViewer.mTableViewer.update(
								(IXPersonalEnclosure) pFile, lProperties);
					} else {
						mMediaViewer.mTableViewer.update(pFile, null);
					}

				}
					break;
				}
			}
		});
	}

	/**
	 * Asynchronously update the current active view.
	 */
	public void update() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				switch (mCurrentView) {
				case ITEM_VIEW: {
					if (mItemViewer.mTreeViewer.getTree().isDisposed()) {
						return;
					}
					mItemViewer.mTreeViewer.refresh();
				}
					break;
				case MEDIA_VIEW: {
					if (mMediaViewer.mTableViewer.getTable().isDisposed()) {
						return;
					}
					mMediaViewer.mTableViewer.refresh();
				}
					break;
				}
			}
		});
	}

	public void setInput(IXPersonalFeed pFeed) {

		if (mPreparator != null) {
			mPreparator.destroyImageMap();
		}

		mPreparator = new ModelPreparator(pFeed);

		switch (mCurrentView) {
		case ITEM_VIEW: {
			mItemViewer.mTreeViewer.setInput(mPreparator);
			mItemInfoView.clean();
		}
			break;
		case MEDIA_VIEW: {
			MediaTableProvider lLabelProvider = new MediaTableProvider(mPreparator);
			mMediaViewer.mTableViewer.setLabelProvider(lLabelProvider);
			mMediaViewer.mTableViewer.setInput(mPreparator);
		}
			break;
		}
	}

	public void clean() {
		if (mPreparator != null) {
			mPreparator.destroyImageMap();
		}
		switch (mCurrentView) {
		case ITEM_VIEW: {
			mItemViewer.mTreeViewer.setInput(null);
			if (mItemInfoView != null) {
				mItemInfoView.clean();
			}
		}
			break;
		case MEDIA_VIEW: {
			mMediaViewer.mTableViewer.setInput(null);
		}
			break;
		}
	}

	public void setSelected(IXFile pFile) {
		StructuredSelection lSelection = new StructuredSelection(pFile);
		switch (mCurrentView) {
		case ITEM_VIEW: {
			mItemViewer.mTreeViewer.setSelection(lSelection);
		}
			break;
		case MEDIA_VIEW: {
			this.mMediaViewer.mTableViewer.setSelection(lSelection);
		}
			break;
		}
	}

	/**
	 * The model preparator accepts a feed and preps it to be viewed without
	 * minimal processing. The feed data is pre-processed and can be retrieved
	 * for each of the applicable columns.
	 */
	class ModelPreparator {

		protected IXPersonalFeed mFeed;

		protected HashMap mImageMap;

		protected Object[] mElements;

		public ModelPreparator(IXPersonalFeed pFeed) {
			mFeed = pFeed;
			mImageMap = buildImageMap(mFeed);
			updateModel();
		}

		/**
		 * When the model changes, we should call this on the preparator. TODO
		 * The preparator should really listen to model changes.
		 */
		public void updateModel() {
			mElements = mFeed.getMergedArray(true);
		}

		public IXPersonalFeed getFeed() {
			return mFeed;
		}

		public int indexOf(IXFile pFile) {
			for (int i = 0; i < mElements.length; i++) {
				if (mElements[i].equals(pFile)) {
					return i;
				}
			}
			return 0;
		}

		/**
		 * Build a map for the required view for this feed. (File extensions in
		 * the enclosures).
		 * 
		 * @return
		 */
		public HashMap buildImageMap(IXPersonalFeed lFeed) {
			HashMap<String,Image> lImageMap = new HashMap<String,Image>();

			mElements = lFeed.getMergedArray(true);
			for (int i = 0; i < mElements.length; i++) {
				IXFile lFile = (IXFile) mElements[i];
				String lFileName = null;
				if (lFile.getFile() != null) {
					lFileName = lFile.getName();
				}
				if (lFileName != null && lFileName.length() > 0) {
					String lExtension = Util.stripName(lFileName);
					if (lExtension != null && lImageMap.get(lExtension) == null) {
						Image lImage = getImage(lExtension);
						if (lImage != null) {
							lImageMap.put(lExtension, lImage);
						}
					}
				}
			}
			return lImageMap;
		}

		public Image getImage(IXPersonalEnclosure pEnclosure) {
			return getImage((IXFile) pEnclosure);
		}

		public Image getImage(IXFile pFile) {
			if (pFile.getFile() == null) {
				return null;
			}
			String lName = pFile.getFile().getName();
			String lExtension = Util.stripName(lName);
			if (lExtension != null && mImageMap.containsKey(lExtension)) {
				return (Image) mImageMap.get(lExtension);
			}
			return null;
		}

		public void destroyImageMap() {
			Collection lValues = mImageMap.values();
			Iterator lIt = lValues.iterator();
			while (lIt.hasNext()) {
				Image lImage = (Image) lIt.next();
				lImage.dispose();
			}
		}

		public IXPersonalEnclosure getEnclosure(IXItem pItem) {
			Iterator lItEncl = mFeed.getEnclosureIterator();
			IXPersonalEnclosure lEncl = null;
			boolean lMatch = false;
			while (lItEncl.hasNext()) {
				lEncl = (IXPersonalEnclosure) lItEncl.next();
				try {
					if (lEncl.getItem().equals((IXItem) pItem)) {
						lMatch = true;
						break;
					}
				} catch (XEnclosureException e) {
					// Item is not available.
				}
			}
			if (lMatch) {
				return lEncl;
			} else {
				return null;
			}
		}

		private Image getImage(String lExtension) {
			Program lProgram = ContentAssociation.getOSAssociation(lExtension);
			if (lProgram != null) {
				try {
					ImageData lData = lProgram.getImageData();
					if (lData != null) {
						return new Image(null, lData);
					}
				} catch (ArrayIndexOutOfBoundsException aio) {
					// CB TODO This is a SWT bug, have to report with SWT.
				}
			}
			// CB TODO, Should perhaps return a default icon.
			return null;
		}
	}

	public boolean isStatic() {
		return false;
	}

	public void setStatic(boolean pStatic) {
	}
}
