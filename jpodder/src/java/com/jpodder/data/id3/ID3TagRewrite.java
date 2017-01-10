package com.jpodder.data.id3;

/**
 * A simple ID3 frame data type. 
 * It contains the Frame ID, the short name and a value. 
 * 
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @version 1.1
 */
public class ID3TagRewrite {

    private String mName;

    private String mValue;

    private String mId;
    
    private String mDescription;
    
    private boolean mValueChanged = false;
    
    /**
     * Basic constructor
     */
    public ID3TagRewrite() {
    }

    /**
     * Basic constructor
     * 
     * @param pName
     *            Name of the Tag which must not be null
     */
    public ID3TagRewrite(String pName) {
        this(pName, "");
    }

    /**
     * Regular Constructor for a completely set up Tag
     * 
     * @param pName
     *            Name of the Tag which must not be null
     * @param pValue
     *            Value of the Tag
     */
    public ID3TagRewrite(String pName, String pValue) {
        this("",pName,pValue);
    }

    /**
     * Regular Constructor for a completely set up Tag
     * 
     * @param pName
     *            Name of the Tag which must not be null
     * @param pValue
     *            Value of the Tag
     */
    public ID3TagRewrite(String pId, String pName, String pValue) {
        mId = pId;
        mName = pName;
        mValue = pValue;
    }

    /**
     * @return Tag Name
     */
    public String getName() {
        return mName;
    }

    /** @return Tag Value * */
    public String getValue() {
        return mValue;
    }

    /**
     * @param pValue
     *            Tag Value to be set
     */
    public void setValue(String pValue) {
        mValue = pValue;
    }

    public String toString() {
        return ID3TagRewrite.class.getName() + " [ name: " + mName
                + ", value: " + mValue + " ]";
    }

    public boolean equals(Object pTest) {
        boolean lReturn = false;
        if (pTest instanceof ID3TagRewrite) {
            ID3TagRewrite lOther = (ID3TagRewrite) pTest;
            if (mName != null) {
                lReturn = mName.equals(lOther.mName);
            }
        }
        return lReturn;
    }

    public int hashCode() {
        if (mName == null) {
            return 37;
        } else {
            return mName.hashCode();
        }
    }
    /**
     * @return Returns the mId.
     */
    public String getId() {
        return mId;
    }
    /**
     * @param id The mId to set.
     */
    public void setId(String id) {
        mId = id;
    }
    /**
     * @return Returns the mDescription.
     */
    public String getDescription() {
        return mDescription;
    }
    
    /**
     * @param description The mDescription to set.
     */
    public void setDescription(String description) {
        mDescription = description;
    }
    
    /**
     * @param name The mName to set.
     */
    public void setName(String name) {
        mName = name;
    }
    
    /** 
     * Clone this tag.
     * @see java.lang.Object#clone()
     */
    public Object clone(){
        ID3TagRewrite lTag = new ID3TagRewrite();
        lTag.setDescription(getDescription());
        lTag.setId(getId());
        lTag.setName(getName());
        lTag.setValue(getValue());
        return lTag;
    }
    
    
    /**
     * @return Returns the mValueChanged.
     */
    public boolean isValueChanged() {
        return mValueChanged;
    }
    /**
     * @param valueChanged The mValueChanged to set.
     */
    public void setValueChanged(boolean valueChanged) {
        mValueChanged = valueChanged;
    }
}