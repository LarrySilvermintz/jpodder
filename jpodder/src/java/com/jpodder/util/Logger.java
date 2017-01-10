package com.jpodder.util;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;


/**
 * Extension of the Log4J Logger class to add additional logger methods
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public class Logger {

    // Constants -----------------------------------------------------

    // Attributes ----------------------------------------------------

    private org.apache.log4j.Logger mLog;
    private String mName;

    // Constructors --------------------------------------------------

    /**
     * Creates new Logger with the given logger name.
     *
     * @param pName The logger name
     **/
    protected Logger( final String pName ) {
       mName = pName;
       mLog = org.apache.log4j.Logger.getLogger( mName );
    }


    /**
     * Creates new Logger the given class.
     *
     * @param pClass Class the Logger works on.
     **/
    protected Logger( final Class pClass ) {
       mName = pClass.getName();
       mLog = org.apache.log4j.Logger.getLogger( pClass );
    }

    // Static Methods -------------------------------------------------

    /** @see org.apache.log4j.Logger#getLogger( String ) **/
    public static Logger getLogger( String pName ) {
       return new Logger( pName );
    }


    /** @see org.apache.log4j.Logger#getLogger( Class ) **/
    public static Logger getLogger( Class pClass ) {
       return new Logger( pClass );
    }

    // Methods --------------------------------------------------------

    /** @see org.apache.log4j.Logger#isDebugEnabled **/
    public boolean isDebugEnabled() {
       return mLog.isDebugEnabled();
    }

    /**
     * @return True if the DEV_DEBUG is enable to be reported
     **/
    public boolean isDevDebugEnabled() {
       if( !mLog.isEnabledFor( XLevel.DEV_DEBUG ) ) {
          return false;
       }
       return XLevel.DEV_DEBUG.isGreaterOrEqual( mLog.getEffectiveLevel() );
    }

    /**
     * @return True if the TRACE is enable to be reported
     **/
    public boolean isTraceEnabled() {
       if( !mLog.isEnabledFor( XLevel.TRACE ) ) {
          return false;
       }
       return XLevel.TRACE.isGreaterOrEqual( mLog.getEffectiveLevel() );
    }

    /**
     * Issue a log msg with a level of DEV_DEBUG.
     *
     * @param pMessage Message of the DEV DEBUG logging
     **/
    public void devDebug( Object pMessage ) {
       mLog.log( XLevel.DEV_DEBUG, pMessage );
    }

    /**
     * Issue a log msg and throwable with a level of DEV_DEBUG.
     *
     * @param pMessage Message of the DEV DEBUG logging
     * @param pThrowable Throwable that goes along with the message
     **/
    public void devDebug( Object pMessage, Throwable pThrowable ) {
       mLog.log( XLevel.DEV_DEBUG, pMessage, pThrowable );
    }

    /**
     * Issue a log msg with a level of TRACE.
     *
     * @param pMessage Message of the TRACE logging
     **/
    public void trace( Object pMessage ) {
       if( isTraceEnabled() ) {
          mLog.log( XLevel.TRACE, pMessage );
       }
    }

    /**
     * Issue a log msg and throwable with a level of TRACE.
     *
     * @param pMessage Message of the TRACE logging
     * @param pThrowable Throwable that goes along with the message
     **/
    public void trace( Object pMessage, Throwable pThrowable ) {
       if( isTraceEnabled() ) {
          mLog.log( XLevel.TRACE, pMessage, pThrowable );
       }
    }

    // --- Methods taken from Log4J Logger --------------------------------------

    /** @see org.apache.log4j.Logger#isInfoEnabled **/
    public boolean isInfoEnabled() {
       return mLog.isInfoEnabled();
    }

    /** @see org.apache.log4j.Logger#debug( Object ) **/
    public void debug( Object pMessage ) {
       mLog.log( Level.DEBUG, pMessage );
    }

    /** @see org.apache.log4j.Logger#debug( Object, Throwable ) **/
    public void debug( Object pMessage, Throwable pThrowable ) {
       mLog.log( Level.DEBUG, pMessage, pThrowable );
    }

    /** @see org.apache.log4j.Logger#error( Object ) **/
    public void error( Object pMessage ) {
       mLog.log( Level.ERROR, pMessage );
    }

    /** @see org.apache.log4j.Logger#error( Object, Throwable ) **/
    public void error( Object pMessage, Throwable pThrowable ) {
       mLog.log( Level.ERROR, pMessage, pThrowable );
    }

    /** @see org.apache.log4j.Logger#fatal( Object ) **/
    public void fatal( Object pMessage ) {
       mLog.log( Level.FATAL, pMessage );
    }

    /** @see org.apache.log4j.Logger#fatal( Object, Throwable ) **/
    public void fatal( Object pMessage, Throwable pThrowable ) {
       mLog.log( Level.FATAL, pMessage, pThrowable );
    }

    /** @see org.apache.log4j.Logger#info( Object ) **/
    public void info( Object pMessage ) {
       mLog.log( Level.INFO, pMessage );
    }

    /** @see org.apache.log4j.Logger#info( Object, Throwable ) **/
    public void info( Object pMessage, Throwable pThrowable ) {
       mLog.log( Level.INFO, pMessage, pThrowable );
    }

    /** @see org.apache.log4j.Logger#log( Priority, Object ) **/
    public void log( Priority pPriority, Object pMessage ) {
       mLog.log( pPriority, pMessage );
    }

    /** @see org.apache.log4j.Logger#log( pPriority, Object, Throwable ) **/
    public void log( Priority pPriority, Object pMessage, Throwable pThrowable ) {
       mLog.log( pPriority, pMessage, pThrowable );
    }

    /** @see org.apache.log4j.Logger#log( Level, Object ) **/
    public void log( Level pLevel, Object pMessage ) {
       mLog.log( pLevel, pMessage );
    }

    /** @see org.apache.log4j.Logger#log( Level, Object, Throwable ) **/
    public void log( Level pLevel, Object pMessage, Throwable pThrowable ) {
       mLog.log( pLevel, pMessage, pThrowable );
    }

    /** @see org.apache.log4j.Logger#warn( Object ) **/
    public void warn( Object pMessage ) {
       mLog.log( Level.WARN, pMessage );
    }

    /** @see org.apache.log4j.Logger#warn( Object, Throwable ) **/
    public void warn( Object pMessage, Throwable pThrowable ) {
       mLog.log( Level.WARN, pMessage, pThrowable );
    }
}

