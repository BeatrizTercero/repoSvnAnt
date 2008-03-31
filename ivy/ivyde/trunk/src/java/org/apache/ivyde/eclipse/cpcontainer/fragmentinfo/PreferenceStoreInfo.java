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
package org.apache.ivyde.eclipse.cpcontainer.fragmentinfo;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceStoreInfo implements IPackageFragmentExtraInfo {
    private static final String SRC_SUFFIX = "-src";

    private static final String SRCROOT_SUFFIX = "-srcroot";

    private static final String DOC_SUFFIX = "-doc";

    private IPreferenceStore _preferenceStore;

    public PreferenceStoreInfo(IPreferenceStore preferenceStore) {
        _preferenceStore = preferenceStore;
    }

    public IPath getSourceAttachment(Path path) {
        String srcPath = _preferenceStore.getString(path.toPortableString() + SRC_SUFFIX);
        if (!"".equals(srcPath)) {
            return new Path(srcPath);
        }
        return null;
    }

    public IPath getSourceAttachmentRoot(Path path) {
        String srcPath = _preferenceStore.getString(path.toPortableString() + SRCROOT_SUFFIX);
        if (!"".equals(srcPath)) {
            return new Path(srcPath);
        }
        return null;
    }

    public URL getDocAttachment(Path path) {
        String srcPath = _preferenceStore.getString(path.toPortableString() + DOC_SUFFIX);
        if (!"".equals(srcPath)) {
            try {
                return new URL(srcPath);
            } catch (MalformedURLException e) {
                return null;
            }
        }
        return null;
    }

    public void setSourceAttachmentPath(IPath containerPath, String entryPath, IPath sourcePath) {
        _preferenceStore.setValue(entryPath + SRC_SUFFIX, sourcePath == null ? "" : sourcePath
                .toPortableString());
    }

    public void setSourceAttachmentRootPath(IPath containerPath, String entryPath, IPath rootPath) {
        _preferenceStore.setValue(entryPath + SRCROOT_SUFFIX, rootPath == null ? "" : rootPath
                .toPortableString());
    }

    public void setJavaDocLocation(IPath containerPath, String entryPath, URL libraryJavadocLocation) {
        _preferenceStore.setValue(entryPath + DOC_SUFFIX, libraryJavadocLocation == null ? ""
                : libraryJavadocLocation.toString());
    }
}
