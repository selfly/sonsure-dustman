/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.jdbc;

import com.sonsure.dumper.core.command.simple.ResultHandler;
import com.sonsure.dumper.test.model.UidUser;

import java.util.Map;

public class CustomResultHandler implements ResultHandler<UidUser> {

    @Override
    public UidUser handle(Object object) {
        Map<String, Object> map = (Map<String, Object>) object;
        UidUser uidUser = new UidUser();
        uidUser.setLoginName((String) map.get("LOGIN_NAME"));
        uidUser.setPassword((String) map.get("PASSWORD"));
        return uidUser;
    }
}
