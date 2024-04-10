package com.sonsure.dumper.test.model;

import com.sonsure.dumper.common.model.BaseEntity;
import com.sonsure.dumper.common.model.BaseProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@BaseProperties
public class BaseUser extends BaseEntity {

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
