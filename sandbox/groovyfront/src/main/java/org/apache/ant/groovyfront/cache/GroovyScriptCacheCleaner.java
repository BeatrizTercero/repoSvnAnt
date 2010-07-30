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
package org.apache.ant.groovyfront.cache;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.tools.ant.Project;

/**
 * Clean old entries in the cache, and don't keep more than some amount of them.
 */
public class GroovyScriptCacheCleaner implements Runnable {

    private static volatile Thread cleanerThread;

    private static volatile long lastLaunched = 0;

    private int maxToKeep = 1000;

    // default is one month
    private long timetolive = 1000 * 60 * 60 * 24 * 31;

    private final Project project;

    private final File cacheDir;

    public GroovyScriptCacheCleaner(Project project, File cacheDir) {
        this.project = project;
        this.cacheDir = cacheDir;
    }

    public void setMaxToKeep(int maxToKeep) {
        this.maxToKeep = maxToKeep;
    }

    public void setTimetolive(long timetolive) {
        this.timetolive = timetolive;
    }

    public void run() {
        try {
            clean();
        } catch (Throwable t) {
            project.log("Some errors occurs while cleaning the cache", t,
                    Project.MSG_WARN);
        } finally {
            cleanerThread = null;
        }
    }

    private void clean() {
        int max = maxToKeep;
        final long time = System.currentTimeMillis() - timetolive;

        if (!cacheDir.exists() || !cacheDir.isDirectory()) {
            // not a proper cache directory: abort
            return;
        }

        // get cache entries which are older enough
        File[] cacheEntries = cacheDir.listFiles(new FileFilter() {
            public boolean accept(File f) {
                if (!f.isDirectory()) {
                    return false;
                }
                long modified = getLastReadTimestamp(f);
                if (modified == 0 || modified > time) {
                    return false;
                }
                return true;
            }
        });

        if (cacheEntries.length < max) {
            // not reaching max
            return;
        }

        // order by time of last read
        Arrays.sort(cacheEntries, new Comparator() {
            public int compare(Object dir1, Object dir2) {
                long modified1 = getLastReadTimestamp((File) dir1);
                long modified2 = getLastReadTimestamp((File) dir1);
                long diff = modified2 - modified1;
                return diff > 0 ? 1 : diff < 0 ? -1 : 0;
            }
        });

        for (int i = 0; i < cacheEntries.length - max; i++) {
            delete(cacheEntries[i]);
        }
    }

    private void delete(File dir) {
        FileLock lock;
        try {
            lock = ThreadSafeFileLocker.tryLock(dir);
        } catch (IOException e) {
            // no any issue, just skip that delete
            return;
        }
        if (lock == null) {
            // cache entry being read, skip the delete
            return;
        }
        try {
            CachedGroovyScriptLoader.cleanCacheDir(dir);
        } catch (IOException e) {
            project.log(
                    "Some groovy script cache entries might not have been deleted correctly in "
                            + dir.getAbsolutePath(), e, Project.MSG_WARN);
        } finally {
            ThreadSafeFileLocker.release(dir, lock);
        }
    }

    /**
     * Get the timestamp of the last read of that folder. It correspond to the
     * timestamp of the file containing the original build file timestamp.
     * 
     * @param dir
     * @return
     */
    private long getLastReadTimestamp(File dir) {
        File timestampFile = new File(dir,
                CachedGroovyScriptLoader.TIMESTAMP_FILE);
        return timestampFile.lastModified();
    }

    public static synchronized void launchClean(Project project, File cacheDir,
            long minCleanerPeriod, Long timetolive, Integer maxToKeep) {
        if (cleanerThread == null
                && lastLaunched < System.currentTimeMillis() - minCleanerPeriod) {
            lastLaunched = System.currentTimeMillis();
            GroovyScriptCacheCleaner cleaner = new GroovyScriptCacheCleaner(
                    project, cacheDir);
            if (timetolive != null) {
                cleaner.setTimetolive(timetolive.longValue());
            }
            if (maxToKeep != null) {
                cleaner.setMaxToKeep(maxToKeep.intValue());
            }
            cleanerThread = new Thread(cleaner);
            cleanerThread.setPriority(Thread.MIN_PRIORITY);
            cleanerThread.setDaemon(true);
            cleanerThread.run();
        }
    }
}
