package com.jpodder.util;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import javax.swing.JFormattedTextField;


/**
 * A custom formatter for the formatted textfield.
 * This formatter attempts to create an URL of the a provided 
 * string. a properly formatted URL is returned if it succeeds.
 * otherwise the field is displayed in a different color. 
 */
public class URLFormatter extends javax.swing.JFormattedTextField.AbstractFormatter {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5121874536501099626L;
	private final JFormattedTextField field;
    private static final String FTP_PROTOCOL_STRING = "ftp";
    private static final String HTTP_PROTOCOL_STRING = "http";
    public static final short FTP_PROTOCOL = 0;
    public static final short HTTP_PROTOCOL = 1;
    private static final String PROTOCOLS[] = {FTP_PROTOCOL_STRING, HTTP_PROTOCOL_STRING};
    
    private String mProtocol;
    
    public URLFormatter( JFormattedTextField field) {
        this(field, HTTP_PROTOCOL);
    }
    
    /**
     * @param field
     */
    public URLFormatter( JFormattedTextField field, short pProtocol) {
        this.field = field;
        if(pProtocol >= 0 && pProtocol < PROTOCOLS.length){
            mProtocol = PROTOCOLS[pProtocol];
        }else{
            mProtocol = HTTP_PROTOCOL_STRING;
        }
    }

    /**
     * Formats a String to a valid URL.
     * if the String is not a valid URL, we add
     * HTTP:// and try again, it fails we return
     * the orginal textString.
     * TODO Should evaluate if this is really what we want.  
     * 
     * @param text
     * @return Object
     * @throws ParseException
     * @see javax.swing.JFormattedTextField.AbstractFormatter#stringToValue(java.lang.String)
     */
    public Object stringToValue(String text) throws ParseException {
        URL url = null;
        try {
            url = new URL(text);
            return url;
        } catch (MalformedURLException e) {
            // try to pre-pend HTTP://
            if(!text.startsWith(mProtocol + "://")){
                text = mProtocol + "://" + text;
                try {
                    url = new URL(text);
//                    field.commitEdit();
                    return url;
                } catch (MalformedURLException e1) {
                    // we give up, no way we can format this. 
                    return text;
                }            
            }
            return text;            
        }
    }

    /**
     * Formats a value to a String. 
     * The value is assumed to be an URL.
     * 
     * 
     * @param value
     * @return
     * @throws ParseException
     * @see javax.swing.JFormattedTextField.AbstractFormatter#valueToString(java.lang.Object)
     */
    public String valueToString(Object value) throws ParseException {
        field.setForeground(Color.blue);
        if (value == null) {
            return "";
        }
        if (value instanceof URL) {
            return ((URL) value).toExternalForm();
        } else {
            if(value instanceof String){
                String text = (String)value;
                URL url = null;
                try {
                    url = new URL(text);
                    return url.toExternalForm();
                } catch (MalformedURLException e) {
                    // try to pre-pend HTTP://
                    if(!text.startsWith(mProtocol + "://")){
                        text = mProtocol + "://" + text;
                        try {
                            url = new URL(text);
                            return url.toExternalForm();
                        } catch (MalformedURLException e1) {
                            // we give up, no way we can format this. 
                            return text;
                        }            
                    }
                }    
            }            
            field.setForeground(Color.red);
            return (String) value;
        }
    }
}