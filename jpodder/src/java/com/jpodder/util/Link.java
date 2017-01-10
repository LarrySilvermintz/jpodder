package com.jpodder.util;

import java.net.URL;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jpodder.JPodderException;

/**
 * Create a Link Object and browse a URL. This class uses reflection
 * to invoke JDIC methods. In our case it will only work if
 * JDic is loaded correctly. 
 * <p>
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.0
 */
public class Link {
    Logger mLog = Logger.getLogger(getClass().getName());
    private static Method lBrowse = null;

    static {
        PersistentObject lLoader = new PersistentObject("jpodder.class.loader");
        Class lObject = (Class) lLoader.invoke("loadClass",
                new Object[] { "org.jdesktop.jdic.desktop.Desktop" });
        try {
            lBrowse = lObject.getMethod("browse", new Class[] { URL.class });
        } catch (NoSuchMethodException nsme) {
        }
    }

    public Link() {
    }

    public static void browse(URL pUrl) throws JPodderException {
        if (lBrowse != null) {
            try {
                lBrowse.invoke(null, new Object[] { pUrl });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }else{
            throw new JPodderException("Native Browsing not supported on this OS");
        }
    }
}