/*
 * An XML document type.
 * Localname: registry
 * Namespace: 
 * Java type: com.jpodder.registry.RegistryDocument
 *
 * Automatically generated - do not modify.
 */
package com.jpodder.registry.impl;
/**
 * A document containing one registry(@) element.
 *
 * This is a complex type.
 */
public class RegistryDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.jpodder.registry.RegistryDocument
{
    
    public RegistryDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REGISTRY$0 = 
        new javax.xml.namespace.QName("", "registry");
    
    
    /**
     * Gets the "registry" element
     */
    public com.jpodder.registry.TRegistry getRegistry()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.jpodder.registry.TRegistry target = null;
            target = (com.jpodder.registry.TRegistry)get_store().find_element_user(REGISTRY$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "registry" element
     */
    public void setRegistry(com.jpodder.registry.TRegistry registry)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.jpodder.registry.TRegistry target = null;
            target = (com.jpodder.registry.TRegistry)get_store().find_element_user(REGISTRY$0, 0);
            if (target == null)
            {
                target = (com.jpodder.registry.TRegistry)get_store().add_element_user(REGISTRY$0);
            }
            target.set(registry);
        }
    }
    
    /**
     * Appends and returns a new empty "registry" element
     */
    public com.jpodder.registry.TRegistry addNewRegistry()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.jpodder.registry.TRegistry target = null;
            target = (com.jpodder.registry.TRegistry)get_store().add_element_user(REGISTRY$0);
            return target;
        }
    }
}
