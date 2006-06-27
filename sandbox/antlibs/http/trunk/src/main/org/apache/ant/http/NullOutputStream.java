/*
 * Copyright  2001-2006 The Apache Software Foundation
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

package org.apache.ant.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * simple output stream which discards all write requests. This should really be
 * part of java.io, as it is sporadically invaluable
 *
 * @created March 17, 2001
 */
public final class NullOutputStream extends OutputStream {

    /**
     * discard all incoming bytes
     *
     * @param b byte to write
     * @throws IOException never throwable in this subclass
     */
    public void write(int b)
            throws IOException {
    }


    /**
     * discard all incoming bytes
     *
     * @param b byte array
     * @throws IOException never throwable in this subclass
     */
    public void write(byte[] b)
            throws IOException {
    }


    /**
     * discard all incoming bytes
     *
     * @param b   byte array
     * @param off starting offset
     * @param len length to write
     * @throws IOException never throwable in this subclass
     */
    public void write(byte[] b,
                      int off,
                      int len)
            throws IOException {
    }

}


