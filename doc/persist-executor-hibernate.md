# 持久化实现 persistExecutor

## 接口说明

对于最终的持久化操作，都是通过`PersistExecutor`接口来执行的，因此只需要实现不同的`PersistExecutor`就可以有不同的数据库交互实现。

`PersistExecutor`接口的定义十分简单，就两个方法：

    public interface PersistExecutor {
    
        /**
         * 获取数据库方言
         *
         * @return
         */
        String getDialect();
    
        /**
         * 执行command
         *
         * @param commandDetails
         * @param executionType
         * @return
         */
        Object execute(CommandContext commandDetails, CommandType executionType);
    }
    
`getDialect`方法是获取数据库方言，特定实现甚至可以是固定或静态配置，就不多讲了。

`execute`方法才是交互的重点，所有的sql执行都是通过它来实现。

当然，真正在执行时会需要区分CRUD不同的操作，抽象类`AbstractPersistExecutor`对execute方法做了扩展，通过`CommandType`来区分：

    public abstract class AbstractPersistExecutor implements PersistExecutor {
    
        //......
    
        @Override
        public Object execute(CommandContext commandDetails, CommandType executionType) {
            switch (executionType) {
                case INSERT:
                    return this.insert(commandDetails);
                case QUERY_FOR_LIST:
                    return this.queryForList(commandDetails);
                case QUERY_SINGLE_RESULT:
                    return this.querySingleResult(commandDetails);
                case QUERY_FOR_MAP:
                    return this.queryForMap(commandDetails);
                case QUERY_FOR_MAP_LIST:
                    return this.queryForMapList(commandDetails);
                case QUERY_ONE_COL:
                    return this.queryOneCol(commandDetails);
                case QUERY_ONE_COL_LIST:
                    return this.queryOneColList(commandDetails);
                case UPDATE:
                    return this.update(commandDetails);
                case DELETE:
                    return this.delete(commandDetails);
                case EXECUTE:
                    return this.doExecute(commandDetails);
                default:
                    throw new SonsureJdbcException("不支持的CommandType:" + executionType);
            }
        }
        //.......
    }
    
既然如此，为什么不直接对接口`execute`方法做拆分呢？考虑到后期扩展的方便性，比如统一的sql打印、拦截器的添加，因此保持了入口的唯一。

看到上面的代码相信并不需要做太多的说明，就已经知道怎么样来扩展一个`PersistExecutor`了。

默认提供了`Spring Jdbc`的实现`JdbcTemplatePersistExecutor`(之前版本还有DBUtils实现，用的太少就去掉了)，具体可以查看源码了解。

## Hibernate实现示例

这只是一个抛砖引玉的示例，目的是试图说明如何扩展该组件。

并不推荐使用`Hibernate`实现，在真正场景下如果`Hibernate`被这样使用，先不说对不对，“脱裤子放屁”的嫌疑肯定是免不了的。

以下是`Hibernate`对于`PersistExecutor`接口实现的部分伪代码，仅实现了列表查询，供参考。如果想要使用`hql`而不是`nativeQuery`可以重写`CommandContextBuilder`：

    public class HibernatePersistExecutor extends AbstractPersistExecutor {
    
        private SessionFactory sessionFactory;
    
        @Override
        protected String doGetDialect() {
            Session session = sessionFactory.openSession();
            String dialect = session.doReturningWork(new ReturningWork<String>() {
                @Override
                public String execute(Connection connection) throws SQLException {
                    return connection.getMetaData().getDatabaseProductName().toLowerCase();
                }
            });
            session.close();
            return dialect;
        }
    
        @Override
        public List<?> queryForList(CommandContext commandDetails) {
            Session session = sessionFactory.openSession();
            NativeQuery<?> nativeQuery = session.createNativeQuery(commandDetails.getCommand(), commandDetails.getResultType());
            List<Object> parameters = commandDetails.getParameters();
            for (int i = 0; i < parameters.size(); i++) {
                nativeQuery.setParameter(i + 1, parameters.get(i));
            }
            List<?> resultList = nativeQuery.getResultList();
            session.close();
            return resultList;
        }
        
        //......
    }

然后在配置文件中增加对Hibernate的声明，并将`HibernatePersistExecutor`设置到`JdbcEngine`：

    <bean id="sessionFactory" class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="hibernateProperties">
            <props>
                <prop key="hibernate.dialect">org.hibernate.dialect.MySQLDialect</prop>
                <prop key="hibernate.show_sql">true</prop>
            </props>
        </property>
        <property name="annotatedClasses">
            <list>
                <value>com.sonsure.dustman.test.model.HbUserInfo</value>
            </list>
        </property>
    </bean>

    <bean id="mappingHandler" class="com.sonsure.dustman.jdbc.mapping.DefaultMappingHandler">
        <constructor-arg name="modelPackages" value="com.sonsure.dustman.test.model.**"/>
    </bean>

    <bean id="hibernatePersistExecutor" class="com.sonsure.dustman.test.jdbc.extension.executor.HibernatePersistExecutor">
        <property name="sessionFactory" ref="sessionFactory"/>
    </bean>

    <bean id="jdbcExecutorConfig" class="com.sonsure.dustman.jdbc.config.JdbcExecutorConfigImpl">
        <property name="dataSource" ref="dataSource"/>
        <property name="mappingHandler" ref="mappingHandler"/>
        <property name="persistExecutor" ref="hibernatePersistExecutor"/>
    </bean>

    <bean id="jdbcContext" class="com.sonsure.dustman.jdbc.config.JdbcContextImpl">
        <property name="jdbcExecutorConfig" ref="jdbcExecutorConfig"/>
    </bean>

    <bean id="jdbcDao" class="com.sonsure.dustman.jdbc.persist.JdbcDaoImpl">
        <property name="defaultJdbcEngine" ref="jdbcContext"/>
    </bean>
    
同样使用`jdbcDao`调用：

    List<HbUserInfo> userInfos = jdbcDao.find(HbUserInfo.class);
    
这时底层与数据库的交互已经换成Hibernate了。