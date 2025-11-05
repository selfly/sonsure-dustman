# 批量更新

当有大量数据需要更新，一般是insert，单条处理跟批量处理性能不可同日而语。

组件也提供了批量更新接口。

## 示例一，直接使用sql

直接传入sql执行，不再进行加工：

    List<UserInfo> userInfoList = new ArrayList<>();
    for (int i = 1; i < 10000; i++) {
        UserInfo user = new UserInfo();
        user.setUserInfoId((long) i);
        user.setLoginName("name-" + i);
        user.setPassword("123456-" + i);
        user.setUserAge(i);
        user.setGmtCreate(new Date());
        userInfoList.add(user);
    }

    String sql = "INSERT INTO USER_INFO (PASSWORD, LOGIN_NAME, GMT_CREATE, USER_AGE, USER_INFO_ID) VALUES (?, ?, ?, ?, ?)";
    jdbcDao.batchUpdate()
            .forceNative() //直接使用sql
            .execute(sql, userInfoList, 1000, (ps, names, userInfo) -> {
                ps.setString(1, userInfo.getPassword());
                ps.setString(2, userInfo.getLoginName());
                ps.setDate(3, new java.sql.Date(userInfo.getGmtCreate().getTime()));
                ps.setInt(4, userInfo.getUserAge());
                ps.setLong(5, userInfo.getUserInfoId());
            });
            
## 示例二，使用类名和属性名

写的语句中使用类名和属性名，将会经过转换处理：

    List<UserInfo> userInfoList = //... 略
    
    String sql1 = "INSERT INTO UserInfo (PASSWORD, loginName, gmtCreate, userAge, userInfoId) VALUES (?, ?, ?, ?, ?)";
    jdbcDao.executeBatchUpdate(sql1, userInfoList, userInfoList.size(), (ps, names, userInfo) -> {
        ps.setString(1, userInfo.getPassword());
        ps.setString(2, userInfo.getLoginName());
        ps.setDate(3, new java.sql.Date(userInfo.getGmtCreate().getTime()));
        ps.setInt(4, userInfo.getUserAge());
        ps.setLong(5, userInfo.getUserInfoId());
    });
    
## 示例三，使用named方式

适合某些特殊场景下，sql需要动态组装，更新的列或参数预先不知道的情况，可以动态的获取sql中的参数`:name`和值：

    String sql = "INSERT INTO USER_INFO (PASSWORD, LOGIN_NAME, GMT_CREATE, USER_AGE, USER_INFO_ID) VALUES (:PASSWORD, :LOGIN_NAME, :GMT_CREATE, :USER_AGE, :USER_INFO_ID)";
    List<Map<String, Object>> userInfoList = new ArrayList<>();
    for (int i = 1; i < 10000; i++) {
        Map<String, Object> map = new LinkedCaseInsensitiveMap<>();
        map.put("password", "123456-" + i);
        map.put("login_name", "name-" + i);
        map.put("gmt_create", new Date());
        map.put("user_age", i);
        map.put("USER_INFO_ID", (long) i);
        userInfoList.add(map);
    }
    jdbcDao.batchUpdate()
            .forceNative()
            .namedParameter()
            .execute(sql, userInfoList, userInfoList.size(), (ps, names, map) -> {
                for (int j = 0; j < names.size(); j++) {
                    Object val = map.get(names.get(j));
                    if (val == null) {
                        throw new RuntimeException("参数不存在");
                    }
                    ps.setObject(j + 1, val);
                }
            });