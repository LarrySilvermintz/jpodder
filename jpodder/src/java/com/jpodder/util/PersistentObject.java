package com.jpodder.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.jpodder.util.Logger;

/**
 * An Object Reference that does not use an Object Reference
 * but a place holder that can persist over time and can be
 * recreated at any later time or place within the same JVM.
 * <br>
 * <b>ATTTENTION:</b> Unlike in JMX you can use any Object you
 *                    like as an Object Name like String, Integer or
 *                    your own class. But this Object <b>must
 *                    overwrite</b> {@link Object#equals(Object) Object.equals()} so that
 *                    another object can be viewed as equal in order to
 *                    allow to create another reference pointing
 *                    to the same object.
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 **/
public class PersistentObject {

    //--- Static Attributes -----------------------------------------------------

    /** Pool used to store objects with its reference **/
    private static HashMap sObjectPool = new HashMap();
    /** Logging facility **/
    protected Logger mLog = Logger.getLogger( getClass().getName() );

    //--- Static Methods --------------------------------------------------------

    /**
     * Checks if the given Object Name is already used to reference a target
     *
     * @param pObjectName Object Name to test for
     *
     * @return True if this object name is already used
     **/
    public static boolean isRegistered( Object pObjectName ) {
       return sObjectPool.containsKey( pObjectName );
    }

    /**
     * Drop the object behind the given Object Name
     *
     * @param pObjectName Object Name of the object to be dropped. If the object
     *                    cannot be found this is a no-op.
     **/
    public static void drop( Object pObjectName ) {
       sObjectPool.remove( pObjectName );
    }

    /** @return Set of all Object Names of currently registered Objects **/
    public static Set listObjectNames() {
       return sObjectPool.keySet();
    }

    //--- Attributes ------------------------------------------------------------

    /** Object Name Reference **/
    private Object mObjectName;

    //--- Constructors ----------------------------------------------------------

    /**
     * Create a new Persistent Object with its Object Name and
     * the class name that is used to create the target object
     *
     * @param pObjectName Object Name that reference the target
     * @param pClassName Fully qualified class name
     * @param pParameters Array of objects used as parameter for the constructor invocation
     *
     * @throws DynamicObjectException If the creation of the target object failed
     * @throws PersistentObjectException If the object name is null or already in use
     *
     * @see DynamicObject#DynamicObject(String, Object[]) DynamicObject(String, Object[])
     * @see #init(Object, DynamicObject)
     **/
    public PersistentObject( Object pObjectName, String pClassName, Object[] pParameters )
          throws DynamicObjectException,
                 PersistentObjectException {
       init( pObjectName, new DynamicObject( pClassName, pParameters ) );
    }

    /**
     * Create a new Persistent Object with its Object Name and
     * the class name and classs loader that is used to create
     * the target object
     *
     * @param pObjectName Object Name that reference the target
     * @param pClassLoader Class loader used to load the target class
     * @param pClassName Fully qualified class name
     * @param pParameters Array of objects used as parameter for the constructor invocation
     *
     * @throws DynamicObjectException If the creation of the target object failed
     * @throws PersistentObjectException If the object name is null or already in use
     *
     * @see DynamicObject#DynamicObject(ClassLoader, String, Object[]) DynamicObject(ClassLoader, String, Object[])
     * @see #init(Object, DynamicObject)
     **/
    public PersistentObject( Object pObjectName, ClassLoader pClassLoader, String pClassName, Object[] pParameters )
          throws DynamicObjectException,
                 PersistentObjectException {
       init( pObjectName, new DynamicObject( pClassLoader, pClassName, pParameters ) );
    }

