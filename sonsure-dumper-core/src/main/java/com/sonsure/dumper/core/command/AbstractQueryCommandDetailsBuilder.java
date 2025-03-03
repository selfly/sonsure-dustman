//package com.sonsure.dumper.core.command;
//
//import com.sonsure.dumper.common.model.Pageable;
//import com.sonsure.dumper.core.config.JdbcEngineConfig;
//
///**
// * @author liyd
// */
//public abstract class AbstractQueryCommandDetailsBuilder extends AbstractCommonCommandDetailsBuilder {
//
//    protected QueryCommandContextBuilderContext queryCommandContextBuilderContext;
//
//    public AbstractQueryCommandDetailsBuilder(QueryCommandContextBuilderContext queryCommandContextBuilderContext) {
//        super(queryCommandContextBuilderContext);
//        this.queryCommandContextBuilderContext = queryCommandContextBuilderContext;
//    }
//
//    public void paginate(int pageNum, int pageSize) {
//        this.queryCommandContextBuilderContext.paginate(pageNum, pageSize);
//    }
//
//    public void paginate(Pageable pageable) {
//        this.queryCommandContextBuilderContext.paginate(pageable);
//    }
//
//    public void limit(int offset, int size) {
//        this.queryCommandContextBuilderContext.limit(offset, size);
//    }
//
//    public void setCount(boolean count) {
//        this.queryCommandContextBuilderContext.setCount(count);
//    }
//
//    @Override
//    public CommandDetails build(JdbcEngineConfig jdbcEngineConfig) {
//        CommandDetails commandDetails = super.build(jdbcEngineConfig);
//        commandDetails.setPagination(this.queryCommandContextBuilderContext.getPagination());
//        commandDetails.setCount(this.queryCommandContextBuilderContext.isCount());
//        return commandDetails;
//    }
//}
