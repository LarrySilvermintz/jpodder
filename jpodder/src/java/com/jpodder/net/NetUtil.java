package com.jpodder.net;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * implements basic search function.
 */
public class NetUtil {

    private static Logger sLog = Logger.getLogger(NetUtil.class.getName());

    private static NetUtil sSelf;
    
    public static NetUtil getInstance(){
        if( sSelf == null){
            sSelf = new NetUtil();
        }
        return sSelf;
    }
    /**
     * Constructor.
     */
    public NetUtil() {
    }


    /**
     * Search method. Posts a search to an OPML/RSS directory.
     * 
     * @param query
     *            String
     * @param server
     *            URL
     * @return Reader the HTTP GET method response in a Reader.
     */
    public Reader search(String query, URL server) {

        Reader result = null;

        HttpClient hClient = new HttpClient();
        HostConfiguration conf = hClient.getHostConfiguration();

        hClient.setHostConfiguration(NetPropertiesHandler
                .setProxySetttings(conf));
        GetMethod gMethod = new GetMethod(server.toString());

        NameValuePair[] post = { new NameValuePair("search", query) };

        gMethod.setQueryString(post);
        try {
            //      hClient.executeMethod(pMethod);
            hClient.executeMethod(gMethod);
            if (gMethod.getStatusCode() == HttpStatus.SC_OK) {
                result = new InputStreamReader(gMethod
                        .getResponseBodyAsStream());

            } else {
                sLog.error("Unexpected failure: "
                        + gMethod.getStatusLine().toString());
            }
            gMethod.releaseConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
    
    /**
     * Simple non-Threaded FTP session. This will freeze up the GUI.
     * 
     * @param server
     * @param workingDir
     * @param file
     * @param type
     * @param username
     * @param password
     * @return boolean
     */
    public static boolean ftpFile(String server, String workingDir, File file,
            int type, String username, String password) {

        FTPClient ftp = new FTPClient();
        String status;
        boolean error = false;
        try {
            int reply;
            ftp.connect(server);
            ftp.login(username, password);
            sLog.info( "Connected to " + server );
            sLog.info( "FTP reply string: " + ftp.getReplyString() );

            // After connection attempt, you should check the reply code to
            // verify
            // success.
            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                sLog.warn( "FTP server refused connection" );
                error = true;
                return error;
            } else {
                if (ftp.changeWorkingDirectory(workingDir)) {
                    ftp.setFileType(type);
                    if (ftp
                            .storeFile(file.getName(),
                                    new FileInputStream(file))) {
                    } else {
                        status = ftp.getStatus();
                    }
                } else {
                    status = ftp.getStatus();
                }
            }
            ftp.logout();
        } catch (IOException e) {
            error = true;
            sLog.error( "FTP IO failure", e );
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                    sLog.info(ftp.getReplyString());
                    
                } catch (IOException ioe) {
                    // do nothing
                }
            }
        }
        return error;
    }
}