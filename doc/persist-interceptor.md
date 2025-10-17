# Jdbc查询自定义类型转换器

## 说明

可以在执行sql前后处理逻辑。

## 接口说明

包含2个方法，分别在sql执行前后调用。

`CommandContext`包含了当前执行的sql、参数等所有信息，对其内容进行修改将影响sql的执行行为。

拦截器可以有多个，当有一个的`executeBefore`方法返回false时，实际的数据库操作将不再执行，`executeAfter`方法仍会执行，`commandResult`为null。

    /**
    * @author selfly
    */
    public interface PersistInterceptor {

        /**
        * 执行前调用
        *
        * @param dialect        the dialect
        * @param commandDetails the command context
        * @param executionType    the command type
        * @return the boolean
        */
        default boolean executeBefore(String dialect, CommandContext commandDetails, CommandType executionType) {
            return true;
        }

        /**
        * 执行后调用,返回结果将替换实际查询结果
        *
        * @param dialect        the dialect
        * @param commandDetails the command context
        * @param executionType    the command type
        * @param commandResult  the command result
        * @return the object
        */
        default Object executeAfter(String dialect, CommandContext commandDetails, CommandType executionType, Object commandResult) {
            return commandResult;
        }
    }

可以在声明`JdbcEngine`时，添加需要的拦截器：

    JdbcTemplateEngineConfigImpl jdbcTemplateEngineConfig = new JdbcTemplateEngineConfigImpl();
    jdbcTemplateEngineConfig.setDataSource(getDataSource());
    final List<PersistInterceptor> persistInterceptors = new ArrayList<>();
    // 添加需要的拦截器
    persistInterceptors.add(new XXXInterceptor())
    jdbcTemplateEngineConfig.setPersistInterceptors(persistInterceptors);
    defaultJdbcEngine = new JdbcEngineImpl(jdbcTemplateEngineConfig);