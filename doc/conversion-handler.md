# sql的解析转换 CommandConversionHandler

通常情况下，使用`JdbcDao`操作的对象都是类名和属性名，在最终到数据库执行时(PersistExecutor之前)会转换成表名和列名。

这部分的转换操作就是由`CommandConversionHandler`来完成的。

CommandConversionHandler的定义就一个方法：

    public interface CommandConversionHandler {
    
        /**
         * command转换
         *
         * @param command     the command
         * @param parameters  the parameters
         * @param jdbcContext the jdbc context
         * @return string string
         */
        String convert(String command, List<CmdParameter> parameters, JdbcContext jdbcContext);
    
    }
    
传入转换前的command和参数，返回转换后的command(通常是sql)。

默认使用`JSqlParser`来实现转换，实现类`JSqlParserCommandConversionHandler`。

如果`JSqlParserCommandConversionHandler`不能满足需求，可以实现自己的`CommandConversionHandler`，把它配置到`jdbcContext`即可。

*注意：当执行自定义sql调用了`forceNative()`方法时，将不经过`commandConversionHandler`的转换。*
