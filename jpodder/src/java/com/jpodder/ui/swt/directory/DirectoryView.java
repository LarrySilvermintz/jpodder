package com.jpodder.ui.swt.directory;

import java.io.File;
import java.util.Iterator;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

import com.jpodder.FileHandler;
import com.jpodder.ui.swt.IView;
import com.jpodder.ui.swt.UIDnD;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.util.Messages;

/**
 * CHANGE THIS AFTER TESTING:
 * 1. The java script libraries path in directory.htm
 * 2. The path to directory.htm in directoryview. 
 */

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class DirectoryView implements IView {

	protected Composite mView;

	protected Browser mBrowser = null;

	protected Label mStatus;

	protected Combo lCombo;

//	protected List mHotFeedList; 
	
	protected Button mReloadButton;
	
	protected String mBrowserText;

	static final String DIGITALPODCAST_ENTRY = "DigitalPodcast";

	static final String ADAM_ENTRY = "IndiePodder";

	static final String JPODDER_ENTRY = "jPodder-live";

	static final String PICKLE_ENTRY = "Podcast Pickles";

	static final String ALLEY_ENTRY = "Podcast Alley";

	static TreeMap mDirectories = new TreeMap();
	static {
		// mDirectories
		// .put("jPodder-test",
		// "d:\\my
		// workspace\\jpodder3\\jpodder\\src\\scripts\\directory\\directory.htm");

		mDirectories.put(DIGITALPODCAST_ENTRY, "http://www.digitalpodcast.com");
		mDirectories.put(PICKLE_ENTRY, "http://www.podcastpickles.com");
		mDirectories.put(ALLEY_ENTRY, "http://www.podcastalley.com");
//		mDirectories.put(JPODDER_ENTRY, FileHandler.sBinDirectory.getPath()
//				+ File.separator + "directory.htm");
		mDirectories.put(ADAM_ENTRY, "http://www.indiepodder.org/");
	}

	final String DEFAULT_ENTRY = DIGITALPODCAST_ENTRY;

	public DirectoryView() {
		mView = new Composite(UILauncher.getInstance().getShell(), SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		mView.setLayout(gridLayout);

		// ------------- The Action bar

		Group lButtonGroup = new Group(mView, SWT.SHADOW_IN);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		lButtonGroup.setLayoutData(gridData);

		RowLayout lButtonRowLayout = new RowLayout(SWT.HORIZONTAL);
		lButtonRowLayout.wrap = false;
		lButtonRowLayout.pack = true;
		lButtonGroup.setLayout(lButtonRowLayout);

		lCombo = new Combo(lButtonGroup, SWT.NONE | SWT.READ_ONLY);
		Iterator it = mDirectories.keySet().iterator();
		while (it.hasNext()) {
			String element = (String) it.next();
			lCombo.add(element);
		}
		// >> PARAMETER
		lCombo.select(lCombo.indexOf(DEFAULT_ENTRY));

		lCombo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if(mBrowser == null){
					return;
				}
				int lIndex = lCombo.getSelectionIndex();
				String lSelection = lCombo.getItem(lIndex);
				mBrowser.setUrl((String) mDirectories.get(lSelection));
			}
		});

		mReloadButton = new Button(lButtonGroup, SWT.PUSH);
		mReloadButton.setText(Messages.getString("directory.reload"));
		mReloadButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event pEvent) {
				if(mBrowser == null){
					return;
				}
				int lIndex = lCombo.getSelectionIndex();
				if (lIndex >= 0) {
					String lSelection = lCombo.getItem(lIndex);
					mBrowser.setUrl((String) mDirectories.get(lSelection));
				}
			}
		});

		// ------------- The directory Browser

		SashForm mSashForm = new SashForm(mView, SWT.HORIZONTAL);
// remove later.
		//		mSashForm.setLayout(new GridLayout());
//		mSashForm.setWeights(new int[] {100,100});
		gridData = new GridData(GridData.FILL_BOTH);
		mSashForm.setLayoutData(gridData);
		
		try {
			mBrowser = new Browser(mSashForm, SWT.NONE);
			GridData lData = new GridData(GridData.FILL_BOTH);
			mBrowser.setLayoutData(lData);

			mBrowser.addStatusTextListener(new StatusTextListener() {
				public void changed(StatusTextEvent pEvent) {
					String mBrowserText = pEvent.text;
				}
			});
			// >> PARAMETER
			mBrowser.setUrl((String) mDirectories.get(DEFAULT_ENTRY));

		}catch(SWTError e){
		
		}
		
//		mHotFeedList = new List(mSashForm, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
//        GridData lData = new GridData(GridData.GRAB_HORIZONTAL);
//        lData.horizontalSpan = 2;
//        lData.widthHint = 220;
//        mHotFeedList.setLayoutData(lData);
		
		
		mStatus = new Label(mView, SWT.BORDER);
		GridData lData = new GridData(GridData.FILL_HORIZONTAL);
		mStatus.setLayoutData(lData);
		
		int operations = DND.DROP_LINK | DND.DROP_COPY | DND.DROP_TARGET_MOVE;
		
		UIDnD.addSource(mStatus,operations);
		
	}

//	public List getHotFeed(){
//		return mHotFeedList;
//	}
	
	public Composite getView() {
		return mView;
	}

	public boolean isStatic() {
		return true;
	}

	public void setStatic(boolean pStatic) {
		// This view is always re-generated.
	}
}
