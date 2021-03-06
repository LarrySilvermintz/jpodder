<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>
   <xsl:variable name="title" select="/rss/channel/title"/>
	<xsl:variable name="feedUrl" select="/rss/channel/atom10:link[@rel='self']/@href" xmlns:atom10="http://www.w3.org/2005/Atom"/>
   <xsl:template match="/">
      <xsl:element name="html">
         <head>
            <title><xsl:value-of select="$title"/> - powered by FeedBurner</title>
            <link href="http://www.feedburner.com/fb/css/undohtml.css" rel="stylesheet" type="text/css" media="all"/>
            <link href="http://www.feedburner.com/fb/feed-styles/bf30.css" rel="stylesheet" type="text/css" media="all"/>
			<link rel="alternate" type="application/rss+xml" title="{$title}" href="{$feedUrl}"/>
            <xsl:element name="script">
               <xsl:attribute name="type">text/javascript</xsl:attribute>
               <xsl:attribute name="src">http://www.feedburner.com/fb/feed-styles/bf30.js</xsl:attribute>
            </xsl:element>
         </head>
         <xsl:apply-templates select="rss/channel"/>
      </xsl:element>
   </xsl:template>
   <xsl:template match="channel">
      <body id="browserfriendly" onload="jsFeedUrl='{$feedUrl}';loadSubscribeArea('standard');go_decoding();">
         <div id="cometestme" style="display:none;">
            <xsl:text disable-output-escaping="yes">&amp;amp;</xsl:text>
         </div>
         <div id="bodycontainer">
            <div id="bannerblock">
               <xsl:apply-templates select="image"/>
               <h1>
                  <a href="{link}" title="Link to original website">
                     <xsl:value-of select="$title"/>
                  </a>
               </h1>
               <h2>syndicated content powered by FeedBurner</h2>
               <p style="clear:both"/>
            </div>
            <div id="bodyblock">
               <div id="subscribenow" class="subscribeblock action">
						<div id="subscribe-userchoice" style="display:none">
							<p id="subscribeLink"><a href="#">...</a></p>
							<p id="resetLink">Reset this favorite; <a href="#" onclick="return clearUserchoice('standard')">show all Subscribe options</a></p>
						</div>
                  <div id="subscribe-options">
                     <h3>Subscribe Now!</h3>
                     <h4>...with web-based news readers. Click your choice below:</h4>
                     <div id="webbased">
                        <a href="http://add.my.yahoo.com/rss?url={$feedUrl}" onclick="this.href = subscribeNow(this.href,'My Yahoo!');return true"><img src="http://us.i1.yimg.com/us.yimg.com/i/us/my/addtomyyahoo4.gif" width="91" height="17" alt="addtomyyahoo4"/></a>

								<a class="img" href="http://www.newsgator.com/ngs/subscriber/subext.aspx?url={$feedUrl}" onclick="subscribeNow(this.href,'NewsGator Online');return true"><img src="http://www.newsgator.com/images/ngsub1.gif" alt="Subscribe in NewsGator Online"/></a>
								<xsl:element name="a">
								  <xsl:attribute name="onclick">this.href = subscribeNow(this.href,"Pluck");return true</xsl:attribute>
								  <xsl:attribute name="href">http://client.pluck.com/pluckit/prompt.aspx?GCID=C12286x053&amp;a=<xsl:value-of select="$feedUrl"/>&amp;t=<xsl:value-of select="$title"/></xsl:attribute>
									<img src="http://www.pluck.com/images/rss-pluck.gif" alt="Subscribe with Pluck RSS reader" border="0" />
								</xsl:element>
                        <br/>
								<a class="img" href="http://www.rojo.com/add-subscription?resource={$feedUrl}" onclick="this.href=subscribeNow(this.href,'Rojo');return true"> 
		<img src="http://www.rojo.com/skins/static/images/add-to-rojo.gif" alt="Subscribe in Rojo"/></a>

								<a class="img" href="http://www.bloglines.com/sub/{$feedUrl}" onclick="this.href=subscribeNow(this.href,'Bloglines');return true"><img src="http://www.bloglines.com/images/sub_modern5.gif" alt="Subscribe with Bloglines"/></a>						
								
								<a href="http://fusion.google.com/add?feedurl={$feedUrl}" onclick="this.href=subscribeNow(this.href,'Google');return true"><img src="http://buttons.googlesyndication.com/fusion/add.gif" width="104" height="17" alt="Add to Google"/></a>
								
                     </div>
                     <h4>...with other readers:</h4>
                     <form action="http://www.feedburner.com" method="get">
                        <select onchange="location.href=subscribeNow('{$feedUrl}',this.options[this.selectedIndex].value)">
                           <option value="--" disabled="disabled" selected="selected" style="padding-left:0">(Choose Your Reader)</option>
									<option value="FeedDemon">FeedDemon</option>
									<option value="NetNewsWire">NetNewsWire</option>
									<option value="NewsFire">NewsFire</option>
									<option value="NewsGator Outlook Edition">NewsGator Outlook Edition</option>
									<option value="shrook">Shrook</option>
									<option value="USM">Universal Subscription Mechanism (USM)</option>									
                        </select>
                        <p><a href="http://www.feedburner.com/fb/products/feeddemon-install.exe">Download a Free Trial of FeedDemon 1.5</a><br/>
                            Learn More about <a href="http://www.kbcafe.com/rss/whatisthis.html#whatisusm" target="usm">USM</a></p>
                     </form>
                  </div>
							<input id="savechoice" type="hidden" value="standard"/>
               </div>
               <p class="about">FeedBurner makes it easy to receive content updates in My Yahoo!, Newsgator, Bloglines, and other news readers.</p>
               <p class="about">
                  <a href="http://www.feedburner.com/fb/a/aboutrss">Learn more about syndication and FeedBurner...</a>
               </p>
			      <xsl:apply-templates xmlns:feedburner="http://rssnamespace.org/feedburner/ext/1.0" select="feedburner:browserFriendly"/>
               <xsl:apply-templates select="item"/>
            </div>
            <div id="footer">
               <a href="http://www.feedburner.com">
                  <img src="http://www.feedburner.com/fb/feed-styles/images/footer_logo.gif"/>
               </a>
               <p>FeedBurner delivers the world's subscriptions wherever they need to go. Publish a feed for text or podcasting? <a href="http://www.feedburner.com" target="_blank"><br/>You should try FeedBurner today</a>.</p>
            </div>
         </div>
      </body>
   </xsl:template>
   <xsl:template match="item" xmlns:dc="http://purl.org/dc/elements/1.1/">
      <xsl:if test="position() = 1">
         <h3 xmlns="http://www.w3.org/1999/xhtml" id="currentFeedContent">Current Feed Content</h3>
      </xsl:if>
      <ul xmlns="http://www.w3.org/1999/xhtml">
         <li class="regularitem">				
            <h4 class="itemtitle">
               <a href="{link}">
                  <xsl:value-of select="title"/>
               </a>
            </h4>
            <h5 class="itemposttime">
               <xsl:if test="count(child::pubDate)=1"><span>Posted:</span><xsl:text> </xsl:text><xsl:value-of select="substring(pubDate,5)"/></xsl:if>
				<xsl:if test="count(child::dc:date)=1"><span>Posted:</span><xsl:text> </xsl:text><xsl:value-of select="dc:date"/></xsl:if>
            </h5>
            <div class="itemcontent" name="decodeable">
               <xsl:call-template name="outputContent"/>
            </div>
            <xsl:if test="count(child::enclosure)=1">
               <p class="mediaenclosure">MEDIA ENCLOSURE: <a href="{enclosure/@url}"><xsl:value-of select="child::enclosure/@url"/></a></p>
            </xsl:if>
         </li>
      </ul>
   </xsl:template>
   <xsl:template match="image">
      <a href="{link}" title="Link to original website">
         <xsl:element name="img" namespace="http://www.w3.org/1999/xhtml">
            <xsl:attribute name="src">
               <xsl:value-of select="url"/>
            </xsl:attribute>
            <xsl:attribute name="alt">Link to <xsl:value-of select="title"/></xsl:attribute>
            <xsl:attribute name="id">feedimage</xsl:attribute>
         </xsl:element>
      </a>
      <xsl:text/>
   </xsl:template>
   <xsl:template xmlns:feedburner="http://rssnamespace.org/feedburner/ext/1.0" match="feedburner:browserFriendly">
      <p xmlns="http://www.w3.org/1999/xhtml" class="about">
         <span style="color:#000">A message from this feed's publisher:</span>
         <xsl:text> </xsl:text>
         <xsl:apply-templates/>
      </p>
   </xsl:template>
   <xsl:template name="outputContent">
      <xsl:choose>
         <xsl:when xmlns:xhtml="http://www.w3.org/1999/xhtml" test="xhtml:body">
            <xsl:copy-of select="xhtml:body/*"/>
         </xsl:when>
         <xsl:when xmlns:xhtml="http://www.w3.org/1999/xhtml" test="xhtml:div">
            <xsl:copy-of select="xhtml:div"/>
         </xsl:when>
         <xsl:when xmlns:content="http://purl.org/rss/1.0/modules/content/" test="content:encoded">
            <xsl:value-of select="content:encoded" disable-output-escaping="yes"/>
         </xsl:when>
         <xsl:when test="description">
            <xsl:value-of select="description" disable-output-escaping="yes"/>
         </xsl:when>
      </xsl:choose>
   </xsl:template>
</xsl:stylesheet>
