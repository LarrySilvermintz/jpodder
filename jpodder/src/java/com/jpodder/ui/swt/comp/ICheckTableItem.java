package com.jpodder.ui.swt.comp;

/**
 * A generic interface to be used by the checked table model.
 * Any class can implement this interface. 
 * @author Christophe Bouhier
 */
public interface ICheckTableItem {

        /**
         * @return Returns the checked.
         */
        public boolean isChecked();

        /**
         * @param checked
         *            The checked to set.
         */
        public void setChecked(boolean checked);

        
        /**
         * @return Returns the value.
         */
        public Object getValue();

        /**
         * @return Returns the string representations.
         */
        public String getString();

        /**
         * @param value
         *            The value to set.
         */
        public void setValue(Object value);
        
        
        
}
