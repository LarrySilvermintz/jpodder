package com.jpodder.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * An advanced wrapper object for reflection
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public class DynamicObject {

     //--- Attributes ------------------------------------------------------------

     /** Logging instance */
     private Logger mLog = Logger.getLogger( getClass() );

     /** Target of this dynamic object **/
     private Object mTarget;
     /** Class of the target object **/
     private Class mTargetClass;
     /** Name of the target object's class **/
     private String mTargetClassName;

     //--- Constructors ----------------------------------------------------------


     /**
      * Creates a new target object and sets up the dynamic object
      *
      * @param pClassName Fully qualified class name of the target object that must not be null or empty
      * @param pParameters Array of parameters of the constructor
      *
      * @throws DynamicObjectException If the class or constructor could not be found, the class is abstract,
      *                                the constructor is not accessible or throws an exception
      **/
     public DynamicObject( String pClassName, Object[] pParameters )
           throws DynamicObjectException {
        this( null, pClassName, pParameters );
     }

     /**
      * Creates a new target object and sets up the dynamic object
      *
     * @param pClassLoader Class loader to be used to load the given class and
     *                     if null this class class loader is used
     * @param pClassName Fully qualified class name of the target object that must not be null or empty
     * @param pParameters Array of parameters of the constructor
     *
     * @throws DynamicObjectException If the class or constructor could not be found, the class is abstract,
     *                                the constructor is not accessible or throws an exception
     **/
    public DynamicObject( ClassLoader pClassLoader, String pClassName, Object[] pParameters )
          throws DynamicObjectException {
       this( pClassLoader, pClassName, pParameters, null );
    }

    /**
     * Creates a new target object and sets up the dynamic object
     *
     * @param pClassLoader Class loader to be used to load the given class and
     *                     if null this class class loader is used
     * @param pClassName Fully qualified class name of the target object that must not be null or empty
     * @param pParameters Array of parameters of the constructor
     * @param pSignature Array of classes defining the signature. Mandatory if null values are used
     *                   or ambigious constructor signatures.
     *
     * @throws DynamicObjectException If the class or constructor could not be found, the class is abstract,
     *                                the constructor is not accessible or throws an exception
     **/
    public DynamicObject( ClassLoader pClassLoader, String pClassName, Object[] pParameters, Class[] pSignature )
          throws DynamicObjectException {
       if( pClassName == null || "".equals( pClassName.trim() ) ) {
          throw new DynamicObjectException( DynamicObjectException.CLASS_NAME_UNDEFINED );
       }
       if( pClassLoader == null ) {
          pClassLoader = getClass().getClassLoader();
       }
       try {
          mTargetClass = pClassLoader.loadClass( pClassName );
          mTargetClassName = pClassName;
          Constructor lConstructor = null;
          if( pSignature == null ) {
             lConstructor = getConstructor( mTargetClass, pParameters );
          } else {
             lConstructor = mTargetClass.getConstructor( pSignature );
          }
          pParameters = checkParameters( lConstructor.getParameterTypes(), pParameters );
          mTarget = lConstructor.newInstance( pParameters );
       } catch( NoSuchMethodException nsme ) {
          throw new DynamicObjectException( DynamicObjectException.NO_SUCH_CONSTRUCTOR, new Object[] { pClassName, mTargetClass, pParameters }, null );
       } catch( ClassNotFoundException cnfe ) {
          throw new DynamicObjectException( DynamicObjectException.CLASS_NOT_FOUND, cnfe );
       } catch( InstantiationException ie ) {
          throw new DynamicObjectException( DynamicObjectException.CLASS_IS_ABSTRACT, ie );
       } catch( InvocationTargetException ite ) {
          throw new DynamicObjectException( DynamicObjectException.CONSTRUCTOR_EXCEPTION, ite.getCause() );
       } catch( IllegalAccessException iae ) {
          throw new DynamicObjectException( DynamicObjectException.CONSTRUCTOR_NOT_ACCESSIBLE, iae );
       }
    }

    /**
     * Creates a dynamic object on an existing target object
     *
     * @param pTarget Target Object to use as target
     *
     * @throws DynamicObjectException If target is null
     *
     * @see DynamicObject#setTarget(java.lang.Object)
     **/
    public DynamicObject( Object pTarget )
          throws DynamicObjectException {
       setTarget( pTarget );
    }

    /**
     * In order to be marshalled with XML Encoder we
     * need a no-args constructor
     * <b>Attention:</b> In order to make this object workable you need the call
     *                   {@link #setTarget setTarget(Object)} right afterwards
     **/
    public DynamicObject() {
    }

    //--- Public Interface ------------------------------------------------------

    /**
     * Getter method for atttribute target
     *
     * @return Underlying object of this wrapper
     **/
    public Object getTarget() {
       return mTarget;
    }

    /**
     * Setter method for attribute target
     *
     * @param pValue Underlying object of this wrapper and null is not permitted
     *
     * @throws DynamicObjectException If given object is null
     **/
    public void setTarget( Object pValue )
          throws DynamicObjectException {
       if( pValue == null ) {
          throw new DynamicObjectException( DynamicObjectException.TARGET_UNDEFINED );
       }
       mTarget = pValue;
       mTargetClass = mTarget.getClass();
       mTargetClassName = mTargetClass.getName();
    }

    /**
    * Get Method for a field in contrast to a {@link #getter Getter Method}
    *
    * @param pFieldName Name of the Field to be retrieved
    *
    * @return Object of the targetted field
    *
    * @throws DynamicObjectException If Field is not defined or if it does not exists
    **/
    public Object get( String pFieldName )
          throws DynamicObjectException {
       if( pFieldName == null ) {
          throw new DynamicObjectException( DynamicObjectException.FIELD_NAME_UNDEFINED );
       }
       try {
          Field lField = mTargetClass.getField( pFieldName );
          return lField.get( mTarget );
       } catch( NoSuchFieldException nsfe ) {
          throw new DynamicObjectException( DynamicObjectException.NO_SUCH_FIELD, nsfe );
       } catch( IllegalAccessException iace ) {
          throw new DynamicObjectException( DynamicObjectException.FIELD_NOT_ACCESSIBLE, iace );
       } catch( IllegalArgumentException iae ) {
          //AS NOTE: Should never happen because class is taken from target object
          return null;
       }
    }


    /**
    * Set Method for a field in contrast to a {@link #setter Setter Method}
    *
    * @param pFieldName Name of the Field to be set
    * @param pNewValue New value of the field
    *
    * @param DynamicObjectException If Field is not defined, does not exist or null is given
    *                               but the field hosts a basic data type
    **/
    public void set( String pFieldName, Object pNewValue )
          throws DynamicObjectException {
       if( pFieldName == null ) {
          throw new DynamicObjectException( DynamicObjectException.FIELD_NAME_UNDEFINED );
       }
       try {
          Field lField = mTargetClass.getField( pFieldName );
          lField.set( mTarget, pNewValue );
       } catch( NoSuchFieldException nsfe ) {
          throw new DynamicObjectException( DynamicObjectException.NO_SUCH_FIELD, nsfe );
       } catch( IllegalAccessException iace ) {
          throw new DynamicObjectException( DynamicObjectException.FIELD_NOT_ACCESSIBLE, iace );
       } catch( NullPointerException npe ) {
          throw new DynamicObjectException( DynamicObjectException.NULL_NOT_ALLOWED_BECAUSE_OF_BASIC_DATA_TYPE, npe );
       } catch( IllegalArgumentException iae ) {
          //AS NOTE: Should never happen because class is taken from target object
       }
    }

    /**
     * Gets an Attribute through its Getter Method in contrast to a {@link #get Field Access Method}
     *
     * @param pFieldName Name of the Getter Method <b>without</b> the 'get' prefix. Note that here
     *                   the Attribute name is most likely in upper case
     *
     * @return Object returned by the Getter Method
     *
     * @throws DynamicObjectException If Field Name is not set or method is not found
     **/
    public Object getter( String pFieldName )
          throws DynamicObjectException {
       if( pFieldName == null ) {
          throw new DynamicObjectException( DynamicObjectException.FIELD_NAME_UNDEFINED );
       }
       return invoke( "get" + Character.toUpperCase( pFieldName.charAt( 0 ) ) + pFieldName.substring( 1 ), null );
    }

    /**
     * Sets an Attribute through its Setter Method in contrast to a {@link #set Field Access Method}.
     *
     * This method will guess the signature of the setter method by using the type of the given object
     * and it <b>will use the first matching type</b>. Naturally this guessing will fail if the given
     * object is <b>null</b> and therefore use {@link #setter(String,Object,Class) Extended Setter Method}.
     *
     * This method does also unwrap a Dynamic Object if necessary and use its target object.
     *
     * @param pFieldName Name of the Setter Method <b>without</b> the 'set' prefix. Note that here
     *                   the Attribute name is most likely in upper case
     * @param pNewValue New Value to be set
     *
     * @throws DynamicObjectException If Field Name is not set or method is not found
     **/
    public void setter( String pFieldName, Object pNewValue )
          throws DynamicObjectException {
       if( pFieldName == null ) {
          throw new DynamicObjectException( DynamicObjectException.FIELD_NAME_UNDEFINED );
       }
       invoke(
          "set" + Character.toUpperCase( pFieldName.charAt( 0 ) ) + pFieldName.substring( 1 ),
          new Object[] { pNewValue }
       );
    }

    /**
     * Sets an Attribute through its Setter Method in contrast to a {@link #set Field Access Method}.
     *
     * It will only use the given signature to find the matching method.
     *
     * This method does also unwrap a Dynamic Object if necessary and use its target object.
     *
     * @param pFieldName Name of the Setter Method <b>without</b> the 'set' prefix. Note that here
     *                   the Attribute name is most likely in upper case
     * @param pNewValue New Value to be set
     * @param pSignature Parameter type of the setter method
     *
     * @throws DynamicObjectException If Field Name is not set or method is not found
     **/
    public void setter( String pFieldName, Object pNewValue, Class pSignature )
          throws DynamicObjectException {
       if( pFieldName == null ) {
          throw new DynamicObjectException( DynamicObjectException.FIELD_NAME_UNDEFINED );
       }
       invoke(
          "set" + Character.toUpperCase( pFieldName.charAt( 0 ) ) + pFieldName.substring( 1 ),
          new Object[] { pNewValue },
          new Class[] { pSignature }
       );
    }

    /**
     * Invokes a given method with the given parameters.
     *
     * This method will guess the signature of the method by using the type of the given parameters
     * and it <b>will use the first matching list of types</b>. Naturally this guessing will fail if one of the given
     * object is <b>null</b> and therefore use {@link #invoke(String,Object[],Class[]) Extended Invoke Method}.
     *
     * This method does also unwrap a Dynamic Object if necessary and use its target object.
     *
     * @param pMethodName Name of the method to be invoked
     * @param pParameters Array of parameters of the method to be invoked. Null will be treated as empty paramater
     *                    array
     *
     * @return Object returned by the method
     *
     * @throws DynamicObjectException If method name is undefined, method could not be found or invocation failed
     **/
    public Object invoke( String pMethodName, Object[] pParameters )
          throws DynamicObjectException {
       return invoke( pMethodName, pParameters, null );
    }


    /**
     * Invokes a given method with the given parameters and method signature. This method should be used
     * if any given parameter is null or ambigous signatures are to be expected.
     *
     * This method will only use the given signature.
     *
     * This method does also unwrap a Dynamic Object if necessary and use its target object.
     *
     * @param pMethodName Name of the method to be invoked
     * @param pParameters Array of parameters of the method to be invoked. Null will be treated as empty paramater
     *                    array
     * @param pSignature Signature of the method to be invoked. Null will be treated as not given
     *
     * @return Object returned by the method
     *
     * @throws DynamicObjectException If method name is undefined, method could not be found or invocation failed
     **/
    public Object invoke( String pMethodName, Object[] pParameters, Class[] pSignature )
          throws DynamicObjectException {
       if( pMethodName == null ) {
          throw new DynamicObjectException( DynamicObjectException.METHOD_NAME_UNDEFINED );
       }
       if( pParameters == null ) {
          pParameters = new Object[ 0 ];
       }
       // If signature is given test that the parameter length and signature length match
       if( pSignature != null && pParameters.length != pSignature.length ) {
          throw new DynamicObjectException( DynamicObjectException.METHOD_SIGNATURE_DOES_NOT_MATCH_PARAMETERS );
       }
       try {
          if( Debug.WITH_DEV_DEBUG ) { mLog.devDebug( "invoke(), method name: " + pMethodName + ", signature: "
             + ( pSignature == null ?
                java.util.Arrays.asList( getSignature( pParameters ) ) + "" :
                java.util.Arrays.asList( pSignature ) + ""
                )
          ); }
          Method lMethod = null;
          if( pSignature == null ) {
             lMethod = getMethod( mTargetClass, pMethodName, pParameters );
          } else {
             lMethod = mTargetClass.getMethod( pMethodName, pSignature );
          }
          pParameters = checkParameters( lMethod.getParameterTypes(), pParameters );
          return lMethod.invoke( mTarget, pParameters );
       } catch( IllegalAccessException iace ) {
          throw new DynamicObjectException( DynamicObjectException.FIELD_NOT_ACCESSIBLE, iace );
       } catch( NoSuchMethodException nsme ) {
          throw new DynamicObjectException( DynamicObjectException.NO_SUCH_METHOD, nsme );
       } catch( InvocationTargetException ite ) {
          ite.getCause().printStackTrace();   
          throw new DynamicObjectException( DynamicObjectException.METHOD_EXCEPTION, ite.getCause() );
       }
    }

    /**
     * Cannot overwrite Object#getClass because it is final therefore
     * we have to rename the method
     *
     * @return Class of the target object
     *
     * @see Object@getClass
     **/
    public Class getTargetClass() {
       return mTargetClass;
    }

    /** @see Object#clone **/
    public Object clone()
          throws CloneNotSupportedException {
       // If the target is cloneable then try it
       if( mTarget instanceof Cloneable ) {
          try {
             Method lClone = mTargetClass.getMethod( "clone", null );
             return lClone.invoke( mTarget, null );
          } catch( NoSuchMethodException nsme ) {
             // Will never be thrown because clone() is part of java.lang.Object
             return null;
          } catch( IllegalAccessException iae ) {
             // Will never be thrown because clone() is a protected part of java.lang.Object and this cannot regress
             return null;
          } catch( InvocationTargetException ite ) {
             Throwable lCause = ite.getCause();
             if( lCause instanceof Error) {
                throw (Error) lCause;
             } else if( lCause instanceof RuntimeException ) {
                 throw (RuntimeException) lCause;
             } else {
                 // It only can be an CloneNotSupportedException
                 throw (CloneNotSupportedException) lCause;
             }
          }
       } else {
          // Could not clone so throw the Clone Not Supported Exception
          throw new CloneNotSupportedException(
              Messages.getString( "dynamicobject.does.not.allow.clone" )
          );
       }
    }

    /** @see Object#equals **/
    public boolean equals( Object pTest ) {
       if( pTest instanceof DynamicObject ) {
          return ( (DynamicObject) pTest ).mTarget.equals( mTarget );
       } else {
          return false;
       }
    }

    /** @see Object#hashcode **/
    public int hashcode() {
       return mTarget.hashCode();
    }

    /** @see Object#toString **/
    public String toString() {
       return mTarget.toString();
    }

    //--- Protected Interface ---------------------------------------------------

    //--- Private ---------------------------------------------------------------

    /**
     * Delivers the signature of given parameters
     *
     * @param pParameters Parameters the signature should be found off
     *
     * @return An array of classes matching the given array of parameters.
     *         If the list of parameters is null it will return null.
     **/
    private Class[] getSignature( Object[] pParameters ) {
       Class[] lSignature = null;
       if( pParameters != null ) {
          lSignature = new Class[ pParameters.length ];
          for( int i = 0; i < pParameters.length; i++ ) {
             lSignature[ i ] = pParameters[ i ].getClass();
          }
       }
       return lSignature;
    }

    /**
     * Tries to find a method of the given class, with the given name and
     * able to handle the given list of parameters
     *
     * @param pTarget Target class where the method is looked for
     * @param pMethodName Name of the Method
     * @param pParameters Array of parameters the method must able to handle meaning
     *                    it matches the number of parameter types, the parameters
     *                    are assignable (including a Dynamic Object must be unwrapped)
     *
     * @return Method of the given class with the given name and able to handle
     *         the given parameters.
     *
     * @throws NoSuchMethodException If no such method is found
     **/
    private Method getMethod( Class pTarget, String pMethodName, Object[] pParameters )
          throws NoSuchMethodException {
       Method lReturn = null;
       Class[] lSignature = getSignature( pParameters );
       try {
          lReturn = pTarget.getMethod( pMethodName, lSignature );
       } catch( NoSuchMethodException nsme ) {
          // Try to use basic data types
          // Only try this when we have parameters
          if( lSignature == null || lSignature.length == 0 ) {
             throw nsme;
          }
          Method[] lMethods = pTarget.getMethods();
          Object[] lUnwrappedParameters = null;
          for( int i = 0; i < lMethods.length; i++ ) {
             // Method name do not match -> next method
             if( !lMethods[ i ].getName().equals( pMethodName ) ) {
                continue;
             }
             Class[] lParameterTypes = lMethods[ i ].getParameterTypes();
             if( doParametersMatch( lParameterTypes, lSignature, pParameters ) ) {
                lReturn = lMethods[ i ];
                break;
             }
          }
          if( lReturn == null ) {
             throw nsme;
          }
       }
       return lReturn;
    }


    /**
     * Tries to find a constructor of the given class and able to handle the given
     * list of parameters
     *
     * @param pTarget Target class where the constructor is looked for
     * @param pParameters Array of parameters the constructor must able to handle meaning
     *                    it matches the number of parameter types, the parameters
     *                    are assignable (including a Dynamic Object must be unwrapped)
     *
     * @return Constructor of the given class able to handle the given parameters.
     *
     * @throws NoSuchMethodException If no such constructor is found
     **/
    private Constructor getConstructor( Class pTarget, Object[] pParameters )
          throws NoSuchMethodException {
       Constructor lReturn = null;
       Class[] lSignature = getSignature( pParameters );
       try {
          lReturn = pTarget.getConstructor( lSignature );
       } catch( NoSuchMethodException nsme ) {
          // Try to use basic data types
          // Only try this when we have parameters
          if( lSignature == null || lSignature.length == 0 ) {
             throw nsme;
          }
          Constructor[] lConstructors = pTarget.getConstructors();
          for( int i = 0; i < lConstructors.length; i++ ) {
             Class[] lParameterTypes = lConstructors[ i ].getParameterTypes();
             if( doParametersMatch( lParameterTypes, lSignature, pParameters ) ) {
                lReturn = lConstructors[ i ];
                break;
             }
          }
          if( lReturn == null ) {
             throw nsme;
          }
       }
       return lReturn;
    }

    /**
     * Checks if a given list of parameters does match another list of types
     * and if it is a dynamic object it will also try its underlying object.
     * This method can be used to see if constructor or method signatures
     * do match a given list of types of parameters
     *
     * @param pParameterTypes List of parameter types to check against
     * @param pSignature List of parameter types to check
     * @param pParameters List of parameter objects to see if a object
     *                    of this class can match by its underlying object type
     *
     * @return True if the given list of parameter types are of the same length
     *         and can be assigned from Signature to Parameter Types.
     **/
    private boolean doParametersMatch( Class[] pParameterTypes, Class[] pSignature, Object[] pParameters ) {
       boolean lReturn = false;
       if( pParameterTypes.length == pSignature.length ) {
          lReturn = true;
          for( int j = 0; j < pParameterTypes.length; j++ ) {
             if( Debug.WITH_DEV_DEBUG ) { mLog.devDebug( "getConstructor(), parameter type: " + pParameterTypes[ j ] + ", signature type: " + pSignature[ j ] ); }
             Class lType = null;
             if( Boolean.class.equals( pSignature[ j ] ) ) {
                lType = Boolean.TYPE;
             } else if( Byte.class.equals( pSignature[ j ] ) ) {
                lType = Byte.TYPE;
             } else if( Short.class.equals( pSignature[ j ] ) ) {
                lType = Short.TYPE;
             } else if( Integer.class.equals( pSignature[ j ] ) ) {
                lType = Integer.TYPE;
             } else if( Long.class.equals( pSignature[ j ] ) ) {
                lType = Long.TYPE;
             } else if( Float.class.equals( pSignature[ j ] ) ) {
                lType = Float.TYPE;
             } else if( Double.class.equals( pSignature[ j ] ) ) {
                lType = Double.TYPE;
             } else if( Character.class.equals( pSignature[ j ] ) ) {
                lType = Character.TYPE;
             } else if( DynamicObject.class.equals( pSignature[ j ] ) ) {
                // If a dynamic object is found and the method signature is not a Dynamic Object
                // get the target class of the Dynamic Object and try with that
                if( !DynamicObject.class.equals( pParameterTypes[ j ] ) ) {
                   lType = ( (DynamicObject) pParameters[ j ] ).getTargetClass();
                } else {
                   lType = pSignature[ j ];
                }
             } else {
                lType = pSignature[ j ];
             }
             if( !pParameterTypes[ j ].isAssignableFrom( lType ) ) {
                // Type do not match so exit here
                lReturn = false;
                break;
             }
          }
       }
       return lReturn;
    }

    /**
     * Checks if in a given list is an instance of this class that must be
     * unwrapped to match the given signature.
     * ATTENTION: there is not check here if the unwrapped target object does
     *            match the given signature.
     *
     * @param pSignature Array of parameter types to check against. If signature
     *                   is null or does not have the same number of ojects as the
     *                   parameters the original list of parameters will be returned
     * @param pParameters Array of parameters to check if they contain a Dynamic Object
     *                    and if check if the signature contains a different type
     *
     * @return Array of parameters that if a Dynmic Object is found and the
     *         given signature does not call for such an type the parameter
     *         will be replaced by the target of the Dynamic Object
     **/
    private Object[] checkParameters( Class[] pSignature, Object[] pParameters ) {
       if( pParameters == null ) {
          pParameters = new Object[ 0 ];
       }
       Object[] lReturn = null;
       // If no signature is given or the number of arguments in the signature and parameters do not match just return the given parameters list
       if( pSignature == null || pSignature.length != pParameters.length ) {
          lReturn = pParameters;
       } else {
          lReturn = new Object[ pParameters.length ];
          for( int i = 0; i < pSignature.length; i++ ) {
             if( pParameters[ i ] != null && DynamicObject.class.equals( pParameters[ i ].getClass() ) ) {
                if( !DynamicObject.class.equals( pSignature[ i ] ) ) {
                   // Unwrap because another type is expected
                   lReturn[ i ] = ( (DynamicObject) pParameters[ i ] ).getTarget();
                } else {
                   lReturn[ i ] = pParameters[ i ];
                }
             } else {
                lReturn[ i ] = pParameters[ i ];
             }
          }
       }
       return lReturn;
    }
}