package com.jpodder.ui.swt.comp;

public class CheckTableItem implements ICheckTableItem{

        boolean checked;

        Object value;

        public CheckTableItem(){
            this(false,null);
        }
        /**
         * @param checked
         * @param value
         */
        public CheckTableItem(boolean checked, Object value) {
            super();
            this.checked = checked;
            this.value = value;
        }

        /**
         * @return Returns the checked.
         */
        public boolean isChecked() {
            return checked;
        }

        /**
         * @param checked
         *            The checked to set.
         */
        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        /**
         * @return Returns the value.
         */
        public Object getValue() {
            return value;
        }

        /**
         * @param value
         *            The value to set.
         */
        public void setValue(Object value) {
            this.value = value;
        }

        /**
         * @see com.jpodder.ui.components.CheckTableItem#getString()
         */
        public String getString() {
            return value.toString();
        }   
}
