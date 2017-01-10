/**

	XML-RPC client for MSIE 5+ on Windows
	@version1.1 kompatibel med Mozilla
	-------------------------------------
	(c) by Olav Junker Kjær <olav@olav.dk>
	
	
	Uses the XMLDOM and the component XMLHTTP which is only available on windows.
	Whith the default security settings in IE, can only communicate
	with the originating server.	
	
	XML-RPC spec: http://www.xml-rpc.com/spec
			
	Issues:
		- does not support base64 (if you need this, mail me!)
		- UTC time is always assumed and used

	
	Data type mappings:
	
	JS type            -> XML-RPC        -> JS type
	-----------------------------------------------
	number		-> float		-> number
	string		-> string		-> string
	boolean		 -> boolean	-> boolean
	Date(), VT_DATE	-> datetime	-> Date()
	null		-> boolean:0 *	-> boolean false
	Array(), [...]	-> array **
	Object(), {...}	-> struct
	function		-> raises an error!
	all other objects ***

	missing return values in xml-rpc-reponse -> null
	
	* has someone a better idea?
	
	** missing values (undefined) in array -> boolean:0 (like null)
	
	*** serialization of custom objects:
	(objects with constructor other than Object)
	
	object.valueOf() is called, and the result is serialized.
	(by default valueOf() returns the object itself)
	if the result is a custom object (such as the object itself)
	it is serialized as a struct, however functions will raise
	an error. Therefore override valueOf to return a serializable
	version of the object.
	
	
	
*/

/**

	rpcExecute
	----------
	function used for single rpcs
	rpcExecute(<ServerURL>, <methodName>, <param>*) -> <returnValue>

	example:
	sum = rpcExecute("/xmlrpcserver.asp", "add", 2, 2)
	
*/

function xmlRpcExecute(serverURL, method) {
	// all additional arguments is passed along to the rpc
	var args = new Array();
	for (var t=2; t < arguments.length; t++) { args[args.length] = arguments[t]; } 
	// execute
	var xmlRpcClient = getXmlRpcClient(serverURL);
	return xmlRpcClient.executeWithArguments(method, args);
}

/**
	XmlRpcClient
	------------
	Use if you need several rpcs
	
	server = new XmlRpcClient("/xmlrpcserver.asp")
	sum = server.execute("add", 2, 2)
	prod = server.execute("multi", 2, 2)
	
	debugging: you can set the 'debug' property on the proxy
	to true, then all steps will be reported by alertboxes.
		
*/

