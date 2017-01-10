package com.jpodder.remote.client;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Vector;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

/**
 * This class processes one-click subscriptions. new feed URL's are received as
 * arguments and send to the XML RPC Server which is running on the main jPodder
 * application.
 */
public class Main {

	public static void main(String[] args) {

		System.out.println(System.getProperty("user.dir"));
		String lArgument = "podcast://www.jpodder.com/podcast.rss";

		if (args.length == 0) {
		} else {
			lArgument = args[0];
			// we might want to check how many arguments.
			System.out.println("Adding: " + lArgument);
			// Do we receive a file?
			File lFile = new File(lArgument);
			if (lFile.exists()) {
				try {
					FileReader lReader = new FileReader(lFile);
					LineNumberReader lLine = new LineNumberReader(lReader);
					StringBuffer lStringBuffer = new StringBuffer();
					try {
						String lString;
						while ((lString = lLine.readLine()) != null) {
							lStringBuffer.append(lString);
						}
					} catch (IOException e2) {
						System.out.println(e2.getMessage());
					}
					lArgument = lStringBuffer.toString();
				} catch (FileNotFoundException e1) {
					// We just checked it??
				}
			}
		}

		// 1. process the arguments.
		// try {
		// localhost will be a random network interface on a
		// multi-homed host.
		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
			XmlRpcClient xmlrpc = new XmlRpcClient(localhost.getHostName(),
					9876);
			Vector params = new Vector();
			params.add(lArgument);
			Object result = xmlrpc.execute("jpodder.add", params);
			System.out.println(result);
		} catch (UnknownHostException e) {
		} catch (MalformedURLException e) {
		} catch (XmlRpcException e) {
			System.out.println(e.getMessage());
			
		} catch (IOException e) {
		}
	}
}