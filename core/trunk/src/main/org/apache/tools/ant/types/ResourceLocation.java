/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.tools.ant.types;

import java.net.URL;

/**
 * <p>Helper class to handle the <code>&lt;dtd&gt;</code> and
 * <code>&lt;entity&gt;</code> nested elements.  These correspond to
 * the <code>PUBLIC</code> and <code>URI</code> catalog entry types,
 * respectively, as defined in the <a
 * href="http://oasis-open.org/committees/entity/spec-2001-08-06.html">
 * OASIS "Open Catalog" standard</a>.</p>
 *
 * <p>Possible Future Enhancements:
 * <ul>
 * <li>Bring the Ant element names into conformance with the OASIS standard</li>
 * <li>Add support for additional OASIS catalog entry types</li>
 * </ul>
 * </p>
 *
 * @see org.apache.xml.resolver.Catalog
 * @author Conor MacNeill
 * @author dIon Gillard
 * @author <a href="mailto:cstrong@arielpartners.com">Craeg Strong</a>
 * @version $Id$
 */

import java.net.URL;

public class ResourceLocation {

    //-- Fields ----------------------------------------------------------------

    /** 
     * name of the catalog entry type, as per OASIS spec.
     */
    protected String name = null;

    /** publicId of the dtd/entity. */
    private String publicId = null;

    /** location of the dtd/entity - a file/resource/URL. */
    private String location = null;

    /** 
     * base URL of the dtd/entity, or null. If null, the Ant project
     * basedir is assumed.  If the location specifies a relative
     * URL/pathname, it is resolved using the base.  The default base
     * for an external catalog file is the directory in which it is
     * located.
     */
    private URL base = null;

    //-- Methods ---------------------------------------------------------------

    /**
     * @param publicId uniquely identifies the resource.
     */
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    /**
     * @param location the location of the resource associated with the
     *      publicId.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @param base the base URL of the resource associated with the
     * publicId.  If the location specifies a relative URL/pathname,
     * it is resolved using the base.  The default base for an
     * external catalog file is the directory in which it is located.
     */
    public void setBase(URL base) {
        this.base = base;
    }

    /**
     * @return the publicId of the resource.
     */
    public String getPublicId() {
        return publicId;
    }

    /**
     * @return the location of the resource identified by the publicId.
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the base of the resource identified by the publicId.
     */
    public URL getBase() {
        return base;
    }

} //-- ResourceLocation
