package com.jpodder;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jpodder.util.Logger;

/**
 * This class loader enables jPodder to add and later find
 * native libraries directly rather through the library.path.
 *
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 **/
public class JPodderClassLoader
        extends URLClassLoader {

    private Logger mLog = Logger.getLogger( getClass().getName() );
    private List<File> mLibraries = new ArrayList<File>();

    public JPodderClassLoader(URL[] pURLs, ClassLoader pParent ) {
        super(pURLs, pParent );
    }

    public void addURL(URL aURL) {
        super.addURL(aURL);
        System.out.println("loaded: " + aURL);
    }

    public void addLibrary(File pLibrary) {
        mLog.info("JPodderClassLoader.addLibrary(), file: "
                        + pLibrary);
        mLibraries.add(pLibrary);
    }

    protected String findLibrary(String pLibraryName) {
        mLog.info("JPodderClassLoader.findLibrary(), name: "
                        + pLibraryName);
        Iterator i = mLibraries.iterator();
        while (i.hasNext()) {
            File lLibrary = (File) i.next();
            if (lLibrary != null) {
                if (lLibrary.toString().indexOf(pLibraryName) >= 0) {
                    mLog.info("JPodderClassLoader.findLibrary(), found: "
                            + lLibrary);
                    return lLibrary.getAbsolutePath();
                }
            }
        }
        mLog.info("JPodderClassLoader.findLibrary(), not found");
        return null;
    }
}