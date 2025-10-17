# 初始化配置

## 基本配置

如果使用默认配置，只需要声明以下bean，传入`dataSource`即可：

    <bean id="jdbcDao" class="com.sonsure.dumper.springjdbc.persist.SpringJdbcTemplateDaoImpl">
        <property name="dataSource" ref="dataSource"/>
    </bean>

## 自定义配置

自定义一些配置时，通过配置`jdbcEngine`参数来完成，以下是使用 Spring Jdbc 相应的实现类`JdbcTemplateEngineFactoryBean`示例：

    <bean id="jdbcTemplateEngine" class="com.sonsure.dumper.springjdbc.config.JdbcTemplateEngineFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="toggleCase" value="lower"/>
    </bean>

    <bean id="jdbcDao" class="com.sonsure.dumper.springjdbc.persist.SpringJdbcTemplateDaoImpl">
        <property name="jdbcEngine" ref="jdbcTemplateEngine"/>
    </bean>
    
*注意：`jdbcDao`中的属性`dataSource`和`jdbcEngine`二选一，不能同时为空。两个都有以`jdbcEngine`为准。*

## jdbcEngine配置参数说明 

- dataSource 数据源
- commandExecutorFactory command执行器构建工厂，如有必要可以自定义构建工厂来改变Insert、Select、Update、Delete行为
- mappingHandler 实体类名、属性名到表名、列名的转换处理器
- pageHandler 分页处理器，默认提供了Mysql、Oracle分页
- keyGenerator 主键生成器，为空表示数据库生成，如常见的自增id
- persistExecutor 持久化执行器
- commandConversionHandler command解析转换处理器，默认使用JSqlParser
- mybatisSqlSessionFactory Mybatis的SqlSessionFactory，整合Mybatis时设置
- toggleCase 最终的sql大小写，`upper`大写，`lower`小写，为空不处理

## jdbcDao配置参数说明

- dataSource 数据源
- defaultJdbcEngine 默认使用的JdbcEngine
- jdbcEngineMap 多个JdbcEngine对象Map，多数据源时使用
