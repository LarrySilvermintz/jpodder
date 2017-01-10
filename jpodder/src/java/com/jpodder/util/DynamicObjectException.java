package com.jpodder.util;

import com.jpodder.util.AbstractTypedException;

/**
 * General Exception for the Dynamic Object that
 * allows for more specific indentification of an
 * exception but still is language independent.
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public class DynamicObjectException
        extends AbstractTypedException {

     //--- Constants -------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = -4692126099826007659L;
	/** Class could not be found by the class loader **/
    public static final int CLASS_NOT_FOUND = 0;
    /** The class does not contain such a constructor **/
    public static final int NO_SUCH_CONSTRUCTOR = 1;
    /** Class is abstract and therefore cannot be instantiated **/
    public static final int CLASS_IS_ABSTRACT = 2;
    /** Constructor is available but cannot be accessed **/
    public static final int CONSTRUCTOR_NOT_ACCESSIBLE = 3;
    /** Constructor threw an exception when invoked **/
    public static final int CONSTRUCTOR_EXCEPTION = 4;
    /** Class name is null or empty **/
    public static final int CLASS_NAME_UNDEFINED = 5;
    /** Target instance is null **/
    public static final int TARGET_UNDEFINED = 6;
    /** The class does not contain such a field **/
    public static final int NO_SUCH_FIELD = 7;
    /** Field name is null or empty **/
    public static final int FIELD_NAME_UNDEFINED = 8;
    /** Field is available but not accessible **/
    public static final int FIELD_NOT_ACCESSIBLE = 9;
    /** Null values are not allowed for basic data type like int, float etc **/
    public static final int NULL_NOT_ALLOWED_BECAUSE_OF_BASIC_DATA_TYPE = 10;
    /** The class does not contain such method **/
    public static final int NO_SUCH_METHOD =11;
    /** Method threw an exception when invoked **/
    public static final int METHOD_EXCEPTION =12;
    /** Method name is null or empty **/
    public static final int METHOD_NAME_UNDEFINED = 13;
    /** Method signature does not match parameter list **/
    public static final int METHOD_SIGNATURE_DOES_NOT_MATCH_PARAMETERS = 14;

    //--- Attributes ------------------------------------------------------------

    //--- Constructors ----------------------------------------------------------

    /**
     * Creates a new Exception with a given type
     *
     * @param pType Type of the exception
     **/
    public DynamicObjectException( int pType ) {
       this( pType, null, null, null );
    }

    /**
     * Creates a new Exception with a given type and cause
     *
     * @param pType Type of the exception
     * @param pCause Cause of this exception
     **/
    public DynamicObjectException( int pType, Throwable pCause ) {
       this( pType, null, null, pCause );
    }

    /**
     * Creates a new Exception with a given type
     *
     * @param pType Type of the exception
     * @param pArguments Message arguments
     * @param pKeys Message argument keys
     **/
    public DynamicObjectException( int pType, Object[] pArguments, String[] pKeys ) {
       this( pType, pArguments, pKeys, null );
    }

    /**
     * Creates a new Exception with a given type and cause
     *
     * @param pType Type of the exception
     * @param pArguments Message arguments
     * @param pKeys Message argument keys
     * @param pCause Cause of this exception
     **/
    public DynamicObjectException( int pType, Object[] pArguments, String[] pKeys, Throwable pCause ) {
       super( pType, pArguments, pKeys, pCause );
    }

    //--- public ----------------------------------------------------------------

}