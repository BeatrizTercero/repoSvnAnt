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
package org.apache.ivy.util.url;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.ivy.util.CopyProgressListener;


/**
 * This class is used to dispatch downloading requests
 * 
 * @author Xavier Hanin
 *
 */
public class URLHandlerDispatcher implements URLHandler {
    protected Map _handlers = new HashMap();
    protected URLHandler _default = new BasicURLHandler();

    public URLHandlerDispatcher() {
    }
    
    public boolean isReachable(URL url) {
        return getHandler(url.getProtocol()).isReachable(url);
    }
    
    public boolean isReachable(URL url, int timeout) {
        return getHandler(url.getProtocol()).isReachable(url, timeout);
    }
    
    public long getContentLength(URL url) {
        return getHandler(url.getProtocol()).getContentLength(url);
    }
    
    public long getContentLength(URL url, int timeout) {
        return getHandler(url.getProtocol()).getContentLength(url, timeout);
    }
    

    public long getLastModified(URL url) {
        return getHandler(url.getProtocol()).getLastModified(url);
    }

    public long getLastModified(URL url, int timeout) {
        return getHandler(url.getProtocol()).getLastModified(url, timeout);
    }

    public URLInfo getURLInfo(URL url) {
        return getHandler(url.getProtocol()).getURLInfo(url);
    }

    public URLInfo getURLInfo(URL url, int timeout) {
        return getHandler(url.getProtocol()).getURLInfo(url, timeout);
    }
    public InputStream openStream(URL url) throws IOException {
        return getHandler(url.getProtocol()).openStream(url);
    }
    
    public void download(URL src, File dest, CopyProgressListener l) throws IOException {
        getHandler(src.getProtocol()).download(src, dest, l);
    }
    
    public void setDownloader(String protocol, URLHandler downloader) {
        _handlers.put(protocol, downloader);
    }
    
    public URLHandler getHandler(String protocol) {
        URLHandler downloader = (URLHandler)_handlers.get(protocol);
        return downloader == null ? _default : downloader;
    }
    
    public URLHandler getDefault() {
        return _default;
    }
    public void setDefault(URLHandler default1) {
        _default = default1;
    }
}
