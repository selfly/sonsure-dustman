# 持久化拦截器 PersistInterceptor

## 说明

可以在执行 SQL 前后处理逻辑。

## 接口说明

包含两个方法，分别在 SQL 执行前后调用。均为 default 空实现，按需覆盖即可。

    public interface PersistInterceptor {

        default void executeBefore(PersistContext persistContext) {
        }

        default void executeAfter(PersistContext persistContext) {
        }
    }

`PersistContext` 包含当前执行的 `ExecutableCmd`（SQL、参数等），以及 `skipExecution` 和 `result` 两个控制字段：

- **skipExecution**：设为 `true` 将跳过实际数据库操作，`executeAfter` 仍会执行
- **result**：设置后作为最终返回值，可用于 mock 或缓存场景

多个拦截器按添加顺序依次执行 `executeBefore`，再执行 SQL（除非 `skipExecution`），再按同样顺序执行 `executeAfter`。

## 示例

记录 SQL 日志的前置拦截器：

    public class LoggingInterceptor implements PersistInterceptor {
        @Override
        public void executeBefore(PersistContext persistContext) {
            System.out.println("SQL: " + persistContext.getExecutableCmd().getCommand());
        }
    }

## 配置

    jdbcContext.setPersistInterceptors(Arrays.asList(new LoggingInterceptor(), new AnotherInterceptor()));
