/*
 * Copyright  2004 The Apache Software Foundation
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
package org.apache.tools.ant;

/**
 * Enables a task to control unknown attributes and elements.
 *
 * @since Ant 1.7
 */
public interface DynamicConfiguratorNS {

    /**
     * Set a named attribute to the given value
     *
     * @param uri The namespace uri for this attribute, "" is
     *            used if there is no namespace uri.
     * @param localName The localname of this attribute.
     * @param qName The qualified name for this attribute
     * @param value The value of this attribute.
     * @throws BuildException when any error occurs
     */
    void setDynamicAttribute(
        String uri, String localName, String qName, String value)
            throws BuildException;

    /**
     * Create an element with the given name
     *
     * @param uri The namespace uri for this attribute.
     * @param localName The localname of this attribute.
     * @param qName The qualified name for this element.
     * @throws BuildException when any error occurs
     * @return the element created for this element.
     */
    Object createDynamicElement(
        String uri, String localName, String qName) throws BuildException;
}
