# 约定

本组件遵循约定优于配置的原则，这在大部分情况下能省去大量的繁琐配置。

典型约定如下，以具体实例方式说明，数据库相关名称只注重下划线不关心大小写：

- 表与实体类约定 实体类中的属性应该与数据库表中的列一一对应，没有多余的属性。static final类型不在此列，如serialVersionUID等。
- 表名与实体类名称约定 USER_INFO表实体类名为UserInfo。
- 列名与属性名约定 LOGIN_NAME列实体类中属性名为loginName。
- 主键名约定 USER_INFO表主键名为USER_INFO_ID，同理实体类中属性名为userInfoId。
- Oracle序列名约定 USER_INFO表对应的主键序列名为SEQ_USER_INFO

遵循以上约定后，一个实体类中不需要任何的额外配置或注解，显得十分的干净整洁。

当然，这些约定都可以通过配置或扩展改变它，但不建议这么做，这本身就是一个良好的规范。

对于一些通用的属性可能会有属性提取，类继承的情况，可以在父类添加`BaseProperties`注解，这样继承的子类在处理时也会处理父类中定义的属性。

**示例**
    
父类：

    @BaseProperties
    public class BaseUser {
    
        private Date gmtCreate;
        private Date gmtModify;
    }

子类：

    public class UserInfo extends BaseUser {
    
        private Long userInfoId;
    
        //...
    }

当操作`UserInfo`时，会包含`gmtCreate`和`gmtModify`两个属性。