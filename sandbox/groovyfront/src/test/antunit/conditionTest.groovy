/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
import groovy.xml.NamespaceBuilder

taskdef(uri: 'org.apache.ant.antunit', resource: 'org/apache/ant/antunit/antlib.xml')
def au = groovyns(prefix: 'au', uri: 'org.apache.ant.antunit')

target(name: 'testIsSet') {
    def myPropIsSet = isset(property: 'myprop')
    au.assertEquals(actual: false, expected: myPropIsSet)
    myprop = 'myvalue'
    myPropIsSet = isset(property: 'myprop')
    au.assertEquals(actual: true, expected: myPropIsSet)
}

target(name: 'testAvailable') {
    available(property: 'antavailable', classname: 'org.apache.tools.ant.Project')
    au.assertEquals(actual: true, expected: '${antavailable}')
    def a = available(classname: 'org.apache.tools.ant.Project')
    au.assertEquals(actual: true, expected: a)
}
