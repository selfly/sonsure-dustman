package com.sonsure.dumper.resource;

import com.sonsure.dumper.common.spring.Resource;
import com.sonsure.dumper.common.utils.ClassUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 基于JDK的类路径资源解析器
 * 支持 classpath: 和 classpath*: 前缀
 * 支持 Ant 风格的路径模式，如 **\/*.sql
 *
 * @author selfly
 */
public class ClassPathMigrationResourcePatternResolver implements MigrationResourceResolver {

    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String CLASSPATH_ALL_PREFIX = "classpath*:";

    @Override
    @SneakyThrows
    public List<MigrationResource> resolveMigrationResources(String resourcePattern) {
        List<Resource> resources = getResources(resourcePattern, ClassUtils.getDefaultClassLoader());
        return resources.stream().map(MigrationResource::new).collect(Collectors.toList());
    }

    /**
     * 获取资源列表
     */
    private List<Resource> getResources(String locationPattern, ClassLoader classLoader) throws IOException {
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

        List<Resource> result = new ArrayList<>();

        if (classLoader == null) {
            classLoader = ClassPathMigrationResourcePatternResolver.class.getClassLoader();
        }

        // 获取资源URL
        Enumeration<URL> urls;
        String basePathForSearch = basePath.startsWith("/") ? basePath.substring(1) : basePath;

        if (searchAllClassPath) {
            urls = classLoader.getResources(basePathForSearch);
        } else {
            URL url = classLoader.getResource(basePathForSearch);
            urls = url != null ? java.util.Collections.enumeration(java.util.Collections.singletonList(url)) : null;
        }

        if (urls != null) {
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    // 文件系统中的资源
                    findFileSystemResources(url, basePath, regex, result);
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
     * 在文件系统中查找资源
     */
    private void findFileSystemResources(URL url, String basePath, Pattern pattern, List<Resource> result) throws IOException {
        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
        File dir = new File(filePath);

        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        Path dirPath = dir.toPath();
        try (Stream<Path> paths = Files.walk(dirPath)) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> {
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
    private void findJarResources(URL url, String basePath, Pattern pattern, List<Resource> result) throws IOException {
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

            // 安全检查：防止路径遍历攻击
            if (isUnsafePath(entryName)) {
                continue;
            }

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
     * 检查路径是否安全，防止路径遍历攻击
     */
    private boolean isUnsafePath(String path) {
        if (path == null) {
            return true;
        }

        // 检查是否包含路径遍历字符
        return path.contains("..") ||
                path.contains("\\") ||
                path.startsWith("/") ||
                path.contains("//");
    }

    /**
     * 文件系统资源实现
     */
    private static class FileSystemResource implements Resource {
        private final Path path;

        public FileSystemResource(Path path, String resourcePath) {
            this.path = path;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return Files.newInputStream(path);
        }

        @Override
        public String getFilename() {
            return path.getFileName().toString();
        }

        @Override
        public String toString() {
            return "file [" + path + "]";
        }
    }

    /**
     * JAR 资源实现
     */
    private static class JarResource implements Resource {
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
                if (compressionRatio > 100) { // 压缩比例超过100:1认为是可疑的
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
