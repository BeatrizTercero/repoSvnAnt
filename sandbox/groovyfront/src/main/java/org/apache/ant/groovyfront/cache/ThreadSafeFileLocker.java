package org.apache.ant.groovyfront.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link FileLock} are great to deal with synchronization between jvms, but
 * within a single jvm some {@link OverlappingFileLockException} is raised if a
 * file lock is tried to be acquired several times on the same file. So the file
 * lock is "surrounded" with a sort of intra jvm memory lock.
 */
public class ThreadSafeFileLocker {

    private static final String LOCK_FILENAME = ".lock";

    // static because the locking should be done on the entire jvm
    private static final Map lockedFiles = new HashMap();

    public static FileLock tryLock(File f) throws FileNotFoundException,
            IOException {
        String absolutePath = f.getAbsolutePath();
        RandomAccessFile lockedFile;
        synchronized (lockedFiles) {
            lockedFile = (RandomAccessFile) lockedFiles.get(absolutePath);
            if (lockedFile != null) {
                // the file is already locked by this jvm, or being tried to be
                // locked. Either way, we won't have the file lock
                return null;
            }
            lockedFile = new RandomAccessFile(f, "rw");
            lockedFiles.put(absolutePath, lockedFile);
        }
        FileLock lock = null;
        try {
            lock = lockedFile.getChannel().tryLock();
        } finally {
            if (lock == null) {
                // we didn't get the lock
                synchronized (lockedFiles) {
                    lockedFiles.remove(absolutePath);
                }
            }
        }
        return lock;
    }

    public static FileLock lock(File f) throws FileNotFoundException,
            IOException {
        String absolutePath = f.getAbsolutePath();
        RandomAccessFile lockedFile;
        synchronized (lockedFiles) {
            lockedFile = (RandomAccessFile) lockedFiles.get(absolutePath);
            if (lockedFile != null) {
                // FIXME here we should somehow wait and retry
                return null;
            }
            lockedFile = new RandomAccessFile(f, "rw");
            lockedFiles.put(absolutePath, lockedFile);
        }
        FileLock lock = null;
        try {
            lock = lockedFile.getChannel().lock();
        } finally {
            if (lock == null) {
                // we didn't get the lock
                synchronized (lockedFiles) {
                    lockedFiles.remove(absolutePath);
                }
            }
        }
        return lock;
    }

    public static void release(File f, FileLock lock) {
        String absolutePath = f.getAbsolutePath();
        RandomAccessFile lockedFile = (RandomAccessFile) lockedFiles
                .get(absolutePath);
        if (lockedFile == null) {
            throw new IllegalStateException(
                    "The file "
                            + f
                            + " should have been sucessfully locked before unlocking it");
        }
        try {
            lock.release();
        } catch (IOException e) {
            // don't care
        } finally {
            // release the file and the jvm "lock"
            try {
                lockedFile.close();
            } catch (IOException e) {
                // don't care
            } finally {
                synchronized (lockedFiles) {
                    lockedFiles.remove(absolutePath);
                }
            }
        }
    }

    public static FileLock tryLockDir(File dir) throws FileNotFoundException,
            IOException {
        return tryLock(new File(dir, LOCK_FILENAME));
    }

    public static FileLock lockDir(File dir) throws FileNotFoundException,
            IOException {
        return lock(new File(dir, LOCK_FILENAME));
    }

    public static void releaseDir(File dir, FileLock lock) {
        release(new File(dir, LOCK_FILENAME), lock);
    }
}
