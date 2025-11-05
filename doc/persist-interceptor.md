# Jdbc查询自定义类型转换器

## 说明

可以在执行sql前后处理逻辑。

## 接口说明

包含2个方法，分别在sql执行前后调用。

拦截器参数`PersistContext`中`ExecutableCmd`包含了当前执行的sql、参数等所有信息，对其内容进行修改将影响sql的执行行为。

拦截器可以有多个，执行方式为链式调用。

当设置`skipExecution`为true时，实际的数据库操作将不再执行，`executeAfter`方法仍会执行，返回结果为PersistContext参数中设置的result。

    public interface PersistInterceptor {
    
        /**
         * 执行前调用
         *
         * @param persistContext the interceptor context
         * @param chain          the chain
         */
        default void executeBefore(PersistContext persistContext, InterceptorChain chain) {
            chain.execute(persistContext);
        }
    
        /**
         * 执行后调用,返回结果将替换实际查询结果
         *
         * @param persistContext the persist context
         * @param chain          the chain
         */
        default void executeAfter(PersistContext persistContext, InterceptorChain chain) {
            chain.execute(persistContext);
        }
    }

可以在声明`jdbcContext`时，添加需要的拦截器：

    jdbcContext.setPersistInterceptors(Arrays.asList(new TestBeforeInterceptor(), new TestAfterInterceptor()));
