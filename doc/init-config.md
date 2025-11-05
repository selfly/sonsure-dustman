# 初始化配置

## 基本配置

如果使用默认配置，只需要配置JdbcTemplate对象即可。

    // jdbcTemplate对象
    @Bean
    public JdbcOperations jdbcOperations(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

**方式一**，直接使用FactoryBean

    @Bean
    public SpringJdbcTemplateDaoFactoryBean jdbcDao(JdbcOperations jdbcOperations) {
        SpringJdbcTemplateDaoFactoryBean factoryBean = new SpringJdbcTemplateDaoFactoryBean();
        factoryBean.setPersistExecutor(new JdbcTemplatePersistExecutor(jdbcOperations));
        return factoryBean;
    }

**方式二**，通过JdbcContext手动创建

    @Bean
    public JdbcDao jdbcDao(JdbcOperations jdbcOperations) {
        JdbcContextImpl jdbcContext = new JdbcContextImpl();
        jdbcContext.setPersistExecutor(new JdbcTemplatePersistExecutor(jdbcOperations));
        JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
        jdbcDao.setJdbcContext(jdbcContext);
        return jdbcDao;
    }

## jdbcContext配置参数说明

- commandExecutorFactory command执行器构建工厂，如有必要可以自定义构建工厂来扩展或改变Insert、Select、Update、Delete的行为
- mappingHandler 实体类名、属性名到表名、列名的转换处理器
- pageHandler 分页处理器，默认提供了Mysql、Oracle、Postgresql、SqlServer、SQLite、H2分页
- keyGenerator 主键生成器，为空表示数据库生成，如常见的自增id
- persistExecutor 持久化执行器
- persistInterceptors 持久化执行拦截器
- commandConversionHandler command解析转换处理器，默认使用JSqlParser
- mybatisSqlSessionFactory Mybatis的SqlSessionFactory，整合Mybatis时设置
- caseStyle 最终的sql大小写样式，`UPPERCASE`大写，`LOWERCASE`小写，`NONE`或为空不处理

## jdbcDao配置参数说明

- jdbcContext 默认使用的jdbcContext
- jdbcContextMap 多个JdbcContext对象Map，多数据源时使用
