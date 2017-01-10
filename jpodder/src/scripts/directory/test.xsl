<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:param name="FEED_NAME">name</xsl:param>
	<xsl:template match="/">
		<table>
			<xsl:apply-templates select="//struct" />
		</table>	
	</xsl:template>
	<xsl:template match="struct">
		<tr><td>
		<xsl:value-of select="member/name[text()=$FEED_NAME]/../value/string"/>
		</td></tr>
	</xsl:template>
</xsl:stylesheet>