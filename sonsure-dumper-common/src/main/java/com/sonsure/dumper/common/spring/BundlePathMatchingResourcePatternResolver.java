/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.spring;


import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author selfly
 */
public class BundlePathMatchingResourcePatternResolver extends PathMatchingResourcePatternResolver {

    public BundlePathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    public BundlePathMatchingResourcePatternResolver(ClassLoader classLoader) {
        super(classLoader);
    }

    /**
     * Find all class location resources with the given location via the ClassLoader.
     *
     * @param location the absolute path within the classpath
     * @return the result as Resource array
     * @throws IOException in case of I/O errors
     * @see ClassLoader#getResources
     * @see #convertClassLoaderURL
     */
    @Override
    protected Resource[] findAllClassPathResources(String location) throws IOException {
        String path = location;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        ClassLoader cl = getClassLoader();
        Enumeration<URL> resourceUrls = (cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path));
        Set<Resource> result = new LinkedHashSet<Resource>(16);
        while (resourceUrls.hasMoreElements()) {
            URL url = this.getLocalUrl(resourceUrls.nextElement());

//            try {
//                URLConnection urlConnection = url.openConnection();
//                String connectionClsName = "org.apache.felix.framework.URLHandlersBundleURLConnection";
//                if (connectionClsName.equals(urlConnection.getClass().getName())) {
//                    Class<?> aClass = urlConnection.getClass().getClassLoader().loadClass(connectionClsName);
//                    Method method = aClass.getDeclaredMethod("getLocalURL");
//                    method.setAccessible(true);
//                    Object invoke = method.invoke(urlConnection);
//                    url = ((URL) invoke);
//                }
//            } catch (Exception e) {
//                throw new RuntimeException("获取bundle的localURL失败", e);
//            }
            result.add(convertClassLoaderURL(url));
        }
        return result.toArray(new Resource[0]);
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        Assert.notNull(locationPattern, "Location pattern must not be null");
        Resource[] resources;
        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            // a class path resource (multiple resources for same name possible)
            if (getPathMatcher().isPattern(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()))) {
                // a class path resource pattern
                resources = findPathMatchingResources(locationPattern);
            } else {
                // all class path resources with the given name
                resources = findAllClassPathResources(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()));
            }
        } else {
            // Only look for a pattern after a prefix here
            // (to not get fooled by a pattern symbol in a strange prefix).
            int prefixEnd = locationPattern.indexOf(":") + 1;
            if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
                // a file pattern
                resources = findPathMatchingResources(locationPattern);
            } else {
                // a single resource with the given name
                resources = new Resource[]{getResourceLoader().getResource(locationPattern)};
            }
        }
        return Arrays.stream(resources)
                .filter(Resource::exists).toArray(Resource[]::new);
    }

    @Override
    protected boolean isJarResource(Resource resource) throws IOException {
        final URL url = resource.getURL();
        return ResourceUtils.isJarURL(url) || "bundle".equals(url.getProtocol());
    }

    @Override
    protected Set<Resource> doFindPathMatchingJarResources(Resource rootDirResource, String subPattern)
            throws IOException {

        URL url = rootDirResource.getURL();
        url = this.getLocalUrl(url);
        URLConnection con = url.openConnection();
        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        boolean newJarFile = false;

        if (con instanceof JarURLConnection) {
            // Should usually be the case for traditional JAR files.
            JarURLConnection jarCon = (JarURLConnection) con;
            ResourceUtils.useCachesIfNecessary(jarCon);
            jarFile = jarCon.getJarFile();
            jarFileUrl = jarCon.getJarFileURL().toExternalForm();
            JarEntry jarEntry = jarCon.getJarEntry();
            rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
        } else {
            // No JarURLConnection -> need to resort to URL file parsing.
            // We'll assume URLs of the format "jar:path!/entry", with the protocol
            // being arbitrary as long as following the entry format.
            // We'll also handle paths with and without leading "file:" prefix.
            String urlFile = url.getFile();
            int separatorIndex = urlFile.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
            if (separatorIndex != -1) {
                jarFileUrl = urlFile.substring(0, separatorIndex);
                rootEntryPath = urlFile.substring(separatorIndex + ResourceUtils.JAR_URL_SEPARATOR.length());
                jarFile = getJarFile(jarFileUrl);
            } else {
                jarFile = new JarFile(urlFile);
                jarFileUrl = urlFile;
                rootEntryPath = "";
            }
            newJarFile = true;
        }

        try {
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash to allow for proper matching.
                // The Sun JRE does not return a slash here, but BEA JRockit does.
                rootEntryPath = rootEntryPath + "/";
            }
            Set<Resource> result = new LinkedHashSet<Resource>(8);
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    if (getPathMatcher().match(subPattern, relativePath)) {
                        result.add(rootDirResource.createRelative(relativePath));
                    }
                }
            }
            return result;
        } finally {
            // Close jar file, but only if freshly obtained -
            // not from JarURLConnection, which might cache the file reference.
            if (newJarFile) {
                jarFile.close();
            }
        }
    }

    @SneakyThrows
    private URL getLocalUrl(URL url) {
        if ("bundle".equals(url.getProtocol())) {
            String connectionClsName = "org.apache.felix.framework.URLHandlersBundleURLConnection";
            final URLConnection urlConnection = url.openConnection();
            Class<?> aClass = urlConnection.getClass().getClassLoader().loadClass(connectionClsName);
            Method method = aClass.getDeclaredMethod("getLocalURL");
            method.setAccessible(true);
            Object invoke = method.invoke(urlConnection);
            return ((URL) invoke);
        }
        return url;
    }
}
