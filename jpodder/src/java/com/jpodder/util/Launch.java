package com.jpodder.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.0
 */
public class Launch extends Thread {

    private static Logger sLog = Logger.getLogger(Launch.class.getName());

    InputStream is;

    /**
     * Constructor.
     * <p>
     * 
     * @param is
     *            An inputstream containing a command to execute.
     */
    public Launch(InputStream is) {
        this.is = is;
    }

    /**
     * run method (Thread).
     * <p>
     * Prints the command in the logarea.
     */
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                sLog.info("Executing: " + line);
            }
        } catch (IOException ioe) {
            sLog.warn("Error whule executing" + ioe.getMessage());
        }
    }
}