package com.jpodder.ui.swt.status;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.list.XPersonalFeedList;
import com.jpodder.data.player.PlayerLogic;
import com.jpodder.schedule.SchedulerLogic;
import com.jpodder.util.Messages;
import com.jpodder.util.Util;

/**
 * A status bar should information about feeds, enclosures, schedule interval
 * and more...
 * 
 * @author <a href="mailto:christophe@kualasoft.com" >Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class StatusController implements IConfigurationListener,
		SelectionListener {

	private static StatusController sInstance = new StatusController();

	// We use a message formatter, to get the complete String with arguments
	// when we need
	// them
	private String STATUS_FILE_MULTIPLE;

	private String STATUS_FILE_SINGLE;

	private String STATUS_FILE_NOLOCAL;

	private String STATUS_SCHEDULE_REMAINING;

	private String STATUS_SCHEDULE_OFF;

	private String STATUS_SCHEDULE_TT_OFF;

	public static StatusController getInstance() {
		return sInstance;
	}

	private StatusBar mView;

	public StatusController() {

	}

	public void initialize(StatusBar pView) {
		mView = pView;
		STATUS_FILE_MULTIPLE = Messages.getString("status.file.multiple");
		STATUS_FILE_SINGLE = Messages.getString("status.file.single");
		STATUS_FILE_NOLOCAL = Messages.getString("status.file.nolocal");
		STATUS_SCHEDULE_REMAINING = Messages
				.getString("status.schedule.remaining");
		STATUS_SCHEDULE_OFF = Messages.getString("status.schedule.off");
		STATUS_SCHEDULE_TT_OFF = Messages
				.getString("status.schedule.off.tooltip");
		mView.mScheduleCheck.addSelectionListener(this);
	}

	public StatusBar getView() {
		return mView;
	}

	public void widgetSelected(SelectionEvent e) {
		Widget w = e.widget;
		if (w instanceof Button) {
			Button b = (Button) w;
			SchedulerLogic.getInstance().setSchedulingState(b.getSelection(),
					this);
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void clearBar() {
		if (mView != null) {
			mView.mFolderField.setText("");
			mView.mFolderField.setToolTipText("");
			mView.mSizeField.setText("");
		}
	}

	public void updateSummary() {
		if (mView != null) {
			XPersonalFeedList.FeedsSummary sum = XPersonalFeedList.getSummary();
			mView.mFeedField.setText(" " + sum.getShortSummary());
			mView.mFeedField.setToolTipText(sum.getLongSummary());
		}
	}

	public void updateFeedBar(IXPersonalFeed pFeed) {
		if (mView != null) {
			if (pFeed != null) {
				if (pFeed.getFolder() != null) {
					mView.mFolderField.setText(pFeed.getFolder());
					mView.mFolderField.setToolTipText(pFeed.getFolder());
					int count = pFeed.getOnDiscFileCount();
					String sizeText;
					if (count != 0) {
						sizeText = (count > 1 ? Messages.getFormatedMessage(
								STATUS_FILE_MULTIPLE,
								new String[] { new Integer(count).toString() })
								: Messages.getFormatedMessage(
										STATUS_FILE_SINGLE,
										new String[] { new Integer(count)
												.toString() }))
								+ pFeed.getAccumulatedFolderSize(true);
					} else {
						// feedstatuscontroller.nolocal
						sizeText = STATUS_FILE_NOLOCAL;
					}
					mView.mSizeField.setText(" " + sizeText);
				}
			} else {
				mView.mSizeField.setText("");
			}
		}

	}

	/**
	 * Update the status bar with the default player for .mp3
	 * 
	 * @param pFeed
	 */
	public void updateApplicationBar(String pName) {
		if (mView != null) {
			// CB TODO, We might want to show the player, which corresponds to
			// the MIME type of a file.
			// This could be based on a selected file option.
			mView.mPlayerField.setText(" " + pName);
		}
	}

	/**
	 * Update the field showing the remaining time before the scanning sequence
	 * starts. This method is called from a non UI thread. UI updates are
	 * performed asynchroneously.
	 * 
	 * @param time
	 */
	public void updateScheduleField(final long time) {
		if (mView != null && !mView.isDisposed()) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if(mView.isDisposed()){
						return;
					}
					
					if (time != 0) {
						String formatedTime = Util.formatTime(time);
						mView.mScheduleField.setText(" @ " + formatedTime);
						mView.mScheduleField
								.setToolTipText(" "
										+ Messages
												.getFormatedMessage(
														STATUS_SCHEDULE_REMAINING,
														new String[] {
																formatedTime,
																XPersonalFeedList
																		.getSummary()
																		.getCandidatesSummary() }));
					} else {
						mView.mScheduleField.setText(" " + STATUS_SCHEDULE_OFF);
						mView.mScheduleField.setToolTipText(" "
								+ STATUS_SCHEDULE_TT_OFF);
					}
				}
			});
		}
	}

	public String getPlural(int pCount, String pSingleString,
			String pPluralString) {
		return "";
	}

	/**
	 * Satisfies the property interface. We want to know the selected player We
	 * update the status bar.
	 * 
	 * @param e
	 *            PropertyEvent
	 */

	public void configurationChanged(ConfigurationEvent e) {

		boolean lAuto = Configuration.getInstance().getAuto();
		if (!lAuto) {
			StatusController.getInstance().updateScheduleField(0);
		}
		mView.mScheduleCheck.setSelection(lAuto);
		String lPlayerName = PlayerLogic.getInstance().getDefaultPlayer()
				.getName();
		updateApplicationBar(lPlayerName);
	}
}