/* 
 * Copyright  2000-2002,2004 Apache Software Foundation
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

package org.apache.tools.ant.taskdefs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.BuildException;

/**
 * Compresses a file with the GZIP algorithm. Normally used to compress
 * non-compressed archives such as TAR files.
 *
 * @author James Davidson <a href="mailto:duncan@x180.com">duncan@x180.com</a>
 * @author Jon S. Stevens <a href="mailto:jon@clearink.com">jon@clearink.com</a>
 * @author Magesh Umasankar
 *
 * @since Ant 1.1
 *
 * @ant.task category="packaging"
 */

public class GZip extends Pack {
    /**
     * perform the GZip compression operation.
     */
    protected void pack() {
        GZIPOutputStream zOut = null;
        try {
            zOut = new GZIPOutputStream(new FileOutputStream(zipFile));
            zipFile(source, zOut);
        } catch (IOException ioe) {
            String msg = "Problem creating gzip " + ioe.getMessage();
            throw new BuildException(msg, ioe, getLocation());
        } finally {
            if (zOut != null) {
                try {
                    // close up
                    zOut.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }
}
