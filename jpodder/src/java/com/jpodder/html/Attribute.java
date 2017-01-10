package com.jpodder.html;

public class Attribute {
    
    public static String A_XMLNS = "xmlns";
    public static String A_ID = "id";    
    public static String A_CLASS = "class";
    public static String A_HTTP_EQUIV = "http-equiv";
    public static String A_CONTENT_TYPE = "content";
    public static String A_REL = "rel";
    public static String A_TYPE = "type";
    public static String A_HREF = "href";
    public static String A_ALIGN = "align";
    public static String A_SRC = "src";
    public static String A_ALT = "alt";
    public static String A_WIDTH = "width";
    public static String A_HEIGHT = "height";

    private String attribute = null;
    
    /**
       * Get the value of attribute.
       * @return Value of attribute.
       */
    public String getAttribute() {return attribute;}
    
    /**
       * Set the value of attribute.
       * @param v  Value to assign to attribute.
       */
    public void setAttribute(String  v) {this.attribute = v;}
    
    String value = null;
    
    /**
       * Get the value of value.
       * @return Value of value.
       */
    public String getValue() {return value;}
    
    /**
       * Set the value of value.
       * @param v  Value to assign to value.
       */
    public void setValue(String  v) {this.value = v;}
    
    public Attribute() {
	attribute = null;
	value = null;
    }

    public Attribute(String attr) {
	this();
	attribute = attr;
    }

    public Attribute(String attr, String value) {
	this(attr);
	this.value = value;
    }

    public String toString() {
	if (attribute == null)
	    return "";
	if (value != null) {
	    return (attribute.toLowerCase() + "=\"" + value + "\"");
	} else {
	    return attribute.toLowerCase();
	}
    }
}
