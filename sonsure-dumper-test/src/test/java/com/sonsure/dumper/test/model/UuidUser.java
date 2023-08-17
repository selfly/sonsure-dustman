package com.sonsure.dumper.test.model;

import com.sonsure.commons.model.BaseEntity;

public class UuidUser extends BaseEntity {

    private static final long serialVersionUID = -5086320285813890787L;

    private String uuidUserId;

    private String loginName;

    private String password;

    public String getUuidUserId() {
        return uuidUserId;
    }

    public void setUuidUserId(String uuidUserId) {
        this.uuidUserId = uuidUserId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
