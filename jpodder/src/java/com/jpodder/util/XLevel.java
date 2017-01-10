package com.jpodder.util;

import org.apache.log4j.Level;


/**
 * Extension of the Log4J Level class to provide customized log levels
 *
 * DEV_DEBUG: Log level set between INFO and DEBUG to indicate a developer
 *            logging statement and to avoid the flood of DEBUG loggings
 *            if not needed in development. Also this log level indicates
 *            that this logging statement is for development purpose only
 *            and can easily be compiled out with a constant in an IF
 *            statement.
 *
 * TRACE:     Level below DEBUG to indicate that this message is indended
 *            to trace a problem but SHOULD only be used in conjuction
 *            with a category because it will provide a lot of messages.
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public class XLevel
         extends Level {

    // Constants -----------------------------------------------------

    /**
     * The integer representation of the level number for 'Dev Debug'
     **/
    public final static int DEV_DEBUG_INT = 15000;
    /**
     * The string name of the 'Dev Debug' level.
     **/
    public final static String DEV_DEBUG_STR = "DEV_DEBUG";
    /**
     * The DEV_DEBUG level object singleton
     **/
    public final static XLevel DEV_DEBUG = new XLevel( DEV_DEBUG_INT, DEV_DEBUG_STR, 7 );

    /**
     * The integer representation of the level number for 'Trace'
     **/
    public final static int TRACE_INT = 5000;
    /**
     * The string name of the 'Trace' level.
     **/
    public final static String TRACE_STR = "TRACE";
    /**
     * The TRACE level object singleton
     **/
    public final static XLevel TRACE = new XLevel( TRACE_INT, TRACE_STR, 7 );

    // Attributes ----------------------------------------------------

    // Constructors --------------------------------------------------

    /**
     * Construct a XLevel.
     *
     * @param pLevel Number of the level
     * @param pStringLevel String representation of this level
     * @param pSyslogEquivalent Equivalent to Un*x Syslog
     **/
    protected XLevel( final int pLevel, final String pStringLevel, final int pSyslogEquivalent ) {
       super( pLevel, pStringLevel, pSyslogEquivalent );
    }

    // Factory Methods ------------------------------------------------

    /**
     * Looks up a level by name and if not found uses the given
     * default level.
     *
     * @param pName Name of the Level to be converted
     * @param pDefaultLevel Default Level used if not found
     *
     * @return Level object for name if one exists, default level otherwise.
     **/
    public static Level toLevel( final String pName, final Level pDefaultLevel ) {
       Level lReturn = null;
       if( pName == null ) {
          lReturn = pDefaultLevel;
       } else {
          String lUpper = pName.toUpperCase();
          if( lUpper.equals( DEV_DEBUG_STR ) ) {
             lReturn = DEV_DEBUG;
          } else if( lUpper.equals( TRACE_STR ) ) {
             lReturn = TRACE;
          } else {
             lReturn = Level.toLevel( pName, pDefaultLevel );
          }
       }
       return lReturn;
    }

    /**
     * Looks up a level by name and if not found uses DEV_DEBUG as default level
     *
     * @param pName Name of the Level to be converted
     *
     * @return Level object for name if one exists, DEV_DEBUG otherwise.
     **/
    public static Level toLevel( final String pName ) {
       return toLevel( pName, DEV_DEBUG );
    }

    /**
     * Convert an integer passed as argument to a level. If the conversion
     * fails, then this method returns the specified default.
     *
     * @param pLevelAsInt Number of the level to be found
     * @param pDefaultLevel Default level to be returned if not found
     * @return Level object for name if one exists, default level otherwise.
     **/
    public static Level toLevel( final int pLevelAsInt, final Level pDefaultLevel ) {
       Level lReturn;
       if( pLevelAsInt == DEV_DEBUG_INT ) {
          lReturn = DEV_DEBUG;
       } else if( pLevelAsInt == TRACE_INT ) {
          lReturn = TRACE;
       } else {
          lReturn = Level.toLevel( pLevelAsInt, pDefaultLevel );
       }
       return lReturn;
    }
}

