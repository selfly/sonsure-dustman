package com.sonsure.dumper.core.command;

import com.sonsure.commons.model.Pageable;
import com.sonsure.commons.model.Pagination;

/**
 * @author liyd
 */
public class QueryCommandContextBuilderContext extends CommandContextBuilderContext {

    private Pagination pagination;

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

    public void setCount(boolean count) {
        this.count = count;
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

    public Pagination getPagination() {
        return pagination;
    }

    public boolean isCount() {
        return count;
    }

}