function XmlRpcClient(serverURL) {
	this.serverURL = serverURL;
	this.debug = false;
	this.supportNil = true;
	this.throwRecievedFaults = true;
	// methods
	this.execute = execute;
	this.executeWithArguments = executeWithArguments;
	this.executeOn = executeOn;
	this.sendXMLRequest = sendXMLRequest;
	this.parseResponse = parseResponse;
	this.validateResponse = validateResponse;
	this.validateXML = validateXML;
	this.createMessage = createMessage;
	this.debugMsg = debugMsg;

	function execute(methodName) {
		var args = new Array();
		// all additional arguments is passed along to the rpc
		for (var t=1; t < arguments.length; t++) { args[args.length] = arguments[t]; }
		return this.executeWithArguments(methodName, args);    
	}

	
	function executeWithArguments(methodName, args) { 		
		var url = this.serverURL; 
		return this.executeOn(url, methodName, args) ;                       
	}

	/*
	*/
	function executeOn(url, methodName, args) {
		if (this.debug) { this.debugMsg("target URL", url); }
		var messageDoc = this.createMessage(methodName, args); 
		if (this.debug) { this.debugMsg("message xml", dom_toXML(messageDoc)); }
		// send!
		var responseDoc = this.sendXMLRequest(url, messageDoc); 
		// 
		if (this.debug) { this.debugMsg("response xml", dom_toXML(responseDoc)); }
		var jsResponseValue = this.parseResponse(responseDoc); 
		if (jsResponseValue.constructor==RpcFault) {
			var fault =  jsResponseValue;
			if (this.throwRecievedFaults) {
				throw new Error(fault.faultString);	
			} else {
				var msg = "Error reported from server: " + fault.faultString; 
				alert("error: " + msg); 
			}

		}
		return jsResponseValue; 
	}


	/* 
		sends xml, returns xml
	*/
	function sendXMLRequest(url, bodyXML) {
		var xmlhttp = dom_createXMLHTTP(); 
		xmlhttp.open("POST", url, false); 
		xmlhttp.send(bodyXML); 
		this.validateResponse(xmlhttp); 
		if (document.all) validateXML(xmlhttp); 
		return xmlhttp.responseXML; 
	}

	/*
		parse a response document element
		return a js value or throws an error
	*/
	function parseResponse(responseDoc) {
		var unmarshaller = new Unmarshaller(); 
		var eResponse = responseDoc.documentElement; 
		assert(eResponse.nodeName,'methodResponse'); 
		var eChild = dom_firstChildElement(eResponse); 
		var eValue;
		if (eChild.nodeName=='fault') {			
			eValue = dom_getChildByTagName(eChild, "value");
			var fault = unmarshaller.parseValueElement(eValue); 
			return new RpcFault(fault.faultString);
		}
		assert(eChild.nodeName,'params');
		var eParams = eChild;
		var eParam = dom_getChildByTagName(eParams, "param"); 
		eValue = dom_getChildByTagName(eParam, "value"); 	
		if (eValue) {
			var jsvalue = unmarshaller.parseValueElement(eValue); 
			return jsvalue; 
		} 
	}
	
	this.assert = assert;
	function assert(x, y){
		if (x!=y) { alert("xml error: found: " + x + " expected: " + y); }
	}

	/*		
		return a request document DOM
	*/
	function createMessage(methodName, params) {
		// create message
		if (this.debug) { alert(methodName + " , " + params.length); }
		var doc = dom_createXMLDocument(); 
		var eMessage = doc.createElement("methodCall")	; 
		doc.appendChild(eMessage); 	
		// method name
		appendTextElement(eMessage, "methodName", methodName); 
		// parameters
		var eParams = appendElement(eMessage, "params"); 
		var marshaller = new Marshaller(); 
		for (var t=0; t < params.length; t++) {
			arg = params[t]; 
			var eParam = appendElement(eParams, "param"); 
			var eValue = marshaller.dump(doc, arg); 		
			eParam.appendChild(eValue); 
			eParams.appendChild(eParam); 
		}
		return doc; 
	}

	/*
		check if the HTTP response is successful
	*/
	function validateResponse(xmlhttp) {
		if (xmlhttp.status != 200) { 
			msg = "http error: " + xmlhttp.status + " - " + xmlhttp.statusText; 
			msg += "\n\n" + xmlhttp.responseText; 
			throw new Error(1, msg); 
		}
	}

	function validateXML(xmlhttp) {
		var docResponse = xmlhttp.responseXML; 
		if (!docResponse.xml || (docResponse.parseError && docResponse.parseError.errorCode)) {
			var msg =  docResponse.parseError.reason + " : " +   docResponse.parseError.srcText; 
			msg += "\nIN DOCUMENT:\n" + xmlhttp.responseText; 
			throw new Error(0, msg); 
		}
	}

	/*
		
	*/
	
	function debugMsg(header, msg) {
		alert(header + ":\n" + msg); 
	}


	function RpcFault(faultString) {
		this.faultString = faultString;	
	}
}

/**

	AsynchronousXmlRpcClient
	------------------------
	Extends XmlRpcClient
	Additional method: 
		executeAsync
	Like execute, except takes a callback function or closure
	as the first argument. The callback is called with the returned
	value as the first argument, when the response is recieved.
	
	example:
	
	function recieveSum(sum) {
		alert "this was calculated: " + sum
	}
	
	proxy.executeAsync(recieveSum, "add", 2 ,2)


*/

