package com.jpodder.ui.swt.comp;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @since 1.0
 * @version 1.1
 */
public interface IBaseActionCondition {

	public boolean conditionMet(Object pConditionObject);

	public void setEnabled(boolean pEnabled, Object pConditionObject);

}
