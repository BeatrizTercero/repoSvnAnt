<?xml version="1.0" encoding="UTF-8"?>
<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
    -->
<stylesheet xmlns="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://www.w3.org/1999/XSL/Transform.xsd" version="1.0">

    <!-- Stylesheet to transform automatically an XML build file into a Groovy one -->

    <output method="text" />

    <!-- search every namespace defined and put it into the map -->
    <key name="namespaces" match="//*[string-length(substring-before(name(), ':')) != 0]" use="namespace-uri()" />

    <template match="/project">

        <!-- first process the project metadata -->
        <if test="/project/@name">
            <text>
project.name = '</text><value-of select="/project/@name" /><text>'</text>
        </if>
        <if test="/project/@basedir">
            <text>
project.basedir = '</text><value-of select="/project/@basedir" /><text>'</text>
        </if>
        <if test="/project/@default">
            <text>
project.defaultTarget = '</text><value-of select="/project/@default" /><text>'</text>
        </if>

        <!-- spacer -->
        <text>
</text>

        <!-- iterate on every element that have a namespace -->
        <for-each select="//*[string-length(substring-before(name(), ':')) != 0]">
            <!-- we want to declare only one groovyns by namespace, so process the element that has been selected into the map -->
            <if test="generate-id(key('namespaces', namespace-uri())) = generate-id(.)">
                <text>
