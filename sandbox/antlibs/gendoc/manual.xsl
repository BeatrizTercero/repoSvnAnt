<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
                xmlns:m="uri:org.apache.ant:manual"
                xmlns:attr="uri:org.apache.ant:manual:attribute"
                xmlns:elem="uri:org.apache.ant:manual:element"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="m attr elem">

  <xsl:output method="html" encoding="UTF-8" indent="yes"
              doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" />

  <!-- Space-only nodes matter in the example snippets only -->
  <xsl:preserve-space elements="m:snippet" />

  <!-- ================================================= -->
  <xsl:template match="/m:manual">
    <!-- For now, process only first <task> of each file -->
    <xsl:apply-templates select="m:task[position()=1]" />
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:task">
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>&lt;<xsl:value-of select="@name"/>&gt; task</title>
        <style type="text/css">
          code     { font: 9pt monospace, regular; }
          .antlib  { background-color: #E5E5E5; }
          .ns      { color: #6A5ACD; background-color: #F2F2F2; }
          .tag     { color: #804040; font-weight: bold; }
          .attr    { color: #2E8B57; font-weight: bold; }
          .value   { color: #FF00FF; background-color: #F2F2F2; }
          .example { border-left: thick solid gray; padding-left: 1em; }
          .example pre { margin-bottom: 0.5em; }
          h4 { margin-bottom: 0.33em; }
          div.nested-1 { margin-left: 1.0em; }
          div.nested-1 h5 { margin-bottom: 0.0em; }
          .synopsis { background-color: #E9E9E9; }
          .since   {font: italic normal; }
          .at { font: 9pt monospace, regular; color: gray; }
          .elem-ref { font: 9pt monospace, regular; font-weight: bold;
                      color: #804040; background-color: #E5E5E5; }
          .attr-ref { font: 9pt monospace, regular; font-weight: bold;
                      color: #2E8B57; background-color: #E5E5E5; }
        </style>
      </head>
      <body>
        <h2 class="{local-name()}" id="{@name}">
          <xsl:call-template name="bracket-name" /> task
        </h2>
        <xsl:apply-templates />
      </body>
    </html>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:alias">
    <!-- Just ignore for now -->
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:description">
    <xsl:apply-templates />
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:task/m:description/m:synopsis">
    <h3 class="description">Description</h3>
    <span class="synopsis"><xsl:apply-templates /></span>
    <br />
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:description/m:synopsis">
    <span class="synopsis"><xsl:apply-templates /></span>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:attributes">
    <xsl:if test="m:attribute">
      <h3>Parameters</h3>
      <xsl:call-template name="attributes" />
    </xsl:if>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:nested-element/m:attributes">
    <xsl:if test="m:attribute">
      <div class="nested-1">
        <h5><xsl:call-template name="bracket-name" />'s parameters</h5>
        <xsl:call-template name="attributes" />
      </div>
    </xsl:if>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template name="attributes">
    <table id="attributes" border="1" cellpadding="2" cellspacing="0">
      <thead>
        <tr>
          <th valign="top"><b>Attribute</b></th>
          <th valign="top"><b>Description</b></th>
          <th align="center" valign="top"><b>Required?</b></th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="m:attribute" />
      </tbody>
    </table>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:attribute">
    <tr>
      <td valign="top">
        <xsl:value-of select="@name" />
      </td>
      <td valign="top">
        <xsl:apply-templates select="m:synopsis | m:description" />
      </td>
      <td align="center" valign="top">
        <xsl:choose>
          <xsl:when test="m:required">
            <xsl:apply-templates select="m:required" />
          </xsl:when>
          <xsl:otherwise>No</xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:nested-elements">
    <xsl:if test="m:nested-element">
      <h3 class="{local-name()}">Parameters specified as nested elements</h3>
      <xsl:apply-templates select="m:nested-element" />
    </xsl:if>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:nested-element">
    <h4 id="{@name}" class="{local-name()}">
      <xsl:call-template name="bracket-name" />
      <xsl:if test="@since">
        <!-- Not great, but will do for now -->
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        <span class="since">(since Ant <xsl:value-of select="@since" />)</span>
      </xsl:if>
    </h4>
    <xsl:apply-templates />
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:section">
    <h3 class="{local-name()}"><xsl:value-of select="@name" /></h3>
    <xsl:apply-templates />
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:required">
    Yes<xsl:text/>
    <xsl:if test="normalize-space(text())!=''">
      <xsl:text/>, <em><xsl:apply-templates /></em>
    </xsl:if>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:examples">
    <xsl:if test="m:example">
      <h3 class="{local-name()}">Examples</h3>
      <xsl:apply-templates select="m:example" />
    </xsl:if>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:example">
    <xsl:variable name="num"><xsl:number /></xsl:variable>
    <div class="{local-name()}" id="e.g.{$num}">
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:snippet">
    <pre><xsl:apply-templates /></pre>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:snippet/text()">
    <!-- Swallow the first and last text nodes for better formatting.
         Note that position() is in the context of all the child nodes
         of the snippet, both text() and element() nodes.
         Test for normalize-space='' ??? -->
    <xsl:if test="not(position()=1 or position()=last())">
      <xsl:value-of select="." />
    </xsl:if>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template match="m:copyright">
    <hr/>
    <p align="center">
      Copyright &#169; <xsl:value-of select="." />
      The Apache Software Foundation. All rights Reserved.
    </p>
  </xsl:template>

  <!-- ================================================= -->
  <xsl:template name="bracket-name">
    <xsl:variable name="name">
      <xsl:choose>
        <xsl:when test="@name"><xsl:value-of select="@name" /></xsl:when>
        <xsl:when test="../@name"><xsl:value-of select="../@name" /></xsl:when>
        <xsl:otherwise><xsl:value-of select="." /></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <code>&lt;</code><xsl:value-of select="$name"/><code>&gt;</code>
  </xsl:template>

  <!-- =================================================
       A reference to a local attribute using the special
       'attr' namespace (prefix). (from task/type, or a nested element.)

       Note: No links for now, until unicity of link can be asserted.
    -->
  <xsl:template match="attr:*">
    <!--<span class="at">@</span>-->
    <span class="attr-ref" title="{local-name()} Attribute">
      <xsl:value-of select="local-name()" />
    </span>
  </xsl:template>

  <!-- =================================================
       A reference to a local nested elemnt using the special
       'elem' namespace (prefix). (from task/type, or a nested element.)

       Note: No links for now, until unicity of link can be asserted.
    -->
  <xsl:template match="elem:*">
    <!--<span class="at">@</span>-->
    <span class="elem-ref" title="&lt;{local-name()}&gt; Nested Element">
      <xsl:text/>&lt;<xsl:value-of select="local-name()" />&gt;<xsl:text/>
    </span>
  </xsl:template>

  <!-- =================================================
       Auto-<code> the boolean values true/false/yes/no.
    -->
  <xsl:template match="m:true | m:false | m:yes | m:no">
    <code><xsl:value-of select="local-name()" /></code>
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

  <!-- =================================================
       Copy HTML tags in the default namespace as-is,
       while still converting custom tags thru templates.
    -->
  <xsl:template match="*[namespace-uri() = '']" priority="-20">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates select="node()" />
    </xsl:copy>
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
          <xsl:text>&gt;</xsl:text>

          <xsl:apply-templates />

          <!-- tag name end (until lexical end of template) -->
          <xsl:text/>&lt;/<xsl:copy-of select="$name" />
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
