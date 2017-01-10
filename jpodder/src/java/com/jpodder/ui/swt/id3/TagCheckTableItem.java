package com.jpodder.ui.swt.id3;

import com.jpodder.data.id3.ID3Tag;
import com.jpodder.ui.swt.comp.ICheckTableItem;

/**
 * An id3 tag object checkable implementation
 * TODO This is a UI functions.
 */
public class TagCheckTableItem implements ICheckTableItem {

    private ID3Tag mTag;
    private boolean mIsSelected;

    /**
     * Empty constructor.
     */
    public TagCheckTableItem() {
    }

    /**
     * @param pTag
     *            Tag this item is delegating to
     * @param pIsSelected
     *            True if the item is selected
     */
    public TagCheckTableItem(ID3Tag pTag, boolean pIsSelected) {
        mTag = pTag;
        mIsSelected = pIsSelected;
    }

    public boolean isChecked() {
        return mIsSelected;
    }

    public void setChecked(boolean pValue) {
        mIsSelected = pValue;
    }

    /** @return The name of the underlying tag * */
    public String getString() {
        return mTag.getName();
    }

    /** This method is empty because it is not supported * 
     * @param value*/
    public void setValue(Object value) {
    }

    /** @return The underlying tag * */
    public Object getValue() {
        return mTag;
    }
}