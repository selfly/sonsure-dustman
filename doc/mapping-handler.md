# MappingHandler使用及扩展

`MappingHandler`的存在就一个作用，将类名、属性名和表名、列名进行互转。

接口定义如下，看方法名就能知道每个方法具体的作用：

    public interface MappingHandler {
    
        /**
         * 根据实体名获取表名
         *
         * @param className the class name
         * @param params    the params
         * @return table name
         */
        String getTable(String className, Map<String, Object> params);
    
        /**
         * 根据实体名获取表名
         *
         * @param clazz  the clazz
         * @param params the params
         * @return table name
         */
        String getTable(Class<?> clazz, Map<String, Object> params);
    
        /**
         * 根据类名获取主键字段名
         *
         * @param clazz the clazz
         * @return pK name
         */
        String getPkField(Class<?> clazz);
    
        /**
         * 根据属性名获取列名
         *
         * @param clazzName the clazz name
         * @param fieldName the field name
         * @return column name
         */
        String getColumn(String clazzName, String fieldName);
    
        /**
         * 根据属性名获取列名
         *
         * @param clazz     the clazz
         * @param fieldName the field name
         * @return column name
         */
        String getColumn(Class<?> clazz, String fieldName);
    
        /**
         * 根据列获取属性
         *
         * @param clazz
         * @param columnName
         * @return
         */
        String getField(Class<?> clazz, String columnName);
    }

默认提供了`DefaultMappingHandler`实现，继承于`AbstractMappingHandler`，遵守了前面的[约定](usage.md)。

另外提供了几个常用的参数可供配置：

**modelPackages** 

实体类所在包，写法跟spring扫描包一致，支持通配符，多个用英文逗号隔开。

当写自定义sql使用短类名时需要指定，由构造方法传入。

**customClassMapping**

添加自定义的全类名到短类名的映射，一般情况下用不到。

当不同的包下有相同的类名，又想在sql中使用短类名时可以使用。

**tablePreFixMap**

表前缀定义，很多时候不同的项目或模块拥有不同的表前缀，但映射的实体类不会有前缀。例如`ss_user`表对应实体类为`User`.

    Map<String, String> tablePrefixMap = new LinkedHashMap<>();
    tablePrefixMap.put("com.sonsure", "ss_");
    mappingHandler.setTablePrefixMap(tablePrefixMap);
    
以上配置将在处理`com.sonsure`包下的所有实体类名转换时，自动添加`ss_`前缀。

*PS:当实体类上有使用注解时，会优先使用注解内指定的名称。*

当默认提供的`DefaultMappingHandler`不能满足需求时，可以自行实现或重写相关转换方法。

然后在声明JdbcEngine时设置：

    <bean id="jdbcTemplateEngine" class="com.sonsure.dumper.springjdbc.config.JdbcTemplateEngineFactoryBean">
        <!-- ...... -->
        <property name="mappingHandler" ref="customMappingHandler"/>
    </bean>