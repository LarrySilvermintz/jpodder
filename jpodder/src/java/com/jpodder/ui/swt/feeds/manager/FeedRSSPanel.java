package com.jpodder.ui.swt.feeds.manager;

import java.net.MalformedURLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.jpodder.data.feeds.IXPersonalFeed;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class FeedRSSPanel extends Composite {
    Browser mBrowser;
    
    public FeedRSSPanel(Composite pParent){
        super(pParent, SWT.NONE);
        setLayout(new FillLayout());
    	
        try{
    		mBrowser = new Browser(this, SWT.BORDER);
    	}
        catch(SWTError e){
        	return;
        }
    }
    
    public void fill(IXPersonalFeed pFeed){
        if(pFeed.getFile() != null && mBrowser != null ){
            try {
                mBrowser.setUrl(pFeed.getFile().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
