# 执行自定义sql

组件提供了`NativeExecutor`来执行自定义sql。

    int count = jdbcDao.nativeExecutor()
            .command("update `com.sonsure.dustman.test.model.UserInfo` set loginName = ? where userInfoId = ?")
            .parameters(new Object[]{"newName", 39L})
            .update();
            
sql中的表名以及列名都是对应实体类的类名和属性名，会自动进行转换。

可以发现，这里实体类我们使用了全名称`com.sonsure.dustman.test.model.UserInfo`,这是因为`native`方式组件并不知道有哪些对应的实体类，所以用全名称指定。

当然，用类全名称来写sql毕竟比较麻烦，所以在初始化时可以配置`MappingHandler`来指定扫描的包，更多配置说明见MappingHandler一节：

    <bean id="mappingHandler" class="com.sonsure.dustman.jdbc.mapping.DefaultMappingHandler">
        <constructor-arg name="modelPackages" value="com.sonsure.dustman.test.model.**"/>
    </bean>

    <bean id="jdbcTemplateEngine" class="com.sonsure.dustman.springjdbc.config.JdbcTemplateExecutorFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="mappingHandler" ref="mappingHandler"/>
    </bean>

    <bean id="jdbcDao" class="com.sonsure.dustman.springjdbc.persist.SpringJdbcTemplateDaoImpl">
        <property name="jdbcContext" ref="jdbcTemplateEngine"/>
    </bean>
    
这样，我们就可以使用短名称来执行sql了：

    int count = jdbcDao.nativeExecutor()
            .command("update UserInfo set loginName = ? where userInfoId = ?")
            .parameters(new Object[]{"newName", 39L})
            .update();
            
*小提示：如果没有指定扫描的包，但是sql中的实体类之前有用其它class的方式执行过，那么短名称也能成功。*

如果不想经过sql的解析转换处理，需要执行100%原生的sql，可以指定`nativeCommand`：

    int count = jdbcDao.nativeExecutor()
            .command("update user_info set login_name = ? where user_info_id = ?")
            .parameters(new Object[]{"newName", 39L})
            .nativeCommand() //不经过转换处理
            .update();
            
*注意：指定了`nativeCommand()`后，由于不经过转换处理，在水平分表时会无法动态处理表名。*




