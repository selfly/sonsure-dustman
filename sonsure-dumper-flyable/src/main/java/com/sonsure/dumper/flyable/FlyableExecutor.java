package com.sonsure.dumper.flyable;

import com.sonsure.dumper.common.utils.VersionUtils;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.core.mapping.TablePrefixSupportHandler;
import com.sonsure.dumper.core.persist.AbstractDaoTemplateImpl;
import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.exception.FlyableException;
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

@Slf4j
public class FlyableExecutor {

    private static final String FLYABLE_HISTORY = "flyable_history";
    public static final String FLYABLE_GROUP = "flyable";
    public static final String FLYABLE_PREFIX_VAR = "flyablePrefix";

    private final JdbcDao jdbcDao;

    private final List<String> groupExecutionOrder;

    private final List<DatabaseExecutor> databaseExecutors;

    @Setter
    @Getter
    private String flyablePrefix = "";

    public FlyableExecutor(JdbcDao jdbcDao) {
        this.jdbcDao = jdbcDao;
        this.setFlyablePrefixWithJdbcDao();
        this.groupExecutionOrder = new ArrayList<>(8);
        this.groupExecutionOrder.add(FLYABLE_GROUP);
        this.databaseExecutors = new ArrayList<>(16);
        this.databaseExecutors.add(new MysqlDatabaseExecutorImpl());
        this.databaseExecutors.add(new H2DatabaseExecutorImpl());
    }

    public void migrate() {
        String databaseProduct = this.getDatabaseProduct();
        DatabaseExecutor databaseExecutor = this.getDatabaseExecutor(databaseProduct);
        List<MigrationResource> migrationResources = databaseExecutor.getMigrationResources();
        Map<String, List<MigrationResource>> map = migrationResources.stream().collect(Collectors.groupingBy(MigrationResource::getGroup));
        boolean flyableHistoryInitialized = databaseExecutor.existFlyableHistory(jdbcDao, this.getFlyablePrefix() + FLYABLE_HISTORY);
        //指定顺序的初始化
        for (String group : this.groupExecutionOrder) {
            List<MigrationResource> platformResources = map.remove(group);
            this.updateMigrate(platformResources, group, flyableHistoryInitialized, databaseExecutor);
            //执行过一次之后，肯定为true
            flyableHistoryInitialized = true;
        }
        //未指定顺序的初始化
        for (Map.Entry<String, List<MigrationResource>> entry : map.entrySet()) {
            this.updateMigrate(entry.getValue(), entry.getKey(), true, databaseExecutor);
        }
    }

    protected void setFlyablePrefixWithJdbcDao() {
        if (!(jdbcDao instanceof AbstractDaoTemplateImpl)) {
            return;
        }
        MappingHandler mappingHandler = ((AbstractDaoTemplateImpl) jdbcDao).getDefaultJdbcEngine().getJdbcEngineConfig().getMappingHandler();
        if (mappingHandler instanceof TablePrefixSupportHandler) {
            this.flyablePrefix = ((TablePrefixSupportHandler) mappingHandler).getTablePrefix(FlyableHistory.class.getName());
        }
    }

    protected String getDatabaseProduct() {
        return jdbcDao.getDatabaseProduct();
    }

    protected void updateMigrate(List<MigrationResource> resources, String migrationGroup, boolean flyableHistoryInitialized, DatabaseExecutor databaseExecutor) {
        String installedVersion = "0.0.0";
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
                installedVersion = flyableHistory.getVersion();
                Map<String, String> collect = list.stream().collect(Collectors.toMap(FlyableHistory::getScript, FlyableHistory::getChecksum));
                checksumMap.putAll(collect);
            }
        }
        resources.sort((o1, o2) -> VersionUtils.compareVersion(o1.getVersion(), o2.getVersion()));
        for (MigrationResource resource : resources) {
            if (VersionUtils.compareVersion(resource.getVersion(), installedVersion) <= 0) {
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
            flyableHistory.setType(FlyableHistory.TYPE_SQL);
            flyableHistory.setDescription(resource.getDescription());
            flyableHistory.setScript(resource.getFilename());
            flyableHistory.setChecksum(resource.getChecksum());
            flyableHistory.setGmtInstalled(LocalDateTime.now());

            long begin = System.currentTimeMillis();
            try {
                if (FLYABLE_GROUP.equals(migrationGroup)) {
                    resource.addVariable(FLYABLE_PREFIX_VAR, this.getFlyablePrefix());
                }
                databaseExecutor.executeResource(jdbcDao, resource);
                long end = System.currentTimeMillis();
                flyableHistory.setExecutionTime(end - begin);
            } catch (Exception e) {
                flyableHistory.setSuccess(false);
                jdbcDao.executeInsert(flyableHistory);
                throw new FlyableException(e);
            }
            flyableHistory.setSuccess(true);
            jdbcDao.executeInsert(flyableHistory);
        }
    }

    protected boolean verifyChecksum(MigrationResource resource, Map<String, String> checksumMap) {
        String checksum = checksumMap.get(resource.getFilename());
        if (checksum == null) {
            return true;
        }
        return StringUtils.equals(checksum, resource.getChecksum());
    }

    protected DatabaseExecutor getDatabaseExecutor(String databaseProduct) {
        for (DatabaseExecutor databaseExecutor : databaseExecutors) {
            if (databaseExecutor.support(databaseProduct)) {
                return databaseExecutor;
            }
        }
        throw new FlyableException("不支持的DatabaseExecutor");
    }
}
