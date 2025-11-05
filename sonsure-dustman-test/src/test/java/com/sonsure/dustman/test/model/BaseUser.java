package com.sonsure.dustman.test.model;

import com.sonsure.dustman.common.model.BaseProperties;
import com.sonsure.dustman.common.model.Pageable;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@BaseProperties
public class BaseUser extends Pageable {

    private static final long serialVersionUID = 2480370601403819197L;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;
}
