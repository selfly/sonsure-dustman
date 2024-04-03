package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.model.Pageable;
import com.sonsure.dumper.common.model.Pagination;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Query command context builder context.
 *
 * @author liyd
 */
@Getter
public class QueryCommandContextBuilderContext extends CommandContextBuilderContext {

    private Pagination pagination;

    @Setter
    private boolean count = true;


    /**
     * Paginate.
     *
     * @param pageNum  the page num
     * @param pageSize the page size
     */
    public void paginate(int pageNum, int pageSize) {
        this.pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
    }

    /**
     * Paginate.
     *
     * @param pageable the pageable
     */
    public void paginate(Pageable pageable) {
        this.paginate(pageable.getPageNum(), pageable.getPageSize());
    }

    /**
     * Limit.
     *
     * @param offset the offset
     * @param size   the size
     */
    public void limit(int offset, int size) {
        this.pagination = new Pagination();
        pagination.setPageSize(size);
        pagination.setOffset(offset);
    }

}
