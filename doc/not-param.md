# `{{}}`符号的使用

某些情况下，在执行sql时不能通过传参的方式。

例如统一更新用户表的用户年龄+1，需要执行这样的sql：update user set user_age = user_age + 1

这时候就不能使用传参方式了，又不想自己写sql，那么可以借助`{{}}`符号来完成。

    jdbcDao.update(User.class)
        .set("{{userAge}}", "userAge+1")
        .execute();
        
同理，`{{}}`符号也可以用在where条件中：

    List<UserInfo> userInfos = jdbcDao.selectFrom(UserInfo.class)
            .where("{{userAge}}", "userInfoId")
            .findList(UserInfo.class);

最终的sql：SELECT ... FROM USER_INFO WHERE USER_AGE = USER_INFO_ID         