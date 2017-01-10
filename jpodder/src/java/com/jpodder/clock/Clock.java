package com.jpodder.clock;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.1
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

/**
 * The master timer/clock of this application. This clock is used to update the
 * tasks of threads in the application. <p/>
 * <ul>
 * <li>Add Actionlisteners: Triggered every second</li>
 * <li>Add ClockListeners: Triggered every 30 seconds</li>
 * </ul>
 */
public class Clock extends TimerTask implements ActionListener, IClockListener {

	private Logger mLog = Logger.getLogger(this.getClass().getName());

	public static Timer lTimer;

	private static final int actionInterval = 30000;
	
	private List<ActionListener> mActionListeners = new CopyOnWriteArrayList<ActionListener>();
	private List<IClockListener> mClockListeners = new CopyOnWriteArrayList<IClockListener>();

	/**
	 * Application elapsed time in miliseconds.
	 */
	public static long timeElapsed;

	private static Clock sSelf;

	public static Clock getInstance() {
		if (sSelf == null) {
			sSelf = new Clock();
		}
		return sSelf;
	}

	public Clock() {
		initialize();
	}

	/**
	 * Initialize the master timer. This method defines the timer which updates
	 * the various time depending tasks. For now these are the download and
	 * torrent download tables. It is also used to decrease the schedule
	 * counter.
	 */
	public void initialize() {
		addActionListener(this);
		lTimer = new Timer();
		lTimer.schedule(this, 200, 1000);
		addClockListener(this);
	}

	public void actionPerformed(ActionEvent evt) {
		timeElapsed += 1000;
		long modulo = timeElapsed % actionInterval;
		long division = modulo / (actionInterval - 1000);
		if (division >= 1) {
			fireClockEvents();
		}
	}

	private void fireClockEvents() {

		Iterator<IClockListener> lIt = mClockListeners.iterator();
		synchronized (lIt) {
			while (lIt.hasNext()) {
				IClockListener lL = lIt.next();
				lL.timeElapsed(new ClockEvent(this));
			}
		}

	}

	public void addActionListener(ActionListener pListener) {
		if (!mActionListeners.contains(pListener)) {
			mActionListeners.add(pListener);
		}
	}

	public void removeActionListener(ActionListener pListener) {
		if (mActionListeners.contains(pListener)) {
			mActionListeners.remove(pListener);
		}
	}

	public void addClockListener(IClockListener pListener) {
		if (!mClockListeners.contains(pListener)) {
			mClockListeners.add(pListener);
		}
	}

	public void removeClockListener(IClockListener pListener) {
		if (mClockListeners.contains(pListener)) {
			mClockListeners.remove(pListener);
		}
	}

	public void run() {
		// fire all listners.

		Iterator lIt = mActionListeners.iterator();
		synchronized (lIt) {
			while (lIt.hasNext()) {
				ActionListener lL = (ActionListener) lIt.next();
				lL.actionPerformed(new ActionEvent(this, 101, "clock"));
			}
		}
	}

	public void timeElapsed(ClockEvent pEvent) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Clock Event: " + pEvent.getSource());
		}
	}
}