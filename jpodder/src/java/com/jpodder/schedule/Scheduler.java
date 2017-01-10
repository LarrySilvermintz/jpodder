package com.jpodder.schedule;

/**
 * @author <a href="mailto:christophe@kualasoft.com>Christophe Bouhier</a>"
 * version 1.0
 */
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.jpodder.util.Messages;

/**
 * Feed scan scheduling. This type contains the timing function needed to
 * initiate scannin of feeds.
 * 
 * It also contains the Component change listener which is triggered by the
 * polling interval slider.
 * 
 * A timer start & stop function are available for external use.
 */
public class Scheduler {

    Logger mLog = Logger.getLogger(getClass().getName());

    public static final int SCHEDULE_INTERVAL = 301;
    public static final int SCHEDULE_FIXTIME = 302;

    private boolean mEnabled = false;
    private boolean intervalBased = false;

    private ActionListener mAction;

    /**
     * The interval countdown field expressed in mili seconds.
     */
    private Integer mIntervalCounter = new Integer(0);
    private Integer mIntervalOriginalCounter = new Integer(0);
    protected FixTimeCounter[] mFixTimeCounterList = new FixTimeCounter[3];

    private int mNearestFixTime = 86400;
    public SimpleDateFormat sf = new SimpleDateFormat("hh:mm a");

    public Scheduler() {

    }

    public void initialize() {
        // CB Nothing to initialize, the scheduling mode and
        // counters are set by a properties listener in the controller.
    }

    public void setAction(ActionListener pAction) {
        mAction = pAction;
    }

    /**
     * Enable or Disable the scheduler.
     * <p>
     * 
     * @param pEnable
     *            boolean
     */
    public void setEnabled(boolean pEnable) {
        mEnabled = pEnable;
    }

    /**
     * Return if the scheduler is enabled.
     * 
     * @return boolean
     */
    public boolean getEnabled() {
        return mEnabled;
    }

    /**
     * updates the schedule counter.
     * 
     * @return Integer
     */
    public void setScheduleIntervalCounter(int delay) {
        synchronized (mIntervalCounter) {
            mIntervalOriginalCounter = new Integer(delay * 60 * 1000);
            mIntervalCounter = mIntervalOriginalCounter;
        }
    }

    /**
     * updates the schedule counter.
     * 
     * @return Integer
     */
    public Integer getIntervalCounter() {
        return mIntervalCounter;
    }
    
    private void updateIntervalCounter() {
        int value = mIntervalCounter.intValue();
        if (value > 0) {
            value -= 1000;
            mIntervalCounter = new Integer(value);
        } else {
            if (mEnabled) {
                mLog.info("Invoke scheduler action (interval)");                
                mAction.actionPerformed(null);
                mIntervalCounter = mIntervalOriginalCounter;
            }
        }
    }

    /**
     * This method is called every second. It is time optimized to perform
     * minimal action. The counters are stepped down with a value above 0, if a
     * counter reaches 0, the action is fired and counter is reset.
     */
    private void updateFixTimeCounters() {
        
        for (int i = 0; i < mFixTimeCounterList.length; i++) {
            FixTimeCounter lFixTimeCounter = mFixTimeCounterList[i];
            if (lFixTimeCounter != null) { // Ignore null counters.
                //                Integer counter = ;
                int value = lFixTimeCounter.getSeconds().intValue();
                if (value > 0) {
                    value -= 1;
                    if (mNearestFixTime > value) {
                        mNearestFixTime = value;
                    }
                    lFixTimeCounter.setSeconds(new Integer(value));
                } else {
                    if (value == 0) {
                        value -= 1;
                        if (mEnabled) {
                            mLog.info("Invoke scheduler action (Fixed Time)");
                            mAction.actionPerformed(null);
                        }                        
                        Calendar c = Calendar.getInstance();
                        Date d = c.getTime();
                        mLog.info(Messages.getString("scheduler.time.invoked",
                                sf.format(d)));
                        
                        lFixTimeCounter.reset();
                        mNearestFixTime = getNearestFixTime().getSeconds().intValue();
                        
                    } else {
                        // The timer should have been reset by now, we should
                        // never get here.
                        mLog.info("Invalid fixed time value: " + value);                        
                    }
                }
            }
        }
    }

