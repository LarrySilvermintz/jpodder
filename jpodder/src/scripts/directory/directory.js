// directory scripts. 
var ipx_url = 'http://directory.iPodderX.com/iPXapi';
// iPodderX Get categories XML.
var get_directory = '<?xml version="1.0"?>' + 
		'<methodCall>' + 
	    '<methodName>iPX.feedCategories</methodName>' + 
   		'<params>' + 
      	'<param>' + 
        '<value><string>4df743ea3a39814d4bfe18b7299f7240</string></value>' + 
        '</param>' + 
        '</params>' + 
   		'</methodCall>';

var get_cat = '<?xml version="1.0"?>' + 
		'<methodCall>' + 
	    '<methodName>iPX.feedsByCategory</methodName>' + 
   		'<params>' + 
      	'<param>' + 
        '<value><string>4df743ea3a39814d4bfe18b7299f7240</string></value>' + 
        '</param>';


var search_feeds = '<?xml version="1.0"?>' + 
		'<methodCall>' + 
	    '<methodName>iPX.searchFeeds</methodName>' + 
   		'<params>' + 
      	'<param>' + 
        '<value><string>4df743ea3a39814d4bfe18b7299f7240</string></value>' + 
        '</param>';


var cat_stylesheet = '<xsl:stylesheet>' + 
                 '<xsl:template match="/">' + 
                 '<xsl:apply-templates select="//value/string"/>'+ 
                 '</xsl:template>' + 
                 '<xsl:template match="//value/string">'+ 
                 '<a href="dummy" onmouseover="return loadCategory({});">' + 
                 '<xsl:value-of select="."/>' + 
                 '</a>'+
                 '</xsl:template>'+
                 '</xsl:stylesheet>';


var cat_stylesheet2 = '<xsl:stylesheet>' + 
                 '<xsl:template match="/">' +
                 '<table>' + 
                 '<xsl:for-each select="//data/value">' + 
                 '<tr>' +
                 '<xsl:variable name="cat" select="string"/>' +
                 '<xsl:variable name="onclick">return loadCategory(&apos;<xsl:value-of select="string($cat)"/>&apos;);</xsl:variable>'+
                 '<a href="{$cat}" onclick="{$onclick}">' + 
                 //'<a href="{$cat}" onclick="{$onclick}">' + 
                 '<xsl:value-of select="string"/>' + 
                 '</tr>' + 
				 '</xsl:for-each>'+
				 '</table>' + 
                 '</xsl:template>'+                 
                 '</xsl:stylesheet>';

// THE FEED PARSING STYLESHEET.
//                 
var cat_stylesheet3 = '<xsl:stylesheet>' + 
				 '<xsl:param name="FEED_URL">feed_url</xsl:param>'+
				 '<xsl:param name="FEED_DESCRIPTION">description</xsl:param>'+
				 '<xsl:param name="FEED_NAME">name</xsl:param>'+
				 '<xsl:param name="SITE_URL">site_url</xsl:param>'+
                 '<xsl:template match="/">' +
                 	'<table class="feeds">' + 
					'<xsl:apply-templates select="//struct"/>' +
				 	'</table>' + 
                 '</xsl:template>'+                 
				 '<xsl:template match="struct">' +
				 	// Test XPath expression which gets an element name.
				 	'<tr><td class="header" colspan="2" ><xsl:value-of select="member/name[text()=$FEED_NAME]/../value/string"/></td></tr>'+
      				'<tr>'+
                 	'<td colspan="2">'+                 	 
                 	'<div class="description">' + 
                 	'<xsl:value-of select="member/name[text()=$FEED_DESCRIPTION]/../value/string"/></td>'+	
      				'<br/><br/></div>'+ 
      				'</tr>'+ 
      				'<tr>'+
      				'<xsl:variable name="channel_link"  select="member/name[text()=$FEED_URL]/../value/string"/>' + 
      				'<xsl:variable name="site_link"  select="member/name[text()=$SITE_URL]/../value/string"/>' +
      				'<xsl:variable name="onclick">return loadFeed(&apos;<xsl:value-of select="string($channel_link)"/>&apos;);</xsl:variable>'+
      				'<td><a href="" onclick="{$onclick}">Subscribe!</a></td>'+
      				'<td>'+           	
      				'<a href="{$channel_link}">Visit the site at: <xsl:value-of select="$site_link"/></a>'+
					'</td>'+
					'</tr>'+ 				 
				 '</xsl:template>' +
                 '</xsl:stylesheet>';
                 
