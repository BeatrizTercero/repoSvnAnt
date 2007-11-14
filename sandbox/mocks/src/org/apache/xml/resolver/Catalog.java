package org.apache.xml.resolver;

import java.io.IOException;
import java.net.URL;

public class Catalog {
    
    public URL base;
    public A catalogManager;
    
    public int PUBLIC;
    public int URI;
    
    protected Catalog newCatalog() {
        return null;
    }
    public void addEntry(CatalogEntry entry) {}
    public String normalizeURI(Object o) {
        return null;
    }
    public void parseCatalog(String s) throws IOException {}
    
    
    public class A {
        public B debug;
    }
    public class B {
        public void message(int i, String s) {}
    }
}
