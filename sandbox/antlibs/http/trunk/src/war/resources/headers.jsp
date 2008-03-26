<%@ page import="java.util.Enumeration" %>
<!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->
<%--
    List all headers in the message.
    If you post a header with HTML or javascript in it wont be escaped, which
    is a potential XSS security hole. Not for use on public systems 
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    //minimal escaping of angle braces.
    String escape(String source) {
        String s1 = source.replace("<", "&lt;");
        String s2 = source.replace(">", "&gt;");
        return s2;
    }

%>
<html>
<head><title>Headers</title></head>

<body>
<h1>Headers</h1>
<ol>
<%
    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
        String name = (String) headerNames.nextElement();
        Enumeration values = request.getHeaders(name);
        while (values.hasMoreElements()) {
            String value = (String) values.nextElement();
            out.print(escape(name));
            out.print('=');
            out.println(escape(value));
        }
    }
%>
</ol>
</body>
</html>
