package com.sonsure.dumper.common.model;

import lombok.Getter;
import lombok.Setter;

/**
 * The type Base query.
 */
@Getter
@Setter
public class BaseQuery extends Pageable {

    /**
     * 关键字
     */
    private String keywords;

}