    /**
     * Create a new Persistent Object with its Object Name and
     * the class name and classs loader that is used to create
     * the target object
     *
     * @param pObjectName Object Name that reference the target
     * @param pClassLoader Class loader used to load the target class
     * @param pClassName Fully qualified class name
     * @param pParameters Array of objects used as parameter for the constructor invocation
     * @param pSignature Array of classes defining the signature of the constructor. You have to specify it
     *                   when there is an ambigous declaration or null parameter values are provided
     *
     * @throws DynamicObjectException If the creation of the target object failed
     * @throws PersistentObjectException If the object name is null or already in use
     *
     * @see DynamicObject#DynamicObject(ClassLoader, String, Object[], Class[]) DynamicObject(ClassLoader, String, Object[], Class[])
     * @see #init(Object, DynamicObject)
     **/
    public PersistentObject( Object pObjectName, ClassLoader pClassLoader, String pClassName, Object[] pParameters, Class[] pSignature )
          throws DynamicObjectException,
                 PersistentObjectException {
       init( pObjectName, new DynamicObject( pClassLoader, pClassName, pParameters, pSignature ) );
    }

    /**
     * Create a new Persistent Object with its Object Name and
     * the target object itself
     *
     * @param pObjectName Object Name that reference the target
     * @param pTarget Object that is invoked throught its reference
     *
     * @throws DynamicObjectException If the creation of the target object failed
     * @throws PersistentObjectException If the object name is null or already in use
     *
     * @see DynamicObject#DynamicObject(Object)
     * @see #init(Object, DynamicObject)
     **/
    public PersistentObject( Object pObjectName, Object pTarget )
          throws DynamicObjectException,
                 PersistentObjectException {
       init( pObjectName, new DynamicObject( pTarget ) );
    }

    /**
     * Creates a new Persistent Object Reference that does not
     * create an underlying object but merely works as another
     * reference if the Object exists. If unsure please use
     * {@link PersistentObject#isRegistered(java.lang.Object) isRegistered(Object)}
     * to ensure that the object exists or wait until it is
     * created
     *
     * @param pObjectName An object referencing the target and must not be null
     *
     * @throws IllegalArgumentException If given Object Name is null
     * @throws PersistentObjectException If the object name is null
     *
     * @see #init(Object, DynamicObject)
     **/
    public PersistentObject( Object pObjectName )
          throws PersistentObjectException {
       init( pObjectName, null );
    }

    //--- Public Interface ------------------------------------------------------

    /** @see DynamicObject#get( String ) **/
    public Object get( String pFieldName )
          throws DynamicObjectException,
                 PersistentObjectException {
       return getTarget().get( pFieldName );
    }

    /** @see DynamicObject#set( String, Object ) **/
    public void set( String pFieldName, Object pNewValue )
          throws DynamicObjectException,
                 PersistentObjectException {
       getTarget().set( pFieldName, pNewValue );
    }

    /** @see DynamicObject#getter( String ) **/
    public Object getter( String pFieldName )
          throws DynamicObjectException,
                 PersistentObjectException {
       return getTarget().getter( pFieldName );
    }

    /** @see DynamicObject#setter( String, Object ) **/
    public void setter( String pFieldName, Object pNewValue )
          throws DynamicObjectException,
                 PersistentObjectException {
       getTarget().setter( pFieldName, pNewValue );
    }

    /** @see DynamicObject#setter( String, Object, Class ) **/
    public void setter( String pFieldName, Object pNewValue, Class pSignature )
          throws DynamicObjectException,
                 PersistentObjectException {
       getTarget().setter( pFieldName, pNewValue, pSignature );
    }

    /** @see DynamicObject#invoke( String, Object[] ) **/
    public Object invoke( String pMethodName, Object[] pParameters )
          throws DynamicObjectException,
                 PersistentObjectException {
       return getTarget().invoke( pMethodName, pParameters );
    }

    /** @see DynamicObject#invoke( String, Object[], Class[] ) **/
    public Object invoke( String pMethodName, Object[] pParameters, Class[] pSignature )
          throws DynamicObjectException,
                 PersistentObjectException {
       return getTarget().invoke( pMethodName, pParameters, pSignature );
    }

    /**
     * Checks if this object still references an existing Object.
     * <b>Attention:</b> This does not mean that this object is invalid
     *                   but that currently there is no object assigned
     *                   to this reference and therefore any use of this
     *                   reference will fail.
     *
     * @return True if there is an object referenced by this object
     **/
    public boolean isReferencing() {
       return PersistentObject.isRegistered( mObjectName );
    }

