# Dustman-jdbc

简单、快速、易用的jdbc持久化操作层。

## 示例

    //根据主键获取
    User user = jdbcDao.get(User.class, 177);
    
    //查询所有
    List<User> users = jdbcDao.findAll(User.class);
    long count = jdbcDao.findAllCount(User.class);

    // 根据实体不为null的属性作为条件,默认主键desc排序
    List<User> users = jdbcDao.findList(user);
    long count = jdbcDao.findCount(user);
    //分页查询
    Page<User> page = jdbcDao.findPage(user);
    //查询一条
    User user = jdbcDao.findOne(user);
    //查询首条
    User user = jdbcDao.findFirst(user);

    //insert
    Long userId = jdbcDao.executeInsert(user);

    //删除
    int deleted = jdbcDao.executeDeleteAll(User.class);
    int deleted = jdbcDao.executeDelete(User.class,1000L);
    int deleted = jdbcDao.executeDelete(user);

    //更新
    int updated = jdbcDao.executeUpdate(user);

    //更新null值
    jdbcDao.update(UserInfo.class)
        .updateNull()
        .setForBean(user)
        .where(UserInfo::getUserInfoId, 1L)
        .execute();

    //更新 userAge = userAge +1,不传参的情况
    //SQL: update User set user_age = user_age + 1 where user_id = 1000L
    jdbcDao.update(User.class)
            .set("{{userAge}}", "userAge+1")
            .where("userId", 1000L)
            .execute();
    
    //查询 自定义列，函数的情况
    //SQL: select user_age, count(*) num from user group by user_age order by num desc limit 0,10
    Page<Map<String,Object>> page = jdbcDao.selectFrom(User.class).addColumn("userAge,count(*) num")
            .groupBy(User::getUserAge)
            .orderBy("num",OrderBy.DESC)
            .paginate(1, 10)
            .disableCount()
            .findPageForMap();

    ## 组装括号、and、or的情况
    UserInfo userInfo = jdbcDao.selectFrom(UserInfo.class)
    .where()
    .openParen()
        .condition(UserInfo::getLoginName, SqlOperator.EQ, "whereAnd")
        .or(UserInfo::getEmail, SqlOperator.EQ, "123456@dustman.com")
    .closeParen()
    .and(UserInfo::getStatus, "normal")
    .findOne(UserInfo.class);

    //named方式，且自定义sql片断
    Map<String, Object> params = new HashMap<>();
    params.put("userInfoId", 40L);
    UserInfo userInfo = jdbcDao.selectFrom(UserInfo.class)
            .namedParameter()
            .where("userAge", SqlOperator.GT, 5)
            .and()
            .appendSegment("userInfoId = (select max(t2.userInfoId) from UserInfo t2 where t2.userInfoId < :userInfoId)", params)
            .findOne(UserInfo.class);

    //表别名
    List<UserInfo> users = jdbcDao.selectFrom(UserInfo.class).as("t1")
            .where("t1.loginName", SqlOperator.IN, new Object[]{"name-11", "name-12", "name-13"})
            .and("t1.userAge", SqlOperator.IN, new Object[]{11L, 12L, 13L})
            .findList(UserInfo.class);

    ## 批量更新
    String sql = "insert into UserInfo(userInfoId,loginName,password,userAge,status,userType,gmtCreate) values(?,?,?,?,?,?,?)";
    Object result = jdbcDao.executeBatchUpdate(sql, users, users.size(), (ps, paramNames, argument) -> {
        ps.setLong(1, argument.getUserInfoId());
        ps.setString(2, argument.getLoginName());
        ps.setString(3, argument.getPassword());
        ps.setInt(4, argument.getUserAge());
        ps.setString(5, argument.getStatus());
        ps.setString(6, argument.getUserType());
        ps.setObject(7, argument.getGmtCreate());
    });

    //执行自自定义sql
    int count = jdbcDao.nativeExecutor()
        .command("update UserInfo set loginName = ? where userInfoId = ?")
        .parameters("newName", 39L)
        .update();

    //多表联接查询
    List<Map<String, Object>> list = jdbcDao.selectFrom(UserInfo.class).as("t1")
        .innerJoin(Account.class).as("t2")
        .on(UserInfo::getUserInfoId, Account::getAccountId)
        //lambda自动识别属性属于哪个表
        .addColumn(UserInfo::getLoginName)
        //明确指定哪个表的属性
        .addAliasColumn("t2", "accountName")
        .findListForMap();
 
