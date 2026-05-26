# 多数据源的使用

在本组件中，对于多个数据源的操作实际上是通过声明相应的 `JdbcContext` 来完成的，即一个 `DataSource` 对应一个 `JdbcContext`。

## 配置方式一：使用 FactoryBean

多个 `JdbcTemplateDaoFactoryBean` 各对应一个数据源：

```java
@Bean
public JdbcTemplateDaoFactoryBean mysqlJdbcContext(DataSource mysqlDataSource) {
    JdbcTemplateDaoFactoryBean factoryBean = new JdbcTemplateDaoFactoryBean();
    factoryBean.setPersistExecutor(new JdbcTemplatePersistExecutor(mysqlDataSource));
    return factoryBean;
}

@Bean
public JdbcTemplateDaoFactoryBean oracleJdbcContext(DataSource oracleDataSource) {
    JdbcTemplateDaoFactoryBean factoryBean = new JdbcTemplateDaoFactoryBean();
    factoryBean.setPersistExecutor(new JdbcTemplatePersistExecutor(oracleDataSource));
    // Oracle 序列主键生成器
    factoryBean.setKeyGenerator(new OracleKeyGenerator());
    return factoryBean;
}

@Bean
public JdbcDao jdbcDao(JdbcContext mysqlJdbcContext, JdbcContext oracleJdbcContext) {
    JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
    jdbcDao.setJdbcContext(mysqlJdbcContext); // 默认数据源
    Map<String, JdbcContext> contextMap = new HashMap<>();
    contextMap.put("mysql", mysqlJdbcContext);
    contextMap.put("oracle", oracleJdbcContext);
    jdbcDao.setJdbcContextMap(contextMap);
    return jdbcDao;
}
```

## 配置方式二：手动创建 JdbcContext

```java
@Bean
public JdbcContext mysqlJdbcContext(DataSource mysqlDataSource) {
    JdbcContextImpl jdbcContext = new JdbcContextImpl();
    jdbcContext.setPersistExecutor(new JdbcTemplatePersistExecutor(mysqlDataSource));
    return jdbcContext;
}

@Bean
public JdbcContext oracleJdbcContext(DataSource oracleDataSource) {
    JdbcContextImpl jdbcContext = new JdbcContextImpl();
    jdbcContext.setPersistExecutor(new JdbcTemplatePersistExecutor(oracleDataSource));
    jdbcContext.setKeyGenerator(new OracleKeyGenerator());
    return jdbcContext;
}

@Bean
public JdbcDao jdbcDao(JdbcContext mysqlJdbcContext, JdbcContext oracleJdbcContext) {
    JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
    jdbcDao.setJdbcContext(mysqlJdbcContext);
    Map<String, JdbcContext> contextMap = new HashMap<>();
    contextMap.put("mysql", mysqlJdbcContext);
    contextMap.put("oracle", oracleJdbcContext);
    jdbcDao.setJdbcContextMap(contextMap);
    return jdbcDao;
}
```

## 使用

```java
// 默认数据源（mysql）
jdbcDao.get(UserInfo.class, 100L);

// 指定使用 mysql 数据源
jdbcDao.use("mysql").get(UserInfo.class, 100L);

// 指定使用 oracle 数据源
jdbcDao.use("oracle").get(TestUser.class, 100L);
```

配置说明：`MappingHandler`、`KeyGenerator`、`PageHandler` 等组件均可根据不同数据源按需独立配置。
