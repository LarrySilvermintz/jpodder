package com.jpodder.util;

import java.util.Arrays;

/**
 * An Exception with a type instead of a message that indicates
 * the problem. It is also possible to add an array of arguments
 * that then can be used to formulate a message.
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public abstract class AbstractTypedException
       extends RuntimeException {

    //--- Constants -------------------------------------------------------------

    /** Unknown Type **/
    public static final int UNKNOWN = -1;

    //--- Attributes ------------------------------------------------------------

    /** Type of the exception **/
    private int mType = UNKNOWN;
    /** List of message arguments **/
    private Object[] mArguments;
    /** List of message argument keys **/
    private String[] mArgumentKeys;

    //--- Constructors ----------------------------------------------------------

    /**
     * Creates a new Exception with a given type
     *
     * @param pType Type of the exception
     **/
    public AbstractTypedException( int pType ) {
       this( pType, null, null, null );
    }

    /**
     * Creates a new Exception with a given type and cause
     *
     * @param pType Type of the exception
     * @param pCause Cause of this exception
     **/
    public AbstractTypedException( int pType, Throwable pCause ) {
       this( pType, null, null, pCause );
    }

    /**
     * Creates a new Exception with a given type
     *
     * @param pType Type of the exception
     * @param pArguments Message arguments
     **/
    public AbstractTypedException( int pType, Object[] pArguments ) {
       this( pType, pArguments, null, null );
    }

    /**
     * Creates a new Exception with a given type and cause
     *
     * @param pType Type of the exception
     * @param pCause Cause of this exception
     **/
    public AbstractTypedException( int pType, Object[] pArguments, Throwable pCause ) {
       this( pType, pArguments, null, pCause );
    }

    /**
     * Creates a new Exception with a given type
     *
     * @param pType Type of the exception
     * @param pArguments Message arguments
     * @param pKeys Message argument keys
     **/
    public AbstractTypedException( int pType, Object[] pArguments, String[] pKeys ) {
       this( pType, pArguments, null, null );
    }

    /**
     * Creates a new Exception with a given type and cause
     *
     * @param pType Type of the exception
     * @param pCause Cause of this exception
     * @param pKeys Message argument keys
     **/
    public AbstractTypedException( int pType, Object[] pArguments, String[] pKeys, Throwable pCause ) {
       super( pCause );
       mType = pType;
       if( pArguments == null ) {
          mArguments = new Object[ 0 ];
       } else {
          mArguments = pArguments;
       }
       if( pKeys == null ) {
          mArgumentKeys = new String[ 0 ];
       } else {
          mArgumentKeys = pKeys;
       }
    }

    //--- public ----------------------------------------------------------------

    /** @return Type of the exception (see this class' constants) **/
    public int getType() {
       return mType;
    }

    /** @return Array of message arguments that is never null but empty if not set **/
    public Object[] getArguments() {
       return mArguments;
    }

    /** @return Array of message argument keys that is never null but empty if not set **/
    public String[] getArgumentKeys() {
       return mArgumentKeys;
    }

    /** @see Object#toString **/
    public String toString() {
       return getClass().getName() + " ["
          + " type: " + mType
          + ( getCause() != null ? ", cause: " + getCause() : "" )
          + ( mArguments.length > 0 ? ", arguments: " + Arrays.asList( mArguments ) : "" )
          + ( mArgumentKeys.length > 0 ? ", keys: " + Arrays.asList( mArgumentKeys ) : "" )
          + " ]";
    }
}