    /**
     * Check the scheduling task, this method should be called every 1000
     * miliseconds.
     */
    public void updateTask() {
        if (intervalBased) {
           updateIntervalCounter();
        } else {
            updateFixTimeCounters();
        }
    }

    public void setSchedulerMode(int mode) {
        intervalBased = (mode == SCHEDULE_INTERVAL);
    }

    public int getSchedulerMode() {
        return intervalBased ? SCHEDULE_INTERVAL : SCHEDULE_FIXTIME;
    }

    /**
     * Get the remaining time before the next scan.
     * 
     * @return int Get the remaining value of the scheduler counter.
     */
    public synchronized int getTimeRemaining() {
        if (intervalBased)
            return mIntervalCounter.intValue();
        else {
            return mNearestFixTime * 1000;
        }
    }

    protected FixTimeCounter getNearestFixTime() {
        FixTimeCounter lNearestCounter = null;
        for (int i = 0; i < mFixTimeCounterList.length; i++) {
            FixTimeCounter lCounter = mFixTimeCounterList[i];
            if (lCounter != null) {
                if (lNearestCounter != null
                        && lCounter.getSeconds().intValue() < lNearestCounter
                                .getSeconds().intValue()) {
                    lNearestCounter = lCounter;
                } else {
                    lNearestCounter = lCounter;
                }
            }
        }
        return lNearestCounter;
    }


    public void setScheduleTimeCounterList(FixTimeCounter[] pList) {
        mFixTimeCounterList = pList;
        FixTimeCounter lNearest = getNearestFixTime();
        if( lNearest != null ) {
            mNearestFixTime = lNearest.getSeconds().intValue();
        }
    }

    public FixTimeCounter getNewCounter(Date d) {
        return new FixTimeCounter(d);
    }

    /**
     * A scheduleTimeCounter type containing the exact Date object and the
     * remaining (absolute) seconds before expiry.
     * A timer class should descrease the seconds value for proper use.
     * The counter can be reset to it's original value by calling
     * the <code>reset()</code> method.
     */
    public class FixTimeCounter {
        
        protected Integer mSeconds;
        protected Date mDate;

        /**
         * @param seconds
         * @param date
         */
        public FixTimeCounter(Date pDate) {
            mDate = pDate;
            reset();
        }

        /**
         * @return Returns the date.
         */
        public Date getDate() {
            return mDate;
        }

        /**
         * @return Returns the seconds.
         */
        public Integer getSeconds() {
            return mSeconds;
        }

        /**
         * @param seconds
         *            The seconds to set.
         */
        public void setSeconds(Integer seconds) {
            this.mSeconds = seconds;
        }
        
        /**
         * Gets the 
         */
        public void reset(){
            setSeconds(getSecondsToDate(getDate()));
        }
        
        /**
         * Calculate the remaining seconds between now and the provided date. If the
         * time is in the past, the remaining seconds is from tomorrow at the same
         * time (Hours and minutes).
         * 
         * @param d
         * @return Integer
         */
        protected Integer getSecondsToDate(Date d) {
            
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            int hours = c.get(Calendar.HOUR_OF_DAY);
            int minutes = c.get(Calendar.MINUTE);
            int seconds = (hours * 3600 + minutes * 60);

            c = Calendar.getInstance();
            int currenthours = c.get(Calendar.HOUR_OF_DAY);
            int currentminutes = c.get(Calendar.MINUTE);
            int currentseconds = (currenthours * 3600 + currentminutes * 60);
            // If the value is negative, it means we intend the next day.
            // timer will be recalculated at midnight
            int remaining = seconds - currentseconds;
            // If we are within the same second (delta is zero), we substract 1
            // to mark that the new remaining value is the next day. If
            // not we would fire several time within the same second. :)
            if (remaining == 0)
                remaining -= 1;
            if (remaining < 0) {
                remaining += 86400;
            }
            return new Integer(remaining);
        }
    }
}