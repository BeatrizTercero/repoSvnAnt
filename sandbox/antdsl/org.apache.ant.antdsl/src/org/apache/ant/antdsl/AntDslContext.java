package org.apache.ant.antdsl;

import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.AntXMLContext;

public class AntDslContext extends AntXMLContext {

    private Map<String, String> namespaces = new HashMap<String, String>();

    public AntDslContext(Project project) {
        super(project);
    }

    public void addNamespace(String name, String uri) {
        namespaces.put(name, uri);
    }

    public String getURI(String name) {
        return namespaces.get(name);
    }

}
