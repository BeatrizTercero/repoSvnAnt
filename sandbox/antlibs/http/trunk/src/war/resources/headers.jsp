<%@ page import="java.util.Enumeration" %>
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