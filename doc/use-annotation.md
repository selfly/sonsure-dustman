# 使用注解

*注意：使用注解即表示打破了约定优于配置的原则，非特殊情况不推荐使用。*

*当使用了注解同时又扩展了`MappingHandler`，`MappingHandler`将不起作用。*

组件默认提供了4个注解，跟Hibernate类似，也可以直接使用Hibernate支持的`javax.persistence`包下的注解：

- `Entity` 表注解，用来指定表名
- `Column` 列注解，用来指定列名
- `Id` 主键属性注解
- `Transient` 表示忽略，即实体类中有数据库中无的属性。

下面演示如何使用。

建立数据库表，这里所有的列都以下划线结尾：

    CREATE TABLE `ktx_user_info` (
      `USER_INFO_ID_` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
      `LOGIN_NAME_` varchar(32) DEFAULT 'NULL' COMMENT '登录名',
      `PASSWORD_` varchar(32) DEFAULT 'NULL' COMMENT '密码',
      `USER_AGE_` int(3) DEFAULT NULL COMMENT '姓名',
      `GMT_CREATE_` datetime DEFAULT NULL COMMENT '创建时间',
      `GMT_MODIFY_` datetime DEFAULT NULL COMMENT '编辑时间',
      PRIMARY KEY (`USER_INFO_ID_`)
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户';

定义实体类，类名和主键属性名跟数据库表中并不对应，`@Id`指定了主键属性，所有的列名因为数据库都是以`_`结尾所以都使用了注解，

另外`testName`属性在数据库中并不存在，使用`@Transient`注解忽略。

    @Entity("ktx_user_info")
    public class KUserInfo extends Model {
    
        @Id
        @Column("USER_INFO_ID_")
        private Long rowId;
    
        @Column("LOGIN_NAME_")
        private String loginName;
    
        @Column("PASSWORD_")
        private String password;
    
        @Column("USER_AGE_")
        private Integer userAge;
    
        @Column("GMT_CREATE_")
        private Date gmtCreate;
    
        @Column("GMT_MODIFY_")
        private Date gmtModify;
    
        @Transient
        private String testName;
    
        //getter and setter
    }

添加好注解后，仍然可以像其它遵守约定的实体类一样正常操作：

    //insert
    KUserInfo ku = new KUserInfo();
    ku.setLoginName("insertName");
    ku.setPassword("123456");
    ku.setUserAge(18);
    ku.setGmtCreate(new Date());
    Long id = (Long) jdbcDao.executeInsert(ku);
    
    //update
    KUserInfo ku = new KUserInfo();
    ku.setRowId(36L);
    ku.setPassword("787878");
    ku.setGmtModify(new Date());
    int count = jdbcDao.executeUpdate(ku);
    
    //get
    KUserInfo annotationUserInfo = jdbcDao.get(KUserInfo.class, id);
    
    //delete
    int count = jdbcDao.executeDelete(KUserInfo.class, id);