    /**
     * Drops the registered object
     **/
    public void drop() {
       PersistentObject.drop( mObjectName );
    }

    /** @see Object#clone **/
    public Object clone()
          throws CloneNotSupportedException {
       if( isReferencing() ) {
           DynamicObject lTarget = (DynamicObject) sObjectPool.get( mObjectName );
           return lTarget.clone();
       } else {
           // Could not clone so throw the Clone Not Supported Exception
           throw new CloneNotSupportedException(
              Messages.getString( "persistentobject.not.found" )
           );
       }
    }

    /** @see Object#equals **/
    public boolean equals( Object pTest ) {
        if( pTest instanceof PersistentObject ) {
            return mObjectName.equals( ((PersistentObject) pTest ) );
       } else {
           return false;
       }
    }

    /** @see Object#hashcode **/
    public int hashcode() {
       return mObjectName.hashCode();
    }

    /** @see Object#toString **/
    public String toString() {
       if( isReferencing() ) {
           Object lTarget = sObjectPool.get( mObjectName );
           return lTarget.toString();
       } else {
           return "Persistent Object [ "
              + "Object name: " + mObjectName
              + " no referecing right now"
              + " ]";
       }
    }

    /** @return Underlying Object Name **/
    public Object getName() {
        return mObjectName;
    }

    /**
     * Delivers the class name of the target
     *
     * @return Class Name of the underlying target
     *
     * @throws PersistentObjectException If not target is available
     **/
    public String getTargetClassName()
          throws PersistentObjectException {
       return getTarget().getTargetClass().getName();
    }

    /**
     * Delivers a particular attribute made publicly available
     *
     * @param pAttributeName Name of the attribute to be found
     *
     * @return Attribute Descriptor if an attribute is found otherwise null
     *
     * @throws PersistentObjectException If not target is available
     **/
    public Attribute getAttribute( String pAttributeName )
          throws PersistentObjectException {
       Class lTarget = getTarget().getTargetClass();
       try {
          Field lField = lTarget.getField( pAttributeName );
          if( Modifier.isPublic( lField.getModifiers() ) ) {
             return new Attribute( pAttributeName, lField.getType().getName(), true, true, true );
          }
       } catch( NoSuchFieldException nsfe ) {
          // Ignore it
       }
       List lMethods = new ArrayList( Arrays.asList( lTarget.getMethods() ) );
       while( true ) {
          if( lMethods.isEmpty() ) {
             break;
          }
          java.lang.reflect.Method lMethod = (java.lang.reflect.Method) lMethods.get( 0 );
          if( Modifier.isPublic( lMethod.getModifiers() ) ) {
             String lName = lMethod.getName();
             if( lName.equals( "get" + pAttributeName ) && lMethod.getParameterTypes().length == 0 ) {
                Class lType = lMethod.getReturnType();
                for( int i = 1; i < lMethods.size(); i++ ) {
                   java.lang.reflect.Method lSetter = (java.lang.reflect.Method) lMethods.get( i );
                   String lSetterName = lSetter.getName();
                   if( lSetterName.equals( "set" + pAttributeName ) ) {
                      // Attribute Name matches
                      if( lSetter.getParameterTypes().length == 1 && lType.equals( lSetter.getParameterTypes()[ 0 ] ) ) {
                         // Types matches -> same attribute
                         return new Attribute( pAttributeName, lType.getName(), false, true, true );
                      }
                   }
                }
                return new Attribute( pAttributeName, lType.getName(), false, true, false );
             } else if( lName.equals( "set" + pAttributeName ) && lMethod.getParameterTypes().length == 1 ) {
                Class lType = lMethod.getParameterTypes()[ 0 ];
                // Check if there is a getter method
                for( int i = 1; i < lMethods.size(); i++ ) {
                   java.lang.reflect.Method lGetter = (java.lang.reflect.Method) lMethods.get( i );
                   String lGetterName = lGetter.getName();
                   if( lGetterName.equals( "get" + pAttributeName ) ) {
                      // Attribute Name matches
                      if( lGetter.getReturnType().equals( lType ) ) {
                         // Types matches -> same attribute
                         return new Attribute( pAttributeName, lType.getName(), false, true, true );
                      }
                   }
                }
                return new Attribute( pAttributeName, lType.getName(), false, false, true );
             } else {
                // Neither a getter or setter method
                lMethods.remove( 0 );
             }
          } else {
                // Not public method
                lMethods.remove( 0 );
          }
       }
       // No attribute found
       return null;
    }

