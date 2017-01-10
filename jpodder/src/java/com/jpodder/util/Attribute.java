package com.jpodder.util;

/**
 * Contains the information about an attribute made
 * available through a field, getter or setter method
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public class Attribute {

     //--- Constants -------------------------------------------------------------

    //--- Attributes ------------------------------------------------------------

    private String mName;
    private String mTypeName;
    private boolean mField;
    private boolean mReadable;
    private boolean mWritable;

    //--- Constructors ----------------------------------------------------------

    /**
     * Creates an new Attribute description
     *
     * @param pName Name of the attribute
     * @param pTypeClassName Name of the class of the attribute's type
     * @param pIsAField True if it is a field access
     * @param pIsReadable If it is not a field then true means there is a Getter method
     * @param pIsWritable If it is not a field then true means there is a Setter method
     **/
    protected Attribute( String pName, String pTypeClassName, boolean pIsAField, boolean pIsReadable, boolean pIsWritable ) {
       mName = pName;
       mTypeName = pTypeClassName;
       mField = pIsAField;
       mReadable = ( mField ? true : pIsReadable );
       mWritable = ( mField ? true : pIsWritable );
    }

    //--- public ----------------------------------------------------------------

    /** @return Attribute Name **/
    public String getName() {
       return mName;
    }

    /** @return Attribute's Type Class Name **/
    public String getType() {
       return mTypeName;
    }

    /** @return True if this is a field **/
    public boolean getIsAField() {
       return mField;
    }

    /** @return True if the attribute is readable **/
    public boolean getIsReadable() {
       return mReadable;
    }

    /** @return True if the attribute is writable **/
    public boolean getIsWritable() {
       return mWritable;
    }

    /** @see Object#toString **/
    public String toString() {
       return "com.jpodder.util.Attribute [ "
          + "name: " + mName
          + ", type: " + mTypeName
          + ", is a field: " + mField
          + ", is readable: " + mReadable
          + ", is writable: " + mWritable
          + " ]";
    }
}