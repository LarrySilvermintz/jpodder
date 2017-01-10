/*
 * XML Type:  tRegistry
 * Namespace: 
 * Java type: com.jpodder.registry.TRegistry
 *
 * Automatically generated - do not modify.
 */
package com.jpodder.registry.impl;
/**
 * An XML tRegistry(@).
 *
 * This is a complex type.
 */
public class TRegistryImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.jpodder.registry.TRegistry
{
    
    public TRegistryImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PROPERTY$0 = 
        new javax.xml.namespace.QName("", "property");
    
    
    /**
     * Gets array of all "property" elements
     */
    public com.jpodder.registry.TProperty[] getPropertyArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(PROPERTY$0, targetList);
            com.jpodder.registry.TProperty[] result = new com.jpodder.registry.TProperty[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "property" element
     */
    public com.jpodder.registry.TProperty getPropertyArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.jpodder.registry.TProperty target = null;
            target = (com.jpodder.registry.TProperty)get_store().find_element_user(PROPERTY$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "property" element
     */
    public int sizeOfPropertyArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PROPERTY$0);
        }
    }
    
    /**
     * Sets array of all "property" element
     */
    public void setPropertyArray(com.jpodder.registry.TProperty[] propertyArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(propertyArray, PROPERTY$0);
        }
    }
    
    /**
     * Sets ith "property" element
     */
    public void setPropertyArray(int i, com.jpodder.registry.TProperty property)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.jpodder.registry.TProperty target = null;
            target = (com.jpodder.registry.TProperty)get_store().find_element_user(PROPERTY$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(property);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "property" element
     */
    public com.jpodder.registry.TProperty insertNewProperty(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.jpodder.registry.TProperty target = null;
            target = (com.jpodder.registry.TProperty)get_store().insert_element_user(PROPERTY$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "property" element
     */
    public com.jpodder.registry.TProperty addNewProperty()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.jpodder.registry.TProperty target = null;
            target = (com.jpodder.registry.TProperty)get_store().add_element_user(PROPERTY$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "property" element
     */
    public void removeProperty(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PROPERTY$0, i);
        }
    }
}