## 特点

- 省去各类dao或baseDao，只需一个jdbcDao
- 学习成本低，api跟sql高度一致，会sql即会使用
- 条件设置支持各类符号，=、!=、or、in、not in甚至是执行函数
- 允许指定排序字段，可以指定多个组合升降自由排序
- 支持显示指定或排除某个字段，以及添加额外的函数类字段，只取想要的数据
- 内置分页功能，自动进行分页处理无需再写分页代码
- 可以使用`{{}}`符号完成一些特殊的sql，例如`user_age = user_age + 1`这种不适合传参的情况
- 支持native方式执行自定义sql
- 支持整合Mybatis，以Mybatis的方式书写sql
- 易扩展，各组件如主键生成器、分页器、命令构建器(sql/hql等)、持久化实现等均可扩展或重写

> 组件本质上也只是封装了一个通用dao，只不过更加方便易用。当发现组件缺少某项功能或不能满足需求时仍可以用本来原生的方式执行，完全无影响。

#### 使用

添加依赖，默认使用`Spring Jdbc`实现，可更换成自己想要的实现：

    <dependency>
        <groupId>com.sonsure</groupId>
        <artifactId>sonsure-dustman-springjdbc</artifactId>
        <version>${version}</version>
    </dependency>
    
声明Bean，更多参数详见相关配置文档：

    <bean id="jdbcDao" class="com.sonsure.dustman.springjdbc.persist.SpringJdbcTemplateDaoImpl">
        <property name="dataSource" ref="dataSource"/>
    </bean>
    
传统注入方式使用JdbcDao：

    //不管哪个实体对象都使用该JdbcDao就可以
    @Autowired
    private JdbcDao jdbcDao;
    
    jdbcDao.get(User.class, 177);

## 相关文档

说明文档中可能会有xml方式配置或JavaConfig方式配置，两种方式效果是一样的，可根据情况自行转换。

- [约定](doc/usage.md)
- [初始化配置](doc/init-config.md)  
- [基本增删改查](doc/basic-crud.md)  
- [Insert|Update|Delete|Select用法](doc/executor-crud.md)
- [指定查询列及表别名使用](doc/spec-column.md)
- [拼接sql片断](doc/append-sql.md)
- [不传参{{ }}符号的使用](doc/not-param.md)
- [注解的使用](doc/use-annotation.md)
- [执行自定义sql](doc/native-sql.md)
- [整合Mybatis执行sql MybatisSqlSessionFactory](doc/mybatis-sql.md)
- [映射转换处理 MappingHandler](doc/mapping-handler.md)
- [分页处理 PageHandler](doc/page-handler.md)
- [主键生成 KeyGenerator](doc/key-generator.md)
- [持久化实现 PersistExecutor，扩展Hibernate示例抛砖引玉](doc/persist-executor-hibernate.md)
- [添加自定义拦截器 PersistInterceptor](doc/persist-interceptor.md)
- [sql的解析转换 CommandConversionHandler](doc/conversion-handler.md)
- [多数据源的使用](doc/multi-ds.md)
- [执行脚本](doc/execute-script.md)
- [named传参方式](doc/named-execute.md)
- [特殊参数处理器](doc/param-handler.md)
- [批量更新](doc/batch-update.md)

## 参与贡献

github：[https://github.com/selfly/sonsure-dustman](https://github.com/selfly/sonsure-dustman)  
gitee: [https://gitee.com/selfly/sonsure-dustman](https://gitee.com/selfly/sonsure-dustman)

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

