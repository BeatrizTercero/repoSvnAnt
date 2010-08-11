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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://www.w3.org/1999/XSL/Transform.xsd" version="1.0">

    <xsl:output method="xml" indent="yes" />

    <xsl:template match="/antlib">
        <xsl:processing-instruction name="eclipse">version="3.4"</xsl:processing-instruction>
        <xsl:comment>
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
        </xsl:comment>
        <plugin>
            <xsl:apply-templates select="child::node()" />
            <extension point="org.eclipse.ant.core.extraClasspathEntries">
                <extraClasspathEntry eclipseRuntime="false" headless="true" library="ivy.jar" />
            </extension>
        </plugin>
    </xsl:template>

    <xsl:template match="taskdef">
        <extension point="org.eclipse.ant.core.antTasks">
            <antTask name="{@name}" class="{@classname}" eclipseRuntime="false" headless="true" library="ivy.jar" />
        </extension>
    </xsl:template>

    <xsl:template match="typedef">
        <extension point="org.eclipse.ant.core.antTypes">
            <antType name="{@name}" class="{@classname}" eclipseRuntime="false" headless="true" library="ivy.jar" />
        </extension> 
    </xsl:template>

</xsl:stylesheet>
