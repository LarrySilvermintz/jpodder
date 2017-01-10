ajaxCaller.shouldDebug = false;
ajaxCaller.shouldEscapeVars = false;
ajaxCaller.shouldMakeHeaderMap = true;

function onDebugClicked() {
  ajaxCaller.shouldDebug = $("debug").checked;
}

function onEscapeVarsClicked() {
  ajaxCaller.shouldEscapeVars = $("escapeVars").checked;
}

function onMakeHeaderMapClicked() {
  ajaxCaller.shouldMakeHeaderMap = $("makeHeaderMap").checked;
}

function buildBodylessVars() {
  return {
    flavour: $("flavour").value,
    topping: $("topping").value
  }
}

function testGet() {
  signalLoadingResults();
  ajaxCaller.get("httpLogger.phtml", buildBodylessVars(), onResponse, false,
                 "a get request");
}

function testGetForXML() {
  signalLoadingResults();
  ajaxCaller.get("httpXMLLogger.phtml", buildBodylessVars(), onXMLResponse, true,
           "a get request");
}

function testGetPlainText() {
  signalLoadingResults();
  // complete url must be constructed for this shortcut function
  flavourValue = $("flavour").value;
  toppingValue = $("topping").value;
  url = "httpLogger.phtml?flavour=" + escape(flavourValue)+"&"
        + "topping=" + escape(toppingValue);
  ajaxCaller.getPlainText(url, onResponse);
}

function testPostForPlainText() {
  signalLoadingResults();
  bodyVars = {
    restaurant: $('restaurant').value,
    location: $('location').value
  }
  ajaxCaller.postForPlainText("httpLogger.phtml", bodyVars, onResponse);
}

function testHead() {
  signalLoadingResults();
  ajaxCaller.head("httpLogger.phtml", buildBodylessVars(), onResponse, false,
          "a head request (no body will be returned)");
}

function testDelete() {
  signalLoadingResults();
  ajaxCaller.deleteIt("httpLogger.phtml", buildBodylessVars(), onResponse, false,
          "a delete request");
}

function testPostVars() {
  signalLoadingResults();
  urlVars = {
    restaurant: $('restaurant').value
  }
  bodyVars = {
    location: $('location').value
  }
  ajaxCaller.postVars("httpLogger.phtml", bodyVars, urlVars, onResponse,
                false, "a postVars request");
}

function getParametersForBodyRequest() {
  urlVars = {
    dish: $("dish").value
  }
  bodyType = $("recipeBodyType").value;
  body = $("recipe").value;
  parameters = {
    urlVars: urlVars,
    bodyType: bodyType,
    body: body
  };
  return parameters;
}

function testPostBody() {
  signalLoadingResults();
  bodyParameters = getParametersForBodyRequest();
  ajaxCaller.postBody("httpLogger.phtml", bodyParameters.urlVars,
      onResponse, false, "a postBody request",
      bodyParameters.bodyType, bodyParameters.body);
}

function testPutBody() {
  signalLoadingResults();
  bodyParameters = getParametersForBodyRequest();
  ajaxCaller.putBody("httpLogger.phtml", bodyParameters.urlVars,
      onResponse, false, "a postBody request",
      bodyParameters.bodyType, bodyParameters.body);
}

function testOptions() {
  signalLoadingResults();
  bodyParameters = getParametersForBodyRequest();
  ajaxCaller.options("httpLogger.phtml", bodyParameters.urlVars,
      onResponse, false, "an options request",
      bodyParameters.bodyType, bodyParameters.body);
}

function testTrace() {
  signalLoadingResults();
  bodyParameters = getParametersForBodyRequest();
  ajaxCaller.trace("httpLogger.phtml", bodyParameters.urlVars,
      onResponse, false, "a trace request",
      bodyParameters.bodyType, bodyParameters.body);
}

function signalLoadingResults() {
  resultBlock = $("result");
  resultBlock.innerHTML = "Loading ...";
}

function onXMLResponse(xml, headers, callingContext) {
  resultBlock = $("result");
  requestURI = xml.length;

  messages = xml.getElementsByTagName("requestMethod");
  resultBlock.innerHTML =
    messages.length + ": " + messages[0].firstChild.nodeValue;

  resultBlock.innerHTML += "<p>Children of root: "
    + xml.getElementsByTagName("requestInfo")[0].childNodes.length + "</p>";

  resultBlock.innerHTML += "<p>Request Method: "
    + xml.getElementsByTagName("requestMethod")[0].firstChild.nodeValue
    + "</p>"
    ;

  resultBlock.innerHTML += "<p>Request URI: "
    + xml.getElementsByTagName("requestURI")[0].firstChild.nodeValue
    + "</p>";

  resultBlock.innerHTML += "<p>Calling Context: " + callingContext + "</p>";

}

function onResponse(text, headers, callingContext) {
  var resultBlock = $("result");
  resultBlock.innerHTML =
    "<h3>Response Body</h3>"
    + "<p class='explanation'>The server-side script has been configured "
    + "to simply echo back a "
    + "summary of what you sent it.</p>"
    + "<pre class='response'>" + text + "</pre>";
 resultBlock.innerHTML +=
    "<h3>Response Headers (in case you care)</h3>"
    + "<pre class='response'>" + getHeaderHTML(headers) + "</pre>";
  resultBlock.innerHTML +=
      "<h3>Calling Context:</h3>\n"
    + "<p class='explanation'>The calling context is a variable the "
    + "caller sets. It's not passed to the server, but rather stored at "
    + "the time of the call and matched back to the call when it "
    + "returns.</p>"
    + "<div class='response'>" + callingContext + "</div>";

}

function getHeaderHTML(headers) {
  if (ajaxCaller.shouldMakeHeaderMap) { // TODO check for array instead
    message = "";
    for (key in headers) {
      message += "[" + key + "] -&gt; [" + headers[key] + "]<br/>";
    }
    return message;
  } else {
    return headers;
  }
}

