<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method= 'html' />
	<xsl:param name="FEED_URL">feed_url</xsl:param>
	<xsl:param name="FEED_DESCRIPTION">description</xsl:param>
	<xsl:param name="FEED_NAME">name</xsl:param>
	<xsl:param name="SITE_URL">site_url</xsl:param>
	<xsl:template match="/">
		<table class='feeds'>
			<xsl:apply-templates select="//struct" />
		</table>
	</xsl:template>
	<xsl:template match="struct">
		<tr>
			<td class="header" colspan="2">
				<xsl:value-of
					select="member/name[text()=$FEED_NAME]/../value/string" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div class="description">
					<xsl:value-of
						select="member/name[text()=$FEED_DESCRIPTION]/../value/string" />
				</div>
			</td>
		</tr>
		<tr>
			<xsl:variable name="channel_link"
				select="member/name[text()=$FEED_URL]/../value/string" />
			<xsl:variable name="site_link"
				select="member/name[text()=$SITE_URL]/../value/string" />
<!-- Bug 12-06-2006			
			<xsl:variable name="onclick">
				<xsl:value-of select="string($channel_link)" />
			</xsl:variable>
-->			
			<xsl:variable name="onclick">return subscribe(&apos;<xsl:value-of select="string($channel_link)"/>&apos;);</xsl:variable>
			<td>
				<a href="dummy" onclick="{$onclick}">subscribe</a>
			</td>
			<td>
				<a href="{$site_link}">
					Visit the site at:
					<xsl:value-of select="$site_link" />
				</a>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>