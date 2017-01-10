package com.jpodder.ui.swt.log;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.jpodder.data.configuration.Configuration;

/**
 * A log area.
 * <p>
 * Allows different appending styles named:
 * <pre>
 *   - Good. (Green)
 *   - Bad. (Red)
 *   - Neutral. (Grey)
 * </pre>
 * When appending text to this model, the style needs to be specified. This
 * class also scrolls to the last appended text.
 * 
 */
public class LogArea {

//    private static LogArea sInstance;
    protected StyledText mLog;

    private LogStyle goodStyle;
    private LogStyle badStyle;
    private LogStyle neutralStyle;
    private LogStyle debugStyle;
    private int mMaxLength = Configuration.getInstance().getLogSize();

    private Log4jAppender appender;
    /**
     * Constructor. Adds the newly defined style to the default stylecontext and
     * set as logical style.
     */
    public LogArea(Composite pParent) {
        mLog = new StyledText(pParent, SWT.NONE | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        mLog.setEditable(false);
        goodStyle = new LogStyle("good", Display.getDefault().getSystemColor(
                SWT.COLOR_DARK_GREEN));
        badStyle = new LogStyle("bad", Display.getDefault().getSystemColor(
                SWT.COLOR_DARK_RED));
        neutralStyle = new LogStyle("neutral", Display.getDefault()
                .getSystemColor(SWT.COLOR_DARK_GRAY));
        debugStyle = new LogStyle("debug", Display.getDefault().getSystemColor(
                SWT.COLOR_BLUE));
        appender = new Log4jAppender(this);
        Logger.getRootLogger().addAppender(appender);
    
    }
    
    public void dispose(){
        Logger.getRootLogger().removeAppender(appender);
    }
    

    /**
     * Constructor. Adds the newly defined style to the default stylecontext and
     * set as logical style.
     */
//    public LogArea(Object lTest) {
//        // Add the Log4J append to redirect the log4j message back into this
//        // panel
//        
//    }
    
    public StyledText getLog(){
        return mLog;
    }
    
    /**
     * Removes any log statements from view
     */
//    public static void clear() {
//        sInstance.removeAll();
//    }

    /**
     */
    private class LogStyle {
        protected Color mColor;
        protected String mTitle;

        public LogStyle(String pTitle, Color pColor) {
            mTitle = pTitle;
            mColor = pColor;
        }
        public Color getColor(){
            return mColor;
        }
    }

    public void removeAll() {
        mLog.setText("");
    }

    /**
     * Append the text with the Neutral style.
     * <p>
     * 
     * @param module
     *            A mandatory module name.
     * @param appText
     *            The text to be appended.
     */
    public void appendNeutral(String module, String appText) {
        append(neutralStyle, module + appText);
    }

    /**
     * Append text with a certain style.
     * <p>
     * This method also scrolls the textpane to the last inserted text in the
     * model.
     * 
     * @param pStyle
     *            The character style.
     * @param pText
     *            The string to be appended.
     */
    protected void append(LogStyle pStyle, String pText) {
        if(!mLog.isDisposed()){
            mLog.insert(pText);
            StyleRange lStyleRange = new StyleRange();
            lStyleRange.start = 0;
            lStyleRange.length = pText.length();
            lStyleRange.foreground = pStyle.getColor();
            mLog.setStyleRange(lStyleRange);            
        }
    }

    public void setLogSize(int pLogSize) {
        mMaxLength = pLogSize;
    }

    private class Log4jAppender extends AppenderSkeleton {

        private Layout mLayout;
        private LogArea mLogArea;

        public Log4jAppender(LogArea aLogArea) {
            mLogArea = aLogArea;
            // addFilter(
            // new Log4jFilter()
            // );
            mLayout = new PatternLayout(
            // "[%c] %m%n"
                    "%d{HH:mm:ss} %-5p [%c{1}] %m%n");
        }

        public synchronized void append(LoggingEvent pLogEvent) {
            final Level lLogLevel = pLogEvent.getLevel();
            final String lMessage = mLayout.format(pLogEvent);
            Display.getDefault().asyncExec(new Runnable(){
                public void run() {
                    if (lLogLevel.isGreaterOrEqual(Level.WARN)) {
                        mLogArea.append(badStyle, lMessage);
                    } else if (lLogLevel.isGreaterOrEqual(Level.INFO)) {
                        mLogArea.append(goodStyle, lMessage);
                    } else {
                        mLogArea.append(debugStyle, lMessage);
                    }
                }
            });
        }

        public boolean requiresLayout() {
            return false;
        }

        public void close() {
        }
    }

    public static class Log4jFilter extends Filter {
        public Log4jFilter() {
        }

        public int decide(LoggingEvent pLogEvent) {
            if (pLogEvent.getLevel().isGreaterOrEqual(Level.INFO)
                    || pLogEvent.getLoggerName().startsWith("jpodder.")) {
                return NEUTRAL;
            } else {
                return DENY;
            }
        }
    }
}
