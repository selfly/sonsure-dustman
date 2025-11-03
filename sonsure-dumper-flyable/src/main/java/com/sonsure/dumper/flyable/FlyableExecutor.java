package com.sonsure.dumper.flyable;

import com.sonsure.dumper.common.utils.VersionUtils;
import com.sonsure.dumper.core.command.build.OrderBy;
import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.database.DatabaseMigrationTask;
import com.sonsure.dumper.database.NegotiatingDatabaseMigrationTaskImpl;
import com.sonsure.dumper.exception.FlyableException;
import com.sonsure.dumper.resource.ClassPathMigrationResourcePatternResolver;
import com.sonsure.dumper.resource.MigrationResource;
import com.sonsure.dumper.resource.MigrationResourceResolver;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author selfly
 */
@Slf4j
@Setter
@Getter
public class FlyableExecutor {

    private static final String FLYABLE_HISTORY = "flyable_history";
    public static final String FLYABLE_EXECUTION_GROUP = "flyable";
    public static final String FLYABLE_PREFIX_VAR = "flyablePrefix";

    private static final String UNKNOWN_EXECUTION_GROUP = "unknown";

    private final JdbcDao jdbcDao;
    private String flyablePrefix = "";
    private boolean enableChecksum = true;
    private final MigrationResourceResolver migrationResourceResolver = new ClassPathMigrationResourcePatternResolver();
    private final Map<String, List<MigrationTask>> migrationTasks = new HashMap<>(8);
    private final Map<Integer, String> executionGroupOrder = new TreeMap<>();

    public FlyableExecutor(JdbcDao jdbcDao) {
        this.jdbcDao = jdbcDao;
        this.addExecutionGroupOrder(0, FLYABLE_EXECUTION_GROUP);
        this.addExecutionGroupOrder(10000, UNKNOWN_EXECUTION_GROUP);
    }

    public void addExecutionGroupOrder(int order, String executionGroup) {
        if (executionGroupOrder.containsValue(executionGroup)) {
            throw new FlyableException("一个ExecutionGroup不能指定多个执行顺序");
        }
        this.executionGroupOrder.put(order, executionGroup);
    }

    public void addMigrationTask(MigrationTask migrationTask) {
        this.addMigrationTask(migrationTask, UNKNOWN_EXECUTION_GROUP);
    }

    public void addMigrationTask(MigrationTask migrationTask, String executionGroup) {
        List<MigrationTask> tasks = this.migrationTasks.computeIfAbsent(executionGroup, k -> new ArrayList<>(8));
        tasks.add(migrationTask);
    }

    public void migrate() {
        if (this.migrationTasks.isEmpty()) {
            this.registerDefaultFlyableMigrationTask();
        }
        boolean historyTableExists = false;
        //group顺序执行，任务根据group顺序执行，任务中的resource也会根据group顺序执行。
        // 比如flyable初始化mysql/**.sql，只有一个group任务，但是sql脚本会有很多的group，user、menu等
        for (Map.Entry<Integer, String> entry : executionGroupOrder.entrySet()) {
            String executionGroup = entry.getValue();
            List<MigrationTask> tasks = migrationTasks.remove(executionGroup);
            //可能为空，比如没有unknown group任务
            if (tasks == null) {
                continue;
            }
            for (MigrationTask task : tasks) {
                //数据库任务
                if (!historyTableExists && task instanceof DatabaseMigrationTask) {
                    DatabaseMigrationTask databaseMigrationTask = (DatabaseMigrationTask) task;
                    if (databaseMigrationTask.support(this.getDatabaseProduct())) {
                        historyTableExists = databaseMigrationTask.isHistoryTableExists(jdbcDao, this.getFlyablePrefix() + FLYABLE_HISTORY);
                    }
                }
                List<MigrationResource> migrationResources = this.migrationResourceResolver.resolveMigrationResources(task.getResourcePattern());
                //初始化
                this.updateMigrate(migrationResources, historyTableExists, task);
            }
            //flyable执行过后必须为true
            if (FLYABLE_EXECUTION_GROUP.equals(executionGroup)) {
                historyTableExists = true;
            }
        }
    }

    protected String getDatabaseProduct() {
        return jdbcDao.getJdbcContext().getPersistExecutor().getDatabaseProduct();
    }

