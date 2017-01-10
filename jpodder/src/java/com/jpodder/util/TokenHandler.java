package com.jpodder.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.data.feeds.IXFile;
import com.jpodder.data.feeds.XLocalFile;
import com.jpodder.data.feeds.IXPersonalEnclosure;
import com.jpodder.data.id3.ID3Tag;
import com.jpodder.data.id3.ID3Wrapper;
import com.jpodder.xml.RSSWrapper;

/**
 * Convenience class for handling tokens used throughout the application. It
 * holds a token object. The value of the token object is
 */
public class TokenHandler {

    private Logger mLog = Logger.getLogger(getClass().getName());

    public static final String START_TOKEN = "$";

    public static final String END_TOKEN = "$";

    public static String RSS_ITEM_TITLE = Messages.getString("feed.title");

    public static String RSS_ITEM_MANAGINGEDITOR = Messages
            .getString("feed.managingeditor");

    public static String RSS_ITEM_WEBMASTER = Messages
            .getString("feed.webmaster");

    public static String RSS_ITEM_DESCRIPTION = Messages
            .getString("feed.description");

    public static String RSS_ITEM_LINK = Messages.getString("feed.link");

    public static String RSS_ITEM_CATEGORY = Messages
            .getString("feed.category");

    public static String RSS_ITEM_COMMENTS = Messages
            .getString("feed.comments");

    public static String RSS_ITEM_GUID = Messages.getString("feed.guid");

    public static String RSS_ITEM_PUBDATE = Messages.getString("feed.pubdate");

    public static String[] RSS_TOKENS = { RSS_ITEM_TITLE,
            RSS_ITEM_MANAGINGEDITOR, RSS_ITEM_DESCRIPTION, RSS_ITEM_LINK,
            RSS_ITEM_CATEGORY, RSS_ITEM_COMMENTS, RSS_ITEM_GUID,
            RSS_ITEM_PUBDATE, RSS_ITEM_WEBMASTER };

    public static String[] SMART_TOKENS = { Messages
            .getString("tokenhandler.smarttitle") };
    
    RSSWrapper lRSSWrapper;

    /**
     * The iPod displays 16 characters in the playlist for the song title. We
     * introduce tokens which give a uniqe description of the feed in the
     * limited number of characters. Example: Royal Groove , which has a
     * Modified date of : 20 March 2005,
     * 
     * becomes: RG-20-03-2005.
     * 
     * The date takes 11 characters. 6 characters remain to produce a unique
     * recognizable name. For Royal Groove this could be ROYGRO-20-03-2005
     *  
     */

    protected static TokenHandler sSelf;

    protected ArrayList<Token> mTokens = new ArrayList<Token>();

    protected ID3Wrapper mID3Wrapper;

    /**
     * @return TokenHandler An instance of this.
     */
    public static TokenHandler getInstance() {
        if (sSelf == null) {
            sSelf = new TokenHandler();
        }
        return sSelf;
    }

    /**
     * Add tokens of type <code>ipID3Tag</code>.
     * 
     * @param id3List
     */
    public void addID3Tokens(List id3List) {
        for (Iterator iter = id3List.iterator(); iter.hasNext();) {
            ID3Tag element = (ID3Tag) iter.next();
            mTokens.add(new Token(START_TOKEN + "ID3_" + element.getName()
                    + END_TOKEN, element.getDescription()));
        }
    }

    /**
     * Add tokens corresponding to the tags in an RSS feed.
     */
    public void addRSSTokens() {
        for (int i = 0; i < RSS_TOKENS.length; i++) {
            mTokens.add(new Token(START_TOKEN + "RSS_" + RSS_TOKENS[i]
                    + END_TOKEN, ""));
        }
    }

    /**
     * Add smart tokens.
     *  
     */
    public void addSmartTokens() {
        for (int i = 0; i < SMART_TOKENS.length; i++) {
            mTokens.add(new Token(START_TOKEN + "SMART_" + SMART_TOKENS[i]
                    + END_TOKEN, ""));
        }
    }
    
    /**
     * Wrap start and endtokens around a string. 
     * @param pIn
     * @return
     */
    public static String wrapToken(String pIn){
        return START_TOKEN + pIn + END_TOKEN;
    }
    
    

    /**
     * A token can contain a name, a description etc...
     */
    public class Token implements Comparable {

        protected String name;

        protected String description;

