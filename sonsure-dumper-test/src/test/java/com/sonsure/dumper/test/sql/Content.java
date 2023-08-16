/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.sql;

import java.util.Date;

public class Content {

    /**
     * 内容id
     */
    private Long contentId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容类型，不能枚举定死，数据字典定义，博客(blog)、问答(question)等，直接影响到url
     */
    private String contentType;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建用户id
     */
    private Long userId;

    private Integer clickCount;

    private Integer commentCount;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;


}
