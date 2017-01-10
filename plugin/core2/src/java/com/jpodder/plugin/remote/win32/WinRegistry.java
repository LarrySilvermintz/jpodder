package com.jpodder.plugin.remote.win32;
import java.io.IOException;

import org.jawin.COMException;
import org.jawin.donated.win32.Registry;
import org.jawin.donated.win32.RegistryConstants;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */
public class WinRegistry {

    public WinRegistry(){
        // We assume win32. 
            // Create a test key and delete it. 
    		int key = openKey(
    					RegistryConstants.HKEY_LOCAL_MACHINE,
    				"SOFTWARE\\JavaSoft\\Java Runtime Environment");    		
    		System.out.println("Key opened successfully.  Handle is " + key);
    		
    } 

    public static void setKeyStringValue(int handle, String pValueName, String value){
        try {
            Registry.SetKeyValue(handle, pValueName, value, RegistryConstants.REG_SZ);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COMException e) {
            e.printStackTrace();
        }
    }
    
//    public static void setKeyBinaryValue(int handle, String pValueName, byte[] value){
//        try {
//            Registry.SetKeyValue(handle, pValueName, value, RegistryConstants.REG_BINARY);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (COMException e) {
//            e.printStackTrace();
//        }
//    }

    
    
    public static String getValueName(int handle){
        try {
            return Registry.RegEnumValue(handle, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    public static String getKeyValue(int handle, String pKey){
        String value = "";
        try {
            value = Registry.QueryStringValue(handle, pKey);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COMException e) {
            e.printStackTrace();
        }
        return value;
    }
    
    public static int openKey(int handle, String key) {
        try {
            return Registry.OpenKey(handle, key);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COMException e) {
            e.printStackTrace();
        }
        return -1;
	}
    
	public static void deleteKey(int handle, String key) {
	    try {
            System.out.println("Deleting key " + handle + " " + key);
            Registry.DeleteKey(handle, key);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COMException e) {
            e.printStackTrace();
        }
	}
	
	public static void closeKey(int handle) {
	    try {
            System.out.println("Closing handle " + handle);
            Registry.CloseKey(handle);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COMException e) {
            e.printStackTrace();
        }
	}
	

	public static int createKey(int handle, String key) {
		System.out.println("Creating key " + handle + " " + key);
		int result=-1;
        try {
            result = Registry.CreateKey(handle, key);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COMException e) {
            e.printStackTrace();
        }
        System.out.println("Created key, value is " + result);
        return result;
	}
}
