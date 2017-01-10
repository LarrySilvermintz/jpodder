package com.jpodder.html;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

public class Tag extends LinkedList {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4930325395584807539L;

	Logger mLog = Logger.getLogger(getClass().getName());

    public static String T_HTML = "html";
    public static String T_BODY = "body";
    public static String T_HEAD = "head";
    public static String T_META = "meta";
    public static String T_LINK = "link";

    public static String T_DIV = "div";
    public static String T_SPAN = "span";

    // --- Headers
    public static String T_H1 = "h1";
    public static String T_H2 = "h2";
    public static String T_H3 = "h3";
    public static String T_H4 = "h4";

    // --- Tables
    public static String T_TABLE = "table";
    public static String T_TR = "tr";
    public static String T_TD = "td";
    public static String T_A = "a";
    public static String T_IMG = "img";

    private Attributes attributes; // tag attributes
    private String name; // name of tag
    private boolean close; // closing tag y/n

    public Tag(String name) {
        super();
        close = true;
        this.name = name.toLowerCase();
        attributes = new Attributes();
    }

    public Tag(String name, boolean close) {
        this(name);
        this.close = close;
    }

    public Tag(String name, Attributes attr) {
        this(name);
        attributes = attr;
    }

    public Tag(String name, Attributes attr, boolean close) {
        this(name, attr);
        this.close = close;
    }

    public Tag(String name, String attr) {
        this(name);
        attributes = new Attributes(attr);
    }

    public Tag(String name, String attr, boolean close) {
        this(name, attr);
        this.close = close;
    }

    public Tag(String name, Attributes attr, List content) {
        super(content);
        this.name = name.toLowerCase();
        attributes = attr;
    }

    public Tag(String name, Attributes attr, List content, boolean close) {
        this(name, attr, content);
        this.close = close;
    }

    /**
     * Get the value of close.
     * 
     * @return Value of close.
     */
    public boolean getClose() {
        return close;
    }

    /**
     * Set the value of close.
     * 
     * @param v
     *            Value to assign to close.
     */

    public void setClose(boolean v) {
        this.close = v;
    }

public String toString() {
        StringBuffer out = new StringBuffer("<" + name);
        out.append(attributes != null ? attributes.toString() : "");
        out.append(">");
        // content
        ListIterator iterator = super.listIterator();
        while (iterator.hasNext()) {
            Object lObject = iterator.next();
            if(lObject != null){
            out.append(lObject.toString());
            }
        }
        if (close)
            out.append("</" + name + ">\n");
        return out.toString();
    }    /**
             * Get the value of attributes.
             * 
             * @return Value of attributes.
             */
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * Set the value of attributes.
     * 
     * @param v
     *            Value to assign to attributes.
     */
    public void setAttributes(Attributes v) {
        this.attributes = v;
    }

    public void addAttribute(Attribute attr) {
        attributes.add(attr);
    }

}
