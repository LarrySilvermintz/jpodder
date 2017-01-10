var httpReq = null;
var callback;


function fetch(id){
	debug("script called");	

	if(id == 0){
		get('main.htm', setContent);
	}
	if(id == 1){
		get('download.htm', setContent);
	}
	if(id == 2){
		get('features.htm', setContent);
	}
	if(id == 3){
		get('support.htm', setContent);
	}
	if(id == 4){
		get('box.htm', setContent);
	}
	return false;
	
}

function setContent(elementOut){
	el('content').innerHTML = elementOut;
}


function debug(text){
	el('status').innerHTML = text;
}

/**
	Get an element by ID.
	Should work for both IE and firefox.
*/
function el(id) {
  return document.getElementById(id);
}


/**
	Perform an HTTP GET request


*/
function get(url, handler) {
	
	debug("ajaxRPC called");
    var failure = false;
	callback = handler;
	
	failure = xmlHttp("GET", url, handler);
	/*
    if (typeof document.body.innerHTML == "undefined") {
        failure = true;
    }
    else {
		
    }
    debug("Request failed?" + failure);
    */
    return failure;
}


function xmlHttp(method, url, handler) {
	xmlHttp(method, url, handler, null);
}
function xmlHttp(method, url, handler, body) {
    	    
    try {
    	netscape.security.PrivilegeManager.enablePrivilege("UniversalBrowserRead");
   	} catch (e) {
   	}

    if (!httpReq) {
    	httpReq = getHTTPRequest();
    }
    
    
	debug("HTTP: " + method + " to: " + url);
    if (!httpReq || typeof httpReq.readyState != "number") {
        httpReq  = null;	
    }
    else {
    	httpReq.onreadystatechange = ajaxResponse;
        httpReq.open(method, url, true);
        httpReq.send(null)
        /* For POST request, add Send() and XML RPC body, removed here*/
    }
    debug("request send");
    return !httpReq;
}


function ajaxResponse() {
	debug("Called back succesfully");
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
