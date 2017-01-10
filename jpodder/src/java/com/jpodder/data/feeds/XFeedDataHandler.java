package com.jpodder.data.feeds;

import java.io.File;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IDataHandler;
import com.jpodder.data.feeds.list.XPersonalFeedList;
import com.jpodder.data.id3.ID3TagRewrite;
import com.jpodder.feeds.FeedsDocument;
import com.jpodder.feeds.TEnclosure;
import com.jpodder.feeds.TFeed;
import com.jpodder.feeds.TFeedHead;
import com.jpodder.feeds.TFeeds;
import com.jpodder.feeds.TID3Tag;
import com.jpodder.feeds.TID3Tags;
import com.jpodder.net.NetHEADInfo;

/**
 * Data Handler implementation for the Feeds.
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public class XFeedDataHandler
        implements IDataHandler {

    protected Logger mLog = Logger.getLogger( getClass().getName() );
    private boolean mIsModified = true;
    private XPersonalFeedList mFeedList;
    private File mFile;
    public XFeedDataHandler( XPersonalFeedList pList ) {
        mFeedList = pList;
    }

    public int getIndex() {
        return ConfigurationLogic.FEED_INDEX;
    }

    public boolean isModified() {
        return mFeedList.isModified();
    }

    public String getContent()
            throws Exception {
        mLog.info( "getContent(), return the content of the feed list" );
        FeedsDocument lDocument = FeedsDocument.Factory.newInstance();
        TFeeds lRoot = lDocument.addNewFeeds();
        // TFeeds.Factory.newInstance();
        lDocument.setFeeds( lRoot );
        Iterator i = mFeedList.getFeedIterator();
        while( i.hasNext() ) {
            IXPersonalFeed lFeed = (IXPersonalFeed) i.next();
            TFeed lTFeed = lRoot.addNewFeed();
            lTFeed.setUrl( lFeed.getURL() + "" );
            lTFeed.setFolder( lFeed.getFolder() );
            lTFeed.setTitle( lFeed.getPersonalTitle() );
            lTFeed.setPoll( lFeed.getPoll() );
            lTFeed.setQuality( new BigInteger( lFeed.getQuality() + "" ) );
            lTFeed.setMax( new BigInteger( lFeed.getMaxDownloads() + "" ) );
            // CB TODO, No model
//            lTFeed.setDescription( lFeed.getDescription() );
            lTFeed.setFile( lFeed.getFile() + "" );
            TFeedHead lHead = getTHeadInfo( lFeed.getHEADInfo() );
            if( lHead != null ) {
                lTFeed.setHead( lHead );
            }
            
            
//// TODO ******************************** ENCLOSURES
            //            Iterator j = lFeed.getEnclosureIterator();
//            while( j.hasNext() ) {
//                IXPersonalEnclosure lEnclosure = (IXPersonalEnclosure) j.next();
//                getTEnclosure( lTFeed, lEnclosure );
//            }
            
            List lID3TagRewrite = lFeed.getTagList();
            if( lID3TagRewrite != null){
                TID3Tags lTTags = lTFeed.addNewId3Tags();
                Iterator k = lID3TagRewrite.iterator();
                while (k.hasNext()) {
                    ID3TagRewrite lTag = (ID3TagRewrite)k.next();
                    getTID3(lTTags, lTag);
                }
            }
        }
        StringWriter lOutput = new StringWriter();
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSavePrettyPrint();
        xmlOptions.setSavePrettyPrintOffset(4);
        lDocument.save( lOutput, xmlOptions );
//AS NOTE: This could be dangerouse when we mark the content but the save to the file
//AS       fails down the road
        mFeedList.setUpdated();
        return lOutput.toString();
    }

    public void setContent( String pContent )
            throws Exception {
        mLog.debug( "setContent(), content to be set: " + pContent );
        FeedsDocument lDocument = FeedsDocument.Factory.parse( pContent );
        TFeeds lRoot = lDocument.getFeeds();
        mLog.debug( "Feed Array: " + java.util.Arrays.asList( lRoot.getFeedArray() ) );

        java.util.ArrayList lFeedList = new java.util.ArrayList();
        TFeed[] lTFeeds = lRoot.getFeedArray();
        for( int i = 0; i < lTFeeds.length; i++ ) {
            TFeed lTFeed = lTFeeds[ i ];
            IXPersonalFeed lFeed = new XPersonalFeed();
            lFeedList.add( lFeed );
            try {
                lFeed.setURL(
                    new URL(
                        getStringValue( lTFeed.getUrl(), "this makes the conversion fail" )
                    )
                );
            } catch( MalformedURLException mue ) {
                mLog.warn( "Could not convert given URL: " + lTFeed.getUrl() + " but keep going", mue );
            }
            lFeed.setFolder( getStringValue( lTFeed.getFolder(), "" ) );
            lFeed.setPersonalTitle( getStringValue( lTFeed.getTitle(), true , "" ) );
            lFeed.setSubscribed( lTFeed.getPoll() );
            lFeed.setQuality( getIntValue( lTFeed.getQuality(), -1 ) );
            lFeed.setMaxDownloads( getIntValue( lTFeed.getMax(), 3 ) );
            // CB TODO
//            lFeed.setDescription( getStringValue( lTFeed.getDescription(), "" ) );
            String lTemp = getStringValue( lTFeed.getFile(), "" );
            if( lTemp.length() > 0 ) {
                lFeed.setFile( new File( lTemp ) );
                lFeed.read();
            }
// 			TODO We don't set the date, as we want to reload.            

//            TFeedHead lTHead = lTFeed.getHead();
//            if( lTHead != null ) {
//                lFeed.setHEADInfo( getHeadInfo( lTHead ) );
//            }
                        
////        TODO ******************************** ENCLOSURES
            
//            TEnclosure[] lEnclosures = lTFeed.getEnclosureArray();
//            for( int j = 0; j < lEnclosures.length; j++ ) {
//                IXPersonalEnclosure lEnclosure = getEnclosure( lEnclosures[ j ], lFeed );
//                if( lEnclosure != null ) {
//                    lFeed.addEnclosure( lEnclosure );
//                }
//            }
            
            TID3Tags lID3TagsRoot = lTFeed.getId3Tags();
            if( lID3TagsRoot != null ) {
                TID3Tag[] lID3Tags = lID3TagsRoot.getId3TagArray();
                for( int j = 0; j < lID3Tags.length; j++ ) {
                    lFeed.addId3Tag(
                        new ID3TagRewrite(
                            getStringValue( lID3Tags[ j ].getName(), "" ),
                            getStringValue( lID3Tags[ j ].getValue(), "" )
                        )
                    );
                }
            }
        }
        mFeedList.clear();
        mFeedList.addFeeds( lFeedList );
        mFeedList.setUpdated();
        mLog.info( "setContent(), feed list: " + lFeedList );
    }

    public boolean validate( String pContent, boolean pCompare ) {
//AS TODO: fill in
        return true;
    }

    private NetHEADInfo getHeadInfo( TFeedHead pHead ) {
        return new NetHEADInfo(
            getIntValue( pHead.getHeadSize(), -1 ),
            (String) null,   // Content type is not set yet
            getStringValue( pHead.getHeadEncoding(), "" ),
            getStringValue( pHead.getHeadDate(), "" ),
            pHead.getHeadExpired() + "",
            getStringValue( pHead.getHeadModified(), "" )
        );
    }

    private TFeedHead getTHeadInfo( NetHEADInfo pHead ) {
        TFeedHead lReturn = null;
        if( pHead != null ) {
            lReturn = TFeedHead.Factory.newInstance();
            lReturn.setHeadSize( new BigInteger( pHead.length + "" ) );
            if( pHead.encoding != null ) {
                lReturn.setHeadEncoding( pHead.encoding );
            }
            lReturn.setHeadDate( pHead.getDateString());
            if( pHead.getExpiredString() != null ) {
                lReturn.setHeadExpired( new Boolean( pHead.getExpiredString()).booleanValue() );
            }
            if( pHead.getModifiedString() != null ) {
                lReturn.setHeadModified( pHead.getModifiedString());
            }
        }
        return lReturn;
    }

    private IXPersonalEnclosure getEnclosure( TEnclosure pEnclosure, IXPersonalFeed pParent ) {
        IXPersonalEnclosure lReturn = new XPersonalEnclosure(pParent);

        // CB TODO, URL is part of the RSS model, can't be changed.
        try {
            lReturn.setPersonalURL(
                new URL(
                    getStringValue( pEnclosure.getUrl(), "non-exist" )
                )
            );
        } catch( MalformedURLException mue ) {
            // If the enclosures URL is bad there is nothing we can do but do discard it
//AS TODO: is there a way to show a bad URL in the UI?
            mLog.error( "Could not convert given URL: " + pEnclosure.getUrl() + " and so this enclosure is discarded", mue );
            return null;
        }
        // CB TODO, type is part of the RSS model.
//        lReturn.setType( getStringValue( pEnclosure.getType(), "non-exist" ) );
        String lTemp = getStringValue( pEnclosure.getFile(), "" );
        if( lTemp.length() > 0 && !lTemp.equals("null") ) {
        	File lFile =  new File(lTemp);
            lReturn.setFileName( lFile.getName());
        }
        lReturn.setMarked( pEnclosure.getMarked() );
        lReturn.setDownloadCompleted(pEnclosure.getCompleted());

        // CB TODO, We don't store the HEAD Info anymore.
//        TFeedHead lTHead = pEnclosure.getHead();
//        
//        if( lTHead != null ) {
//            lReturn.setHEADInfo( getHeadInfo( lTHead ) );
//        }
        
        return lReturn;
    }

    private void getTEnclosure( TFeed pParent, IXPersonalEnclosure pEnclosure ) {
        TEnclosure lTEnclosure = pParent.addNewEnclosure();
        lTEnclosure.setUrl( pEnclosure.getPersonalURL() + "" );
        // CB TODO No point in storing the type, it's part of the RSS model
        // lTEnclosure.setType( pEnclosure.getType() );
        String lTemp = pEnclosure.getFile() + "";
        lTEnclosure.setFile( lTemp != null ? lTemp + "" : "" );
        lTEnclosure.setMarked( pEnclosure.isMarked() );
        lTEnclosure.setCompleted(pEnclosure.isDownloadCompleted());
        
//      CB TODO, We don't store the HEAD Info anymore.        
//        TFeedHead lHead = getTHeadInfo( pEnclosure.getHEADInfo() );
//        if( lHead != null ) {
//            lTEnclosure.setHead( lHead );
//        }
    }
    
    private void getTID3( TID3Tags lTTags, ID3TagRewrite pTag) {
        TID3Tag lTTag = lTTags.addNewId3Tag();
        lTTag.setName(pTag.getName());
        lTTag.setValue(pTag.getValue());
    }
    
    

    private String getStringValue( String pValue, String pDefault ) {
        return getStringValue( pValue, false, pDefault );
    }

    private String getStringValue( String pValue, boolean pSetEmptyToNull, String pDefault ) {
        String lReturn = pDefault;
        if( pValue != null ) {
            if( pSetEmptyToNull && pValue.length() == 0 ) {
                lReturn = null;
            } else {
                lReturn = pValue;
            }
        }
        return lReturn;
    }

    private int getIntValue( BigInteger pValue, int pDefault ) {
        int lReturn = pDefault;
        if( pValue != null ) {
            lReturn = pValue.intValue();
        }
        return lReturn;
    }

	public void setPersistentFile(File pFile) {
		mFile = pFile;
	}

	public File getPersistentFile() {
		return mFile;
	}
}