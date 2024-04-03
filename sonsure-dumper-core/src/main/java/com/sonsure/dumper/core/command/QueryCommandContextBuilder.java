package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.model.Pageable;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * @author liyd
 */
public abstract class QueryCommandContextBuilder extends AbstractCommonCommandContextBuilder {

    protected QueryCommandContextBuilderContext queryCommandContextBuilderContext;

    public QueryCommandContextBuilder(QueryCommandContextBuilderContext queryCommandContextBuilderContext) {
        super(queryCommandContextBuilderContext);
        this.queryCommandContextBuilderContext = queryCommandContextBuilderContext;
    }

    public void paginate(int pageNum, int pageSize) {
        this.queryCommandContextBuilderContext.paginate(pageNum, pageSize);
    }

    public void paginate(Pageable pageable) {
        this.queryCommandContextBuilderContext.paginate(pageable);
    }

    public void limit(int offset, int size) {
        this.queryCommandContextBuilderContext.limit(offset, size);
    }

    public void setCount(boolean count) {
        this.queryCommandContextBuilderContext.setCount(count);
    }

    @Override
    public CommandContext build(JdbcEngineConfig jdbcEngineConfig) {
        CommandContext commandContext = super.build(jdbcEngineConfig);
        commandContext.setPagination(this.queryCommandContextBuilderContext.getPagination());
        commandContext.setCount(this.queryCommandContextBuilderContext.isCount());
        return commandContext;
    }
}
