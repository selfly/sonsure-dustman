package com.sonsure.dumper.flyable;

import com.sonsure.dumper.common.utils.VersionUtils;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.core.mapping.TablePrefixSupportHandler;
import com.sonsure.dumper.core.persist.AbstractJdbcDaoImpl;
import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.database.DatabaseMigrationTaskExecutor;
import com.sonsure.dumper.database.DatabaseMigrationTaskExecutorResolver;
import com.sonsure.dumper.exception.FlyableException;
import com.sonsure.dumper.resource.ClassPathMigrationResourcePatternResolver;
import com.sonsure.dumper.resource.MigrationResource;
import com.sonsure.dumper.resource.MigrationResourceResolver;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author selfly
 */
@Slf4j
@Setter
@Getter
public class FlyableExecutor {

    private static final String FLYABLE_HISTORY = "flyable_history";
    public static final String FLYABLE_GROUP = "flyable";
    public static final String FLYABLE_PREFIX_VAR = "flyablePrefix";

    private final JdbcDao jdbcDao;
    private String flyablePrefix = "";
    private boolean enableChecksum = true;
    private final MigrationResourceResolver migrationResourceResolver = new ClassPathMigrationResourcePatternResolver();
    private final DatabaseMigrationTaskExecutorResolver databaseMigrationTaskExecutorResolver = new DatabaseMigrationTaskExecutorResolver();
    private final List<MigrationTask> migrationTasks = new ArrayList<>(8);

    public FlyableExecutor(JdbcDao jdbcDao) {
        this.jdbcDao = jdbcDao;
        this.setFlyablePrefixWithJdbcDao();
    }

    public void registerDatabaseMigrationTask(DatabaseMigrationTaskExecutor databaseMigrationTaskExecutor) {
        this.databaseMigrationTaskExecutorResolver.registerDatabaseExecutor(databaseMigrationTaskExecutor);
    }

    public void registerMigrationTask(MigrationTaskExecutor migrationTaskExecutor, String... executionGroupOrder) {
        this.migrationTasks.add(new MigrationTask(migrationTaskExecutor.getResourcePattern(), migrationTaskExecutor, executionGroupOrder));
    }

    public void migrate() {
        if (this.migrationTasks.isEmpty()) {
            this.registerDefaultDatabaseMigrationTask();
        }
        DatabaseMigrationTaskExecutor databaseMigrationTaskExecutor = this.getDatabaseMigrationTaskExecutor();
        boolean flyableHistoryInitialized = databaseMigrationTaskExecutor.existFlyableHistoryTable(jdbcDao, this.getFlyablePrefix() + FLYABLE_HISTORY);
        for (MigrationTask migrationTask : this.migrationTasks) {
            List<MigrationResource> migrationResources = this.migrationResourceResolver.resolveMigrationResources(migrationTask.getResourcePattern());
            Map<String, List<MigrationResource>> map = migrationResources.stream().collect(Collectors.groupingBy(MigrationResource::getGroup));
            //指定顺序的初始化
            for (String group : migrationTask.getGroupOrder()) {
                List<MigrationResource> groupResources = map.remove(group);
                if (groupResources == null) {
                    continue;
                }
                this.updateMigrate(groupResources, group, flyableHistoryInitialized, migrationTask.getMigrationTaskExecutor());
                //执行过一次之后，肯定为true
                flyableHistoryInitialized = true;
            }
            //未指定顺序的初始化
            for (Map.Entry<String, List<MigrationResource>> entry : map.entrySet()) {
                this.updateMigrate(entry.getValue(), entry.getKey(), true, migrationTask.getMigrationTaskExecutor());
            }
        }
    }

    protected void setFlyablePrefixWithJdbcDao() {
        if (!(jdbcDao instanceof AbstractJdbcDaoImpl)) {
            return;
        }
        MappingHandler mappingHandler = ((AbstractJdbcDaoImpl) jdbcDao).getDefaultJdbcEngine().getJdbcEngineConfig().getMappingHandler();
        if (mappingHandler instanceof TablePrefixSupportHandler) {
            this.flyablePrefix = ((TablePrefixSupportHandler) mappingHandler).getTablePrefix(FlyableHistory.class.getName());
        }
    }

    protected String getDatabaseProduct() {
        return jdbcDao.getDatabaseProduct();
    }

    protected void updateMigrate(List<MigrationResource> resources, String migrationGroup, boolean flyableHistoryInitialized, MigrationTaskExecutor migrationTaskExecutor) {
        String installedLatestVersion = "0.0.0";
        Map<String, String> checksumMap = new HashMap<>(16);
        if (flyableHistoryInitialized) {
            List<FlyableHistory> list = jdbcDao.selectFrom(FlyableHistory.class)
                    .where(FlyableHistory::getMigrationGroup, migrationGroup)
                    .orderBy(FlyableHistory::getFlyableHistoryId).desc()
                    .list(FlyableHistory.class);
            if (list != null && !list.isEmpty()) {
                FlyableHistory flyableHistory = list.iterator().next();
                if (BooleanUtils.isFalse(flyableHistory.getSuccess())) {
                    throw new FlyableException("Execution failures have been recorded. :" + flyableHistory.getScript());
                }
                installedLatestVersion = flyableHistory.getVersion();
                Map<String, String> collect = list.stream().collect(Collectors.toMap(FlyableHistory::getScript, FlyableHistory::getChecksum));
                checksumMap.putAll(collect);
            }
        }
        resources.sort((o1, o2) -> VersionUtils.compareVersion(o1.getVersion(), o2.getVersion()));
        for (MigrationResource resource : resources) {
            if (VersionUtils.compareVersion(resource.getVersion(), installedLatestVersion) <= 0) {
                log.info("Installed script:{}", resource.getFilename());
                if (!this.verifyChecksum(resource, checksumMap)) {
                    log.info("The script changed after installation :{}", resource.getFilename());
                    throw new FlyableException("The script changed after installation:" + resource.getFilename());
                }
                continue;
            }
            log.info("Execution script:{}", resource.getFilename());
            FlyableHistory flyableHistory = new FlyableHistory();
            flyableHistory.setMigrationGroup(migrationGroup);
            flyableHistory.setVersion(resource.getVersion());
            flyableHistory.setDescription(resource.getDescription());
            flyableHistory.setScript(resource.getFilename());
            flyableHistory.setChecksum(resource.getChecksum());
            flyableHistory.setGmtInstalled(LocalDateTime.now());

            long begin = System.currentTimeMillis();
            try {
                if (FLYABLE_GROUP.equals(migrationGroup)) {
                    resource.addVariable(FLYABLE_PREFIX_VAR, this.getFlyablePrefix());
                }
                migrationTaskExecutor.executeResource(jdbcDao, resource);
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
    }

    protected boolean verifyChecksum(MigrationResource resource, Map<String, String> checksumMap) {
        if (!this.enableChecksum) {
            return true;
        }
        String checksum = checksumMap.get(resource.getFilename());
        if (checksum == null) {
            return true;
        }
        return StringUtils.equals(checksum, resource.getChecksum());
    }

    public void registerDefaultDatabaseMigrationTask() {
        DatabaseMigrationTaskExecutor databaseMigrationTaskExecutor = this.getDatabaseMigrationTaskExecutor();
        this.registerMigrationTask(databaseMigrationTaskExecutor, FLYABLE_GROUP);
    }

    private DatabaseMigrationTaskExecutor getDatabaseMigrationTaskExecutor() {
        String databaseProduct = this.getDatabaseProduct();
        return this.databaseMigrationTaskExecutorResolver.resolveDatabaseExecutor(databaseProduct);
    }
}
