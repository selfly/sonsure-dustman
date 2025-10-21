package com.sonsure.dumper.common.utility;

import com.sonsure.dumper.common.exception.SonsureCommonsException;
import com.sonsure.dumper.common.exception.SonsureException;
import com.sonsure.dumper.common.utils.ClassUtils;
import com.sonsure.dumper.common.utils.FileIOUtils;
import com.sonsure.dumper.common.utils.StrUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author selfly
 */
public class GenericResourcePatternResolverImpl implements GenericResourcePatternResolver {

    private static final String FILE_PREFIX = "file:";
    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String CLASSPATH_ALL_PREFIX = "classpath*:";
    public static final String CLASS_RESOURCE_PATTERN = "**/*.class";

    public static final String PATH_SEPARATOR = "/";

    public static final String PACKAGE_SEPARATOR = ".";

    @Override
    @SneakyThrows
    public List<GenericResource> getResources(String locationPattern) {
        if (StrUtils.startsWith(locationPattern, CLASSPATH_PREFIX) || StrUtils.startsWith(locationPattern, CLASSPATH_ALL_PREFIX)) {
            return this.getClasspathResources(locationPattern, ClassUtils.getDefaultClassLoader());
        } else if (StrUtils.startsWith(locationPattern, FILE_PREFIX)) {
            return this.getFileSystemResources(locationPattern);
        } else {
            throw new SonsureCommonsException("不支持的资源加载:" + locationPattern);
        }
    }

    @Override
    public List<Class<?>> getResourcesClasses(String packageName) {
        String basePackagePath = StrUtils.replace(packageName, PACKAGE_SEPARATOR, PATH_SEPARATOR);
        String packageSearchPath = CLASSPATH_ALL_PREFIX + basePackagePath + PATH_SEPARATOR + CLASS_RESOURCE_PATTERN;

        List<Class<?>> classes = new ArrayList<>();
        try {
            List<GenericResource> resources = this.getResources(packageSearchPath);
            for (GenericResource resource : resources) {
                try (InputStream is = resource.getInputStream()) {
                    ClassReader classReader = new ClassReader(FileIOUtils.toByteArray(is));
                    String className = StrUtils.replace(classReader.getClassName(), PATH_SEPARATOR, PACKAGE_SEPARATOR);
                    Class<?> cls = ClassUtils.loadClass(className);
                    classes.add(cls);
                }
            }
        } catch (IOException e) {
            throw new SonsureException("扫描class失败,package:" + packageName, e);
        }
        return classes;
    }

    private String getUserDir() {
        String userDir = System.getProperty("user.dir");
        if (!StrUtils.endsWith(userDir, "/")) {
            userDir += "/";
        }
        return userDir;
    }

    private List<GenericResource> getFileSystemResources(String locationPattern) throws IOException {
        // 移除 file: 前缀
        String pattern = locationPattern.substring(FILE_PREFIX.length());
        if (!StrUtils.startsWith(pattern, "/")) {
            pattern = this.getUserDir() + pattern;
        }
        String basePath = this.getBasePath(pattern);
        // 处理相对路径和绝对路径
        File baseFile = new File(basePath);
        String filePattern = pattern.substring(basePath.length());
        // 检查是否包含多层通配符 **
        boolean recursive = filePattern.contains("**");
        // 转换 Ant 风格通配符为正则表达式
        Pattern regex = convertAntPatternToRegex(filePattern);

        List<GenericResource> result = new ArrayList<>();

        if (baseFile.exists() && baseFile.isDirectory()) {
            // 目录存在，递归查找匹配的文件
            findFileSystemResourcesRecursive(baseFile, regex, recursive, result);
        }
        return result;
    }

