package com.jpodder.ui.swt.feeds;

import java.text.ParseException;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.XFeedException;
import com.jpodder.html.Attribute;
import com.jpodder.html.HTMLLogic;
import com.jpodder.html.Tag;
import com.jpodder.util.Messages;
import com.jpodder.util.Util;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */

/**
 * The find infoview is a little browser, which shows some of the RSS channel
 * information in a formatted way.
 * CB TODO New: load a page which invokes java script and XSLT to
 * render this page. The static creation of HTML, is not the
 * preferred way. besides the used classes are hard coded, the
 * style can't be changed easily.
 * 
 * See this bug below for annoying click sound when setting text in the browser.
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=66101
 */

public class FeedInfoView {

    Browser mBrowser = null;
    
    private static String MSG_NO_RSS;
    private static String MSG_LOADING;
    private static String MSG_WELCOME;
    private static String MSG_EMPTY_SELECTION;
    private static String MSG_NO_NETWORK;
    private static String MSG_FORMAT_ERROR;
    
    public FeedInfoView(Composite pParent) {
    	
    	try{
    		mBrowser = new Browser(pParent, SWT.BORDER);
    	}
        catch(SWTError e){
        	return;
        }
        MSG_NO_RSS = Messages.getString("feedInfoview.msg.preview");
        MSG_LOADING = Messages.getString("feedInfoview.msg.loading");
        MSG_WELCOME = Messages.getString("feedInfoview.msg.loading.more");
        MSG_EMPTY_SELECTION = Messages.getString("feedInfoview.msg.select");
        MSG_NO_NETWORK = Messages.getString("feedInfoview.msg.connect.error");
        MSG_FORMAT_ERROR = Messages.getString("feedInfoview.msg.format.error");
        
        mBrowser.setText(formatMessage(MSG_EMPTY_SELECTION, HTMLLogic.STYLE_SHEET));
    }

    public void formatFeed(IXPersonalFeed pFeed, String pStylesheet) {
    	
    	if( Configuration.getInstance().getAutoPreview()){
    		mBrowser.setText(formatMessage(MSG_LOADING, HTMLLogic.STYLE_SHEET));
    	}
    	if(mBrowser == null){
    		return;
    	}
    	try {
            Tag html = new Tag(Tag.T_HTML);
            html.addAttribute(new Attribute(Attribute.A_XMLNS,
                    "http://www.w3.org/1999/xhtml"));
            Tag head = new Tag(Tag.T_HEAD);
            Tag meta = new Tag(Tag.T_META);
            meta.addAttribute(new Attribute(Attribute.A_HTTP_EQUIV,
                    "Content-Type"));
            meta.addAttribute(new Attribute(Attribute.A_CONTENT_TYPE,
                    "text/html; charset=utf-8"));
            Tag link = new Tag(Tag.T_LINK);
            link.addAttribute(new Attribute(Attribute.A_REL, "stylesheet"));
            link.addAttribute(new Attribute(Attribute.A_TYPE, "text/css"));
            link.addAttribute(new Attribute(Attribute.A_HREF, pStylesheet));
            head.add(meta);
            head.add(link);
            html.add(head);

            Tag body = new Tag(Tag.T_BODY);
            Tag div_1 = new Tag(Tag.T_DIV);
            div_1.addAttribute(new Attribute(Attribute.A_ID, "box"));
            Tag div_2 = new Tag(Tag.T_DIV);
            div_2.addAttribute(new Attribute(Attribute.A_CLASS, "content"));
            div_1.add(div_2);
            Tag title = new Tag(Tag.T_H2);
            title.add(pFeed.getTitle());
            div_2.add(title);

            // -- Image and description table.
            Tag table = new Tag(Tag.T_TABLE);
            Tag row = new Tag(Tag.T_TR);
            Tag cell = new Tag(Tag.T_TD);
            cell.addAttribute(new Attribute(Attribute.A_CLASS, "image"));
            
            boolean lImagePresent = false;
            String lImage = null;
            try{
                lImage = pFeed.getIToonsImage();
            	
            }catch(XFeedException e){
            	
            }
            if (lImage != null && lImage.length() > 0) {
                lImagePresent = true;
            } else {
                lImage = pFeed.getImage();
                if (lImage != null && lImage.length() > 0) {
                    lImagePresent = true;
                }
            }
            if (lImagePresent) {
                Tag img = new Tag(Tag.T_IMG);
                img.addAttribute(new Attribute(Attribute.A_SRC, lImage));
                img.addAttribute(new Attribute(Attribute.A_WIDTH, "150"));
                img.addAttribute(new Attribute(Attribute.A_HEIGHT, "150"));
                cell.add(img);
            }

            Tag cell2 = new Tag(Tag.T_TD);
            cell2.addAttribute(new Attribute(Attribute.A_CLASS, "description"));
            cell2.add(pFeed.getDescription());
            row.add(cell);
            row.add(cell2);
            table.add(row);
            div_2.add(table);

            String lLink = pFeed.getLink();
            if (lLink != null) {
                Tag span = new Tag(Tag.T_SPAN);
                span.addAttribute(new Attribute(Attribute.A_CLASS, "link"));
                Tag url = new Tag(Tag.T_A);
                url.addAttribute(new Attribute(Attribute.A_HREF, lLink));
                url.add(lLink);
                span.add(url);
                div_2.add(span);
            }
            try {
                String lExplicit = pFeed.getIToonsExplicit();
                if (lExplicit != null) {
                    Tag span = new Tag(Tag.T_SPAN);
                    span.addAttribute(new Attribute(Attribute.A_CLASS, "link"));
                    if ("yes".compareToIgnoreCase(lExplicit) == 0) {
                        span.add("Explicit Content (Not for kids or easily offended)");
                    } else {
                        span.add("Content is not explicit");
                    }
                    div_2.add(span);
                }
            } catch (XFeedException e) {
                // we skip this tag.
            }

            Tag span = new Tag(Tag.T_SPAN);
            span.addAttribute(new Attribute(Attribute.A_CLASS, "link"));
            int lEnclCount = pFeed.getEnclosureSize();
            span.add("Episodes: " + lEnclCount);
            div_2.add(span);
            
            try {
                String lCategory = pFeed.getIToonsCategory();
                if(lCategory != null){
                    Tag spancat = new Tag(Tag.T_SPAN);
                    spancat.addAttribute(new Attribute(Attribute.A_CLASS, "link"));
                	spancat.add("Category: " + lCategory);    
                	div_2.add(spancat);
                }else{
                    String lRSSCategory = pFeed.getCategory();
                    if(lRSSCategory != null){
                        Tag spancat = new Tag(Tag.T_SPAN);
                        spancat.addAttribute(new Attribute(Attribute.A_CLASS, "link"));
                    	spancat.add("Category: " + lRSSCategory);
                        div_2.add(spancat);
                    }
                }
            } catch (XFeedException e) {
                // We skip this tag.
            }
            
            try{
            Tag div_4 = new Tag(Tag.T_DIV);
            div_4.addAttribute(new Attribute(Attribute.A_CLASS, "pubdate"));
            if (pFeed.getPubDate() != null) {
                String lDateString = pFeed.getPubDate();
                try {
                    Date lDate = Util.resolvedDateRFC822(lDateString);
                    lDateString = Util.formatDate(lDate, "EEEE, MMM d hh:mm a");
                } catch (ParseException e) {
                } catch(IllegalArgumentException e){
                }
                
                div_4.add(lDateString);
            }
            div_2.add(div_4);
            } catch (XFeedException e) {
                // We skip this tag.
            }

            body.add(div_1);
            html.add(body);
            mBrowser.setText(html.toString());
        } catch (XFeedException e) {
        		mBrowser.setText(formatMessage(MSG_FORMAT_ERROR, HTMLLogic.STYLE_SHEET));	
        }
    }
    
