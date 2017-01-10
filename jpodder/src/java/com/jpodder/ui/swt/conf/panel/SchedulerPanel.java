package com.jpodder.ui.swt.conf.panel;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.jpodder.data.configuration.Configuration;
import com.jpodder.ui.swt.conf.ConfigurationBinder;
import com.jpodder.ui.swt.conf.IConfigurationBinder;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Messages;

public class SchedulerPanel implements IConfigurationPanel {

	// --- UI Components
	protected Button mSchedulerOnCheckBox;

	protected Button mScheduleChoiceIntervalButton;

	protected Button mScheduleChoiceTimeButton;

	protected Button mScheduleAddTimeButton;

	protected Button mScheduleRemoveTimeButton;

	protected Button mExecuteOnStartup;

	protected Table mScheduleTimesTable;;

	protected Scale mIntervalSlider;

	protected TimeSpinner mSpinner;

	protected Text mIntervalText;

	// --- Data
	protected ArrayList<ScheduleTimeItem> scheduleTimeList = new ArrayList<ScheduleTimeItem>();

	public IConfigurationBinder mIntervalSliderBinder;

	public IConfigurationBinder mExecuteOnStartupBinder;

	public IConfigurationBinder mSchedulerOnBinder;

	// public IConfigurationBinder mSchedulerIntervalTypeBinder;
	// public IConfigurationBinder mSchedulerTimeTypeBinder;
	public IConfigurationBinder mScheduleTypeBinder;

	public IConfigurationBinder mScheduleTimerBinder;

	public IConfigurationBinder mScheduleModeBinder;

	public IConfigurationBinder[] mBinderList = new IConfigurationBinder[5];

	protected Composite mView;

	public SchedulerPanel(Composite pParent) {
		initialize(pParent);
	}

	public SchedulerPanel() {
	}

	public Composite getView() {
		return mView;
	}

