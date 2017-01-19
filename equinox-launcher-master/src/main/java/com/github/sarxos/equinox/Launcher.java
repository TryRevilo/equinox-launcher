package com.github.sarxos.equinox;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import java.io.File;
import java.util.*;


public class Launcher {

    private static String[] jars = null;
    private static String[] libs = null;

    private BundleContext context;

    private Launcher() {

        FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();

        Map<String, String> config = new HashMap<String, String>();
        config.put("osgi.console", "");
        config.put("osgi.clean", "true");
        config.put("osgi.noShutdown", "true");
        config.put("eclipse.ignoreApp", "true");
        config.put("osgi.bundles.defaultStartLevel", "4");
        config.put("osgi.configuration.area", "./configuration");

        // automated bundles deployment
        config.put("felix.fileinstall.dir", "./dropins");
        config.put("felix.fileinstall.noInitialDelay", "true");
        config.put("felix.fileinstall.start.level", "4");

        Framework framework = frameworkFactory.newFramework(config);

        try {
            framework.start();
        } catch (BundleException e) {
            e.printStackTrace();
        }

        context = framework.getBundleContext();

        // logging
        Bundle b1 = install("slf4j-api");
        Bundle b2 = install("logback-core");
        Bundle b3 = install("logback-classic");

        Bundle launcher = install("launcher-1.0-SNAPSHOT");
        Bundle ui = install("ui-1.0-SNAPSHOT");
        Bundle mainscreen = install("mainscreen-1.0-SNAPSHOT");

        Bundle OSGiDmHelloWorldProvider = install("OSGiDmHelloWorldProvider");
        Bundle OSGiDmHelloWorldConsumer = install("OSGiDmHelloWorldConsumer");
        try {
            b1.start();
            b2.start();
            b3.start();

            launcher.start();
            ui.start();
            mainscreen.start();

            OSGiDmHelloWorldProvider.start();
            OSGiDmHelloWorldConsumer.start();
        } catch (BundleException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Launcher();
    }

    private String[] getLibs() {
        if (libs == null) {
            List<String> jarsList = new ArrayList<String>();
            File pluginsDir = new File("libs");

            System.out.println("PATHS : " + pluginsDir.getAbsolutePath());

            for (String jar : pluginsDir.list()) {
                jarsList.add(jar);
            }
            libs = jarsList.toArray(new String[jarsList.size()]);
        }
        return libs;
    }

    protected Bundle start(String name) {
        Bundle bundle = install(name);
        if (bundle != null) {
            try {
                bundle.start();
            } catch (BundleException e) {
                e.printStackTrace();
            }
        }
        return bundle;
    }

    protected Bundle install(String name) {
        String found = null;

        for (String jar : getLibs()) {
            if (jar.startsWith(name)) {
                found = String.format("file:libs/%s", jar);
                System.out.println(found);
                break;
            }
        }
        if (found == null) {
            throw new RuntimeException(String.format("JAR for %s not found", name));
        }
        try {
            return context.installBundle(found);
        } catch (BundleException e) {
            e.printStackTrace();
        }
        return null;
    }
}
