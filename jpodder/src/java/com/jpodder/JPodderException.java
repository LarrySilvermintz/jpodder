package com.jpodder;

import java.lang.Exception;

/**
 * Generic exception class.
 *
 * <p>Title: iPodder</p>
 * <p>Description: iTunes RSS feeds</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 0.5
 */
public class JPodderException
        extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5655332369265664249L;
	private int mType;

    public JPodderException( int pType ) {
        super();
        mType = pType;
    }

    public JPodderException( int pType, Throwable pCause ) {
        super( pCause );
        mType = pType;
    }

    /**
     *
     * @param msg String
     **/
    public JPodderException(String msg) {
        super(msg);
    }
    
    /**
     *
     * @param msg String
     * @param pCause Throwable that caused this exception
     **/
    public JPodderException(String msg, Throwable pCause ) {
        super(msg, pCause );
    }

    public int getType() {
        return mType;
    }
}
