package com.jpodder.ui.swt.text;

import java.util.ArrayList;

/**
 * An insighthandler class.
 * <p>
 * A handler which holds a list of insight controllers. One single controller is
 * the active one which will tight to the text component.
 * <p>
 * 
 * @see InsightControl
 */
public class InsightHandler implements InsightHandlerInterface {

	private InsightControl selectedInsight;
	ArrayList<InsightControl> mList = new ArrayList<InsightControl>();
	
	// private final wpEditor tab;
	public InsightHandler() {
		// Selected Control depends on the activated action.
	}

	public void addControl(InsightControl control) {
		mList.add(control);
		selectedInsight = control;
	}

	public void removeControl(InsightControl control) {
		mList.remove(control);
	}

	public InsightControl getSelectedControl() {
		return selectedInsight;
	}

}
