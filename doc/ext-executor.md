# 扩展 CommandExecutor

通过`JdbcDao`操作Insert、Select、Update、Delete、nativeExecutor、mybatisExecutor本质上都是创建了一个`CommandExecutor`的实现。

对于上述几个内置的CommandExecutor对象都是由`CommandExecutorBuilderImpl`来进行构建的。

可以实现自己的`CommandExecutor`来整合不同的执行方式。

## 实现逻辑

`CommandExecutor`的生产是在`CommandExecutorFactory`内进行的，`CommandExecutorFactory`负责找到对应的`CommandExecutorBuilder`然后构建出具体的CommandExecutor对象。

而`CommandExecutor`执行时需要的command上下文信息由其内部的`CommandContextBuilder`来完成，在构建`CommandExecutor`时需要为其指定使用的`CommandContextBuilder`。

## 示例

这里我们扩展一个 count 的 CommandExecutor，用来专门查询表的 count(*) 记录数。

最终的调用方式就像下面这样：

    //自定义的CountCommandExecutor，传入实体类获取记录数
    long count = jdbcDao.executor(CountCommandExecutor.class)
                .clazz(UserInfo.class)
                .getCount();

总的来说实现一个`CountCommandExecutor`需要以下几个类：

- CountCommandExecutor 接口以及实现类 CountCommandExecutorImpl
- CountCommandExecutorBuilderImpl 负责构建 CountCommandExecutor
- CountCommandContextBuilder 负责构建command的上下文信息
- CountExecutorContext 用来保存在构建CountCommandExecutor过程中的一些数据信息，类似于实体对象

CountCommandExecutor 类代码：

    public interface CountCommandExecutor extends CommandExecutor {
    
        CountCommandExecutor clazz(Class<?> clazz);
    
        long getCount();
    }

实现类：

    public class CountCommandExecutorImpl implements CountCommandExecutor {
    
        private JdbcEngineConfig jdbcEngineConfig;
    
        private CommandContextBuilder commandContextBuilder;
    
        private CountExecutorContext countExecutorContext;
    
        public CountCommandExecutorImpl(JdbcEngineConfig jdbcEngineConfig) {
            this.jdbcEngineConfig = jdbcEngineConfig;
            countExecutorContext = new CountExecutorContext();
        }
    
        @Override
        public CountCommandExecutor clazz(Class<?> clazz) {
            this.countExecutorContext.setClazz(clazz);
            return this;
        }
    
        @Override
        public long getCount() {
            CommandContext commandContext = this.commandContextBuilder.build(this.countExecutorContext, this.jdbcEngineConfig);
            PersistExecutor persistExecutor = this.jdbcEngineConfig.getPersistExecutor();
            commandContext.setResultType(Long.class);
            Object result = persistExecutor.execute(commandContext, CommandType.QUERY_ONE_COL);
            return (Long) result;
        }
    
        public void setCommandContextBuilder(CommandContextBuilder commandContextBuilder) {
            this.commandContextBuilder = commandContextBuilder;
        }
    }

CountCommandExecutorBuilderImpl 代码：

    public class CountCommandExecutorBuilderImpl extends AbstractCommandExecutorBuilder {
    
        @Override
        public boolean support(Class<? extends CommandExecutor> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig) {
            return commandExecutorClass == CountCommandExecutor.class;
        }
    
        @SuppressWarnings("unchecked")
        @Override
        public <T extends CommandExecutor> T build(Class<T> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig) {
            CountCommandExecutorImpl commandExecutor = new CountCommandExecutorImpl(jdbcEngineConfig);
            return (T) commandExecutor;
        }
    }
    
CountCommandContextBuilder 代码：

    public class CountCommandContextBuilder extends AbstractCommandContextBuilder {
    
        public CountCommandContextBuilder(CommandContextBuilderContext commandContextBuilderContext) {
            super(commandContextBuilderContext);
        }
    
        @Override
        public CommandContext doBuild(JdbcEngineConfig jdbcEngineConfig) {
            Class<?> clazz = this.getCommandContextBuilderContext().getUniqueModelClass();
            CommandContext commandContext = new CommandContext();
            commandContext.setCommand("select count(*) from " + clazz.getSimpleName());
            return commandContext;
        }
    }
    
## 配置

有了上面的实现代码，需要在声明JdbcDao的时候将我们自定义实现的 CommandExecutor 配置进去，主要配置如下，显示声明了一个 CommandExecutorFactoryImpl 并将我们实现的 CountCommandExecutorBuilderImpl 进入了初始化：

    <bean id="commandExecutorFactory" class="com.sonsure.dumper.core.config.CommandExecutorFactoryImpl">
        <property name="commandExecutorBuilders">
            <list>
                <bean class="com.sonsure.dumper.test.executor.CountCommandExecutorBuilderImpl"/>
            </list>
        </property>
    </bean>

    <bean id="jdbcTemplateEngine" class="com.sonsure.dumper.springjdbc.config.JdbcTemplateEngineFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="commandExecutorFactory" ref="commandExecutorFactory"/>
    </bean>

    <bean id="jdbcDao" class="com.sonsure.dumper.springjdbc.persist.SpringJdbcDaoTemplateImpl">
        <property name="defaultJdbcEngine" ref="jdbcTemplateEngine"/>
    </bean>
    
## 最后

通过以上步骤就实现了一个我们自定义的 CommandExecutor ，可以按此逻辑整合不同的执行方式实现(例如组件中的Mybatis)，或者封装一些统一的查询逻辑。


