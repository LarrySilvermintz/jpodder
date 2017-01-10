<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:atom="http://purl.org/atom/ns#">

<xsl:output method="xml"/>

<!-- Define subscribeUrl parameter - this should be passed in by the XSLT Processor -->
<xsl:param name="subscribeUrl" />

<xsl:template match="@*|node( )">
	<xsl:copy>
		<xsl:apply-templates select="@*|node( )" />
	</xsl:copy>
</xsl:template>

<xsl:template match="/rss/channel">
	<xsl:copy>
		<!-- insert a line break after the <channel> element -->	
		<xsl:text>
		</xsl:text>
		<!-- Insert the oneclick subscribe URL -->
		<xsl:element name="atom:link">
		<xsl:attribute name="rel">self</xsl:attribute>
		<xsl:attribute name="type">application/rss+xml</xsl:attribute>
		<xsl:attribute name="title"><xsl:value-of select="/rss/channel/title" /></xsl:attribute>
		<xsl:attribute name="href"><xsl:value-of select="$subscribeUrl" /></xsl:attribute>
		</xsl:element>
		<!-- Continue to process children -->		
		<xsl:apply-templates select="@*|node( )" />
	</xsl:copy>
</xsl:template>

</xsl:stylesheet>