function AsyncXmlRpcClient(serverURL) {
	this.base = XmlRpcClient; 
	this.base(serverURL); 
	this.executeAsync = executeAsync; 
	this.onAsyncReturn = onAsyncReturn; 	
	this.sendXMLRequestAsync = sendXMLRequestAsync; 

	/*
			
	*/
	function executeAsync(callbackFunction, methodName) {		
		this.callbackFunction = callbackFunction; 
		// arguments
		var args = new Array();	
		for (var t=2; t < arguments.length; t++) { args[args.length] = arguments[t]; }
		// method name
		if (this.methodNamePrefix) { methodName = methodNamePrefix + "." + methodName; }
		// build message
		var messageDoc = this.createMessage(methodName, args);
		if (this.debug) { this.debugMsg("message xml", dom_toXML(messageDoc)); }
		// send
		var url = this.serverURL; 
		this.sendXMLRequestAsync(url, messageDoc); 
	}

	// sender xml, return xml
	function sendXMLRequestAsync(url, bodyXML) {
		var xmlhttp = dom_createXMLHTTP();
		this.xmlhttp = xmlhttp
		xmlhttp.open("POST", url, true);	
		var self = this; // used in closure		
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4){
				xmlhttp.onreadystatechange = function(){}; 
				self.onAsyncReturn(); 
			}					
		}; 		
		xmlhttp.send(bodyXML); 
	}
	
	function onAsyncReturn() {
		this.validateResponse(this.xmlhttp); 
		if (document.all) this.validateXML(this.xmlhttp); 
		var responseDoc = this.xmlhttp.responseXML; 
		if (this.debug) { this.debugMsg("response xml", dom_toXML(responseDoc)); }
		var jsResponseValue = this.parseResponse(responseDoc); 
		// execute callback
		this.callbackFunction(jsResponseValue); 
	}
	
	
}

/**
	Marshaller & Unmarshaller
	--------------------------------------

	The objects Marshaller and Unmarshaller
	is used by xml-rpc to marshal between XML and js-values
	
	They could be used for other purposes, eg. persisting
	data in cookies or such. Two convenience functions for
	this is included:
	xmlrpc_parse(<DOM Document>) -> <jsvalue>
	xmlrpc_dump(<jsvalue>) -> <DOM Document>


*/

function xmlrpc_parse(dom) {
	var u = new Unmarshaller(); 
	return u.parseValueElement(dom.documentElement); 
}

function xmlrpc_dump(jsvalue) {
	var doc = dom_createXMLDocument(); 
	var m = new Marshaller(); 
	eValue = m.dump(doc, jsvalue); 
	doc.appendChild(eValue);
	return doc; 
}