def </text><value-of select="substring-before(name(), ':')" /><text> = groovyns(uri: '</text><value-of select="namespace-uri()" /><text>')</text>
            </if>
        </for-each>
        <apply-templates select="child::node()" />
    </template>

    <!-- heuristic about antcontrib's if -->
    <template match="*[contains(namespace-uri(.), 'antcontrib') and local-name() = 'if']">
        <text>if (</text>
        <apply-templates select="*[position() = 1]" mode="condition" />
        <text>) {</text>
        <apply-templates select="then/child::node()" />
        <if test="count(else/child::node()) != 0">
            <text>} else {</text>
            <apply-templates select="else/child::node()" />
        </if>
        <text>}</text>
    </template>

    <!-- condition task transformation into if-then-else -->
    <template match="*[local-name() = 'condition' and @property]">
        <text>if (</text>
            <apply-templates select="child::*" mode="condition" />
        <text>) {
</text>
        <call-template name="setprop">
            <with-param name="name" select="@property" />
            <with-param name="value" select="@value" />
        </call-template>
        <text>
</text>
        <if test="@else">
            <text>} else {
</text>
            <call-template name="setprop">
                <with-param name="name" select="@property" />
                <with-param name="value" select="@else" />
            </call-template>
        </if>
        <text>}</text>
    </template>

    <!-- 'pure' condition handling -->
    <template match="*[local-name() = 'istrue']" mode="condition">
        <call-template name="anteval">
            <with-param name="value" select="@value" />
        </call-template>
    </template>
    <template match="*[local-name() = 'isfalse']" mode="condition">
        <text>!</text>
        <call-template name="anteval">
            <with-param name="value" select="@value" />
        </call-template>
    </template>
    <template match="*[local-name() = 'not']" mode="condition">
        <text>!</text><apply-templates select="child::*" />
    </template>
    <template match="*[local-name() = 'and']" mode="condition">
        <for-each select="*">
            <apply-templates select="." mode="condition" />
            <if test="position() != last()">
                <text> &amp;&amp; </text>
            </if>
        </for-each>
    </template>
    <template match="*[local-name() = 'or']" mode="condition">
        <for-each select="*">
            <apply-templates select="." mode="condition" />
            <if test="position() != last()">
                <text> || </text>
            </if>
        </for-each>
    </template>
    <template match="*" mode="condition">
        <apply-templates select="." />
    </template>

    <!-- generic xml to groovy transformer -->
    <template match="*">

        <!-- if there is some namespace, append the use of the grooyns -->
        <variable name="nsPrefix" select="substring-before(name(), ':')" />
        <if test="string-length($nsPrefix) != 0">
            <value-of select="$nsPrefix" />
            <text>.</text>
        </if>

        <!-- actual element / function name -->
        <choose>
            <when test="local-name() = 'import'">
                <text>include</text>
            </when>
            <when test="contains(local-name(), '-')">
                <call-template name="replace">
                    <with-param name="input" select="local-name()" />
                    <with-param name="match" select="'-'" />
                    <with-param name="replace" select="'_'" />
                </call-template>
            </when>
            <otherwise>
                <value-of select="local-name()" />
            </otherwise>
        </choose>

        <text>(</text>

        <!-- process the XML attributes -->
        <for-each select="@*">
            <value-of select="local-name()" />
            <text>: '</text>
            <choose>
                <when test="local-name(..) = 'macrodef' and local-name() = 'name' and contains(., '-')">
                    <!-- try to handle macrodefs with a dashed name -->
                    <call-template name="replace">
                        <with-param name="input" select="." />
                        <with-param name="match" select="'-'" />
                        <with-param name="replace" select="'_'" />
                    </call-template>
                </when>
                <otherwise>
                    <call-template name="escape-quotes" />
                </otherwise>
            </choose>
            <text>'</text>
            <if test="position() != last()">
                <text>, </text>
            </if>
        </for-each>

        <!-- process the content in the XML element if not only spaces -->
        <if test="string-length(normalize-space(node())) != 0">
            <if test="count(@*) != 0">
                <text>, </text>
            </if>
            <choose>
                <!-- if there is some line break, let's triple quote -->
                <when test="contains(node(), '&#xa;')">
                    <text>'''</text>
                    <call-template name="escape-quotes">
                        <with-param name="text" select="node()" />
                    </call-template>
                    <text>'''</text>
                </when>
                <otherwise>
                    <text>'</text>
                    <call-template name="escape-quotes">
                        <with-param name="text" select="node()" />
                    </call-template>
                    <text>'</text>
                </otherwise>
            </choose>
        </if>

        <text>)</text>

        <!-- process the children by recursively -->
        <if test="count(child::*) != 0">
            <text> {</text>
            <apply-templates select="child::node()" />
            <text>}</text>
        </if>
    </template>

    <!-- escape simple quote to be output in attributes (always surrounded by simple quotes) -->
    <template name="escape-quotes">
        <param name="text" select="." />
        <choose>
            <!-- if there is a simple quote -->
            <when test='contains($text, "&apos;")'>
                <!-- put what is before -->
                <value-of select='substring-before($text, "&apos;")' />
                <!-- escape -->
                <text>\&apos;</text>
                <!-- recursively escape after -->
                <call-template name="escape-quotes">
                    <with-param name="text" select='substring-after($text, "&apos;")' />
                </call-template>
            </when>
            <!-- nothing to escape -->
            <otherwise>
	            <value-of select="$text" />
            </otherwise>
        </choose>
    </template>

    <!-- try to not loose XML comments -->
    <template match="comment()" >
        <text>/* </text><value-of select="." /><text> */</text>
    </template>

    <!-- here should be process only white spaces -->
    <template match="text()">
        <if test="string-length(normalize-space(.)) = 0">
            <!-- just output it to keep the same indentation -->
            <value-of select="." />
        </if>
    </template>

    <template name="replace">
        <param name="input" />
        <param name="match" />
        <param name="replace" />
        <if test="string-length(substring-before($input,$match)) =0">
            <value-of select="$input" />
        </if>
        <if test="string-length(substring-before($input,$match)) > 0">
            <value-of select="substring-before($input,$match)" />
            <value-of select="$replace" />
        </if>
        <if test="string-length(substring-after($input,$match)) > 0">
            <call-template name="replace">
                <with-param name="input" select="substring-after($input,$match)" />
                <with-param name="match" select="$match" />
                <with-param name="replace" select="$replace" />
            </call-template>
        </if>
    </template>

    <!-- try to be smart by avoiding a call anteval when it is not necessary -->
    <template name="anteval">
        <param name="value" />
        <choose>
            <when test="contains($value, '$')">
                <text>anteval(&apos;</text><value-of select="$value" /><text>&apos;)</text>
            </when>
            <otherwise>
                <text>&apos;</text><value-of select="$value" /><text>&apos;</text>
            </otherwise>
        </choose>
    </template>

    <!-- try to handle properly setting a ant property -->
    <template name="setprop">
        <param name="name" />
        <param name="value" />
        <choose>
            <when test="contains($name, '-') or contains($name, '.') ">
                <text>property(name: &apos;</text>
                <value-of select="$name" />
                <text>&apos;, value: &apos;</text>
                <value-of select="$value" />
                <text>&apos;)
</text>
            </when>
            <otherwise>
                <value-of select="$name" />
                <text> = </text>
                <call-template name="anteval">
                    <with-param name="value" select="$value" />
                </call-template>
            </otherwise>
        </choose>
    </template>
</stylesheet>