    public String formatMessage(String pMessage, String pStylesheet){
        
    	Tag html = new Tag(Tag.T_HTML);
        html.addAttribute(new Attribute(Attribute.A_XMLNS,
                "http://www.w3.org/1999/xhtml"));
        Tag head = new Tag(Tag.T_HEAD);
        Tag meta = new Tag(Tag.T_META);
        meta.addAttribute(new Attribute(Attribute.A_HTTP_EQUIV,
                "Content-Type"));
        meta.addAttribute(new Attribute(Attribute.A_CONTENT_TYPE,
                "text/html; charset=utf-8"));
        Tag link = new Tag(Tag.T_LINK);
        link.addAttribute(new Attribute(Attribute.A_REL, "stylesheet"));
        link.addAttribute(new Attribute(Attribute.A_TYPE, "text/css"));
        link.addAttribute(new Attribute(Attribute.A_HREF, pStylesheet));
        head.add(meta);
        head.add(link);
        html.add(head);

        Tag body = new Tag(Tag.T_BODY);
        Tag div = new Tag(Tag.T_DIV);
        div.addAttribute(new Attribute(Attribute.A_CLASS, "message"));
        div.add(pMessage);
        body.add(div);
        html.add(body);
        return html.toString();
    }
    
    public void setLoading() {
        if( mBrowser == null){
        	return;
        }
    	mBrowser.setText(formatMessage(MSG_LOADING, HTMLLogic.STYLE_SHEET));
    }
    
    public void setNoNetwork() {
        if( mBrowser == null){
        	return;
        }
    	mBrowser.setText(formatMessage(MSG_NO_NETWORK, HTMLLogic.STYLE_SHEET));
    }
    
    public void setFormatError() {
        if( mBrowser == null){
        	return;
        }
    	mBrowser.setText(formatMessage(MSG_FORMAT_ERROR, HTMLLogic.STYLE_SHEET));
    }
    
    public void clearInfo() {
        if( mBrowser == null){
        	return;
        }
    	mBrowser.setText(formatMessage(MSG_EMPTY_SELECTION, HTMLLogic.STYLE_SHEET));
    }
}