function Unmarshaller() {
	this.parseValueElement = parseValueElement; 
	this.parseSimpleValue = parseSimpleValue; 
	
	function firstElement() {
		
	}

	/*
		parse a jsvalue fra <value><float>47.2</float></value>
	*/
	function parseValueElement(eValue) {
		var eType = dom_firstChildElement(eValue); 	
		var value;
		if (!eType) {
			// ingen type-tag, dvs. string			
			if (eValue.hasChildNodes) {
				var nodeValue = eValue.firstChild.nodeValue;
				value = this.parseSimpleValue("string", nodeValue);
			} else {
				value = "";
			}
		} else {
			var typeName = eType.nodeName;			
			if (typeName=="struct") {
				value = this.parseStruct(eType);
			} else if (typeName=="array") {
				value = this.parseArray(eType);
			} else {
				var strValue;	
				var tValue = eType.firstChild;
				if (tValue) { strValue = tValue.nodeValue; } else { strValue = ""; }
				value = this.parseSimpleValue(typeName, strValue);
			}
		}
		return value;
	}
	
	this.parseArray = parseArray;
	function parseArray(eArray) {
		var jsArray = new Array();
		var eData = dom_getChildByTagName(eArray, "data");
		var nlValue = eData.childNodes;
		for (var t=0;  t< nlValue.length; t++) {
			var eValue = nlValue.item(t);
			if (eValue.nodeType!=1) { continue; }
			var memberValue = this.parseValueElement(eValue);
			jsArray[jsArray.length] = memberValue;
		}		
		return jsArray;
	}
	
	this.parseStruct = parseStruct;
	function parseStruct(eStruct) {
		var name, eMember;
		var jsStruct = new Object();				
		var nlMembers = eStruct.childNodes;		
		for (var t=0;  t< nlMembers.length; t++) {
			eMember = nlMembers.item(t);				
			if (eMember.nodeType!=1) { continue; }
			var eName = dom_getChildByTagName(eMember, "name");				
			name = eName.firstChild.nodeValue;
			var eMemberValue = dom_getChildByTagName(eMember, "value");
			var memberValue = this.parseValueElement(eMemberValue);
			jsStruct[name] = memberValue;
		}
		return jsStruct;
	}

	/*
		converts from a xmlrpc-typename and a string, to a value
		eg ("number", "42") -> 42
	*/
	function parseSimpleValue(typeName, strValue) {	
		var value;
		if (typeName=="nil") {
			value = null;
		} else if (typeName=="string") {
			value = strValue;
		} else if (typeName=="int" || typeName=="i4") {
			value = parseInt(strValue);
		} else if (typeName=="double") {
			value = parseFloat(strValue);
		} else if (typeName=="boolean") {
			value = (strValue == "1");
		} else if (typeName=="dateTime.iso8601") {
			value = parseISO8601Date(strValue);			
		} else if (typeName=="base64") {
			value = new Binary( (new Base64()).decode(strValue) );	
		} else {
			throw new Error(0, "unsupportet type: " + typeName);
		}		
		return value;		
	}
	
	function parseISO8601Date(dateStr) {
		// YYYYMMDDTHH:MM:SS 
		var datePtn = /(\d{4})(\d{2})(\d{2})T(\d{2}):(\d{2}):(\d{2})/i;
		var match = dateStr.match(datePtn);
		var year = match[1];
		var month = parseFloat(match[2]) - 1;
		var day = match[3];
		var hour = match[4], minute = match[5], sec = match[6];
		return new Date(Date.UTC(year, month, day, hour, minute, sec));
	}	
}

