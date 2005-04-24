<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
                xmlns:w="uri:org.apache.ant:whatsnew"
                xmlns:ac="antlib:net.sf.antcontrib"
                xmlns:ant="antlib:org.apache.tools.ant"
                xmlns:bm="antlib:com.lgc.buildmagic"
                xmlns:ds="antlib:com.lgc.decisionspace"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" encoding="UTF-8" indent="yes"
              doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" />

  <!-- ================================================= -->
  <!-- ================================================= -->
  <xsl:template match="/w:whatsnew">
    <xsl:variable name="title">
      DecisionSpace Artifacts
      ~ <xsl:value-of select="/artifacts/@cyclename"/>
    </xsl:variable>
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title><xsl:value-of select="@title"/></title>
        <style type="text/css">
          code     { font: 9pt CourrierNew, regular; }
          .antlib  { background-color: #E5E5E5; }
          .ns      { color: #6A5ACD; background-color: #F2F2F2; }
          .tag     { color: #804040; font-weight: bold; }
          .attr    { color: #2E8B57; font-weight: bold; }
          .value   { color: #FF00FF; background-color: #F2F2F2; }
        </style>
      </head>
      <body>
        <h1>~ <xsl:value-of select="@title"/> ~</h1>
        <xsl:apply-templates select="w:changes" />
      </body>
    </html>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="w:changes">
    <h2>
      From <xsl:value-of select="@from" />
        to <xsl:value-of select="@to" />
    </h2>
    <xsl:apply-templates select="w:group" />
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="w:group">
    <h3><xsl:value-of select="@name" /></h3>
    <ul>
      <xsl:apply-templates select="w:entry" />
    </ul>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="w:entry">
    <li>
      <xsl:apply-templates />
    </li>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="w:bugzilla">
    <xsl:if test="@report">
      BugZilla report 
      <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id={@report}">
        <xsl:value-of select="@report" />
      </a>
    </xsl:if>
    <xsl:if test="@reports">
      BugZilla reports
      <xsl:call-template name="tokenize-ids">
        <xsl:with-param name="ids" select="@reports" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template name="tokenize-ids">
    <xsl:param name="ids" select="normalize-space(.)" />
    <xsl:param name="lhs"  select="substring-before($ids, ',')" />
    <xsl:param name="rhs"  select="substring-after($ids, ',')" />
    <xsl:param name="id" select="normalize-space($lhs)" />
    <xsl:param name="rest" select="normalize-space($rhs)" />

    <xsl:choose>
      <xsl:when test="not(contains($ids, ','))">
        <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id={$ids}">
          <xsl:value-of select="$ids" />
        </a>
      </xsl:when>
      <xsl:otherwise>
        <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id={$id}">
          <xsl:value-of select="$id" />
        </a>
        <xsl:if test="not($rest = '')">
          <xsl:text>, </xsl:text>
          <xsl:call-template name="tokenize-ids">
            <xsl:with-param name="ids" select="$rest" />
          </xsl:call-template>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="w:class">
    <code><xsl:apply-templates /></code>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="w:google">
    <a href="http://www.google.com/search?q={text()}">
      <xsl:value-of select="text()" />
    </a>
  </xsl:template>


  <!-- =================================================
    Handle generic XML
    -->

  <!-- ================================================= -->
  <xsl:template match="*[starts-with(namespace-uri(), 'antlib:')]">
    <xsl:variable name="ns" select="namespace-uri()" />
    <xsl:call-template name="tag">
      <xsl:with-param name="class" select="'antlib'" />
      <!-- TODO: Name should be a link to task/type documentation -->
      <xsl:with-param name="name">
        <span title="{$ns}:{local-name()}">
          <!-- Drop the namespace prefix for Ant tasks/types -->
          <xsl:if test="not($ns = 'antlib:org.apache.tools.ant')">
            <span class="ns">
              <xsl:value-of select="name(namespace::*[. = $ns])" />
          <!--<xsl:value-of select="substring-before(name(), ':')" />-->
            </span>:<xsl:text/>
          </xsl:if>
          <span class="tag"><xsl:value-of select="local-name()" /></span>
        </span>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="*[not(namespace-uri() = '')]" priority="-10">
    <xsl:call-template name="tag" />
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="*[namespace-uri() = '']" priority="-20">
    <xsl:copy-of select="." />
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template name="tag">
    <xsl:param name="name" select="name()" />
    <xsl:param name="class" select="''" />

    <code>
      <xsl:if test="$class">
        <xsl:attribute name="class">
          <xsl:value-of select="$class" />
        </xsl:attribute>
      </xsl:if>

      <!-- tag name start -->
      <xsl:text/>&lt;<xsl:copy-of select="$name" />

      <!-- show its attributes, if any -->
      <xsl:apply-templates select="@*" mode="antlib" />

      <xsl:choose>
        <xsl:when test="node()"> <!-- SOME nested elements -->
          <xsl:text/>&gt;<xsl:apply-templates />

          <!-- tag name end (until lexical end of template) -->
          <xsl:text/>&lt;<xsl:value-of select="$name" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="@*"> <!-- No nested elements but SOME attributes -->
            <xsl:text> /</xsl:text>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>&gt;</xsl:text>
    </code>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="@*" mode="antlib">
    <xsl:text> </xsl:text>
    <span class="attr">
      <xsl:value-of select="name()"/>
    </span>=<span class="value">
      <xsl:text/>"<xsl:value-of select="."/>"<xsl:text/>
    </span>
  </xsl:template>

</xsl:stylesheet>
