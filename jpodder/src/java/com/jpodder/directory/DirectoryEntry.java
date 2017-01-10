package com.jpodder.directory;
/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */

/**
 * Simple holder of a directory Entry.
 * 1. The URL of the entry (Feed)
 * 2. The description
 * 3. The title
 */
public class DirectoryEntry {
    String mDescription;
    String mTitle;
    String mURL;
    
    public DirectoryEntry(){
        
    }
    public String getDescription() {
        return mDescription;
    }
    public void setDescription(String description) {
        mDescription = description;
    }
    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }
    public String getURL() {
        return mURL;
    }
    public void setURL(String murl) {
        mURL = murl;
    }
}