function Marshaller() {
	this.dump = dump;
	this.dumpSimpleValue = dumpSimpleValue;
	this.dumpDictionary = dumpDictionary;
	this.dumpArray = dumpArray;

	/*
		writes a jsvalue as <value><double>47.2</double></value>

	*/
	function dump(doc, jsvalue) {
		var eValue = doc.createElement("value");
		if (jsvalue === null || typeof(jsvalue) != "object" || is(jsvalue, Date) || is(jsvalue, Binary)) {
			this.dumpSimpleValue(eValue, jsvalue);
		} else { // typeof == "object"		
			if (is(jsvalue, Array)) {
				this.dumpArray(eValue, jsvalue);
			} else if (is(jsvalue, Object)) {
				this.dumpDictionary(eValue, jsvalue);			
			} else { 
				// custom object
				var intValue = jsvalue.valueOf();
				if (typeof(intValue) != "object") {
					// object er wrapper for simple type
					this.dumpSimpleValue(eValue, intValue);
				} else if (isDictionary(intValue)) {
					this.dumpDictionary(eValue, intValue);
				} else {
					throw new Error(1, "custom objects cannot be serialized");
				}
			}
		}	
		return eValue;		
	}
	
	
	function isDate(obj) {
		return (obj.constructor == Date||obj.constructor.toString()==Date.toString());
	}	
		
	function isArray(obj) {
		return (obj.constructor == Array || String(obj.constructor)==String(Array));
	}
	function isDictionary(obj) {
		return  (obj.constructor == Object || String(obj.constructor)==String(Object));
	}	
	
	function is(obj, constructor) {
		return (typeof(obj) == "object" && (obj.constructor==constructor || String(obj.constructor)==String(constructor)));
	}	
	
	function dumpDictionary(eValue, jsObject) {
		var eStruct = appendElement(eValue, "struct");
		for (key in jsObject) {
			var eMember = appendElement(eStruct, "member");
			appendTextElement(eMember, "name", key);
			var eItem = this.dump(eMember.ownerDocument, jsObject[key]);
			eMember.appendChild(eItem);
		}
	}	
	
	function dumpArray(eValue, jsArray) {
		var eArray = appendElement(eValue, "array");
		var eData = appendElement(eArray, "data");
		for (var t=0; t<jsArray.length; t++) {			
			var eItem = this.dump(eData.ownerDocument, jsArray[t]);
			eData.appendChild(eItem);
		}
	}		
		
	function dumpSimpleValue(eValue, jsvalue) {	
		var typeName, strValue;
		if (jsvalue === null || typeof(jsvalue) == "null" || typeof(jsvalue) == "undefined") {
			if (this.supportNil) { typeName = "nil"; } else { typeName = "string";  }
			strValue = "";
		} else if (typeof jsvalue == "boolean") {
			typeName = "boolean";
			strValue = jsvalue ? "1" : "0";
		} else if (typeof jsvalue == "number") {
			typeName = "double";
			if (!isFinite(jsvalue)) { throw new Error(1, "unsupported number: "  + jsvalue); }
			strValue = String(jsvalue);
		} else if (typeof jsvalue == "string") {
			typeName = "string";
			strValue = jsvalue;
		} else if (typeof jsvalue == "date") {
			typeName = "dateTime.iso8601";
			strValue = packISO8601Date(new Date(jsvalue));
		} else if (is(jsvalue, Date)) {
			typeName = "dateTime.iso8601";
			strValue = packISO8601Date(jsvalue);
		} else if (is(jsvalue, Binary)) {
			typeName = "base64";
			strValue = (new Base64()).encode(jsvalue.toString());
		} else if (typeof jsvalue == "function") {
			throw new Error(1, "functions can not be serialized!");
		} else {
			throw new Error(1, "Unknown type: " + typeof jsvalue);
		}
		appendTextElement(eValue, typeName, strValue);
	}
	
	function packISO8601Date(date) {
		// YYYYMMDDTHH:MM:SS 
		return padYear(date.getUTCFullYear()) + 
			pad(date.getUTCMonth() + 1) +
			pad(date.getUTCDate()) +
			"T" +
			pad(date.getUTCHours()) + ":" +
			pad(date.getUTCMinutes()) + ":" +
			pad(date.getUTCSeconds());		
	}	
	function pad(number, length) {
		var strNumber = String(number);
		if (strNumber.length==1) { strNumber = "0" + strNumber; }
		return strNumber;
	}	
	function padYear(year) {
		if (year>9999 || year<0) { throw new Error(0, "unsupported year: " + year); }
		var strYear = String(year);
		while (strYear.length < 4) { strYear = "0" + strYear; }
		return strYear;
	}		
}

// Base64: http://www.fourmilab.ch/webtools/base64/rfc1341.html

function Binary(str) {
	this.data = str;
	this.toString = function() { return this.data }
}

