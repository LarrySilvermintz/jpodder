package com.jpodder.schedule;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.feeds.XFeedLogic;
import com.jpodder.schedule.Scheduler.FixTimeCounter;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class SchedulerLogic implements ActionListener, IConfigurationListener {


    private Logger mLog = Logger.getLogger(getClass().getName());
    private static SchedulerLogic sSelf;
    private Scheduler mScheduler;

    public static SchedulerLogic getInstance() {
        if (sSelf == null) {
            sSelf = new SchedulerLogic();
        }
        return sSelf;
    }

    public SchedulerLogic() {
        mScheduler = new Scheduler();
        mScheduler.setAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                XFeedLogic.getInstance().scan(SchedulerLogic.class, true);
            }
        });
    }

    public void setSchedulingState(boolean pState, Object pSource) {
        mScheduler.setEnabled(pState);
        Configuration.getInstance().setAuto(pState);
        ConfigurationLogic.getInstance().fireConfigurationChanged(
                new ConfigurationEvent(pSource, Configuration.CONFIG_AUTO));
    }

    /**
     * Get the remaining seconds for a timer. There are 3 timers in the system.
     * The values are retrieved from the scheduling properties.
     * <code>null</code> is returned for a non-defined timer.
     * 
     * @param number
     */
    private void updateFixedTimeCounters() {
        Configuration.Scheduling lScheduling = Configuration.getInstance()
                .getScheduling();
        int lTimerSize = lScheduling.getTimerSize();
        FixTimeCounter[] scheduleTimeCounterList = new FixTimeCounter[lTimerSize];
        Iterator i = lScheduling.getTimerIterator();
        int j = 0;
        while (i.hasNext()) {
            Date lTimer = ((Configuration.Scheduling.Timer) i.next())
                    .getTimer();
            scheduleTimeCounterList[j] = mScheduler.getNewCounter(lTimer);
            j++;
        }
        mScheduler.setScheduleTimeCounterList(scheduleTimeCounterList);
    }

    public void updateIntervalCounter() {
        int lDelay = Configuration.getInstance().getDelay();
        if (lDelay >= 0) {
            mScheduler.setScheduleIntervalCounter(lDelay);
        }
    }

    /**
     * Get the scheduler hold by this controller.
     * 
     * @return
     */
    public Scheduler getScheduler() {
        return mScheduler;
    }

    public void actionPerformed(ActionEvent e) {
        mScheduler.updateTask();
    }

    /**
     * Satisfy the properties interface.
     * 
     * @param event
     */
    public void configurationChanged(ConfigurationEvent event) {

        if (!event.getSource().equals(ConfigurationLogic.class)) {
            return;
        }
        Configuration lConfiguration = Configuration.getInstance();
        boolean lAuto = lConfiguration.getAuto();
        mLog.debug("Scheduling is :"
                + (lConfiguration.getAuto() ? "On" : "Off"));
        mScheduler.setEnabled(lAuto);
        
        Configuration.Scheduling lScheduling = Configuration.getInstance()
                .getScheduling();
        // CB TODO Get rid of the scheduling type parameter, as this 
        // id replaced by flags for interval and time based scheduling. 
        // See getIntervalType and getTimeType
        // 3-02-2006
        int lType = lScheduling.getType();
        if (lType != Configuration.SCHEDULING_OFF) {
            mScheduler
                    .setSchedulerMode(lType == Configuration.SCHEDULING_TYPE_INTERVAL ? Scheduler.SCHEDULE_INTERVAL
                            : Scheduler.SCHEDULE_FIXTIME);
            mLog
                    .debug("Scheduling is :"
                            + (lType == Configuration.SCHEDULING_TYPE_INTERVAL ? "Interval "
                                    : " Fixed Time "));
        }

        /**
         * Support for new configuration paramaters, for the scheduling type.
         */
        if (lScheduling.getType()==1) {
            mScheduler.setSchedulerMode(Scheduler.SCHEDULE_INTERVAL);
        } else if (lScheduling.getType()==0) {
            mScheduler.setSchedulerMode(Scheduler.SCHEDULE_FIXTIME);
        }

        updateIntervalCounter();
        updateFixedTimeCounters();
    }
}
