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

import static org.apache.ant.parallelexecutor.TargetContainerStatus.CHECKING;
import static org.apache.ant.parallelexecutor.TargetContainerStatus.FAILED;
import static org.apache.ant.parallelexecutor.TargetContainerStatus.PREREQUISITE_FAILED;
import static org.apache.ant.parallelexecutor.TargetContainerStatus.RUNNING;
import static org.apache.ant.parallelexecutor.TargetContainerStatus.STARTING;
import static org.apache.ant.parallelexecutor.TargetContainerStatus.SUCCESSED;
import static org.apache.ant.parallelexecutor.TargetContainerStatus.WAITING;

import java.util.Enumeration;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Target;


/**
 * The TargetContainer wrapps a target for the use in a multithreaded environment.
 * It provides the "management methods" needed by the ParallelExecutor.
 */
public class TargetContainer implements Runnable {
    
    /** Current status of the target. */
    private TargetContainerStatus currentStatus = WAITING;
    
    /** Wrapped target. */
    private Target target;
    
    /** Caught exception if the target throws one. */
    private BuildException caughtBuildExeption;
    
    /** The calling ParallelExecutor to inform on finished works. */
    private ParallelExecutor caller;

    
    /**
     * Constructor.
     * @param target target to wrap
     * @param caller calling exectuor for call back
     */
    public TargetContainer(Target target, ParallelExecutor caller) {
        this.target = target;
        this.caller = caller;
        setCurrentStatus(WAITING);
    }

    
    /**
     * Gets the current status of the TargetContainer.
     * @return status
     */
    public TargetContainerStatus getCurrentStatus() {
        return currentStatus;
    }

    
    /**
     * Sets the current status of the TargetContainer.
     * @param currentStatus new status
     */
    private void setCurrentStatus(TargetContainerStatus currentStatus) {
        synchronized (this.currentStatus) {
            this.currentStatus = currentStatus;
        }
    }


    /**
     * Called by the {@link ParallelExecutor} if the target should try
     * to start.
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        setCurrentStatus(CHECKING);
        TargetContainerStatus statusDepends = checkDependentTargets();
        if (statusDepends == SUCCESSED) {
            if (evaluateIf() && !evaluateUnless()) {
                setCurrentStatus(RUNNING);
                try {
                    target.execute();
                    setCurrentStatus(SUCCESSED);
                } catch (BuildException be) {
                    // Don't handle the be, just store it for handling by the executor.
                    caughtBuildExeption = be;
                    setCurrentStatus(FAILED);
                }
            } else {
                // 'if' or 'unless' attributes failed
                setCurrentStatus(PREREQUISITE_FAILED);
            }
        } else {
            // Finishing the run method stops the thread. Status here is WAITING or PREFAILED
            setCurrentStatus(statusDepends);
        }
        caller.targetFinished(this);
    }
    

    /**
     * Checks the result of the dependend targets. 
     * @return <tt>SUCCESSED</tt> if <b>all</b> targets finished successfully,
     *         <tt>PREREQUISITE_FAILED</tt> if one or more failed or
     *         <tt>WAITING</tt> otherwise. 
     */
    @SuppressWarnings("unchecked")
    private TargetContainerStatus checkDependentTargets() {
        for (Enumeration deps = target.getDependencies(); deps.hasMoreElements();) {
            TargetContainerStatus status = caller.getStatus((String) deps.nextElement());
            if (status == FAILED || status == PREREQUISITE_FAILED) {
                return PREREQUISITE_FAILED;
            }
            if (status != SUCCESSED) {
                return WAITING;
            }
        }
        return SUCCESSED; 
    }


    /**
     * Checks if the property specified as <tt>unless</tt> is set.
     * @return <tt>true</tt> if set, <tt>false</tt> otherwise
     */
    private boolean evaluateUnless() {
        return target.getProject().getProperty(target.getUnless()) != null;
    }


    /**
     * Checks if the property specified as <tt>if</tt> is set.
     * @return <tt>true</tt> if set, <tt>false</tt> otherwise
     */
    private boolean evaluateIf() {
        String ifName = target.getIf();
        return ifName == null || target.getProject().getProperty(ifName) != null;
    }


    /**
     * Gets the name of the target.
     * @return target name
     * @see Target#getName()
     */
    public String getName() {
        return target.getName();
    }

    
    /**
     * Returns the caught exception thrown by the target. 
     * @return the exception or <tt>null</tt>
     */
    public BuildException getBuildExeption() {
        return caughtBuildExeption;
    }

    
    /**
     * Marks this container as starting.
     */
    public void start(){
        setCurrentStatus(STARTING);
    }
    
}
