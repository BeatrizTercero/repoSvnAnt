/* 
 * Copyright  2001,2004 Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
package org.apache.tools.ant.taskdefs.optional.junit;

/**
 * <p> Interface groups XML constants.
 * Interface that groups all constants used throughout the <tt>XML</tt>
 * documents that are generated by the <tt>XMLJUnitResultFormatter</tt>
 * As of now the DTD is:
 * <code><pre>
 * <----------------- @todo describe DTDs ---------------------->
 *
 * </pre></code>
 * @author <a href="mailto:sbailliez@imediation.com">Stephane Bailliez</a>
 * @see XMLJUnitResultFormatter
 * @see XMLResultAggregator
 */
public interface XMLConstants {
    /** the testsuites element for the aggregate document */
    String TESTSUITES = "testsuites";

    /** the testsuite element */
    String TESTSUITE = "testsuite";

    /** the testcase element */
    String TESTCASE = "testcase";

    /** the error element */
    String ERROR = "error";

    /** the failure element */
    String FAILURE = "failure";

    /** the system-err element */
    String SYSTEM_ERR = "system-err";

    /** the system-out element */
    String SYSTEM_OUT = "system-out";

    /** package attribute for the aggregate document */
    String ATTR_PACKAGE = "package";

    /** name attribute for property, testcase and testsuite elements */
    String ATTR_NAME = "name";

    /** time attribute for testcase and testsuite elements */
    String ATTR_TIME = "time";

    /** errors attribute for testsuite elements */
    String ATTR_ERRORS = "errors";

    /** failures attribute for testsuite elements */
    String ATTR_FAILURES = "failures";

    /** tests attribute for testsuite elements */
    String ATTR_TESTS = "tests";

    /** type attribute for failure and error elements */
    String ATTR_TYPE = "type";

    /** message attribute for failure elements */
    String ATTR_MESSAGE = "message";

    /** the properties element */
    String PROPERTIES = "properties";

    /** the property element */
    String PROPERTY = "property";

    /** value attribute for property elements */
    String ATTR_VALUE = "value";

    /** classname attribute for testcase elements */
    String ATTR_CLASSNAME = "classname";

}
