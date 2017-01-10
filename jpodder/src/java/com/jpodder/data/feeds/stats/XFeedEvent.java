package com.jpodder.data.feeds.stats;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 */
public class XFeedEvent extends java.util.EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3754324010479548597L;

	public static final int INSTRUCTION_COLLECT = 0;

	public static final int INSTRUCTION_COLLECT_ENCL = 1;

	public static final int INSTRUCTION_INSPECT = 2;

	public static final int INSTRUCTION_DOWNLOAD = 3;

	public static final int INSTRUCTION_STORE = 4;

	public static final int INSTRUCTION_MARK = 5;

	public static final int INSTRUCTION_INFO = 6;

	private static final String[] DESCRIPTIONS = { "Collect",
			"Collect Enclosure", "Inspect", "Download", "Store", "Mark", "Info" };

	protected int mInstruction = -1;

	protected Exception exception;

	protected Object subject;

	protected String information;

	/**
	 * @param source
	 * @param task
	 * @param subject
	 * @param exception
	 */
	public XFeedEvent(Object source, int task, Object subject,
			String information) {
		super(source);
		this.mInstruction = task;
		this.subject = subject;
		this.information = information;
	}

	/**
	 * @param source
	 * @param task
	 * @param subject
	 * @param exception
	 */
	public XFeedEvent(Object source, int task, Object subject,
			Exception exception) {
		super(source);
		this.mInstruction = task;
		this.exception = exception;
		this.subject = subject;
	}

	/**
	 * @return Returns the exception.
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @return Returns the task.
	 */
	public int getTask() {
		return mInstruction;
	}

	/**
	 * @return Returns the subject.
	 */
	public Object getSubject() {
		return subject;
	}

	public String getInformation() {
		return information;
	}

	public String getDescription() {
		if (mInstruction != -1) {
			return DESCRIPTIONS[mInstruction];
		}
		return "";
	}

}
