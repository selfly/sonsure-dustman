package com.sonsure.dumper.core.command;

import com.sonsure.commons.model.Pageable;
import com.sonsure.dumper.core.command.entity.AbstractCommandContextBuilder;

/**
 * @author liyd
 */
public abstract class QueryCommandContextBuilder extends AbstractCommandContextBuilder {

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

    public QueryCommandContextBuilderContext getQueryCommandContextBuilderContext() {
        return queryCommandContextBuilderContext;
    }
}