        public Token(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String toString() {
            return name + ": " + description;
        }

        /**
         * @return Returns the description.
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description
         *            The description to set.
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }

        /**
         * @param name
         *            The name to set.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Satisfy comparable interface.
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o) {
            int cmp = 0;
            if (o instanceof Token) {
                Token t = (Token) o;
                cmp = name.compareTo(t.name);
            }
            return cmp;
        }
    }

    /**
     * Construct a list of tokens from ID3, RSS and Smart tokens. ID3 tokens are
     * only text based ID3 frames. See http://www.id3.org for more information.
     * 
     * @return Returns the tokens.
     */
    public ArrayList getTokens() {
        if (mTokens.size() == 0) {
            addID3Tokens(ID3Tag.getID3TextTagList());
            addRSSTokens();
            addSmartTokens();
        }
        Collections.sort(mTokens);
        return mTokens;
    }

    /**
     * Analysis the token type and invokes the corresponding wrapper. Currently
     * ID3 and RSS wrappers are supported. Additional tokens are now supported:
     * 
     * @param pToken
     * @param pFile
     * @return String the actual content from the underlying wrapper.
     * @see ID3Wrapper
     * @see RSSWrapper
     */
    public String getContent(IXFile pFile, String pToken) {

        // Check what kind of token this considers.
        if (pToken.startsWith("RSS_")) {
                pToken = pToken.substring("RSS_".length());
                if(lRSSWrapper != null){
                    return lRSSWrapper.getContent(pToken);
                }else{
                    return null;
                }
        }
        if (pToken.startsWith("ID3_")) {
            if (mID3Wrapper != null) {
                pToken = pToken.substring("ID3_".length());
                return mID3Wrapper.getContent(pToken);
            } else {
                throw new java.lang.NullPointerException("ID3 Wrapper not set");
            }
        }
        if (pToken.startsWith("SMART_")) {
            if (pToken.equals("SMART_TITLE")) {
                // Build a readable date. 
                String lDateString = "";
                long lDate;
                if (pFile instanceof IXPersonalEnclosure) {
                    IXPersonalEnclosure lEncl = (IXPersonalEnclosure) pFile;
                    
                    // CB TODO, use the RSS Item Publication date instead of the
                    // file publication on the server.
                    lDate = lEncl.getContentDate();
                    if (lDate == -1) {
                        lDate = lEncl.getFile().lastModified();
                    }
                    lDateString = Util.formatSmartDate(new Date(lDate));
                }
                if (pFile instanceof XLocalFile ) {
                    XLocalFile lFile = (XLocalFile) pFile;
                    lDate = lFile.getFile().lastModified();
                    lDateString = Util.formatSmartDate(new Date(lDate));
                }
                // Build a readable title. 
                String lTitle = lRSSWrapper.getContent("Title");                
                String lNewTitle = "";
                if (lTitle != null && lTitle.length() > 7) {
                    String[] lWords = lTitle.split("\\s");
                    if (lWords.length == 1) {
                        lNewTitle = lWords[0].substring(0, 6);
                    }
                    if (lWords.length == 2) {
                        lNewTitle = (lWords[0].length() >= 3 )? lWords[0].substring(0, 3):lWords[0];
                        lNewTitle += (lWords[1].length() >= 3 )? lWords[1].substring(0, 3):lWords[1];
                    }
                    if (lWords.length >= 3) {
                        lNewTitle = (lWords[0].length() >= 2 )? lWords[0].substring(0, 2):lWords[0];
                        lNewTitle += (lWords[1].length() >= 2 )? lWords[1].substring(0, 2):lWords[1];
                        lNewTitle += (lWords[2].length() >= 2 )? lWords[2].substring(0, 2):lWords[2];
                    }
                } else {
                    mLog.info("SMART_TITLE: RSS title is not available");
                    lTitle = mID3Wrapper.getContent("Title");
                    if( lTitle != null){
                        lNewTitle = lTitle;
                    }
                }
                mLog.info("SMART_TITLE:" + lNewTitle);
                return lNewTitle.toUpperCase() + " " + lDateString;
            }
        }
        return "";
    }

    /**
     * @return ID3Wrapper
     */
    public ID3Wrapper setIDWrapper(File file) {
        mID3Wrapper = new ID3Wrapper(file.getParentFile(), file);
        return mID3Wrapper;
    }

    /**
     * @param file
     * @return ID3Wrapper
     */
    public ID3Wrapper setIDWrapper(IXFile file) {
        mID3Wrapper = new ID3Wrapper(file.getFile().getParentFile(), file
                .getFile());
        return mID3Wrapper;
    }
    
    /**
     * Set and return the RSS wrapper.
     * 
     * @param file
     * @return RSSWrapper The RSS wrapper of <code>null</code> if it's not
     *         available.
     */
    public void setRSSWrapper(IXFile file) {
        if (file.getFeed().getFile() != null) {
            lRSSWrapper = RSSWrapper.getInstance(file.getFeed().getFile());
        } 
    }

    /**
     * Strip the token start and end from a suspected token. if this is not
     * really a token, return the argument value.
     * 
     * 
     * @param pToken
     * @return String The actual stripped value of the token or
     *         <code>null</code> if
     */
    public String replacePlaceHolders(IXFile pFile, String pToken) {

        mLog.info("tag value to be replaced: " + pToken);
        String lReturn = pToken;
        int lStartIndex = 0;
        while (true) {
            lStartIndex = lReturn.indexOf(START_TOKEN, lStartIndex);
            if (lStartIndex < 0) {
                // No or no more place holders found -> exit
                break;
            }
            int lEndIndex = lReturn.indexOf(END_TOKEN, lStartIndex + START_TOKEN.length());
            if (lEndIndex < 0) {
                // No end token found so we exit because we cannot determine the
                // right end
                break;
            }
            String lToken = lReturn.substring(lStartIndex
                    + START_TOKEN.length(), lEndIndex - END_TOKEN.length() + 1);
            // We get the token from the token handler

            String lValue = getContent(pFile, lToken);
            if (lValue == null) {
                // Token is not found and so we drop the place holder and go
                // ahead
                lValue = "";
            }
            if (lReturn.length() > lEndIndex + 1) {
                lReturn = lReturn.substring(0, lStartIndex) + lValue
                        + lReturn.substring(lEndIndex + 1);
            } else {
                lReturn = lReturn.substring(0, lStartIndex) + lValue;
            }
            // Correct the start index so that it will start at the place where
            // the replaced value ended
            // this is done because we do treat the text from the source as
            // black box so that we do not
            // accidentely replace parts that looks like our tokens
            lStartIndex = lEndIndex
                    - (START_TOKEN.length() + lToken.length() + END_TOKEN
                            .length()) + lValue.length();
        }
        mLog.info("tag value returned: " + lReturn);
        return lReturn;
    }

}