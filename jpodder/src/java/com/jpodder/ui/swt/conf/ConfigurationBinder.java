package com.jpodder.ui.swt.conf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import com.jpodder.JPodderException;
import com.jpodder.data.configuration.Configuration;
import com.jpodder.util.Debug;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */

/**
 * Binds a UI component to a property.
 * 
 * The sub type is a Configuration subType
 * The provided properyname is used to build a 'get' or 'set' method and 
 * invoke <code>Reflections</code>.
 * <p/>
 * @see com.jpodder.ui.swt.conf.IConfigurationBinder
 * 
 */
public class ConfigurationBinder implements IConfigurationBinder {

    private static final int COMPONENT_TYPE_TEXT_FIELD = 0;
    private static final int COMPONENT_TYPE_BUTTON = 1;
    private static final int COMPONENT_TYPE_SLIDER = 2;
    private static final int COMPONENT_TYPE_TEXT_FIELD_URL = 4;
    private static final int COMPONENT_TYPE_TEXT_FIELD_INTEGER = 5;
    private static final int COMPONENT_TYPE_COMBOBOX = 6;
    private static final int COMPONENT_TYPE_SCALE = 8;
    

    protected Logger mLog = Logger.getLogger(getClass().getName());

    private Object mDataContainer;

    private Object mComponent;

    private int mComponentType;

    private String mPropertyName;

    private int mSubType;

    private Method mGetter;

    private Method mSetter;

    /**
     * Get the name of this binder.
     * 
     * @return
     */
    public String getName() {
        return mPropertyName;
    }

    public ConfigurationBinder(Object pComponent, int pSubType,
            String pPropertyName) throws JPodderException {
        mComponent = pComponent;
        mSubType = pSubType;
        mPropertyName = pPropertyName;

        // Get the method and its type
        Configuration lConfiguration = Configuration.getInstance();
        switch (mSubType) {
            case SUB_TYPE_CONFIGURATION:
                mDataContainer = lConfiguration;
                break;
            case SUB_TYPE_CONNECTION:
                mDataContainer = lConfiguration.getConnection();
                break;
            case SUB_TYPE_GUI:
                mDataContainer = lConfiguration.getGui();
                break;
            case SUB_TYPE_PRODUCTION:
                mDataContainer = lConfiguration.getProduction();
                break;
            case SUB_TYPE_SCHEDULING:
                mDataContainer = lConfiguration.getScheduling();
                break;
            case SUB_TYPE_PLUGIN:
                mDataContainer = null;
                break;
            default:
                throw new JPodderException("Unknown subtype: " + mSubType);
        }
        setConfigurationMethods();
    }

    
    /**
     * Get the getter and setter for a property and associated component. 
     * the property
     * 
     * @throws JPodderException
     */
    private void setConfigurationMethods() throws JPodderException {

        try {
            Class lClass = mDataContainer.getClass();
            String lMethodName = "get" + mPropertyName;
            mGetter = lClass.getMethod(lMethodName, null);
            Class lReturnType = mGetter.getReturnType();
            lMethodName = "set" + mPropertyName;
            mSetter = lClass
                    .getMethod(lMethodName, new Class[] { lReturnType });
            
            if (mComponent instanceof Scale) {
                mComponentType = COMPONENT_TYPE_SCALE;
            } else if (mComponent instanceof Combo) {
                if (lReturnType.equals(String.class)) {
                    mComponentType = COMPONENT_TYPE_COMBOBOX;
                } else {
                    // We don't bind other return types than String.
                }
            } else if (mComponent instanceof Text) {
                if (lReturnType.equals(URL.class)) {
                    mComponentType = COMPONENT_TYPE_TEXT_FIELD_URL;
                } else if (lReturnType.equals(Integer.TYPE)
                        || lReturnType.equals(Integer.class)) {
                    mComponentType = COMPONENT_TYPE_TEXT_FIELD_INTEGER;
                } else {
                    mComponentType = COMPONENT_TYPE_TEXT_FIELD;
                }
            } else if (mComponent instanceof Button) {
                mComponentType = COMPONENT_TYPE_BUTTON;
            } else {
                throw new JPodderException("Unknown component: " + mComponent);
            }
            
        } catch (JPodderException jpe) {
            throw jpe;
        } catch (NoSuchMethodException nsme) {
            throw new JPodderException("Property Name: " + mPropertyName
                    + " of sub type: " + mSubType + " is not correct", nsme);
        }
    }
    
    public void read() {
        Object lObject = null;
        try {
            lObject = mGetter.invoke(mDataContainer, null);
            switch (mComponentType) {
                case COMPONENT_TYPE_TEXT_FIELD:
                    ((Text) mComponent).setText((lObject == null ? "" : lObject
                            .toString()));
                    break;
                case COMPONENT_TYPE_TEXT_FIELD_URL:
                    ((Text) mComponent).setText((lObject == null ? ""
                            : ((URL) lObject).toExternalForm()));
                    break;
                case COMPONENT_TYPE_TEXT_FIELD_INTEGER:
                    ((Text) mComponent).setText((lObject == null ? ""
                            : ((Integer) lObject).toString()));
                    break;
                case COMPONENT_TYPE_BUTTON:
                    boolean lSelected = ((Boolean) lObject).booleanValue();
                    ((Button) mComponent).setSelection(lSelected);
                    boolean lEnabled = ((Button)mComponent).getEnabled();
                    
                    if (lEnabled && lSelected) {
                        ((Button) mComponent).notifyListeners(SWT.Selection,
                                new Event());
                    }
                    break;
                case COMPONENT_TYPE_SLIDER:
                    int lNumber = ((Integer) lObject).intValue();
                    ((Slider) mComponent).setSelection(lNumber);
                    break;
                case COMPONENT_TYPE_SCALE:
                    lNumber = ((Integer) lObject).intValue();
                    ((Scale) mComponent).setSelection(lNumber);
                    break;
                case COMPONENT_TYPE_COMBOBOX:
                    if (Debug.WITH_DEV_DEBUG) {
                        mLog.info("Setting value for JComboBox(): " + lObject);
                    }
                    Combo lComponent = (Combo) mComponent;
                    if (lObject instanceof String) {
                        int lComboIndex = lComponent.indexOf((String) lObject);
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
        } catch (IllegalAccessException iae) {
        } catch (ClassCastException cce) {
            if (com.jpodder.util.Debug.WITH_DEV_DEBUG) {
                mLog.error("read(), failed because of object: " + lObject
                        + " on property: " + mPropertyName, cce);
            }
        } catch (InvocationTargetException ite) {
            Throwable lCause = ite.getCause();
            throw new RuntimeException(lCause.getMessage(), lCause);
        }
    }

    public void save() {
        try {
            Object lObject = null;
            switch (mComponentType) {
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
            if (lObject != null) {
                mSetter.invoke(mDataContainer, new Object[] { lObject });
            }
        } catch (IllegalAccessException iae) {
        } catch (InvocationTargetException ite) {
            Throwable lCause = ite.getCause();
            throw new RuntimeException(lCause.getMessage(), lCause);
        } catch (RuntimeException re) {
            if (com.jpodder.util.Debug.WITH_DEV_DEBUG) {
                mLog.error("save(), failed for property: " + mPropertyName
                        + ", sub type: " + mSubType + ", data container: "
                        + mDataContainer + ", setter method: " + mSetter, re);
            }
            throw re;
        }
    }
}