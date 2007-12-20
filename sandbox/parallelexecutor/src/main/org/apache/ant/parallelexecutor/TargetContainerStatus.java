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
package org.apache.ant.parallelexecutor;

/**
 * TargetContainer can be in the here defined states.
 * The transistion between them is:<pre>
 *   init --> WAITING
 *   WAITING --> STARTING
 *   STARTING --> CHECKING
 *   CHECKING --> RUNNING, PREREQUISITE_FAILED
 *   RUNNING --> SUCCESSED, FAILED
 *   SUCCESSED --> end
 *   FAILED --> end
 *   PREREQUISITE_FAILED --> end
 * </pre>
 */
public enum TargetContainerStatus {
    /* Waiting for starting by the Executor. */
    WAITING,
    /* This state is directly set by the executor for marking as 'not-waiting'. */
    STARTING,
    /* Checking prerequisites (depends, if, unless). */
    CHECKING,
    /* Target is currently running. */
    RUNNING,
    /* Target has finished without any error. */
    SUCCESSED,
    /* Target has thrown a BuildException. */
    FAILED,
    /* Target could not run because a prerequisite has not succeeded. */
    PREREQUISITE_FAILED;
    
    
    /**
     * Utility method for easier access to a set of states.
     * <pre>Finished = [SUCCESSED|FAILED|PREREQUISITE_FAILED]</pre>
     * @return computed state
     */
    public boolean hasFinished() {
        return this == SUCCESSED || this == FAILED || this == PREREQUISITE_FAILED;
    }
}
