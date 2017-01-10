package com.jpodder.directory;

import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.AsyncCallback;
import org.apache.xmlrpc.XmlRpcClient;

import com.jpodder.JPodderException;

public class XMLRPC {

    protected static String serviceKey = "4df743ea3a39814d4bfe18b7299f7240";

    protected static String serverURL = "http://directory.iPodderX.com/iPXapi";

    private static final String IPX_SEARCH = "iPX.searchFeeds";

    private static final String IPX_FEED_CATEGORIES = "iPX.feedCategories";

    private static final String IPX_FEEDS_BY_CATEGORY = "iPX.feedsByCategory";

    private static final String IPX_SUB_ADD = "iPX.subAdd";

    private static final String IPX_SUB_DELETE = "iPX.subDelete";

    public static final String IPX_KEY_DATE_ADDED = "dateAdded";

    public static final String IPX_KEY_FEED_URL = "feed_url";

    public static final String IPX_KEY_FEED_ID = "feed_id";

    public static final String IPX_KEY_DESCRIPTION = "description";

    public static final String IPX_KEY_SCORE = "score";

    public static final String IPX_KEY_SITE_URL = "site_url";

    public static final String IPX_KEY_NAME = "name";

    private static Logger sLog = Logger.getLogger(XMLRPC.class.getName());

    public XMLRPC() {
    }

    public static Vector subscriptionAdd(String subURL) throws Exception {
        try {
            XmlRpcClient xmlrpc = new XmlRpcClient(serverURL);
            Vector params = new Vector();
            params.addElement(serviceKey);
            params.addElement(subURL);
            Object result = xmlrpc.execute(IPX_SUB_ADD, params);
            if (result instanceof Vector) {
                return (Vector) result;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new JPodderException(e.getMessage());
        }

        //    finally {
        return null;
        //    }
    }

    public static Vector subscriptionDelete(String subURL) throws Exception {
        try {

            XmlRpcClient xmlrpc = new XmlRpcClient(serverURL);
            Vector params = new Vector();
            params.addElement(serviceKey);
            params.addElement(subURL);
            Object result = xmlrpc.execute(IPX_SUB_DELETE, params);
            if (result instanceof Vector) {
                return (Vector) result;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new JPodderException(e.getMessage());
        }
        //    finally {
        return null;
        //    }
    }

    public static Vector getFeedsByCategory(String category) throws Exception {
        try {

            XmlRpcClient xmlrpc = new XmlRpcClient(serverURL);
            Vector params = new Vector();
            params.addElement(serviceKey);
            params.addElement(category);
            Object result = xmlrpc.execute(IPX_FEEDS_BY_CATEGORY, params);
            if (result instanceof Vector) {
                return (Vector) result;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new JPodderException(e.getMessage());
        }
        //    finally {
        return null;
        //    }
    }

    public static Vector getFeedCategories() throws JPodderException {
        try {

            XmlRpcClient xmlrpc = new XmlRpcClient(serverURL);
            Vector params = new Vector();
            params.addElement(serviceKey);
            Object result = xmlrpc.execute(IPX_FEED_CATEGORIES, params);
            if (result instanceof Vector) {
                return (Vector) result;
            }else{
                throw new Exception();
            }
        } catch (Exception e) {
            throw new JPodderException(e.getMessage());
        }
    }

    public static Vector searchFeeds(final String searchString) throws Exception {
        Object result = null;
        // this method returns a string
        try {
            XmlRpcClient xmlrpc = new XmlRpcClient(serverURL);
            Vector params = new Vector();
            params.addElement(serviceKey);
            params.addElement(searchString);
            result = xmlrpc.execute(IPX_SEARCH, params);
        } catch (Exception e) {
            throw new JPodderException(e.getMessage());
        }

        if (result instanceof Vector) {
            return (Vector) result;
        }
        return null;
    }
    
    public static void searchFeeds(String searchString, AsyncCallback pCallBack) {
        try {
            XmlRpcClient xmlrpc = new XmlRpcClient(serverURL);
            Vector params = new Vector();
            params.addElement(serviceKey);
            params.addElement(searchString);
            xmlrpc.executeAsync(IPX_SEARCH, params, pCallBack);
        } catch (MalformedURLException mue) {
            sLog.warn("Programmatic error", mue);
        }
    }

    
    public void printResult(Object result) {
        if (result instanceof Vector) {
            Iterator it = ((Vector) result).iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (o instanceof Hashtable) {
                    this.printResult((Hashtable) o);
                }
            }
        }
    }

    private void printResult(Hashtable ht) {
        //    Iterator it = ht.values().iterator();
        Enumeration keys = ht.keys();
        while (keys.hasMoreElements()) {
            Object o = keys.nextElement();
            sLog.debug( o + "" );
            Object value = ht.get(o);
            if (value instanceof Hashtable) {
                printResult((Hashtable) o);
            } else {
                if (value instanceof String) {
                    sLog.debug( value + "" );
                }
            }
        }

        //    while(it.hasNext()){
        //      Object o = it.next();
        //      if(o instanceof Hashtable){
        //        printResult((Hashtable)o);
        //      }else{
        //        if(o instanceof String){
        //          sLog.debug("[Directory]", (String)o + "\n");
        //        }
        //      }
        //    }
    }

    public static class subscribe implements Runnable {

        protected boolean on;

        protected String url;

        public subscribe(boolean on, String url) {
            this.on = on;
            this.url = url;
            Thread s = new Thread(this);
            s.start();
        }

        public void run() {
            // Subscribe/unsubsribe to iPodderX directory.
            try {
                if (on) {
                    subscriptionAdd(url);
                } else {
                    subscriptionDelete(url);
                }
            } catch (Exception ie) {
                sLog.error( "...Error subscribing to:" + url, ie );
            }
        }
    }
}