function Base64() {
	this.maxLineLength = 76;

	this.base64chars =  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	
	this.decode = function(encStr) {
		var base64charToInt = {};
		for (var i = 0; i < 64; i++) base64charToInt[this.base64chars.substr(i,1)] = i;
	
		encStr = encStr.replace(/\s+/g, "");
		var charCodes = [];			
		for (var i = 0; i < encStr.length; i++) {
			charCodes[i] = base64charToInt[encStr.charAt(i)];
		}
		var decStr = "";
		var linelen = 0
		for (var i = 0; i < encStr.length; i += 4) {
			var bits24  = ( charCodes[i]     & 0xFF  ) <<  18; 
			bits24 |= ( charCodes[i + 1] & 0xFF  ) <<  12; 
			bits24 |= ( charCodes[i + 2] & 0xFF  ) <<   6;
			bits24 |= ( charCodes[i + 3] & 0xFF  ) <<   0;
			decStr += String.fromCharCode((bits24 & 0xFF0000) >> 16);
			if (encStr.charAt(i + 2) != '=')  // check for padding character = 
				decStr += String.fromCharCode((bits24 &   0xFF00) >>  8);
			if (encStr.charAt(i + 3) != '=')  // check for padding character =
				decStr += String.fromCharCode((bits24 &     0xFF) >>  0);			
	  	}
		return decStr;
	}
	
	this.encode = function(decStr){
		var bits, dual, i = 0, encOut = "";
		var linelen = 0;
		while(decStr.length >= i + 3){
			bits =	(decStr.charCodeAt(i++) & 0xff) <<16 |
				(decStr.charCodeAt(i++) & 0xff) <<8  |
				decStr.charCodeAt(i++) & 0xff;
			encOut +=
		 		this.base64chars.charAt((bits & 0x00fc0000) >>18) +
				this.base64chars.charAt((bits & 0x0003f000) >>12) +
				this.base64chars.charAt((bits & 0x00000fc0) >> 6) +
				this.base64chars.charAt((bits & 0x0000003f));
			linelen += 4;
			if (linelen>this.maxLineLength-3) {
				encOut += "\n";
				linelen = 0;
			}
		}
		if(decStr.length -i > 0 && decStr.length -i < 3) {
			dual = Boolean(decStr.length -i -1);
			bits =
				((decStr.charCodeAt(i++) & 0xff) <<16) |
				(dual ? (decStr.charCodeAt(i) & 0xff) <<8 : 0);
			encOut +=
				this.base64chars.charAt((bits & 0x00fc0000) >>18) +
				this.base64chars.charAt((bits & 0x0003f000) >>12) +
	      			(dual ? this.base64chars.charAt((bits & 0x00000fc0) >>6) : '=') +
	      			'=';
		}
		return encOut;
	}
}


/*
	DOM utils
	used by Marshaller and directly by XMLProxy
*/

// tilføjer et element med det angivne tagnavn, og returnerer det
function appendElement(element, tagname) {
	var eTag = element.ownerDocument.createElement(tagname);
	element.appendChild(eTag);
	return eTag;
}

// tilføjer et tekstelment med det angivne tagnavn og tekst
function appendTextElement(element, tagname, text) {
	var eTag = createTextElement(element.ownerDocument, tagname, text);
	element.appendChild(eTag);
}

function createTextElement(doc, tagname, text) {
	var eTag = doc.createElement(tagname);	
	var tValue = doc.createTextNode(text);
	eTag.appendChild(tValue);
	return eTag;
}

function dom_firstChildElement(node) {
	var children = node.childNodes;
	for (var t=0; t < children.length; t++) {
		if (children[t].nodeType==1) { return children[t]; }
	}
}

function dom_getChildByTagName(node, name) {
	var children = node.childNodes;
	for (var t=0; t < children.length; t++) {
		if (children[t].nodeName==name) { return children[t]; }
	}
}

function dom_toXML(node) {	
	if (document.all) { return node.xml; }
	return (new XMLSerializer()).serializeToString(node);
}

function dom_createXMLDocument() {
	if (document.all) { return new ActiveXObject("Microsoft.XMLDOM"); }
	return document.implementation.createDocument("", "", null);
}

function dom_createXMLHTTP() {
	if (document.all) { return new ActiveXObject("Microsoft.XMLHTTP"); }
	return new XMLHttpRequest(); 
}