# named传参方式

sql使用named书写方式并传参，类似于`NamedParameterJdbcTemplate`执行方式支持。

示例，可以是单参数或者List、String[]等：

    List<Long> ids = new ArrayList<>();
    ids.add(39L);
    ids.add(40L);

    Long[] userInfoId2 = new Long[]{23L, 24l};

    Map<String, Object> params = new HashMap<>();
    params.put("loginName", "newName");
    params.put("userInfoId", ids);
    params.put("userInfoId2", userInfoId2);

    String sql = "update User_Info set login_Name = :loginName where user_Info_Id in (:userInfoId) or user_info_id in (:userInfoId2)";

    int count = jdbcDao.nativeExecutor()
            .namedParameter()
            .nativeCommand()
            .command(sql)
            .parameters(params)
            .update();
    Assert.assertEquals(4, count);
