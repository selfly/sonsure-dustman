//package com.sonsure.dumper.core.command;
//
//import com.sonsure.commons.model.Page;
//import com.sonsure.commons.model.Pageable;
//import com.sonsure.dumper.core.config.JdbcEngineConfig;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author liyd
// */
//@SuppressWarnings("unchecked")
//public abstract class AbstractQueryCommandExecutor<E extends QueryCommandExecutor<E>> extends AbstractCommonCommandExecutor<E> implements QueryCommandExecutor<E> {
//
//    public AbstractQueryCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
//        super(jdbcEngineConfig);
//    }
//
//    protected QueryCommandContextBuilder getQueryCommandContextBuilder() {
//        return getCommandContextBuilder();
//    }
//
//
//    @Override
//    public E paginate(int pageNum, int pageSize) {
//        this.getQueryCommandContextBuilder().paginate(pageNum, pageSize);
//        return (E) this;
//    }
//
//    @Override
//    public E paginate(Pageable pageable) {
//        this.getQueryCommandContextBuilder().paginate(pageable);
//        return (E) this;
//    }
//
//    @Override
//    public E limit(int offset, int size) {
//        this.getQueryCommandContextBuilder().limit(offset, size);
//        return (E) this;
//    }
//
//    @Override
//    public E isCount(boolean isCount) {
//        this.getQueryCommandContextBuilder().setCount(isCount);
//        return (E) this;
//    }
//
////    @Override
////    public long count() {
////        CommandContext commandContext = this.getQueryCommandContextBuilder().build(getJdbcEngineConfig());
////        PersistExecutor persistExecutor = this.jdbcEngineConfig.getPersistExecutor();
////        String countCommand = this.jdbcEngineConfig.getPageHandler().getCountCommand(commandContext.getCommand(), persistExecutor.getDialect());
////        CommandContext countCommandContext = BeanKit.copyProperties(new CommandContext(), commandContext);
////        countCommandContext.setCommand(countCommand);
////        countCommandContext.setResultType(Long.class);
////        Object result = persistExecutor.execute(countCommandContext, CommandType.QUERY_ONE_COL);
////        return (Long) result;
////    }
//
////    @Override
////    public <T> T singleResult(Class<T> cls) {
////        CommandContext commandContext = this.getQueryCommandContextBuilder().build(getJdbcEngineConfig());
////        commandContext.setResultType(cls);
////        return (T) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_SINGLE_RESULT);
////    }
//
//    @Override
//    public Map<String, Object> singleResult() {
//        CommandContext commandContext = this.getQueryCommandContextBuilder().build(getJdbcEngineConfig());
//        return (Map<String, Object>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP);
//    }
//
//    @Override
//    public <T> T firstResult(Class<T> cls) {
//        this.paginate(1, 1);
//        this.isCount(false);
//        Page<T> page = this.pageResult(cls);
//        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
//    }
//
//    @Override
//    public Map<String, Object> firstResult() {
//        this.paginate(1, 1);
//        this.isCount(false);
//        Page<Map<String, Object>> page = this.pageResult();
//        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> T oneColResult(Class<T> clazz) {
//        CommandContext commandContext = this.getQueryCommandContextBuilder().build(getJdbcEngineConfig());
//        commandContext.setResultType(clazz);
//        return (T) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> oneColList(Class<T> clazz) {
//        CommandContext commandContext = this.getQueryCommandContextBuilder().build(getJdbcEngineConfig());
//        commandContext.setResultType(clazz);
//        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL_LIST);
//    }
//
//    @Override
//    public <T> T oneColFirstResult(Class<T> clazz) {
//        this.paginate(1, 1);
//        this.isCount(false);
//        Page<T> page = this.oneColPageResult(clazz);
//        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
//    }
//
////    @SuppressWarnings("unchecked")
////    @Override
////    public <T> List<T> list(Class<T> cls) {
////        CommandContext commandContext = this.getQueryCommandContextBuilder().build(getJdbcEngineConfig());
////        commandContext.setResultType(cls);
////        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_LIST);
////    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public List<Map<String, Object>> list() {
//        CommandContext commandContext = this.getQueryCommandContextBuilder().build(getJdbcEngineConfig());
//        return (List<Map<String, Object>>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP_LIST);
//    }
//
////    @SuppressWarnings("unchecked")
////    @Override
////    public <T> Page<T> pageResult(Class<T> cls) {
////        CommandContext commandContext = this.getQueryCommandContextBuilder().build(getJdbcEngineConfig());
////        commandContext.setResultType(cls);
////        return this.doPageResult(commandContext, commandContext.getPagination(), commandContext.isCount(), commandContext1 -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_FOR_LIST));
////    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public Page<Map<String, Object>> pageResult() {
//        CommandContext commandContext = this.getQueryCommandContextBuilder().build(getJdbcEngineConfig());
//        return this.doPageResult(commandContext, commandContext.getPagination(), commandContext.isCount(), commandContext1 -> (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_FOR_MAP_LIST));
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> Page<T> oneColPageResult(Class<T> clazz) {
//        CommandContext commandContext = this.getQueryCommandContextBuilder().build(getJdbcEngineConfig());
//        commandContext.setResultType(clazz);
//        return this.doPageResult(commandContext, commandContext.getPagination(), commandContext.isCount(), commandContext1 -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_ONE_COL_LIST));
//    }
//
//}
