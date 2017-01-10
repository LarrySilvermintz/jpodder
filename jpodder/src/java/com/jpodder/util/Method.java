/* **************************************************
 *                                                  *
 *  JDoppio: The next generation Application Server *
 *                                                  *
 *  Distributable under LGPL license.               *
 *  See terms of license at gnu.org.                *
 *                                                  *
 ****************************************************/
package com.jpodder.util;

import java.util.List;

/**
 * Contains the information about an method made publicly
 * available
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public class Method {

    //--- Constants -------------------------------------------------------------

    //--- Attributes ------------------------------------------------------------

    private String mName;
    private String mReturnTypeName;
    private List mParameterTypeNames;

    //--- Constructors ----------------------------------------------------------

    /**
     * Creates an new Method description
     *
     * @param pName Name of the method
     * @param pReturnTypeClassName Name of the class of the method's return type
     * @param pParameterTypeClassNames List of Parameters' Type Class Names
     **/
    protected Method( String pName, String pReturnTypeClassName, List pParameterTypeClassNames ) {
       mName = pName;
       mReturnTypeName = pReturnTypeClassName;
       mParameterTypeNames = pParameterTypeClassNames;
    }

    //--- public ----------------------------------------------------------------

    /** @return Method Name **/
    public String getName() {
       return mName;
    }

    /** @return Return Type Class Name **/
    public String getReturnTypeName() {
       return mReturnTypeName;
    }

    /** @return List of Parameter Type Class Names **/
    public List getParameterTypeNames() {
       return mParameterTypeNames;
    }

    /** @see Object#toString **/
    public String toString() {
       return getClass().getName() + " [ "
          + "name: " + mName
          + ", return type: " + mReturnTypeName
          + ", parameter types: " + mParameterTypeNames
          + " ]";
    }
}