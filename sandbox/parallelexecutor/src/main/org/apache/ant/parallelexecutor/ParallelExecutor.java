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

import static org.apache.ant.parallelexecutor.TargetContainerStatus.WAITING;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Executor;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;

/** 
 * <p>This executors parallelizes the exution of Ant targets.
 * Each target will run in its own thread. That thread will be started
 * if all dependend targets are finished, the if/unless attributes are
 * evaluated and no dependend target failed. When a TargetContainer finishes
 * it calls this Executors <tt>targetFinished</tt> so that the Executor could
 * restart all the waiting threads.</p>
 * <p>This executor is used via Ants magic property <tt>ant.executor.class</tt></p>
 * <pre>
 *   ant 
 *     -Dant.executor.class=org.apache.ant.parallelexecutor.ParallelExecutor
 *     -lib pathToJarContainingTheExecutor
 * </pre>
 */
public class ParallelExecutor implements Executor {
    
    /** 
     * Default value for waiting for shutting down the ExecutorService.
     * @see ExecutorService#awaitTermination(long, TimeUnit)
     * @see #EXECUTOR_SERVICE_SHUTDOWN_TIME_UNIT
     */
    public static final long EXECUTOR_SERVICE_SHUTDOWN_TIME_VALUE = 10;

    /**
     * TimeUnit for the shutdown time.
     */
    public static final TimeUnit EXECUTOR_SERVICE_SHUTDOWN_TIME_UNIT = TimeUnit.MINUTES;
    
    
    /**
     * Targets which should run, wrapped by TargetContainers for threading and monitoring.
     */
    private Set<TargetContainer> targetsToProcess;

    
    /**
     * ExecutorService for Thread-creation.
     */
    private ExecutorService executorService;


    /**
     * Entry-point defined in the Executor interface.
     * Initializes this Executor and starts the threads.
     * @param project Ant-project which hold the targets
     * @param targetNames target names as specified on the command line to execute 
     * @see org.apache.tools.ant.Executor#executeTargets(org.apache.tools.ant.Project, java.lang.String[])
     */
    @Override
    public void executeTargets(Project project, String[] targetNames) throws BuildException {
        targetsToProcess = getTargetsToProcess(project, targetNames);
        executorService = java.util.concurrent.Executors.newCachedThreadPool();
        startWaitingContainers();
        while (unfinishedTargets()) {
            sleep(50);
        }
        sleep(100);
        buildFinished();
    }


    /**
     * Lets the current thread sleep for a given time.
     * @param timeMS sleep time in milliseconds
     */
    protected void sleep(long timeMS) {
        try {
            Thread.sleep(timeMS);
        } catch (InterruptedException e) {
            // no-op
        }
    }
    
    
    /**
     * Initializes the list of TargetContainers with all targets which should be started.
     * @param project      project containing the targets
     * @param targetNames  list of the targets to start
     * @return             list of TargetContainers for these targets
     */
    private Set<TargetContainer> getTargetsToProcess(Project project, String[] targetNames) {
        Set<TargetContainer> rv = new HashSet<TargetContainer>();
        // iterate over all required targets - including dependent targets
        for (Object targetName : project.topoSort(targetNames, project.getTargets(), false)) {
            // must be a String - Objects are not equal while iterating
            String key = targetName.toString();
            Target target = (Target)project.getTargets().get(key);
            TargetContainer container = new TargetContainer(target, this);
            rv.add(container);
        }
        return rv;
    } 
    
    
    /**
     * not used
     * @see org.apache.tools.ant.Executor#getSubProjectExecutor()
     */
    public Executor getSubProjectExecutor() {
        return null;
    }
    
    
    /**
     * Starts all waiting TargetContainers.
     * @return <tt>true</tt> if one or more containers were be started, <tt>false</tt> otherwise.
     */
    private boolean startWaitingContainers() {
        boolean hasStartedAContainer = false;
        for (TargetContainer container : targetsToProcess) {
            if (container.getCurrentStatus() == WAITING) {
                container.start();
                executorService.execute(container);
                hasStartedAContainer = true;
            }
        }
        return hasStartedAContainer;
    }


    /**
     * Call-back method for finishing TargetContainers.
     * 
     * @param container
     *            TargetContainer which finished.
     */
    public void targetFinished(TargetContainer container) {
        if (unfinishedTargets()) {
            startWaitingContainers();
        }
    }


    /**
     * Checks if there are targets which haven't finished.
     * @return result of the check
     */
    protected synchronized boolean unfinishedTargets() {
        for (TargetContainer container : targetsToProcess) {
            if (!container.getCurrentStatus().hasFinished()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Collects all the results from the different targets.
     */
    private void buildFinished() {
        // we have already checked that all started thread have completed
        executorService.shutdown();

        // Checks the targets for BuildExceptions and their state.
        List<BuildException> thrownExceptions = new ArrayList<BuildException>();
        for (TargetContainer container : targetsToProcess) {
            if (container.getBuildExeption()!=null) { 
                thrownExceptions.add(container.getBuildExeption());
            }
        }
        
        // throw BuildExceptions if needed
        throwCaughtExceptions(thrownExceptions);
    }


    /**
     * Checks the given list of BuildExceptions and <ol>
     * <li>throws a composite BuildException with the information provided by that list</li>
     * <li>throws the BuildException if the list does contain only one exception</li>
     * <li>does not throw any Exception if the list is empty</li>
     * </ul>
     * @param thrownExceptions list of caught exceptions
     */
    private void throwCaughtExceptions(List<BuildException> thrownExceptions) {
        if (thrownExceptions.isEmpty()) {
            return;
        }
        if (thrownExceptions.size() == 1) {
            throw thrownExceptions.get(0);
        }
        // Collect all BEs into one new 
        StringBuilder sb = new StringBuilder();
        sb.append("Multiple BuildExceptions occured:")
          .append(System.getProperty("line.separator"));
        for (BuildException be : thrownExceptions) {
            sb.append("\t")
              .append(be.getLocation())
              .append(" : ")
              .append(be.getMessage())
              .append(System.getProperty("line.separator"));
        }
        throw new BuildException(sb.toString());
    }


    /**
     * Returns the current status for a given target.
     * @param depName name of the target
     * @return status of that target
     */
    public TargetContainerStatus getStatus(String depName) {
        return getContainer(depName).getCurrentStatus();
    }
    
    
    /**
     * Returns a TargetContainer by its name.
     * @param targetName name of the target to look for
     * @return the target container wrapping that target
     */
    private TargetContainer getContainer(String targetName) {
        for (TargetContainer container : targetsToProcess) {
            if (container.getName().equals(targetName)) {
                return container;
            }
        }
        return null;
    }
    
}
