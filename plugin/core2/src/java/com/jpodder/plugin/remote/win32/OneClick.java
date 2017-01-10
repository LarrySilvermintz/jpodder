package com.jpodder.plugin.remote.win32;

import org.jawin.donated.win32.RegistryConstants;


/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */

public class OneClick {

    String MR_CLASSROOT = "rssfile";
    String MR_EXTENSION = ".rss";
    String MR_MIME = "MIME";
    
    //    String MR_SUBKEY = r"%s\shell\open\command" % MR_CLASSROOT;
    //    String MR_COMMAND = '"%s" --add-feed-from-rss "%%1"' % EXE;
    String MR_MIME_TYPE = "application/rss+xml";
    
    // Full working key.
    // java -jar "c:/Program Files/jPodder/bin/jPodder.one.click.jar" %1    
    String MR_ONE_CLICK_APP = "jPodder.one.click.jar";
    
    String PP_PROTOCOL = "podcast";
    //    String PP_SUBKEY = r"%s\shell\open\command" % PP_PROTOCOL;
    //    String PP_COMMAND = '"%s" --add-feed "%%1"' % EXE;
    
    private String mPath;
    private boolean mPathSet = false;
    
    // A String retrieving the current values of the oneclick setting.
    private StringBuffer mParseResult = new StringBuffer();
    
    
    public OneClick(){
    }
    
    public OneClick(String pPath){
        setAppPath(pPath);
    }
    
    public String getParseResult(){
        return mParseResult.toString();
    }
    
    /**
     * The path
     * @param lPath
     */
    public void setAppPath(String pPath) {
        mPath = pPath;
        mPathSet = true;
    }

    public boolean checkRSS() {
        mParseResult.delete(0, mParseResult.length());
        String os = System.getProperty("os.name");

        // win32 OS?
        if (os.startsWith("Windows")) {
            return checkWin32();
        }
        if (os.startsWith("mac")) {
            // association for apple.
            return checkMac();
        }
        if (os.startsWith("linux")) {
            return checkUnix();
        }
        return false;
    }
    
    public void setRSS() {
        String os = System.getProperty("os.name");

        // win32 OS?
        if (os.startsWith("Windows")) {
            enableOneClickWin32();
        }
        if (os.startsWith("mac")) {
            // association for apple.
            // CB TODO Mac method
        }
        if (os.startsWith("linux")) {
            enableOneClickLinux();
        }
    
    }
    
    public void removeRSS() {
        String os = System.getProperty("os.name");
        // win32 OS?
        if (os.startsWith("Windows")) {
            disableOneClickwin32();
        }
        if (os.startsWith("mac")) {
            // association for apple.
            // CB TODO Mac method
        }
        if (os.startsWith("linux")) {
            disableOneClickLinux();
        }
    }
    
    /**
     * Check RSS association with jPodder on a win32 system in the registry.
     * jawin is used.
     * 
     * @return
     */
    public boolean checkWin32() {
        boolean lEntriesFound = false;
        
        int lKey = WinRegistry.openKey(RegistryConstants.HKEY_CLASSES_ROOT,
                MR_CLASSROOT);
        String lValue = WinRegistry.getKeyValue(lKey, MR_CLASSROOT);
        WinRegistry.closeKey(lKey);
        if (lValue.length() > 0) {
            mParseResult.append(MR_CLASSROOT + "=" + lValue + "\n");
            // we have an entry
            lEntriesFound = true;
        }
        lKey = WinRegistry.openKey(RegistryConstants.HKEY_CLASSES_ROOT,
                MR_EXTENSION);
        lValue = WinRegistry.getKeyValue(lKey, MR_EXTENSION);
        if(lValue.length() > 0 ){
            mParseResult.append(MR_EXTENSION + "=" + lValue + "\n");
        }
        return lEntriesFound;
    }

    public boolean checkMac() {
        return false;
    }

    public boolean checkUnix() {
        return false;
    }

