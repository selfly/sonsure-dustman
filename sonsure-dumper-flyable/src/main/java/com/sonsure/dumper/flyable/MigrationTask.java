package com.sonsure.dumper.flyable;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author selfly
 */
@Getter
public class MigrationTask {

    private final String resourcePattern;
    private final MigrationTaskExecutor migrationTaskExecutor;
    private final List<String> groupOrder;

    public MigrationTask(String resourcePattern, MigrationTaskExecutor migrationTaskExecutor, String... groupOrder) {
        this.resourcePattern = resourcePattern;
        this.migrationTaskExecutor = migrationTaskExecutor;
        this.groupOrder = groupOrder == null ? Collections.emptyList() : Arrays.asList(groupOrder);
    }

}
