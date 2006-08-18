/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.ant.antunit;

import org.apache.tools.ant.Project;

/**
 * A test listener for &lt;antunit&gt;.
 */
public interface AntUnitListener {
    /**
     * Invoked once per build file, before any targets get executed.
     */
    void startTestSuite(Project testProject, String buildFile);
    /**
     * Invoked once per build file, after all targets have been executed.
     */
    void endTestSuite(Project testProject, String buildFile);
    /**
     * Invoked before a test target gets executed.
     */
    void startTest(String target);
    /**
     * Invoked after a test target has been executed.
     */
    void endTest(String target);
    /**
     * Invoked if an assert tasked caused an error during execution.
     */
    void addFailure(String target, AssertionFailedException ae);
    /**
     * Invoked if any error other than a failed assertion occured
     * during execution.
     */
    void addError(String target, Throwable ae);
}