    /**
     * 获取资源列表
     */
    private List<GenericResource> getClasspathResources(String locationPattern, ClassLoader classLoader) throws IOException {
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        // 移除 classpath: 或 classpath*: 前缀
        boolean searchAllClassPath = false;
        String pattern = locationPattern;

        if (pattern.startsWith(CLASSPATH_ALL_PREFIX)) {
            searchAllClassPath = true;
            pattern = pattern.substring(CLASSPATH_ALL_PREFIX.length());
        } else if (pattern.startsWith(CLASSPATH_PREFIX)) {
            pattern = pattern.substring(CLASSPATH_PREFIX.length());
        }

        // 确保路径以 / 开头
        if (!pattern.startsWith("/")) {
            pattern = "/" + pattern;
        }

        // 提取基础路径和文件模式
        String basePath = getBasePath(pattern);
        String filePattern = pattern.substring(basePath.length());

        // 转换 Ant 风格通配符为正则表达式
        Pattern regex = convertAntPatternToRegex(filePattern);

        List<GenericResource> result = new ArrayList<>();

        // 获取资源URL
        Enumeration<URL> urls;
        String basePathForSearch = basePath.startsWith("/") ? basePath.substring(1) : basePath;

        if (searchAllClassPath) {
            urls = classLoader.getResources(basePathForSearch);
        } else {
            URL url = classLoader.getResource(basePathForSearch);
            urls = url != null ? Collections.enumeration(Collections.singletonList(url)) : null;
        }

        if (urls != null) {
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    // 文件系统中的资源
                    findClasspathFileSystemResources(url, basePath, regex, result);
                } else if ("jar".equals(protocol)) {
                    // JAR 包中的资源
                    findJarResources(url, basePath, regex, result);
                }
            }
        }

        return result;
    }

    /**
     * 获取基础路径（不包含通配符的部分）
     */
    private String getBasePath(String pattern) {
        int wildcardIndex = pattern.indexOf('*');
        if (wildcardIndex == -1) {
            // 没有通配符，检查是否是目录
            int lastSlash = pattern.lastIndexOf('/');
            return lastSlash > 0 ? pattern.substring(0, lastSlash) : "";
        }
        // 找到最后一个 / 在通配符之前
        int lastSlash = pattern.lastIndexOf('/', wildcardIndex);
        return lastSlash > 0 ? pattern.substring(0, lastSlash) : "";
    }

    /**
     * 将 Ant 风格的通配符模式转换为正则表达式
     * 支持: * (匹配任意字符，不包括 /) 和 ** (匹配任意字符，包括 /)
     */
    private Pattern convertAntPatternToRegex(String antPattern) {
        if (antPattern.startsWith("/")) {
            antPattern = antPattern.substring(1);
        }

        StringBuilder regex = new StringBuilder();
        int i = 0;
        while (i < antPattern.length()) {
            char c = antPattern.charAt(i);
            if (c == '*') {
                if (i + 1 < antPattern.length() && antPattern.charAt(i + 1) == '*') {
                    // ** 匹配任意字符包括路径分隔符
                    regex.append(".*");
                    i += 2;
                    // 跳过后面的 /
                    if (i < antPattern.length() && antPattern.charAt(i) == '/') {
                        i++;
                    }
                } else {
                    // * 匹配除路径分隔符外的任意字符
                    regex.append("[^/]*");
                    i++;
                }
            } else if (c == '?') {
                regex.append("[^/]");
                i++;
            } else {
                // 转义正则表达式特殊字符
                if ("[](){}+|^$.\\".indexOf(c) >= 0) {
                    regex.append('\\');
                }
                regex.append(c);
                i++;
            }
        }

        return Pattern.compile(regex.toString());
    }

    /**
     * 递归在文件系统中查找资源
     */
    private void findFileSystemResourcesRecursive(File baseDir, Pattern pattern, boolean recursive, List<GenericResource> result) throws IOException {
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return;
        }

        // 安全检查：限制递归深度，防止无限递归
        String basePath = baseDir.getAbsolutePath();
        if (basePath.split("/").length > 20) {
            return;
        }

        File[] files = baseDir.listFiles();
        if (files == null) {
            return;
        }

        // 限制处理的文件数量，防止内存溢出
        if (files.length > 1000) {
            throw new SonsureCommonsException("目录包含过多文件，可能存在安全风险: " + basePath);
        }

        for (File file : files) {
            if (file.isDirectory() && recursive) {
                // 递归处理子目录
                findFileSystemResourcesRecursive(file, pattern, true, result);
            } else if (file.isFile()) {
                // 检查文件是否匹配模式
                String relativePath = getRelativePath(baseDir, file);
                if (pattern.matcher(relativePath).matches()) {
                    result.add(new FileSystemResource(file.toPath(), basePath + "/" + relativePath));
                }
            }
        }
    }

    /**
     * 获取相对路径
     */
    private String getRelativePath(File baseDir, File file) {
        String basePath = baseDir.getAbsolutePath();
        String filePath = file.getAbsolutePath();

        if (filePath.startsWith(basePath)) {
            String relativePath = filePath.substring(basePath.length());
            // 移除开头的路径分隔符
            if (relativePath.startsWith(File.separator)) {
                relativePath = relativePath.substring(1);
            }
            // 统一使用 / 作为路径分隔符
            return relativePath.replace(File.separator, "/");
        }

        return file.getName();
    }

    /**
     * 在文件系统中查找资源（用于classpath中的file协议）
     */
    private void findClasspathFileSystemResources(URL url, String basePath, Pattern pattern, List<GenericResource> result) throws IOException {
        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
        File dir = new File(filePath);

        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        Path dirPath = dir.toPath();
        try (Stream<Path> paths = Files.walk(dirPath)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                String relativePath = dirPath.relativize(path).toString().replace(File.separator, "/");
                if (pattern.matcher(relativePath).matches()) {
                    result.add(new FileSystemResource(path, basePath + "/" + relativePath));
                }
            });
        }
    }

    /**
     * 在 JAR 包中查找资源
     */
    private void findJarResources(URL url, String basePath, Pattern pattern, List<GenericResource> result) throws IOException {
        JarURLConnection jarConn = (JarURLConnection) url.openConnection();

        // 设置安全属性，防止ZIP炸弹攻击
        jarConn.setUseCaches(false);

        JarFile jarFile = jarConn.getJarFile();
        String basePathClean = basePath.startsWith("/") ? basePath.substring(1) : basePath;
        if (!basePathClean.isEmpty() && !basePathClean.endsWith("/")) {
            basePathClean += "/";
        }

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();

            // 检查是否在基础路径下
            if (!entry.isDirectory() && entryName.startsWith(basePathClean)) {
                String relativePath = entryName.substring(basePathClean.length());
                if (pattern.matcher(relativePath).matches()) {
                    result.add(new JarResource(jarFile, entry, entryName));
                }
            }
        }
    }

    /**
     * 文件系统资源实现
     */
    private static class FileSystemResource implements GenericResource {
        private final Path path;
        private final String resourcePath;

        public FileSystemResource(Path path, String resourcePath) {
            this.path = path;
            this.resourcePath = resourcePath;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            // 安全检查：限制文件大小，防止内存溢出
            long fileSize = Files.size(path);
            if (fileSize > 100 * 1024 * 1024) { // 100MB 限制
                throw new IOException("文件过大，无法加载: " + resourcePath + " (大小: " + fileSize + " bytes)");
            }

            return Files.newInputStream(path);
        }

        @Override
        public String getFilename() {
            return path.getFileName().toString();
        }

        /**
         * 获取文件大小
         */
        public long getFileSize() throws IOException {
            return Files.size(path);
        }

        /**
         * 检查文件是否存在
         */
        public boolean exists() {
            return Files.exists(path);
        }

        /**
         * 获取最后修改时间
         */
        public long getLastModified() throws IOException {
            return Files.getLastModifiedTime(path).toMillis();
        }

        @Override
        public String toString() {
            return "file [" + resourcePath + "]";
        }
    }

    /**
     * JAR 资源实现
     */
    private static class JarResource implements GenericResource {
        private final JarFile jarFile;
        private final JarEntry entry;
        private final String entryName;
        // 10MB 限制
        private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

        public JarResource(JarFile jarFile, JarEntry entry, String entryName) {
            this.jarFile = jarFile;
            this.entry = entry;
            this.entryName = entryName;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            // 安全检查：限制文件大小，防止ZIP炸弹攻击
            if (entry.getSize() > MAX_FILE_SIZE) {
                throw new IOException("File too large: " + entryName + " (size: " + entry.getSize() + ")");
            }

            // 检查压缩比例，防止ZIP炸弹
            long compressedSize = entry.getCompressedSize();
            if (compressedSize > 0 && entry.getSize() > 0) {
                double compressionRatio = (double) entry.getSize() / compressedSize;
                // 压缩比例超过100:1认为是可疑的
                if (compressionRatio > 100) {
                    throw new IOException("Suspicious compression ratio detected for: " + entryName);
                }
            }

            return jarFile.getInputStream(entry);
        }

        @Override
        public String getFilename() {
            String name = entryName;
            int lastSlash = name.lastIndexOf('/');
            return lastSlash >= 0 ? name.substring(lastSlash + 1) : name;
        }

        @Override
        public String toString() {
            return "jar [" + jarFile.getName() + "!/" + entryName + "]";
        }
    }
}
