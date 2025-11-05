# 指定查询的列

有时在查询时只想要返回指定的列，特别是表中某个属性是text或lob等类型时。

## 指定返回列

以下查询只返回user_Info_Id、password、loginName：

    List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
    .addColumn("userInfoId", "password")
    .addColumn(UserInfo::getLoginName)
    .where("userAge", "<=", 10)
    .findList();

## 排除返回列

以下查询不返回password列：

    List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
    .dropColumn("password")
    .where("userAge", "<=", 10)
    .findList();

两个方法均可多次调用。

## 其它用法

本质上是指定一个查询的列，所以也可通过变通的方式完成某些操作，比如以下查询：

    Long maxId = jdbcDao.selectFrom(UserInfo.class)
    .addColumn("max(userInfoId) maxid")
    .findOneForScalar(Long.class);

## 表别名使用

表别名单表也可使用不过意义不大。

    List<Map<String, Object>> list1 = jdbcDao.selectFrom(UserInfo.class).as("t1")
            .addColumn("t1.loginName as name1")
            .findListForMap();

以下where条件使用不传参方式：

    List<Map<String, Object>> list1 = jdbcDao.selectFrom(UserInfo.class).as("t1")
            .addColumn("t1.loginName as name1")
            .where("{{t1.userInfoId}}", 1000L)
            .findListForMap();