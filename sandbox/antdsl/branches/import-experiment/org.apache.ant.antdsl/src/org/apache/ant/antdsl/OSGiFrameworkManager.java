package org.apache.ant.antdsl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.framework.wiring.BundleWiring;

public class OSGiFrameworkManager {

    //@formatter:off
    private static final String ANT_PACKAGES =
            "org.apache.tools.ant,"
          + "org.apache.tools.ant.types,"
          + "org.apache.tools.ant.taskdefs.condition,"
          + "org.apache.tools.ant.taskdefs,"
          + "org.apache.tools.ant.util";
    //@formatter:on

    private Framework framework;

    private List<Bundle> bundles = new ArrayList<Bundle>();

    private GodClassLoader godClassLoader = new GodClassLoader();

    public OSGiFrameworkManager(File basedir) throws BundleException {
        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, ANT_PACKAGES);
        configMap.put(Constants.FRAMEWORK_STORAGE, new File(basedir, "ant/felix-cache").getAbsolutePath());
        configMap.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
        framework = getFrameworkFactory().newFramework(configMap);
        framework.init();
    }

    private FrameworkFactory getFrameworkFactory() {
        Enumeration<URL> urls;
        try {
            urls = OSGiFrameworkManager.class.getClassLoader().getResources("META-INF/services/org.osgi.framework.launch.FrameworkFactory");
        } catch (IOException e) {
            throw new BuildException(e);
        }
        if (urls == null || !urls.hasMoreElements()) {
            throw new BuildException("No OSGi framework could be found in Ant's classpath");
        }
        URL url = null;
        ArrayList<URL> urlList = Collections.list(urls);
        for (URL candidate : urlList) {
            if (candidate.toExternalForm().contains("felix")) {
                url = candidate;
            }
        }
        if (url == null) {
            url = urlList.iterator().next();
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
        if (bundleURI.startsWith("file:")) {
            bundleURI = "reference:" + bundleURI;
        }
        Bundle bundle = framework.getBundleContext().installBundle(bundleURI);
        if (!isFragment(bundle)) {
            bundles.add(bundle);
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
        for (Bundle bundle : bundles) {
            bundle.start();
        }
    }

    public GodClassLoader getGodClassLoader() {
        return godClassLoader;
    }

    public List<Bundle> getBundles() {
        return bundles;
    }

    /**
     * Find which classloader is responsible for resolving holding url
     * 
     * @return
     */
    public ClassLoader getClassLoader(String resource, URL url) {
        for (Bundle bundle : bundles) {
            BundleWiring wiring = bundle.adapt(BundleWiring.class);
            int i = resource.lastIndexOf('/');
            String path = resource.substring(0, i);
            String name = resource.substring(i + 1);
            List<URL> entries = wiring.findEntries(path, name, 0);
            if (!entries.isEmpty() && containsUrls(entries, url)) {
                return wiring.getClassLoader();
            }
        }
        return null;
    }

    private boolean containsUrls(List<URL> urls, URL url) {
        for (URL u : urls) {
            if (urlEquals(u, url)) {
                return true;
            }
        }
        return false;
    }

    private boolean urlEquals(URL url1, URL url2) {
        if (!url1.getProtocol().equals("bundle") || !url2.getProtocol().equals("bundle")) {
            return url1.equals(url2);
        }
        return url1.getHost().equals(url2.getHost()) && url1.getPath().equals(url2.getPath());
    }

    private class GodClassLoader extends ClassLoader {

        private GodClassLoader() {
            super(null);
        }

        @Override
        public URL getResource(String name) {
            for (Bundle bundle : bundles) {
                BundleWiring wiring = bundle.adapt(BundleWiring.class);
                ClassLoader cl = wiring.getClassLoader();
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
            for (Bundle bundle : bundles) {
                BundleWiring wiring = bundle.adapt(BundleWiring.class);
                ClassLoader cl = wiring.getClassLoader();
                Enumeration<URL> resources = cl.getResources(name);
                if (resources != null) {
                    urls.addAll(Collections.list(resources));
                }
            }
            return Collections.enumeration(urls);
        }

        @Override
        public Class< ? > loadClass(String name) throws ClassNotFoundException {
            for (Bundle bundle : bundles) {
                BundleWiring wiring = bundle.adapt(BundleWiring.class);
                ClassLoader cl = wiring.getClassLoader();
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
