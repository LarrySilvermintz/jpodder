<?php
// One-Click Subscription PHP 4 Script
// Takes a URL to a podcast RSS file via URL query string
// Returns the podcast MIME type and the RSS with the atom:link element

header('Content-Type: application/podcast+xml');
$url = $_GET['url'];

// For PHP 4
$xsl = xslt_create();
// Download the RSS feed XML data
$rss = file_get_contents($url);
// Passing the XML as an argument (not as a file)
$arguments = array('/_xml' => $rss);
// Need to pass the URL of the feed to the XSLT processor
$params = array('subscribeUrl' => $url);
// Do it now!
$xml = xslt_process($xsl, 'arg:/_xml', 'oneclick.xsl', NULL, $arguments, $params);
xslt_free($xsl);
echo $xml;

?>