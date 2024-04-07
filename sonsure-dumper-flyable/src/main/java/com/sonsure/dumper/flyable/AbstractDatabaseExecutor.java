package com.sonsure.dumper.flyable;

import com.sonsure.dumper.common.spring.PathMatchingResourcePatternResolver;
import com.sonsure.dumper.common.spring.Resource;
import com.sonsure.dumper.core.persist.JdbcDao;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author selfly
 */
public abstract class AbstractDatabaseExecutor implements DatabaseExecutor {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    private final String resourcePattern;

    public AbstractDatabaseExecutor(String resourcePattern) {
        this.resourcePattern = resourcePattern;
    }

    @Override
    @SneakyThrows
    public List<MigrationResource> getMigrationResources() {
        Resource[] resources = resolver.getResources(resourcePattern);
        return Arrays.stream(resources).map(MigrationResource::new).collect(Collectors.toList());
    }

    @Override
    public void executeResource(JdbcDao jdbcDao, MigrationResource resource) {
        jdbcDao.executeScript(resource.getResourceContent(StandardCharsets.UTF_8));
    }

}
