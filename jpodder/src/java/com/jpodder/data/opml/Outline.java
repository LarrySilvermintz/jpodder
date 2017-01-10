package com.jpodder.data.opml;

import java.net.URL;

/**
 * Holds the enclosure model. It contains the URL, and the enclosure type.
 * It also keeps track if the enclosure was downloaded before.
 */
public class Outline {

    String text;

    private URL url;

    /**
     * Constructor.
     * 
     * @param url
     *            URL
     * @param text
     *            String
     */
    public Outline(String text, URL url) {
        this.text = text;
        this.url = url;
    }

    /**
     * Get the outline text
     * 
     * @return String
     */
    public String getText() {
        return this.text;
    }

    /**
     * Get the Outline URL.
     * 
     * @return URL
     */
    public URL getURL() {
        return this.url;
    }

    public String toString() {
//        super.toString();
        return text;
    }
    
}