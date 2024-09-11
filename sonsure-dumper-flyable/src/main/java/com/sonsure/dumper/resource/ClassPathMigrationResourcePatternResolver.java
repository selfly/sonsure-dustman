package com.sonsure.dumper.resource;

import com.sonsure.dumper.common.spring.PathMatchingResourcePatternResolver;
import com.sonsure.dumper.common.spring.Resource;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author selfly
 */
public class ClassPathMigrationResourcePatternResolver implements MigrationResourceResolver {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @Override
    @SneakyThrows
    public List<MigrationResource> resolveMigrationResources(String resourcePattern) {
        Resource[] resources = resolver.getResources(resourcePattern);
        return Arrays.stream(resources).map(MigrationResource::new).collect(Collectors.toList());
    }

}

