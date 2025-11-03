/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.jdbc.extension.executor;

import com.sonsure.dumper.core.command.simple.ResultHandler;
import com.sonsure.dumper.test.model.Account;

import java.util.Map;

public class CustomResultHandler implements ResultHandler<Account> {

    @Override
    public Account handle(Object object) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        Account account = new Account();
        account.setLoginName((String) map.get("LOGIN_NAME"));
        account.setPassword((String) map.get("PASSWORD"));
        return account;
    }
}
