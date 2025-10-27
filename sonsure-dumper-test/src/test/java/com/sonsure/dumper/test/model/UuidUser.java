package com.sonsure.dumper.test.model;


import com.sonsure.dumper.common.model.Pageable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UuidUser extends Pageable {

    private static final long serialVersionUID = -5086320285813890787L;

    private String uuidUserId;

    private String loginName;

    private String password;

}
