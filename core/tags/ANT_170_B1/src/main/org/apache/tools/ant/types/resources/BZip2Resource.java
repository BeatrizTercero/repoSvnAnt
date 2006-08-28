/*
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tools.ant.types.resources;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.bzip2.CBZip2OutputStream;

/**
 * A Bzip2 compressed resource.
 *
 * <p>Wraps around another resource, delegates all quries to that
 * other resource but uncompresses/compresses streams on the fly.</p>
 *
 * @since Ant 1.7
 */
public class BZip2Resource extends CompressedResource {
    private static final char[] MAGIC = new char[] {'B', 'Z'};

    public BZip2Resource() {
    }

    public BZip2Resource(org.apache.tools.ant.types.ResourceCollection other) {
        super(other);
    }

    protected InputStream wrapStream(InputStream in) throws IOException {
        for (int i = 0; i < MAGIC.length; i++) {
            if (in.read() != MAGIC[i]) {
                throw new IOException("Invalid bz2 stream.");
            }
        }
        return new CBZip2InputStream(in);
    }
    protected OutputStream wrapStream(OutputStream out) throws IOException {
        for (int i = 0; i < MAGIC.length; i++) {
            out.write(MAGIC[i]);
        }
        return new CBZip2OutputStream(out);
    }
    protected String getCompressionName() {
        return "Bzip2";
    }
}