/*
 * Copyright  2000-2004 Apache Software Foundation
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
package org.apache.tools.ant.taskdefs.optional.depend;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A class file iterator which iterates through the contents of a Java jar
 * file.
 *
 * @author Conor MacNeill
 */
public class JarFileIterator implements ClassFileIterator {
    /** The jar stream from the jar file being iterated over*/
    private ZipInputStream jarStream;

    /**
     * Construct an iterator over a jar stream
     *
     * @param stream the basic input stream from which the Jar is received
     * @exception IOException if the jar stream cannot be created
     */
    public JarFileIterator(InputStream stream) throws IOException {
        super();

        jarStream = new ZipInputStream(stream);
    }

    /**
     * Get the next ClassFile object from the jar
     *
     * @return a ClassFile object describing the class from the jar
     */
    public ClassFile getNextClassFile() {
        ZipEntry jarEntry;
        ClassFile nextElement = null;

        try {
            jarEntry = jarStream.getNextEntry();

            while (nextElement == null && jarEntry != null) {
                String entryName = jarEntry.getName();

                if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {

                    // create a data input stream from the jar input stream
                    ClassFile javaClass = new ClassFile();

                    javaClass.read(jarStream);

                    nextElement = javaClass;
                } else {

                    jarEntry = jarStream.getNextEntry();
                }
            }
        } catch (IOException e) {
            String message = e.getMessage();
            String text = e.getClass().getName();

            if (message != null) {
                text += ": " + message;
            }

            throw new RuntimeException("Problem reading JAR file: " + text);
        }

        return nextElement;
    }

}