var cat;


var test = '<?xml version="1.0"?>' + 
'<methodResponse>' +
'  <params>'+
'    <param>'+
'      <value>'+
'        <array><data>'+
'  <value><struct>'+
'  <member><name>feed_id</name><value><string>748</string></value></member>'+
'  <member><name>feed_url</name><value><string>http://clogwog.net/cenvi/rss.xml</string></value></member>'+
'  <member><name>site_url</name><value><string>http://clogwog.net/cenvi/</string></value></member>'+
'  <member><name>description</name><value><string>Romantiek, Gevaar en Lust</string></value></member>'+
'  <member><name>name</name><value><string>The Legendary Curry &amp; van Inkel show</string></value></member>'+
'  <member><name>dateAdded</name><value><string>2005-06-30</string></value></member>'+
'  <member><name>score</name><value><string>6.2123713493347</string></value></member>'+
'</struct></value>'+
'  <value><struct>'+
'  <member><name>feed_id</name><value><string>11116</string></value></member>'+
'  <member><name>feed_url</name><value><string>http://clogwog.net/cenvi/rss.xml</string></value></member>'+
'  <member><name>site_url</name><value><string>http://clogwog.net/cenvi/</string></value></member>'+
'  <member><name>description</name><value><string>Romantiek, Gevaar en Lust</string></value></member>'+
'  <member><name>name</name><value><string>The Legendary Curry &amp; van Inkel show</string></value></member>'+
'  <member><name>dateAdded</name><value><string>2005-10-11</string></value></member>'+
'  <member><name>score</name><value><string>6.2123713493347</string></value></member>'+
'</struct></value>  '+
'</data></array>'+
'      </value>'+
'    </param>'+
'  </params>'+
'</methodResponse>';

function testXSL(){
	window.status = test;
}

function loadDirectory(){
	signal('Loading categories...');
	var result = ajaxRPC(ipx_url, catResponse, get_directory);
//	if(result){
//		el('catdisplay').innerHTML = 'The directory is not available...' + 
//									 'This could be a problem with your Internet connection.' + 
//									 'it could also be that the online directory is not available' + 
//									 'In the latter case, please try again later.' + 
//									 'If the problem persists, please report this on <a href="http://www.jpodder.com">';		
//	}
	return false;
}

function loadCategory(category){
	var cat_param = '<param><value><string>'+ category +'</string></value></param></params></methodCall>';
	var method = get_cat + cat_param;
	signal('Loading...' + category);	
	var result = ajaxRPC(ipx_url, feedsResponse, method);
	return result;
}

function searchFeeds(){
	var query = document.searchform.search.value;
	var cat_param = '<param><value><string>'+ query +'</string></value></param></params></methodCall>';
	var method = search_feeds + cat_param;	
	signal('Querying...');	
	ajaxRPC(ipx_url, searchResponse, method);
	return false;
}

function subscribe(url){
	// Christophe: We should set the MIME type to what we have registered and 
	// let the association launch the subscription
	var subscribe = 'subscribe=' + url;
	window.status = subscribe;
	return false;
}

function loadFeed(url){
	signal('Loading feed...');
	window.status = "_instruction:ref="+url;
	// CB TODO: create method to get the feed RSS.
	// CB TODO: create XSL file to parse the feed.
	return false;
}


function catResponse(text, headers, callingContext){
	cat = text;
	var formatted_cat = transform(cat, cat_stylesheet2);
  	el('catdisplay').innerHTML = formatted_cat;
  	loadCategory("iPodderX.com Top Picks");
}

function feedsResponse(text, headers, callingContext){
	window.status = text;
}

function searchResponse(text, headers, callingContext){
	window.status = text;
}

function transform(text, pStylesheet){
	signal('Parsing XML...');
	var xml = xmlParse(text);
	signal('Parsing XSL...');	
	var xsl = xmlParse(pStylesheet);
	signal('Transforming...');
	var html = xsltProcess(xml, xsl);  	
	signal('Done...');
	return html;
}

function signal(text){
	el('status').innerHTML = text;
}

function el(id) {
  return document.getElementById(id);
}