    protected void updateMigrate(List<MigrationResource> resources, boolean historyTableExists, MigrationTask migrationTask) {
        Map<String, List<MigrationResource>> resourceMap = resources.stream().collect(Collectors.groupingBy(MigrationResource::getGroup));
        for (Map.Entry<Integer, String> entry : executionGroupOrder.entrySet()) {
            String executionGroup = entry.getValue();
            List<MigrationResource> taskResources = resourceMap.remove(executionGroup);
            //可能为空
            if (taskResources == null) {
                continue;
            }
            taskResources.sort((o1, o2) -> VersionUtils.compareVersion(o1.getVersion(), o2.getVersion()));
            for (MigrationResource resource : taskResources) {
                this.executeResource(resource, historyTableExists, migrationTask);
            }
        }
        //未指定顺序的初始化
        for (Map.Entry<String, List<MigrationResource>> entry : resourceMap.entrySet()) {
            List<MigrationResource> taskResources = entry.getValue();
            for (MigrationResource taskResource : taskResources) {
                this.executeResource(taskResource, historyTableExists, migrationTask);
            }
        }
    }

    protected void executeResource(MigrationResource resource, boolean historyTableExists, MigrationTask migrationTask) {
        String installedLatestVersion = "0.0.0";
        Map<String, String> checksumMap = new HashMap<>(16);
        if (historyTableExists) {
            List<FlyableHistory> list = jdbcDao.selectFrom(FlyableHistory.class)
                    .where(FlyableHistory::getMigrationGroup, resource.getGroup())
                    .orderBy(FlyableHistory::getFlyableHistoryId, OrderBy.DESC)
                    .list(FlyableHistory.class);
            if (list != null && !list.isEmpty()) {
                FlyableHistory flyableHistory = list.iterator().next();
                if (flyableHistory.getSuccess() == null || !flyableHistory.getSuccess()) {
                    throw new FlyableException("Execution failures have been recorded. :" + flyableHistory.getScript());
                }
                installedLatestVersion = flyableHistory.getVersion();
                Map<String, String> collect = list.stream()
                        .collect(Collectors.toMap(FlyableHistory::getScript, FlyableHistory::getChecksum));
                checksumMap.putAll(collect);
            }
        }
        if (VersionUtils.compareVersion(resource.getVersion(), installedLatestVersion) <= 0) {
            log.info("Installed script:{}", resource.getFilename());
            if (!this.verifyChecksum(resource, checksumMap)) {
                log.info("The script changed after installation :{}", resource.getFilename());
                throw new FlyableException("The script changed after installation:" + resource.getFilename());
            }
            return;
        }
        log.info("Execution script:{}", resource.getFilename());
        FlyableHistory flyableHistory = new FlyableHistory();
        flyableHistory.setMigrationGroup(resource.getGroup());
        flyableHistory.setVersion(resource.getVersion());
        flyableHistory.setDescription(resource.getDescription());
        flyableHistory.setScript(resource.getFilename());
        flyableHistory.setChecksum(resource.getChecksum());
        flyableHistory.setGmtInstalled(LocalDateTime.now());

        long begin = System.currentTimeMillis();
        try {
            if (FLYABLE_EXECUTION_GROUP.equals(resource.getGroup())) {
                resource.addVariable(FLYABLE_PREFIX_VAR, this.getFlyablePrefix());
            }
            migrationTask.execute(jdbcDao, resource);
            long end = System.currentTimeMillis();
            flyableHistory.setExecutionTime(end - begin);
        } catch (Exception e) {
            log.error("执行 Migration Task 失败:", e);
            flyableHistory.setSuccess(false);
            jdbcDao.executeInsert(flyableHistory);
            throw new FlyableException(e);
        }
        flyableHistory.setSuccess(true);
        jdbcDao.executeInsert(flyableHistory);
    }

    protected boolean verifyChecksum(MigrationResource resource, Map<String, String> checksumMap) {
        if (!this.enableChecksum) {
            return true;
        }
        String checksum = checksumMap.get(resource.getFilename());
        if (checksum == null) {
            return true;
        }
        return checksum.equals(resource.getChecksum());
    }

    public void registerDefaultFlyableMigrationTask() {
        this.addMigrationTask(new NegotiatingDatabaseMigrationTaskImpl(), FLYABLE_EXECUTION_GROUP);
    }

}
