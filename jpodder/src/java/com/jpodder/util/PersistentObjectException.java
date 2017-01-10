package com.jpodder.util;

import com.jpodder.util.AbstractTypedException;

/**
 * General Exception for the Persistent Object that
 * allows for more specific indentification of an
 * exception but still is language independent.
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public class PersistentObjectException
        extends AbstractTypedException {

    //--- Constants -------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = -227025908547536964L;
	/** No Object Name specified **/
    public static final int OBJECT_NAME_UNDEFINED = 0;
    /** Object Name already in use **/
    public static final int OBJECT_NAME_ALREADY_IN_USE = 1;
    /** No Object referencing **/
    public static final int NO_SUCH_TARGET = 2;

    //--- Attributes ------------------------------------------------------------

    //--- Constructors ----------------------------------------------------------

    /**
     * Creates a new Exception with a given type
     *
     * @param pType Type of the exception
     **/
    public PersistentObjectException( int pType ) {
       this( pType, null, null, null );
    }

    /**
     * Creates a new Exception with a given type and cause
     *
     * @param pType Type of the exception
     * @param pCause Cause of this exception
     **/
    public PersistentObjectException( int pType, Throwable pCause ) {
       this( pType, null, null, pCause );
    }

    /**
     * Creates a new Exception with a given type
     *
     * @param pType Type of the exception
     * @param pArguments Message arguments
     * @param pKeys Message argument keys
     **/
    public PersistentObjectException( int pType, Object[] pArguments, String[] pKeys ) {
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
    public PersistentObjectException( int pType, Object[] pArguments, String[] pKeys, Throwable pCause ) {
       super( pType, pArguments, pKeys, pCause );
    }

    //--- public ----------------------------------------------------------------

}