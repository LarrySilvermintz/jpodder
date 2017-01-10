package com.jpodder.data.configuration;

import java.io.File;
import java.math.BigInteger;

/**
 * An interface that provides the methods a data handler has to provide
 * so that it can work together with the FileHandler to read and save
 * the data at any time.
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public interface IDataHandler {
	
	
	
    public static final int CONTENT_EMPTY = 1;
    public static final int CONTENT_CORRUPT = 2;

    /**
     * @return Index of the Data Handler so that the File Handler can identify
     *         the data handler and provide the appropriate file content
     **/
    public int getIndex();

    /**
     * @return True if the data has changed. The File Handler will use this
     *         flag to decide if it will save the data or not.
     **/
    public boolean isModified();

    /**
     * @return The content of the data as a string. The File Handler will us
     *         this string to save it to a file
     *
     * @throws Exception If the conversion of the data into a string fails
     **/
    public String getContent()
        throws Exception;

    /**
     * @param pContent The content of the data as string to be set. The implementation
     *                 must make sure that it preserves the original data if the data
     *                 cannot be set
     *
     * @throws Exception If the data could not be set
     **/
    public void setContent( String pContent )
        throws Exception;
    
    /**
     * @param pFile The file to which the data is saved.
     */
    public void setPersistentFile(File pFile);
    
    
    /**
     * @return The file from which the data is read.
     */
    public File getPersistentFile(); 
    
    
    /**
     * @param pContent The content of the data as string to be validated
     * @param pCompare True if the content also should be compared to the current
     *                 content to make sure it matches. This is used by the FileHandler
     *                 to ensure that the file (after saving) has the correct content.
     *
     * @return True if the validation succeeded otherwise false
     **/
    public boolean validate( String pContent, boolean pCompare );

    public static class Util {
        /**
         * Checks if the given value is null and if then
         * it will return the given default value
         *
         * @param pValue Value to check
         * @param pDefault Default value to be returned when value is null
         *
         * @return The given value if not null otherwise the default value
         **/
        public static String checkString( String pValue, String pDefault ) {
            return pValue == null ? pDefault : pValue;
        }

        /**
         * Check the given integer value and if not null
         * it will extract the number from it
         *
         * @param pBigInteger Big Integer to be converted
         * @param pDefault
         *
         * @return The extracted number and otherwise the default value
         **/
        public static int getInt( BigInteger pBigInteger, int pDefault ) {
            return pBigInteger == null ? pDefault : pBigInteger.intValue();
        }

        /**
         * Convert the given number into a Big Integer
         *
         * @param pNumber Number to be converted into a big integer
         *
         * @return Big Integer containing the number
         **/
        public static BigInteger getBigInteger( int pNumber ) {
            return new BigInteger( pNumber + "" );
        }
    }
}