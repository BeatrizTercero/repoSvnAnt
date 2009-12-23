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

taskdef(uri: 'org.apache.ant.antunit', resource: 'org/apache/ant/antunit/antlib.xml')
def au = groovyns(uri: 'org.apache.ant.antunit')

target(name: 'setUp') {
    foo = 'foo'
}

target(name: 'tearDown') {
    delete(file: '${foo}', quiet: 'true')
    delete(file: 'file.txt', quiet: 'true')
}

// a test that shows things run
target(name: 'testSimple') {
    au.assertFileDoesntExist(file: '${foo}')
    touch(file: '${foo}')
    au.assertFileExists(file: '${foo}')
}

target(name: 'testIfThenElse') {
    touch(file: 'file.txt')
    condition(property: 'fileExist', value: 'true', else: 'false') {
        available(file: 'file.txt')
    }
    if (fileExist) {
        au.assertPropertyEquals(name: 'fileExist', value: 'true')
    } else {
        au.testFail(message: 'the if branching of "fileExist" failed')
    }
    if (!fileExist) {
        au.testFail(message: 'the else branching of "fileExist" failed')
    } else {
        au.assertPropertyEquals(name: 'fileExist', value: 'true')
    }

    condition(property: 'fileExistNoAlternative') {
        available(file: 'file.txt')
    }
    if (project.properties['fileExistNoAlternative'] != null && fileExistNoAlternative) {
        au.assertPropertyEquals(name: 'fileExistNoAlternative', value: 'true')
    } else {
        au.testFail(message: 'the if branching of "fileExistNoAlternative" failed')
    }
    if (project.properties['fileExistNoAlternative'] == null || !fileExistNoAlternative) {
        au.testFail(message: 'the else branching of "fileExistNoAlternative" failed')
    } else {
        au.assertPropertyEquals(name: 'fileExistNoAlternative', value: 'true')
    }

    condition(property: 'fileNotExistNoAlternative') {
        not {
            available(file: 'file.txt')
        }
    }
    if (project.properties['fileNotExistNoAlternative'] != null && fileNotExistNoAlternative) {
        au.testFail(message: 'the else branching of "fileNotExistNoAlternative" failed')
    } else {
        au.assertTrue() {
            not() {
                isset(property: 'fileNotExistNoAlternative')
            }
        }
    }
    if (project.properties['fileNotExistNoAlternative'] == null || !fileNotExistNoAlternative) {
        au.assertTrue() {
            not() {
                isset(property: 'fileNotExistNoAlternative')
            }
        }
    } else {
        au.testFail(message: 'the if branching of "fileNotExistNoAlternative" failed')
    }
}

target(name: 'testSetProperty') {
    au.assertFalse(message: 'property "bar" is not expected to be set') {
        isset(property: 'bar')
    }
    bar = '3'
    au.assertPropertySet(name: 'bar')
    au.assertPropertyEquals(name: 'bar', value: '3')
    bar = '4'
    au.assertPropertyEquals(name: 'bar', value: '3')
}

target(name: 'testGString') {
    propTestGString = 'foo'
    // property resolved by Groovy
    au.assertEquals(expected: 'foo', actual: "${propTestGString}")
    // property resolved by Ant
    au.assertEquals(expected: 'foo', actual: '${propTestGString}')
    property(name: 'prop.with.dot', value: 'bar')
    // property resolved by Ant (impossible with Groovy)
    au.assertEquals(expected: 'bar', actual: '${prop.with.dot}')
}

target(name: 'testSetPropertyNonString') {
    propInt = 3
    au.assertPropertyEquals(name: 'propInt', value: '3')
    propFloat = 1.2
    au.assertPropertyEquals(name: 'propFloat', value: '1.2')
    propClosure = { 1 }
    // the value should be the result of Object#toString()
    au.assertPropertyContains(name: 'propClosure', value: 'varMappingTest$$_run_closure')
}

target(name: 'testSetPropertyTask') {
    echo = '3'
    property(name: 'propEcho', value: '${echo}')
    au.assertEquals(expected: '3', actual: '${propEcho}')
    // check that echoing works
    echo(message: '${echo}')

    condition = { true }
    au.assertPropertyContains(name: 'condition', value: 'varMappingTest$$_run_closure')
    // check that checking a condition still works
    condition(property: 'propCondition') {
        isset(property: 'propEcho')
    }
    au.assertEquals(expected: 'true', actual: '${propCondition}')

    // check that reading the property works
    echo2 = echo
    au.assertEquals(expected: '3', actual: '${echo2}')
    condition2 = condition
    au.assertPropertyContains(name: 'condition2', value: 'varMappingTest$$_run_closure')
}

target(name: 'testSetPropertyProject') {
    def fail = false
    try {
        project = '3'
    } catch (Throwable t) {
        fail = true
    }
    au.assertEquals(message: 'Setting "project" should fail', expected: 'true', actual: fail)
}

target(name: 'testSetPropertyAndReference') {
    au.assertTrue(message: 'property and reference "filesetRef" are not expected to be set') {
        and() {
            not() {
                isset(property: 'filesetRef')
            }
            not() {
                isreference(refid: 'filesetRef')
            }
        }
    }
    fileset(id: 'filesetRef', dir: '${basedir}')
    au.assertTrue(message: 'reference "filesetRef" is expected to be set') {
        isreference(refid: 'filesetRef')
    }
    filesetRef = '3'
    au.assertPropertyEquals(name: 'filesetRef', value: '3')
}

target(name: 'testLocalVariable') {
    def add = { n -> n + 1 }
    au.assertEquals(expected: '2', actual: add(1))

    def setProp = { n, v ->
        property(name: n, value: v)
    }
    au.assertFalse(message: 'property "propFunction" is not expected to be set') {
        isset(property: 'propFunction')
    }
    setProp('propFunction', 'foo')
    au.assertPropertyEquals(name: 'propFunction', value: 'foo')

    // reference shouldn't override tasks
    project.addReference('zip', { n -> n })
    def fail = false
    try {
        zip(3)
    } catch (Throwable t) {
        fail = true
    }
    au.assertEquals(message: 'Calling zip should have failed', expected: 'true', actual: fail)

    // local variable override tasks
    def zip = { n -> n }
    au.assertEquals(expected: '3', actual: zip(3))

    def echo = '3'
    fail = false
    try {
        echo(message: 'some message')
    } catch (Throwable t) {
        fail = true
    }
    au.assertEquals(message: 'Calling echo should have failed', expected: 'true', actual: fail)
}
