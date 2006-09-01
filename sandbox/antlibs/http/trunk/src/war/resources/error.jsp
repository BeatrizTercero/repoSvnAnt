<%--
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
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String codeParam = request.getParameter("code");

    int error_code =HttpServletResponse.SC_OK;
    if(codeParam!=null) {
        try {
            error_code =Integer.valueOf(codeParam).intValue();
        } catch (NumberFormatException e) {
            error_code =HttpServletResponse.SC_BAD_REQUEST;
        }
    } 
    if(error_code !=HttpServletResponse.SC_OK) {
        response.sendError(error_code);
    }
%>
<html>
  <head><title>Error page</title></head>
  <body>
  <p>
  ?code parameter <%= codeParam!=null?codeParam:"absent" %>
  </p>
  <p>
      <%-- this string is searched for in the tests; do not edit without patching 
      them -->
  error_code=<%= error_code %>
  </p>
  </body>
</html>