    /**
     * Enable one-click subscription on linux.
     */
    public void enableOneClickLinux() {
        // For linux developer, We need some code to add the association above.
        // Access .mailcap & .mime
    }

    /**
     * Disable one-click subscription on linux.
     */
    public void disableOneClickLinux() {
        // For linux developer, We need some code to add the association above.
        // Access .mailcap & .mime
    }

    /**
     * Enable one-click subscription on win32.
     * It creates an association for: 
     * <ul>
     * 		<li>.rss</li>
     * 		<li>application/rss+xml</li>
     * </ul>
     * Note: Should not be called for winME, win95.
     */
    public void enableOneClickWin32() {
        int[] lKeys = new int[15];
        int key1 = WinRegistry.createKey(RegistryConstants.HKEY_CLASSES_ROOT,
                MR_CLASSROOT);
        WinRegistry.setKeyStringValue(key1, "", "jPodder");
        lKeys[0] = key1;
        int key2 = WinRegistry.createKey(key1, "shell");
        lKeys[1] = key2;
        int key3 = WinRegistry.createKey(key2, "open");
        lKeys[2] = key3;
        int key4 = WinRegistry.createKey(key3, "command");
        lKeys[3] = key4;
        // "c:/program files/jpodder/bin/jpodder.jar" %1"
        
        if( mPath == null){
            mPath = "c:/program files/jpodder/bin";
        }
        
        WinRegistry
                .setKeyStringValue(key4, "", "javaw -jar \"" + mPath + "/" + MR_ONE_CLICK_APP + "\" \"%1\"");
        int key5 = WinRegistry.createKey(RegistryConstants.HKEY_CLASSES_ROOT,
                MR_EXTENSION);
        WinRegistry.setKeyStringValue(key5, "", MR_CLASSROOT);
        lKeys[4] = key5;
        WinRegistry.setKeyStringValue(key5, "Content Type", MR_MIME_TYPE);
        //        int key = WinRegistry.openKey(RegistryConstants.HKEY_CLASSES_ROOT,
        //                MR_CLASSROOT);
        
        int key6 = WinRegistry.createKey(RegistryConstants.HKEY_CLASSES_ROOT,PP_PROTOCOL);        
        WinRegistry.setKeyStringValue(key6, "", "URL: Podcast protocol");
        WinRegistry.setKeyStringValue(key6, "URL Protocol", "");
        
        lKeys[5] = key6;
        int key7 = WinRegistry.createKey(key6, "shell");
        lKeys[6] = key7;
        int key8 = WinRegistry.createKey(key7, "open");
        lKeys[7] = key8;
        int key9 = WinRegistry.createKey(key8, "command");
        lKeys[8] = key9;
        WinRegistry
        .setKeyStringValue(key9, "", "javaw -jar \"" + mPath + "/" + MR_ONE_CLICK_APP + "\" \"%1\"");

        int key10 = WinRegistry.openKey(RegistryConstants.HKEY_CLASSES_ROOT , MR_MIME);
        lKeys[9] = key10;
        int key11 = WinRegistry.openKey(key10, "Database");
        lKeys[10] = key11;        
        int key12 = WinRegistry.openKey(key11, "Content Type");        
        lKeys[11] = key12;
        int key13 = WinRegistry.createKey(key12,MR_MIME_TYPE);
        lKeys[12] = key13;
        WinRegistry.setKeyStringValue(key13, "Extension", MR_EXTENSION);

        for (int j = 0; j < 13; j++) {
            WinRegistry.closeKey(lKeys[j]);
        }
    }	

    /**
     * Disable one-click subscription on win32.
     * Note: Should not be called for winME, win95.
     */
    public void disableOneClickwin32() {
        WinRegistry
                .deleteKey(RegistryConstants.HKEY_CLASSES_ROOT, MR_CLASSROOT);
        WinRegistry
                .deleteKey(RegistryConstants.HKEY_CLASSES_ROOT, MR_EXTENSION);
    }
}