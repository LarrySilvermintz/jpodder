package com.jpodder.ui.swt.bind;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import com.jpodder.util.Debug;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */

/**
 * A generic UI binder.
 */
public abstract class AbstractBinder {

	private static final int COMPONENT_TYPE_TEXT_FIELD = 0;

	private static final int COMPONENT_TYPE_BUTTON = 1;

	private static final int COMPONENT_TYPE_SLIDER = 2;

	private static final int COMPONENT_TYPE_TEXT_FIELD_URL = 4;

	private static final int COMPONENT_TYPE_TEXT_FIELD_INTEGER = 5;

	private static final int COMPONENT_TYPE_COMBOBOX = 6;

	private static final int COMPONENT_TYPE_LABEL = 7;

	private static final int COMPONENT_TYPE_SCALE = 8;

	private static final int COMPONENT_UNRESOLVED = -1;

	protected Logger mLog = Logger.getLogger(getClass().getName());

	private Object mComponent;

	private int mComponentType = COMPONENT_UNRESOLVED;

	public AbstractBinder(Object pComponent) {
		mComponent = pComponent;
		resolveComponentType();
	}

	private void resolveComponentType() {
		if (mComponent instanceof Scale) {
			mComponentType = COMPONENT_TYPE_SCALE;
		} else if (mComponent instanceof Text) {
			mComponentType = COMPONENT_TYPE_TEXT_FIELD;
		} else if (mComponent instanceof Label) {
			mComponentType = COMPONENT_TYPE_LABEL;
		} else if (mComponent instanceof Combo) {
			mComponentType = COMPONENT_TYPE_COMBOBOX;
		} else if (mComponent instanceof Slider) {
			mComponentType = COMPONENT_TYPE_SLIDER;
		}
		if (mComponentType == COMPONENT_UNRESOLVED) {
			throw new IllegalArgumentException();
		}
	}

	public int getComponentType() {
		return mComponentType;
	}

	public void read(Object pObject) {
		try {
			switch (mComponentType) {
			case COMPONENT_TYPE_LABEL:
				Label lLabel = (Label) mComponent;
				lLabel.setText((pObject == null ? "" : pObject.toString()));
				break;
			case COMPONENT_TYPE_TEXT_FIELD:
				((Text) mComponent).setText((pObject == null ? "" : pObject
						.toString()));
				break;
			case COMPONENT_TYPE_TEXT_FIELD_URL:
				((Text) mComponent).setText((pObject == null ? ""
						: ((URL) pObject).toExternalForm()));
				break;
			case COMPONENT_TYPE_TEXT_FIELD_INTEGER:
				((Text) mComponent).setText((pObject == null ? ""
						: ((Integer) pObject).toString()));
				break;
			case COMPONENT_TYPE_BUTTON:
				boolean lSelected = ((Boolean) pObject).booleanValue();
				((Button) mComponent).setSelection(lSelected);
				boolean lEnabled = ((Button) mComponent).getEnabled();

				if (lEnabled && lSelected) {
					((Button) mComponent).notifyListeners(SWT.Selection,
							new Event());
				}
				break;
			case COMPONENT_TYPE_SLIDER:
				int lNumber = ((Integer) pObject).intValue();
				((Slider) mComponent).setSelection(lNumber);
				break;
			case COMPONENT_TYPE_SCALE:
				lNumber = ((Integer) pObject).intValue();
				((Scale) mComponent).setSelection(lNumber);
				break;
			case COMPONENT_TYPE_COMBOBOX:
				if (Debug.WITH_DEV_DEBUG) {
					mLog.info("Setting value for JComboBox(): " + pObject);
				}
				Combo lComponent = (Combo) mComponent;
				if (pObject instanceof String) {
					int lComboIndex = lComponent.indexOf((String) pObject);
					if (lComboIndex != -1) {
						lComponent.select(lComboIndex);
					}
				}
				lComponent.notifyListeners(SWT.Selection, new Event());
				break;

			default: {
				mLog.warn("unresolved binding component");
			}
			}
		} catch (ClassCastException cce) {
			if (com.jpodder.util.Debug.WITH_DEV_DEBUG) {
				mLog.error("read(), failed because of object: " + pObject, cce);
			}
		}
	}

	public Object save() {
		try {
			Object lObject = null;
			switch (mComponentType) {
			case COMPONENT_TYPE_LABEL:
				lObject = ((Label) mComponent).getText();
				break;

			case COMPONENT_TYPE_TEXT_FIELD:
				lObject = ((Text) mComponent).getText();
				break;
			case COMPONENT_TYPE_TEXT_FIELD_URL:
				try {
					lObject = new URL(((Text) mComponent).getText());
				} catch (MalformedURLException e1) {
					try {
						lObject = new URL("http://");
					} catch (MalformedURLException e2) {
						e2.printStackTrace();
					}
				}
				break;
			case COMPONENT_TYPE_TEXT_FIELD_INTEGER:
				String lText = ((Text) mComponent).getText();
				if (lText.length() > 0) {
					lObject = new Integer(lText);
				}
				break;
			case COMPONENT_TYPE_BUTTON:
				lObject = new Boolean(((Button) mComponent).getSelection());
				break;
			case COMPONENT_TYPE_SLIDER:
				lObject = new Integer(((Slider) mComponent).getSelection());
				break;
			case COMPONENT_TYPE_SCALE:
				lObject = new Integer(((Scale) mComponent).getSelection());
				break;
			case COMPONENT_TYPE_COMBOBOX:
				lObject = ((Combo) mComponent).getItem(((Combo) mComponent)
						.getSelectionIndex());
				break;
			}

			return lObject;

		} catch (RuntimeException re) {
			if (com.jpodder.util.Debug.WITH_DEV_DEBUG) {
				mLog.error("save(), failed , re");
			}
			throw re;
		}
	}
}