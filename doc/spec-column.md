# 指定查询的列

有时在查询时只想要返回指定的列，特别是表中某个属性是text或lob等类型时。

## 指定返回列

以下查询只返回user_Info_Id、password、loginName：

    List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
    .addColumn("userInfoId", "password")
    .addColumn(UserInfo::getLoginName)
    .where("userAge", "<=", 10)
    .list();

## 排除返回列

以下查询不返回password列：

    List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
    .dropColumn("password")
    .where("userAge", "<=", 10)
    .list();

两个方法均可多次调用。

注：当同时指定`addColumn`和`dropColumn`时，以addColumn为准。

## 其它用法

本质上是指定一个查询的列，所以也可通过变通的方式完成某些操作，比如以下查询：

    Long maxId = jdbcDao.selectFrom(UserInfo.class)
    .addColumn("max(userInfoId) maxid")
    .oneColResult(Long.class);

## 表别名使用

表别名单表也可使用不过意义不大。
以下查询，from两张表并使用了表别名及列别名。

    List<Map<String, Object>> list1 = jdbcDao.selectFrom(UserInfo.class).tableAlias("t1")
            .from(Account.class, "t2")
            .addColumn("t1.loginName as name1", "t2.loginName as name2")
            .where()
            .append("t1.userInfoId = t2.accountId")
            .listMaps();

以下where条件使用不传参方式，与上面等价：

    List<Map<String, Object>> list1 = jdbcDao.selectFrom(UserInfo.class).tableAlias("t1")
            .from(Account.class, "t2")
            .addColumn("t1.loginName as name1", "t2.loginName as name2")
            .where("{{t1.userInfoId}}", "t2.accountId")
            .listMaps();