package com.sonsure.dumper.test.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserOrder {

    private Long userOrderId;
    private Long userId;
    private String orderName;
    private String orderDesc;
}
