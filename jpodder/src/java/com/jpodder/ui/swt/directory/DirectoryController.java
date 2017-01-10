package com.jpodder.ui.swt.directory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;

import com.jpodder.data.content.ContentAssociation;
import com.jpodder.data.feeds.XPersonalFeed;
import com.jpodder.data.feeds.list.XPersonalFeedList;
import com.jpodder.ui.swt.IController;
import com.jpodder.ui.swt.IView;
import com.jpodder.ui.swt.feeds.FeedController;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class DirectoryController implements IController {

	Logger mLog = Logger.getLogger(getClass().getName());

	protected DirectoryView mView;

	private FeedController mFeedController;

	protected String mPreviousInstruction;

	public DirectoryController() {
	}

	public void setView(IView pView) {
		mView = (DirectoryView) pView;
		initializeUI();
	}

	public void setController(FeedController pFeedController) {
		mFeedController = pFeedController;
	}

	private String lPreviousText;

	private void initializeUI() {

		
		if(mView.mBrowser != null){
			mView.mBrowser.addLocationListener(new LocationListener() {
				public void changing(LocationEvent pEvent) {
					String lLocation = pEvent.location;
				}

				public void changed(LocationEvent pEvent) {
				}
			});
		
		mView.mBrowser.addProgressListener(new ProgressListener() {
			public void changed(ProgressEvent pEvent) {
				// mLog.info("Progress:" + pEvent.current);
			}

			public void completed(ProgressEvent pEvent) {
				mLog.info("Directory, loading completed");
			}
		});
		
		mView.mBrowser.addTitleListener(new TitleListener(){

			public void changed(TitleEvent arg0) {
				processStatus(arg0.title);
			}
			
		});
		mView.mBrowser.addStatusTextListener(new StatusTextListener() {
			public void changed(StatusTextEvent pEvent) {

				if(pEvent.text.length() > 0 ){
					if( pEvent.text.startsWith("Shortcut")){
//						mView.getHotFeed().add(pEvent.text + "##" + pEvent.toString());	
					}
					
					processStatus(pEvent.text);	
					
				}
				
			}
		});
		}
	}

	public void processStatus(String pEventText) {
		mView.mStatus.setText("");
		InstructionParser lParser = new InstructionParser();
		String lText = pEventText;
		if (lText.length() == 0
				|| (lPreviousText != null && lPreviousText.equals(lText))) {
			return;
		}
		
		// 1. Is it an instruction. 
		// CB TODO, the Instruction syntax is a hack for now, need
		// to generalize this as this technique can be used in other places, 
		// like links which switch the tabs in the application. Very cool. 
		
		if(lText.startsWith("subscribe")){
			lPreviousText = lText;
			String lFeedURL = lText.substring(lText.indexOf("=") + 1, lText.length());
			mLog.info("Received instruction to subscribe" + lFeedURL);
			subscribe(lFeedURL);
			return;
		}
		
		// 1. Is the status text XML (RPC response?)
		if (lText.startsWith("<") && lParser.parse(lText)) {
			lPreviousText = lText;
			String lOutputText = lParser.getOutput();
			lOutputText = makeJSString("feedsdisplay", lOutputText);

			if (mView.mBrowser.execute(lOutputText)) {
				mView.mBrowser.execute("signal('');");
				mView.mStatus.setText("instruction received");
			} else {
				mView.mStatus.setText("Error: Invalid instruction");
				debug(lOutputText);
			}
			return;
		}
		
		mView.mStatus.setText(lText);
		// debug(lText);
		
		// Hot Feed handling. 
		try{
			URL lUrl = new URL(lText);
//			mView.getHotFeed().add(lUrl.toExternalForm());
		}catch(MalformedURLException mue){
		}

	}

	public String makeJSString(String pDivider, String pText) {
		StringBuffer lExecString = new StringBuffer();
		lExecString.append("document.getElementById('" + pDivider
				+ "').innerHTML = '");
		int last = pText.lastIndexOf(">") + 1;
		lExecString.append(pText.substring(0, last));
		lExecString.append("';");
		return lExecString.toString();
	}

	public void debug(String pResult) {
		try {
			File lOutputFile = File.createTempFile("parsing", ".htm");
			FileWriter lWriter = new FileWriter(lOutputFile);
			lWriter.write(pResult);
			lWriter.close();
			ContentAssociation.openProgram("htm", lOutputFile.getPath());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subscribe(String pUrlString) {

		if (XPersonalFeedList.getInstance().indexOfUrl(pUrlString) < 0) {
			try {
				URL url = new URL(pUrlString);
				XPersonalFeed feed = new XPersonalFeed();
				feed.setURL(url);
				feed.setSubscribed(true);
				feed.setMaxDownloads(1);
				mFeedController.showFeedManager(true, null, feed);

			} catch (java.net.MalformedURLException mue) {
				mLog.warn("subscribe()" + mue.getMessage());
			}
		} else {
			mLog.warn("subscribe(): URL: " + pUrlString + " already in list");
		}
	}

}
