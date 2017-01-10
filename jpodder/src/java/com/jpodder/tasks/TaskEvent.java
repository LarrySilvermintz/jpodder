package com.jpodder.tasks;

import java.util.EventObject;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class TaskEvent extends EventObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6331811869996629503L;
	protected Object mResult;
    protected String information;

    /**
     * @param source
     * @param task
     * @param subject
     * @param exception
     */
    public TaskEvent(Object pSource, Object pResult) {
        super(pSource);
        mResult = pResult;
    }

    public Object getResult() {
        return mResult;
    }

}