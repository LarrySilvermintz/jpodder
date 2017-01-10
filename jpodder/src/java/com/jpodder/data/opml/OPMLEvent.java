package com.jpodder.data.opml;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class OPMLEvent extends java.util.EventObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1164775248585956587L;

	protected Object result;

    protected String information;

    /**
     * @param source
     * @param task
     * @param subject
     * @param exception
     */
    public OPMLEvent(Object pSource, Object pResult) {
        super(pSource);
        this.result = pResult;
    }

    public Object getResult() {
        return result;
    }

}