    /**
     * Delivers a list of Attributes made available
     *
     * @return List of Attribute descriptions
     *
     * @throws PersistentObjectException If not target is available
     **/
    public List getAttributes()
          throws PersistentObjectException {
       Class lTarget = getTarget().getTargetClass();
       Field[] lFields = lTarget.getFields();
       List lReturn = new ArrayList();
       for( int i = 0; i < lFields.length; i++ ) {
          if( Modifier.isPublic( lFields[ i ].getModifiers() ) && !Modifier.isFinal( lFields[ i ].getModifiers() ) ) {
             lReturn.add(
                new Attribute( lFields[ i ].getName(), lFields[ i ].getType().getName(), true, true, true )
             );
          }
       }
       List lMethods = new ArrayList(
          Arrays.asList(
             lTarget.getMethods()
          )
       );
       while( true ) {
          if( lMethods.isEmpty() ) {
             break;
          }
          java.lang.reflect.Method lMethod = (java.lang.reflect.Method) lMethods.get( 0 );
          if( Modifier.isPublic( lMethod.getModifiers() ) ) {
             String lName = lMethod.getName();
             if( lName.length() > 3 && lName.startsWith( "get" ) && lMethod.getParameterTypes().length == 0 ) {
                Class lType = lMethod.getReturnType();
                // Check if there is a setter method
                boolean lSetterFound = false;
                for( int i = 1; i < lMethods.size(); i++ ) {
                   java.lang.reflect.Method lSetter = (java.lang.reflect.Method) lMethods.get( i );
                   String lSetterName = lSetter.getName();
                   if( lSetterName.length() > 3 && lSetterName.startsWith( "set" ) && lSetterName.substring( 3 ).equals( lName.substring( 3 ) ) ) {
                      // Attribute Name matches
                      if( lSetter.getParameterTypes().length == 1 && lType.equals( lSetter.getParameterTypes()[ 0 ] ) ) {
                         // Types matches -> same attribute
                         lSetterFound = true;
                         lReturn.add(
                            new Attribute( lName.substring( 3 ), lSetter.getParameterTypes()[ 0 ].getName(), false, true, true )
                         );
                         // Remove the setter from the search list
                         lMethods.remove( i );
                         break;
                      }
                   }
                }
                if( !lSetterFound ) {
                   lReturn.add(
                      new Attribute( lName.substring( 3 ), lType.getName(), false, true, false )
                   );
                }
             } else if( lName.length() > 3 && lName.startsWith( "set" ) && lMethod.getParameterTypes().length == 1 ) {
                Class lType = lMethod.getParameterTypes()[ 0 ];
                // Check if there is a getter method
                boolean lGetterFound = false;
                for( int i = 1; i < lMethods.size(); i++ ) {
                   java.lang.reflect.Method lGetter = (java.lang.reflect.Method) lMethods.get( i );
                   String lGetterName = lGetter.getName();
                   if( lGetterName.length() > 3 && lGetterName.startsWith( "get" ) && lGetterName.substring( 3 ).equals( lName.substring( 3 ) ) ) {
                      // Attribute Name matches
                      if( lGetter.getReturnType().equals( lType ) ) {
                         // Types matches -> same attribute
                         lGetterFound = true;
                         lReturn.add(
                            new Attribute( lName.substring( 3 ), lType.getName(), false, true, true )
                         );
                         // Remove the getter from the search list
                         lMethods.remove( i );
                         break;
                      }
                   }
                }
                if( !lGetterFound ) {
                   lReturn.add(
                      new Attribute( lName.substring( 3 ), lType.getName(), false, false, true )
                   );
                }
             } else {
                // Neither a getter or setter method
             }
          } else {
             // Not public method
          }
          lMethods.remove( 0 );
       }
       return lReturn;
    }

