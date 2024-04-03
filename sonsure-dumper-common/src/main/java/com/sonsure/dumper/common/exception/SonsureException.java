/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.exception;


import com.sonsure.dumper.common.enums.BaseEnum;
import com.sonsure.dumper.common.utils.UUIDUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 自定义异常类
 * <p/>
 *
 * @author liyd
 * @date 6/27/14
 */
@Getter
public class SonsureException extends RuntimeException {

    private static final long serialVersionUID = 3731036212843506314L;

    /**
     * 生成错误码前缀
     */
    private static final String ERROR_CODE_PREFIX = "sonsure.error.";

    /**
     * Exception code
     */
    protected String resultCode = "UN_KNOWN_EXCEPTION";

    /**
     * Exception message
     */
    protected String resultMsg = "未知异常";

    /**
     * Instantiates a new KtanxException.
     *
     * @param e the e
     */
    public SonsureException(BaseEnum e) {
        this(e.getCode(), e.getDesc());
    }

    public SonsureException(String message, Throwable e) {
        this(null, message, e);
    }

    public SonsureException(BaseEnum baseEnum, Throwable e) {
        this(baseEnum.getCode(), baseEnum.getDesc(), e);
    }

    /**
     * Instantiates a new KtanxException.
     *
     * @param e the e
     */
    public SonsureException(Throwable e) {
        this(null, e.getMessage(), e);
    }

    /**
     * Constructor
     *
     * @param message the message
     */
    public SonsureException(String message) {
        this(null, message);
    }

    /**
     * Constructor
     *
     * @param code    the code
     * @param message the message
     */
    public SonsureException(String code, String message) {
        super(message);
        this.resultCode = StringUtils.isBlank(code) ? ERROR_CODE_PREFIX + UUIDUtils.getUUID16(message.getBytes()) : code;
        this.resultMsg = message;
    }

    /**
     * Constructor
     *
     * @param code    the code
     * @param message the message
     * @param e       the e
     */
    public SonsureException(String code, String message, Throwable e) {
        super(message, e);
        this.resultCode = StringUtils.isBlank(code) ? ERROR_CODE_PREFIX + UUIDUtils.getUUID16(message.getBytes()) : code;
        this.resultMsg = message;
    }

}
