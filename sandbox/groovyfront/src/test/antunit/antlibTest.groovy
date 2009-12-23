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

target(name: 'testWithNamespace') {
    taskdef(uri: 'org.apache.ant.groovyfront.test', resource: 'org/apache/ant/groovyfront/antlib.xml')
    au.assertFalse(message: 'property "myprop" is not expected to be set') {
        isset(property: 'myprop')
    }
    def test = NamespaceBuilder.newInstance([ 'test': 'org.apache.ant.groovyfront.test' ], project.references['groovyfront.builder'])
    test.'test:setprop'(n: 'myprop', v: 'foo')
    au.assertPropertyEquals(name: 'myprop', value: 'foo')
}

target(name: 'testGroovyns') {
    taskdef(uri: 'org.apache.ant.groovyfront.test', resource: 'org/apache/ant/groovyfront/antlib.xml')
    def test = groovyns(prefix: 'test', uri: 'org.apache.ant.groovyfront.test')
    au.assertFalse(message: 'property "myprop" is not expected to be set') {
        isset(property: 'myprop')
    }
    test.setprop(n: 'myprop', v: 'bar')
    au.assertPropertyEquals(name: 'myprop', value: 'bar')
}

target(name: 'testGroovynsNoPrefix') {
    taskdef(uri: 'org.apache.ant.groovyfront.test', resource: 'org/apache/ant/groovyfront/antlib.xml')
    def test = groovyns(uri: 'org.apache.ant.groovyfront.test')
    au.assertFalse(message: 'property "myprop" is not expected to be set') {
        isset(property: 'myprop')
    }
    test.setprop(n: 'myprop', v: 'bar')
    au.assertPropertyEquals(name: 'myprop', value: 'bar')
}

target(name: 'testNoNamespace') {
    taskdef(resource: 'org/apache/ant/groovyfront/antlib.xml')
    au.assertFalse(message: 'property "myprop" is not expected to be set') {
        isset(property: 'myprop')
    }
    setprop(n: 'myprop', v: 'foo')
    au.assertPropertyEquals(name: 'myprop', value: 'foo')
}
