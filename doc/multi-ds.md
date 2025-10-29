# 多数据源的使用

在本组件中，对于多个数据源的操作实际上是通过声明相应的`JdbcEngine`来完成的，即一个`DataSource`对应一个`JdbcEngine`。

以下示例配置分别声明了一个`mysql`和一个`oracle`的`JdbcEngine`操作对象：

    <bean id="mappingHandler" class="com.sonsure.dumper.core.mapping.DefaultMappingHandler">
        <constructor-arg name="modelPackages" value="com.sonsure.dumper.test.model.**"/>
    </bean>

    <bean id="mysqlJdbcTemplateEngine" class="com.sonsure.dumper.springjdbc.config.JdbcTemplateExecutorFactoryBean">
        <property name="dataSource" ref="mysqlDataSource"/>
        <property name="mappingHandler" ref="mappingHandler"/>
    </bean>

    <bean id="oracleKeyGenerator" class="com.sonsure.dumper.core.persist.OracleKeyGenerator"/>

    <bean id="oracleJdbcTemplateEngine" class="com.sonsure.dumper.springjdbc.config.JdbcTemplateExecutorFactoryBean">
        <property name="dataSource" ref="oracleDataSource"/>
        <property name="mappingHandler" ref="mappingHandler"/>
        <property name="keyGenerator" ref="oracleKeyGenerator"/>
    </bean>

    <bean id="jdbcDao" class="com.sonsure.dumper.springjdbc.persist.SpringJdbcTemplateDaoImpl">
        <property name="defaultJdbcEngine" ref="mysqlJdbcTemplateEngine"/>
        <property name="jdbcEngineMap">
            <map>
                <entry key="mysql" value-ref="mysqlJdbcTemplateEngine"/>
                <entry key="oracle" value-ref="oracleJdbcTemplateEngine"/>
            </map>
        </property>
    </bean>

`MappingHandler`、`KeyGenerator`等根据实际需要声明。

将不同的`JdbcEngine`注入到`JdbcDao`的`jdbcEngineMap`属性即可，key即为后面使用时指定的名称。

以上配置可以通过如下方式指定使用：

    //defaultJdbcEngine为mysql，不指定将使用mysql数据源，
    jdbcDao.get(UserInfo.class,100L);

    //指定使用mysql数据源
    jdbcDao.use("mysql").get(UserInfo.class,100L);

    //指定使用oracle数据源
    jdbcDao.use("oracle").get(TestUser.class,100L);