    /**
     * Delivers a method with the given Signature
     *
     * @param pName Name of the Method
     * @param pSignature List of fully qualified class names for method signature
     *
     * @return Method if found otherwise null
     *
     * @throws PersistentObjectException If not target is available
     **/
    public Method getMethod( String pName, List pSignature )
          throws PersistentObjectException {
       Method lReturn = null;
       if( pSignature == null ) {
          pSignature = new ArrayList();
       }
       try {
          // Load appropriate classes
          ClassLoader lLoader = getTarget().getTargetClass().getClassLoader();
          Class[] lSignature = new Class[ pSignature.size() ];
          Iterator i = pSignature.iterator();
          int j = 0;
          while( i.hasNext() ) {
             lSignature[ j++ ] = lLoader.loadClass( i.next() + "" );
          }
          java.lang.reflect.Method lMethod = getTarget().getTargetClass().getMethod( pName, lSignature );
          lReturn = new Method( lMethod.getName(), lMethod.getReturnType().getName(), pSignature );
       } catch( PersistentObjectException poe ) {
          throw poe;
       } catch( Exception e ) {
          // Ignore it and return null
       }
       return lReturn;
    }

    /**
     * Delivers a list of method names publicly available except
     * the Getter and Setter methods
     *
     * @return List of method names publicly available
     *
     * @throws PersistentObjectException If not target is available
     **/
    public List getMethods()
          throws PersistentObjectException {
       java.lang.reflect.Method[] lMethods = getTarget().getTargetClass().getMethods();
       List lReturn = new ArrayList();
       for( int i = 0; i < lMethods.length; i++ ) {
          if( Modifier.isPublic( lMethods[ i ].getModifiers() ) ) {
             String lName = lMethods[ i ].getName();
             Class lReturnType = lMethods[ i ].getReturnType();
             Class[] lParameterTypes = lMethods[ i ].getParameterTypes();
             if( lName.length() > 3 && lName.startsWith( "get" ) && lParameterTypes.length == 0 ) {
                // Ignore it
             } else if( lName.length() > 3 && lName.startsWith( "set" ) && lParameterTypes.length == 1 ) {
                // Ignore it
             } else {
                List lTypes = new ArrayList( lParameterTypes.length );
                for( int j = 0; j < lParameterTypes.length; j++ ) {
                   lTypes.add( lParameterTypes[ j ].getName() );
                }
                lReturn.add(
                   new Method( lName, lReturnType.getName(), lTypes )
                );
             }
          }
       }
       return lReturn;
    }

    //--- Protected -------------------------------------------------------------

    /**
     * Initializes this Persistent Object with
     * the object name and, if provided, the target object
     *
     * @param pObjectName Object that will be used as a reference to the target object
     * @param pTarget If not null this object will be set as target
     *
     * @throws IllegalArgumentException If object name is null or already used with another target
     **/
    protected void init( Object pObjectName, DynamicObject pTarget )
            throws PersistentObjectException {
       if( pObjectName == null ) {
          throw new PersistentObjectException( PersistentObjectException.OBJECT_NAME_UNDEFINED );
       }
       if( pTarget != null ) {
          if( sObjectPool.containsKey( pObjectName ) ) {
             throw new PersistentObjectException( PersistentObjectException.OBJECT_NAME_ALREADY_IN_USE );
          } else {
             sObjectPool.put( pObjectName, pTarget );
          }
       }
       mObjectName = pObjectName;
    }

    /**
     * Looks up a target by the current object name
     *
     * @return Dynamic Object found by the object name
     *
     * @throws PersistentObjectException If the object could not been found
     **/
    protected DynamicObject getTarget()
          throws PersistentObjectException {
       Object lTarget = sObjectPool.get( mObjectName );
       if( lTarget == null) {
           throw new PersistentObjectException( PersistentObjectException.NO_SUCH_TARGET );
       } else {
           return (DynamicObject) lTarget;
       }
    }

    //--- Private ---------------------------------------------------------------
}