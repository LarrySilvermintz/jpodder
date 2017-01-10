package com.jpodder.ui.swt.directory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.EscapeStrategy;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.XSLTransformer;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.jpodder.FileHandler;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class InstructionParser implements ErrorHandler, EscapeStrategy {

	Logger mLog = Logger.getLogger(getClass().getName());

	public static String XSL_PATH = FileHandler.sBinDirectory.getAbsolutePath()
			+ File.separator + "directory.xsl";

	public InstructionParser() {
	}

	protected String mParseOut;

	public String getOutput() {
		return mParseOut;
	}

	public static String getTestString(int lCount) {
		IXMLElement lMethodResponse = new XMLElement("methodResponse");
		IXMLElement lParams = new XMLElement("params");
		IXMLElement lParam = new XMLElement("param");
		IXMLElement lValue = new XMLElement("value");
		IXMLElement lArray = new XMLElement("array");
		IXMLElement lData = new XMLElement("data");

		for (int i = 0; i < lCount; i++) {
			IXMLElement lValue2 = new XMLElement("value");
			IXMLElement lStruct = new XMLElement("struct");
			IXMLElement lMember = new XMLElement("member");

			IXMLElement lName = new XMLElement("name");
			lName.setContent("name");
			IXMLElement lValue3 = new XMLElement("value");
			IXMLElement lString = new XMLElement("string");
			lString.setContent("content" + i);

			lValue3.addChild(lString);

			lMember.addChild(lValue3);
			lMember.addChild(lName);
			lStruct.addChild(lMember);
			lValue2.addChild(lStruct);
			lData.addChild(lValue2);
		}

		lArray.addChild(lData);
		lValue.addChild(lArray);
		lParam.addChild(lValue);
		lParams.addChild(lParam);
		lMethodResponse.addChild(lParams);

		StringWriter lStringWriter = new StringWriter();
		try {
			XMLWriter lWriter = new XMLWriter(lStringWriter);
			lWriter.write(lMethodResponse);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return lStringWriter.toString();
	}

	public boolean parse(String pInstruction) {

		if (pInstruction.length() <= 0) {
			return false;
		}

		StringBufferInputStream lInputStream = new StringBufferInputStream(
				pInstruction);
		InputStreamReader lReader;
		try {
			// String lEncoding = "ISO-8859-1";
			lReader = new InputStreamReader(lInputStream, "ISO-8859-1");
			SAXBuilder builder = new SAXBuilder();
			builder.setErrorHandler(this);
			String lEncoding = "UTF-16";

			Document doc = builder.build(lReader);
			File lStylesheet = new File(XSL_PATH);
			if (lStylesheet.exists()) {
				XSLTransformer transformer = new XSLTransformer(lStylesheet);
				Document doc2 = transformer.transform(doc);
				Format lFormat = Format.getRawFormat();
				lFormat.setOmitDeclaration(true);
				lFormat.setOmitEncoding(true);
				lFormat.setEncoding(lEncoding);
				lFormat.setTextMode(Format.TextMode.NORMALIZE);
				lFormat.setEscapeStrategy(this);
				// lFormat.setLineSeparator();
				XMLOutputter outp = new XMLOutputter(lFormat);

				ByteArrayOutputStream lStream = new ByteArrayOutputStream();
				OutputStreamWriter lStreamWriter = new OutputStreamWriter(
						lStream, "UTF-16");
				outp.output(doc2, lStreamWriter);
				mParseOut = lStream.toString("UTF-16");
				lStream.close();
				lStreamWriter.close();

				if (mLog.isDebugEnabled()) {
					DataOutputStream dos = new DataOutputStream(
							new FileOutputStream(File.createTempFile("_test",
									".xml")));
					OutputStreamWriter osw = new OutputStreamWriter(dos,
							"UTF-16");
					osw.write(mParseOut, 0, mParseOut.length());
					osw.close();
					dos.close();
				}

				return true;
			} else {
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			mLog.warn("Encoding error: " + e.getMessage());
		} catch (JDOMException e) {
			// It's not XML, but that's OK.
			// mLog.warn("JDOM error: " + e.getMessage());
		} catch (IOException e) {
			mLog.warn("IO Error:" + e.getMessage());
		}
		return false;
	}

	public void error(SAXParseException arg0) throws SAXException {
		mLog.warn(arg0.getMessage());
	}

	public void fatalError(SAXParseException arg0) throws SAXException {
		mLog.warn(arg0.getMessage());
	}

	public void warning(SAXParseException arg0) throws SAXException {
		mLog.warn(arg0.getMessage());
	}

	public boolean shouldEscape(char arg0) {
		if (arg0 == '\'') {
			return true;
		}
		return false;
	}

}