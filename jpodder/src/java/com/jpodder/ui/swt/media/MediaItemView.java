package com.jpodder.ui.swt.media;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.jpodder.data.feeds.IXItem;
import com.jpodder.data.feeds.XItemException;
import com.jpodder.html.Attribute;
import com.jpodder.html.Tag;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class MediaItemView {
    
    private String NO_SELECTION = "<html><body>Select an Item to be displayed</body></html>";
    Browser mBrowser;
    public MediaItemView(Composite pParent) {
        mBrowser = new Browser(pParent, SWT.BORDER);
        GridData lData = new GridData(GridData.FILL_BOTH);
        mBrowser.setLayoutData(lData);
        mBrowser.setText(NO_SELECTION);
    }
    
    public void clean(){
        mBrowser.setText("");
    }
    
    public void formatItem(IXItem pItem, String pStylesheet) {
        
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
        
        try{
        	div_2.add(pItem.getDescription());	
        }
        catch(XItemException e){
        	div_2.add("No Description");
        }

        body.add(div_1);
        html.add(body);
            mBrowser.setText(html.toString());
    }
}
