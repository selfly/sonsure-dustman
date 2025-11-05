package com.sonsure.dustman.resource;

import com.sonsure.dustman.common.utility.GenericResource;
import com.sonsure.dustman.common.utility.GenericResourcePatternResolver;
import com.sonsure.dustman.common.utility.GenericResourcePatternResolverImpl;
import lombok.SneakyThrows;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于JDK的类路径资源解析器
 * 支持 classpath: 和 classpath*: 前缀
 * 支持 Ant 风格的路径模式，如 **\/*.sql
 *
 * @author selfly
 */
public class ClassPathMigrationResourcePatternResolver implements MigrationResourceResolver {

    private static final GenericResourcePatternResolver RESOLVER = new GenericResourcePatternResolverImpl();

    @Override
    @SneakyThrows
    public List<MigrationResource> resolveMigrationResources(String resourcePattern) {
        List<GenericResource> genericResources = RESOLVER.getResources(resourcePattern);
        return genericResources.stream().map(MigrationResource::new).collect(Collectors.toList());
    }
}
