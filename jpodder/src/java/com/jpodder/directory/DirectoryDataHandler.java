package com.jpodder.directory;

import java.io.File;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IDataHandler;

/**
 * Data Handler implementation for the Feeds.
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public class DirectoryDataHandler
        implements IDataHandler {

    protected Logger mLog = Logger.getLogger( getClass().getName() );
//    private boolean mIsModified = true;
    private DirectoryList mDirectoryList;
	private File mFile;

    public DirectoryDataHandler( DirectoryList pList ) {
        mDirectoryList = pList;
    }

    public int getIndex() {
        return ConfigurationLogic.DIRECTORY_INDEX;
    }

    public boolean isModified() {
        return mDirectoryList.isModified();
    }

    public String getContent()
            throws Exception {
        mLog.info( "getContent(), return the content of the Directory list" );
        
        DirectoryDocument lDocument = DirectoryDocument.Factory.newInstance();
        TDirectory lRoot = lDocument.addNewDirectory();
        // TFeeds.Factory.newInstance();
        lDocument.setDirectory( lRoot );
        Iterator i = mDirectoryList.getIterator();
        while( i.hasNext() ) {
            DirectoryEntry lFeed = (DirectoryEntry) i.next();
            TFeed lTFeed = lRoot.addNewFeed();
            lTFeed.setUrl( lFeed.getURL());
            lTFeed.setTitle( lFeed.getTitle() );
            lTFeed.setDescription( lFeed.getDescription() );
        }
        StringWriter lOutput = new StringWriter();
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSavePrettyPrint();
        lDocument.save( lOutput );
//AS NOTE: This could be dangerouse when we mark the content but the save to the file
//AS       fails down the road
        mDirectoryList.setUpdated();
        return lOutput.toString();
    }

    public void setContent( String pContent )
            throws Exception {
        mLog.info( "setContent(), content to be set: " + pContent );
        DirectoryDocument lDocument = DirectoryDocument.Factory.parse( pContent );
        TDirectory lRoot = lDocument.getDirectory();
        mLog.info( "Directory Array: " + java.util.Arrays.asList( lRoot.getFeedArray() ) );

        ArrayList<DirectoryEntry> lDirectoryList = new ArrayList<DirectoryEntry>();
        TFeed[] lTFeeds = lRoot.getFeedArray();
        for( int i = 0; i < lTFeeds.length; i++ ) {
            TFeed lTFeed = lTFeeds[ i ];
            DirectoryEntry lFeed = new DirectoryEntry();
            lDirectoryList.add( lFeed );

//            try {
                lFeed.setURL(
//                    new URL(
                        getStringValue( lTFeed.getUrl(), "this makes the conversion fail" ));
//                    )
//                );
//            } catch( MalformedURLException mue ) {
//                mLog.warn( "Could not convert given URL: " + lTFeed.getUrl() + " but keep going", mue );
//            }
            lFeed.setTitle( getStringValue( lTFeed.getTitle(), true , "" ) );
            lFeed.setDescription( getStringValue( lTFeed.getDescription(), "" ) );
        }
        mDirectoryList.clear();
        mDirectoryList.addFeeds( lDirectoryList );
        mDirectoryList.setUpdated();
        mLog.info( "setContent(), directory list: " + lDirectoryList );
    }

    public boolean validate( String pContent, boolean pCompare ) {
        return true; // Validation is implicit.
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