	public void initialize(Composite pParent) {
		mView = new Composite(pParent, SWT.NONE);
		FormLayout lMainLayout = new FormLayout();
		mView.setLayout(lMainLayout);

		// ----------------- SCHEDULER ENABLED

		mSchedulerOnCheckBox = new Button(mView, SWT.CHECK);
		mSchedulerOnCheckBox.setText(Messages
				.getString("schedulerpanel.toggle"));

		mSchedulerOnCheckBox.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				setSchedulingOn(mSchedulerOnCheckBox.getSelection());
			}
		});

		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		mSchedulerOnCheckBox.setLayoutData(formData);

		Group lGroup = new Group(mView, SWT.SHADOW_IN);
		FormData formData2 = new FormData();
		formData2.top = new FormAttachment(mSchedulerOnCheckBox, 5);
		formData2.left = new FormAttachment(0, 5);
		formData2.right = new FormAttachment(100, -5);
		lGroup.setLayoutData(formData2);

		GridLayout lLayout = new GridLayout();
		lLayout.numColumns = 3;
		lGroup.setLayout(lLayout);

		Label scheduleChoiceDescription = new Label(lGroup, SWT.BORDER);
		scheduleChoiceDescription.setText(Messages
				.getString("schedulerpanel.choice"));
		GridData lData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		lData.horizontalSpan = 3;
		scheduleChoiceDescription.setLayoutData(lData);
		scheduleChoiceDescription
				.setBackground(UITheme.getInstance().TABLE_ODD_BACKGROUND_COLOR);

		mScheduleChoiceTimeButton = new Button(lGroup, SWT.RADIO);
		mScheduleChoiceTimeButton.setText(Messages
				.getString("schedulerpanel.type.time"));
		mScheduleChoiceTimeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				setTimeBased(true);
				setIntervalBased(false);
			}
		});
		mScheduleChoiceTimeButton.setEnabled(false);
		lData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		lData.horizontalSpan = 3;
		mScheduleChoiceTimeButton.setLayoutData(lData);

		mSpinner = new TimeSpinner(lGroup);
		lData = new GridData();
		lData.horizontalIndent = 50;
		lData.horizontalSpan = 1;
		mSpinner.setLayoutData(lData);
		mSpinner.setTime(new Date(System.currentTimeMillis()));

		// JLabel scheduleTimeSelectLabel = new JLabel(Messages
		// .getString("schedulerpanel.time.description"));

		mScheduleAddTimeButton = new Button(lGroup, SWT.PUSH);
		mScheduleAddTimeButton.setText(Messages.getString("general.add"));
		mScheduleAddTimeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// Limit to 3 scheduled times.
				if (mScheduleTimesTable.getItemCount() < 3) {
					Date lTime = mSpinner.getTime();
					ScheduleTimeItem lScheduleItem = new ScheduleTimeItem(lTime);
					addTableItem(lScheduleItem);
					scheduleTimeList.add(lScheduleItem);
					mScheduleAddTimeButton.setEnabled(true);
					if (mScheduleTimesTable.getItemCount() == 3) {
						mScheduleAddTimeButton.setEnabled(false);
					}
				}
			}
		});
		lData = new GridData();
		lData.horizontalAlignment = GridData.END;
		mScheduleAddTimeButton.setLayoutData(lData);

		mScheduleRemoveTimeButton = new Button(lGroup, SWT.PUSH);
		mScheduleRemoveTimeButton.setText(Messages.getString("general.remove"));
		mScheduleRemoveTimeButton.setEnabled(false);
		mScheduleRemoveTimeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int size = scheduleTimeList.size();
				mScheduleAddTimeButton.setEnabled(size < 3 && size >= 0);
				int lIndex = mScheduleTimesTable.getSelectionIndex();
				if (lIndex != -1) {
					mScheduleTimesTable.remove(lIndex);
					scheduleTimeList.remove(lIndex);
				}
				if (mScheduleTimesTable.getItemCount() == 0) {
					mScheduleRemoveTimeButton.setEnabled(false);
				}
				if (mScheduleTimesTable.getItemCount() < 3) {
					mScheduleAddTimeButton.setEnabled(true);
				}
			}
		});

		mScheduleTimesTable = new Table(lGroup, SWT.BORDER | SWT.CHECK
				| SWT.SINGLE | SWT.FULL_SELECTION | SWT.VIRTUAL);
		// scheduleTimesTable.setItemCount(3);
		mScheduleTimesTable.setHeaderVisible(true);
		mScheduleTimesTable.setLinesVisible(true);
		// scheduleTimesTable.setBounds(0, 0, 140, 100);

		lData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		lData.horizontalIndent = 50;
		lData.horizontalSpan = 3;
		lData.widthHint = 170;
		lData.heightHint = 70;
		mScheduleTimesTable.setLayoutData(lData);

		TableColumn lColumn = new TableColumn(mScheduleTimesTable, SWT.CENTER);
		lColumn.setWidth(50);
		lColumn.setText(Messages.getString("schedulerpanel.time.enabled"));
		lColumn.setResizable(false);

		TableColumn lColumn1 = new TableColumn(mScheduleTimesTable, SWT.LEFT);
		lColumn1.setText(Messages.getString("schedulerpanel.time.when"));
		lColumn1.setWidth(120);

		mScheduleTimesTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int lSelection = mScheduleTimesTable.getSelectionIndex();
				if (lSelection != -1) {
					mScheduleRemoveTimeButton.setEnabled(true);
				} else {
					mScheduleRemoveTimeButton.setEnabled(false);
				}
			}
		});

		mScheduleChoiceIntervalButton = new Button(lGroup, SWT.RADIO);
		mScheduleChoiceIntervalButton.setText(Messages
				.getString("schedulerpanel.type.interval"));
		mScheduleChoiceIntervalButton.addListener(SWT.Selection,
				new Listener() {
					public void handleEvent(Event e) {
						setTimeBased(false);
						setIntervalBased(true);
					}
				});

		mScheduleChoiceIntervalButton.setEnabled(false);
		lData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		lData.horizontalSpan = 3;
		mScheduleChoiceIntervalButton.setLayoutData(lData);

		mExecuteOnStartup = new Button(lGroup, SWT.CHECK);
		mExecuteOnStartup.setText(Messages
				.getString("schedulerpanel.executeOnStartup"));
		lData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		lData.horizontalSpan = 3;
		lData.horizontalIndent = 50;
		mExecuteOnStartup.setLayoutData(lData);

		mIntervalSlider = new Scale(lGroup, SWT.HORIZONTAL);
		mIntervalSlider.setIncrement(15);
		mIntervalSlider.setMinimum(0);
		mIntervalSlider.setMaximum(120);
		mIntervalSlider.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				int lValue = mIntervalSlider.getSelection();
				mIntervalText
						.setText(new Integer(lValue).toString() + " (min)");
			}

		});

		lData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		lData.horizontalSpan = 2;
		lData.horizontalIndent = 50;
		mIntervalSlider.setLayoutData(lData);

		mIntervalText = new Text(lGroup, SWT.BORDER | SWT.READ_ONLY
				| SWT.SINGLE);
		lData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		lData.horizontalSpan = 1;
		lData.widthHint = 10;
		mIntervalText.setLayoutData(lData);

		// ---- UI Binding to property.
		try {
			mIntervalSliderBinder = new ConfigurationBinder(mIntervalSlider,
					IConfigurationBinder.SUB_TYPE_CONFIGURATION,
					Configuration.SCHEDULING_DELAY);
			mExecuteOnStartupBinder = new ConfigurationBinder(
					mExecuteOnStartup,
					IConfigurationBinder.SUB_TYPE_SCHEDULING,
					Configuration.SCHEDULING_EXEC);
			mSchedulerOnBinder = new ConfigurationBinder(mSchedulerOnCheckBox,
					IConfigurationBinder.SUB_TYPE_CONFIGURATION,
					Configuration.CONFIG_AUTO);
			mScheduleTimerBinder = new TimerBinder();
			mScheduleModeBinder = new ScheduleModeBinder();

			mBinderList[0] = mIntervalSliderBinder;
			mBinderList[1] = mExecuteOnStartupBinder;
			mBinderList[2] = mSchedulerOnBinder;
			mBinderList[3] = mScheduleTimerBinder;
			mBinderList[4] = mScheduleModeBinder;

		} catch (Exception e) {
		}
		setIntervalBased(false);
		setTimeBased(false);
	}

	public void addTableItem(ScheduleTimeItem pScheduleItem) {
		TableItem lItem = new TableItem(mScheduleTimesTable, SWT.NONE);
		lItem.setText(1, pScheduleItem.getString());
		lItem.setChecked(true);
	}

	public void clearTableItems() {
		mScheduleTimesTable.removeAll();
	}

	/**
	 * Custom Check table item. formats the Date object using a date formatter,
	 * showing hours and minutes only.
	 */
	public class ScheduleTimeItem {

		Object mValue;

		public String getString() {
			Date d = (Date) mValue;
			SimpleDateFormat f = new SimpleDateFormat("HH:mm a");
			return f.format(d);
		}

		/**
		 * @param checked
		 * @param value
		 */
		public ScheduleTimeItem(Object value) {
			mValue = value;
		}

		public Object getValue() {
			return mValue;
		}
	}

	public void setSchedulingOn(boolean pEnabled) {
		mScheduleChoiceIntervalButton.setEnabled(pEnabled);
		mScheduleChoiceTimeButton.setEnabled(pEnabled);
		setTimeBased(pEnabled && mScheduleChoiceTimeButton.getSelection());
		setIntervalBased(pEnabled
				&& mScheduleChoiceIntervalButton.getSelection());
	}

	/**
	 * Set the UI status of the time based components to the provided status.
	 * When set to false, the UI components are greyed out.
	 * 
	 * @param pOn
	 */
	public void setTimeBased(boolean pOn) {
		mScheduleTimesTable.setEnabled(pOn);
		mSpinner.setEnabled(pOn);
		mScheduleAddTimeButton.setEnabled(pOn);
		if (pOn) {
			int size = scheduleTimeList.size();
			if (size >= 3) {
				mScheduleAddTimeButton.setEnabled(false);
			} else {
				mScheduleAddTimeButton.setEnabled(true);
			}
		} else {
			mScheduleAddTimeButton.setEnabled(false);
			mScheduleRemoveTimeButton.setEnabled(false);
		}
	}

	public void setIntervalBased(boolean pOn) {
		mIntervalSlider.setEnabled(pOn);
		int lValue = mIntervalSlider.getSelection();
		mIntervalText.setText(new Integer(lValue).toString() + " (min)");
		mExecuteOnStartup.setEnabled(pOn);
		mIntervalText.setEnabled(pOn);
	}

	public class TimeSpinner extends Composite {

		TimeSpinnerComponent mHourComponent;

		TimeSpinnerComponent mMinuteComponent;

		public TimeSpinner(Composite pParent) {
			super(pParent, SWT.NONE);
			FillLayout lLayout = new FillLayout();
			setLayout(lLayout);
			mHourComponent = new TimeSpinnerComponent(this);
			mHourComponent.setMaximum(23);
			mHourComponent.setMinimum(0);
			mMinuteComponent = new TimeSpinnerComponent(this);
			mMinuteComponent.setMaximum(59);
			mMinuteComponent.setMinimum(0);
			this.pack();
		}

		public void setTime(Date pDate) {
			int lHours = pDate.getHours();
			mHourComponent.setValue(lHours);
			int lMinutes = pDate.getMinutes();
			mMinuteComponent.setValue(lMinutes);

		}

		public Date getTime() {
			int lHours = mHourComponent.getValue();
			int lMinutes = mMinuteComponent.getValue();
			Date lDate = new Date();
			lDate.setHours(lHours);
			lDate.setMinutes(lMinutes);
			return lDate;
		}

		public void setEnabled(boolean pEnabled) {
			mHourComponent.setEnabled(pEnabled);
			mMinuteComponent.setEnabled(pEnabled);
		}

		class TimeSpinnerComponent extends Composite {

			Text mText;

			Button mUpButton;

			Button mDownButton;

			int mMaximum = 100;

			int mMinimum = 0;

			Date lDate;

			public TimeSpinnerComponent(Composite pParent) {
				super(pParent, SWT.NONE);
				GridLayout lLayout = new GridLayout();
				setLayout(lLayout);
				lLayout.numColumns = 2;
				lLayout.horizontalSpacing = 0;
				lLayout.verticalSpacing = 0;
				lLayout.marginHeight = 0;
				lLayout.marginWidth = 0;
				mText = new Text(this, SWT.BORDER | SWT.SINGLE);
				mText.addVerifyListener(new NumberVerifier());
				GridData lData = new GridData(GridData.FILL_BOTH);
				lData.horizontalSpan = 1;
				lData.verticalSpan = 2;
				lData.widthHint = 12;
				mText.setLayoutData(lData);
				mUpButton = new Button(this, SWT.ARROW | SWT.UP);
				GridData lData1 = new GridData(GridData.FILL_BOTH);
				lData1.horizontalSpan = 1;
				lData1.verticalSpan = 1;
				lData1.heightHint = 10;
				mUpButton.setLayoutData(lData1);
				mUpButton.addListener(SWT.Selection, new UpListener());
				mDownButton = new Button(this, SWT.ARROW | SWT.DOWN);
				GridData lData2 = new GridData(GridData.FILL_BOTH);
				lData2.horizontalSpan = 1;
				lData2.verticalSpan = 1;
				lData2.heightHint = 10;
				mDownButton.setLayoutData(lData2);
				mDownButton.addListener(SWT.Selection, new DownListener());
				pack();
			}

			public void setMaximum(int pMaximum) {
				mMaximum = pMaximum;
			}

			public void setMinimum(int pMinimum) {
				mMinimum = pMinimum;
			}

			public void setValue(int pValue) {
				mText.setText(new Integer(pValue).toString());
			}

			public int getValue() {
				return new Integer(mText.getText()).intValue();
			};

			public void setEnabled(boolean pEnabled) {
				mText.setEnabled(pEnabled);
				mUpButton.setEnabled(pEnabled);
				mDownButton.setEnabled(pEnabled);
			};

			class NumberVerifier implements VerifyListener {
				public void verifyText(VerifyEvent e) {
					String lText = e.text;
					try {
						new Integer(lText);
						e.doit = true;
					} catch (NumberFormatException nfe) {
						e.doit = false;
					}
				}
			}

			class UpListener implements Listener {
				public void handleEvent(Event event) {
					String lText = mText.getText();
					if (lText.length() > 0) {
						int lIntValue = new Integer(lText).intValue();
						if (lIntValue < mMaximum) {
							lIntValue++;
						} else {
							lIntValue = mMinimum;
						}
						mText.setText(new Integer(lIntValue).toString());
					}
				}
			}

			class DownListener implements Listener {
				public void handleEvent(Event event) {
					String lText = mText.getText();
					if (lText.length() > 0) {
						int lIntValue = new Integer(lText).intValue();
						if (lIntValue > mMinimum) {
							lIntValue--;
						} else {
							lIntValue = mMaximum;
						}
						mText.setText(new Integer(lIntValue).toString());
					}
				}
			}
		}
	}

	public class TimerBinder implements IConfigurationBinder {
		private Configuration.Scheduling lScheduling = Configuration
				.getInstance().getScheduling();

		public String getName() {
			return "Timer";
		}

		public void read() {
			clearTableItems();
			scheduleTimeList.clear();
			Iterator i = lScheduling.getTimerIterator();
			while (i.hasNext()) {
				Configuration.Scheduling.Timer lTimer = (Configuration.Scheduling.Timer) i
						.next();
				ScheduleTimeItem lScheduleItem = new ScheduleTimeItem(lTimer
						.getTimer());
				scheduleTimeList.add(lScheduleItem);
				addTableItem(lScheduleItem);
			}
		}

		public void save() {
			lScheduling.clearTimers();
			Iterator<ScheduleTimeItem> i = scheduleTimeList.iterator();			
			while (i.hasNext()) {
				ScheduleTimeItem lItem = i.next();
				lScheduling.addTimer((Date) lItem.getValue());
			}
		}
	}

	public class ScheduleModeBinder implements IConfigurationBinder {

		public String getName() {
			return "type";
		}

		public void read() {
			Configuration.Scheduling lScheduling = Configuration.getInstance()
					.getScheduling();
			mScheduleChoiceIntervalButton
					.setSelection(lScheduling.getType() == 1);
			mScheduleChoiceTimeButton.setSelection(lScheduling.getType() == 0);
			if (Configuration.getInstance().getAuto()) {
				setIntervalBased(lScheduling.getType() == 1);
				setTimeBased(lScheduling.getType() == 0);
			} else {
				setIntervalBased(false);
				setTimeBased(false);
			}
		}

		public void save() {
			Configuration.Scheduling lScheduling = Configuration.getInstance()
					.getScheduling();
			if (mScheduleChoiceIntervalButton.getSelection()) {
				lScheduling.setType(1);
			} else {
				lScheduling.setType(0);
			}
		}
	}

	public IConfigurationBinder[] getBindings() {
		return mBinderList;
	}
}