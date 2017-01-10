package com.jpodder.ui.swt.remote;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.jpodder.ui.swt.feeds.FeedController;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class RPCServerController {

	Logger mLog = Logger.getLogger(getClass().getName());

	private FeedController mFeedController;

	public RPCServerController() {
		initialize();
	}

	public void initialize() {
		mLog.info("<init>");
	}

	public void setControllers(FeedController pFeedController) {
		mFeedController = pFeedController;
	}

	public void addFeed(String lRPCParam) {
		// 1. We check the string, is it wellformed.
		mLog.info("Received: " + lRPCParam);
		// First we check if the received parameter is
		// podcast://

		if (lRPCParam.startsWith("podcast")) {
			String lRestString = lRPCParam.substring("podcast".length());
			lRPCParam = "http" + lRestString;
		} else {
			// AS ToDo: this is a complete hack !!!!!
			String lAtomToken = "<atom:link";
			int lStart = lRPCParam.indexOf(lAtomToken);
			int lEnd = lRPCParam.indexOf("/>", lStart);
			if (lStart > 0 && lEnd > 0) {
				String lToken = getAttribute("href", lRPCParam.substring(lStart
						+ lAtomToken.length(), lEnd));
				if (lToken != null) {
					lRPCParam = lToken;
				}
			}
		}
		try {
			final URL lFeedUrl = new URL(lRPCParam);
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Shell[] lShells = Display.getDefault().getShells();
					if(lShells != null ){
						for (int i = 0; i < lShells.length; i++) {
							Shell shell = lShells[i];
							shell.setActive();
						}
					}
					mFeedController.showFeedManager(true, lFeedUrl.toExternalForm());
				}
			});
		} catch (MalformedURLException e) {
			mLog.warn(e.getMessage());
			// We exit without adding the feed.
		}
	}

	private String getAttribute(String pName, String pSource) {
		mLog.info("getAttribute(), source " + pSource);
		String lReturn = null;
		int lStart = 0;
		String lRemaining = pSource;
		while (true) {
			lStart = lRemaining.indexOf(pName);
			if (lStart < 0) {
				break;
			} else {
				// Get the rest of the string without the attribute name
				lRemaining = lRemaining.substring(lStart + pName.length());
				mLog.info("getAttribute(), found attribute, remaining: "
						+ lRemaining);
				// Look for the next '=' with only whitespace in between
				int i = 0;
				boolean lFound = false;
				while (true) {
					char lNext = lRemaining.charAt(i);
					mLog.info("getAttribute(), next char: " + lNext);
					if (lNext == '=') {
						mLog.info("getAttribute(), '=' found in remaining: "
								+ lRemaining);
						lFound = true;
						break;
					} else if (!Character.isWhitespace(lNext)) {
						break;
					}
					i++;
				}
				if (lFound) {
					lRemaining = lRemaining.substring(i + 1).trim();
					lStart = lRemaining.indexOf("\"");
					int lEnd = lRemaining.indexOf("\"", lStart + 1);
					mLog.info("getAttribute(), remaining after '=': "
							+ lRemaining + ", start: " + lStart + ", end: "
							+ lEnd);
					if (lStart >= 0 && lEnd >= 0) {
						lReturn = lRemaining.substring(lStart + 1, lEnd);
						break;
					}
				}
			}
		}
		mLog.info("getAttribute(), return: " + lReturn);
		return lReturn;
	}
}
