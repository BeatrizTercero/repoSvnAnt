<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="xml"/>
  <xsl:include href="stylesheet_include.xsl"/>
  <xsl:template match="/">
    <authors>
        <xsl:apply-templates/>
    </authors>
  </xsl:template>
</xsl:stylesheet>
