package com.jpodder.remote;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.xmlrpc.MultiCall;
import org.apache.xmlrpc.SystemHandler;
import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcServer;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class RPCLogic {

	private WebServer webServer;

	private XmlRpcServer server;

	private static RPCLogic sSelf;

	private ArrayList<IRPCListener> mListeners = new ArrayList<IRPCListener>();

	public static RPCLogic getInstance() {
		if (sSelf == null) {
			sSelf = new RPCLogic();
		}
		return sSelf;
	}

	public RPCLogic() {
		initialize();
	}

	public void initialize() {

		// XmlRpc.setDebug(true);

		// Server (only)
		server = new XmlRpcServer();
		server.addHandler("jpodder", new Feed());

		InetAddress localhost = null;
		try {
			// localhost will be a random network interface on a
			// multi-homed host.
			localhost = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// fail(e.toString());
		}

		// Setup system handler
		SystemHandler webServerSysHandler = new SystemHandler();
		webServerSysHandler.addSystemHandler("multicall", new MultiCall());

		// WebServer (contains its own XmlRpcServer instance)
		webServer = new WebServer(9876, localhost);
		webServer.addHandler("jpodder", new Feed());
		webServer.addHandler("system", webServerSysHandler);
		webServer.start();
	}
	
	public void fireCall(Object pRPCString){
		Iterator<IRPCListener> lIterator = mListeners.iterator();
		while(lIterator.hasNext()){
			lIterator.next().rpcInvoked(new RPCEvent(pRPCString));
		}
	}
	
	public void addListener(IRPCListener pListener){
		if(!mListeners.contains(pListener)){
			mListeners.add(pListener);
		}
	}
	
	public void removeListener(IRPCListener pListener){
		if(mListeners.contains(pListener)){
			mListeners.remove(pListener);
		}
	}
	
	/**
	 * An XML RPC event.
	 */
	protected class Feed {

		public Feed() {
		}

		public Object add(String pFeed) {
			// 1. Add the feed to the feed
			fireCall(pFeed);
			return "OK";
		}
	}
}