package org.apache.ant.antdsl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

public class OSGiFrameworkManager {

    private static final String ANT_PACKAGES = "org.apache.tools.ant," + "org.apache.tools.ant.types," + "org.apache.tools.ant.taskdefs.condition,"
            + "org.apache.tools.ant.taskdefs," + "org.apache.tools.ant.util";

    private Framework framework;

    private List<Bundle> bundleToStart = new ArrayList<Bundle>();

    private Map<String, ClassLoader> classloaders = new LinkedHashMap<String, ClassLoader>();

    private GodClassLoader godClassLoader;

    public OSGiFrameworkManager() throws BundleException {
        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, ANT_PACKAGES);
        framework = getFrameworkFactory().newFramework(configMap);
        framework.init();
        godClassLoader = new GodClassLoader();
    }

    private FrameworkFactory getFrameworkFactory() {
        URL url = OSGiFrameworkManager.class.getClassLoader().getResource("META-INF/services/org.osgi.framework.launch.FrameworkFactory");
        if (url == null) {
            throw new BuildException("No OSGi framework could be found in Ant's classpath");
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            for (String s = br.readLine(); s != null; s = br.readLine()) {
                s = s.trim();
                // Try to load first non-empty, non-commented line.
                if ((s.length() > 0) && (s.charAt(0) != '#')) {
                    return (FrameworkFactory) Class.forName(s).newInstance();
                }
            }
        } catch (Exception e) {
            throw new BuildException("The OSGi framework factory could not be instanciated (" + e.getMessage() + ")", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // don't care
                }
            }
        }
        throw new BuildException("No OSGi framework factory found");
    }

    public void install(String bundleURI) throws BundleException {
        Bundle bundle = framework.getBundleContext().installBundle(bundleURI);
        classloaders.put(bundleURI, new BundleClassLoader(bundle));
        if (!isFragment(bundle)) {
            bundleToStart.add(bundle);
        }
    }

    private static boolean isFragment(Bundle bundle) {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }

    public void start() throws BundleException {
        framework.start();
        Runtime.getRuntime().addShutdownHook(new Thread("OSGi Framwork Shutdown Hook") {
            public void run() {
                try {
                    if (framework != null) {
                        framework.stop();
                        framework.waitForStop(0);
                    }
                } catch (Exception ex) {
                    System.err.println("Error stopping framework: " + ex);
                }
            }
        });
        for (Bundle bundle : bundleToStart) {
            bundle.start();
        }
    }

    public ClassLoader getClassloader(String bundleURI) {
        return classloaders.get(bundleURI);
    }

    public GodClassLoader getGodClassLoader() {
        return godClassLoader;
    }

    private static class BundleClassLoader extends ClassLoader {

        private Bundle bundle;

        private BundleClassLoader(Bundle bundle) {
            super(null);
            this.bundle = bundle;
        }

        @Override
        public URL getResource(String name) {
            return bundle.getResource(name);
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return bundle.getResources(name);
        }

        @Override
        public Class< ? > loadClass(String name) throws ClassNotFoundException {
            return bundle.loadClass(name);
        }
    }

    private class GodClassLoader extends ClassLoader {

        private GodClassLoader() {
            super(null);
        }

        @Override
        public URL getResource(String name) {
            for (ClassLoader cl : classloaders.values()) {
                URL url = cl.getResource(name);
                if (url != null) {
                    return url;
                }
            }
            return null;
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            List<URL> urls = new ArrayList<URL>();
            for (ClassLoader cl : classloaders.values()) {
                Enumeration<URL> resources = cl.getResources(name);
                if (resources != null) {
                    urls.addAll(Collections.list(resources));
                }
            }
            return Collections.enumeration(urls);
        }

        @Override
        public Class< ? > loadClass(String name) throws ClassNotFoundException {
            for (ClassLoader cl : classloaders.values()) {
                try {
                    return cl.loadClass(name);
                } catch (ClassNotFoundException e) {
                    // next...
                }
            }
            throw new ClassNotFoundException();
        }

    }

}
