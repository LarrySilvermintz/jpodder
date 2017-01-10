//browser.isMSIE && browser.version < 5.5 ||
//Mozilla/Netscape 6+,
//browser.isOpera && browser.version < 7 ||
//browser.isSafari && browser.version < 1.2 ||
//browser.isKonqueror && browser.version < 3.3 ||
//browser.isOmniWeb && browser.version < 5.1

var httpReq = null;
var callback;
var shouldDebug = false;

function scriptloaded(){
	alert("xmlrpc-loaded");
}

function ajaxRPC(url, handler, body) {
	
	debug("ajaxRPC called");
    var failure = false;
	callback = handler;
    if (typeof document.body.innerHTML == "undefined") {
        /* Cannot do RPC so show alternate page */
        failure = true;
    }
    else {
		failure = xmlHttp("POST", url, handler, body);
    }
    debug("Request failed?" + failure);
    return failure;
}


function xmlHttp(method, url, handler, body) {
    	    
    if (!httpReq) {
    	httpReq = getHTTPRequest();
    }
	debug("HTTP: " + method + " to: " + url);
    if (!httpReq || typeof httpReq.readyState != "number") {
        httpReq  = null;	
    }
    else {
        httpReq.open(method, url, true);
        httpReq.onreadystatechange = ajaxResponse;
        httpReq.send(body);
    }
    return !httpReq;
}


function ajaxResponse() {
    if (httpReq.readyState == 4) {
        if (httpReq.status == 200) {
			var elemOut = httpReq.responseText;
			callback(elemOut, null, null);
        }	
        else {
            alert(httpReq.status + ": " + httpReq.status.Text);
        }
    }
}

function getHTTPRequest(){

        if (typeof window.XMLHttpRequest !='undefined' ) {
            // branch for native XMLHttpRequest object - Mozilla
            try {
                xReq = new XMLHttpRequest();
            }
            catch (e) {
                xReq = null;
            }
        }
        else if (typeof window.ActiveXObject !='undefined') {
            // branch for IE/Windows ActiveX version
            try {
                xReq = new ActiveXObject("Msxml2.XMLHTTP");
            }
            catch(e) {
                try{
                    xReq = new ActiveXObject("Microsoft.XMLHTTP");
                }
                catch(e) {
                    xReq = null;
                }
            }//catch
    }
	return xReq;
}


function headRPC(url, handler, altPage) {
    var useAlt = false;

    if (typeof document.body.innerHTML == "undefined") {
        /* Cannot do RPC so show alternate page */
        useAlt = true;
    }
    else {

        /* make httprequest with response handler*/
        if (typeof handler == "undefined") {
            useAlt = xmlHttp("HEAD", url, handleFormResponse);
        }
        else {
            useAlt = xmlHttp("HEAD", url, handler);
        }
    }
	alert(url);
    return useAlt;
}

function headReturn() {
    if (httpReq.readyState == 4) {
        if (httpReq.status == 200) {
            //alert(httpReq.getResponseHeader("Content-Length"));
            alert(httpReq.getAllResponseHeaders() );
        }
        else {
            alert(httpReq.status + ": " + httpReq.status.Text);
        }//httpReq.readyState == 200

    }//httpReq.readyState == 4
}

function debug(text){
 	if(shouldDebug){
    	alert(text);
    }
	
}
