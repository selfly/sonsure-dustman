# 整合Mybatis

Mybatis是一个优秀的框架，但也有它的不足之处。

一个分页插件，一个通用mapper都能玩出那么多的花样还有这么高的人气，足以说明它的痛点。

那么在使用本组件带来方便的同时，能不能再集成Mybatis进行独立的sql管理呢？

答案当然是可以的。

## Mybatis配置

首先当然是要配置Mybatis，这跟平时使用没什么两样，可自行百度，这里列出简单的配置。

添加依赖：

    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>${version}</scope>
    </dependency>
    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis-spring</artifactId>
        <version>${version}</scope>
    </dependency>

声明Mybatis的`sqlSessionFactory`：

    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="configLocation" value="mybatis/mybatis-config.xml"/>
        <property name="dataSource" ref="dataSource"/>
    </bean>
    
然后将`sqlSessionFactory`注入到我们的`jdbcContext`就可以了：

    <bean id="jdbcTemplateEngine" class="com.sonsure.dustman.springjdbc.config.JdbcTemplateExecutorFactoryBean">
        <property name="mybatisSqlSessionFactory" ref="sqlSessionFactory"/>
        <property name="dataSource" ref="dataSource"/>
    </bean>
 
从以上可以看出，我们只是需要拿到Mybatis的`sqlSessionFactory`注入到`jdbcContext`就可以了，至于Mybatis其它的东西并不关心。

这也就表示Mybatis原生使用的方法和接口都不受影响。

## 使用

在xml中声明一个sql方法，这些都是Mybatis本身的东西，写法上并没有任何区别，如果使用Mybatis注解的方式，也是可以的：

    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="com.sonsure.mybatis.mapper.UserMapper">
    
        <!-- 这里写的sql使用的是类名和属性名 -->
        <select id="getUser">
            SELECT * FROM UserInfo WHERE userInfoId = #{id} and loginName = #{loginName}
        </select>
        
        <!-- 这里写的sql直接使用表名和列名 -->
        <select id="getUserSql">
            SELECT * FROM user_info WHERE user_info_id = #{id} and login_name = #{loginName}
        </select>
    
    </mapper>
    
在代码中调用，command指定Mybatis的id即可，namespace根据实际情况可以省略：

    //这里注意，默认Mybatis中写的sql也会经过类名、属性名到sql表名、列名的转换
    UserInfo user = jdbcDao.myBatisExecutor()
            .command("getUser")
            .parameter("id", 9L)
            .parameter("loginName", "name-9")
            .singleResult(UserInfo.class);
            
    //这里指定了nativeCommand，sql将不经过转换
    Map<String, Object> params = new HashMap<>();
    params.put("id", 9L);
    params.put("loginName", "name-9");

    UserInfo user = jdbcDao.myBatisExecutor()
            .command("getUserSql")
            .parameters(params)
            .nativeCommand()
            .singleResult(UserInfo.class);
            
此种方式调用，Mybatis中可以不必再写分页等相关代码，如果需要，直接指定分页参数将自动完成分页：

    Page<UserInfo> page = jdbcDao.myBatisExecutor()
            .command("queryUserList")
            .paginate(1, 10) //第一页，取10条
            .pageResult(UserInfo.class);