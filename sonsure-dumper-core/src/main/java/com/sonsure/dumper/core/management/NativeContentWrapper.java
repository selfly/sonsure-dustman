package com.sonsure.dumper.core.management;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author selfly
 */
@Getter
public class NativeContentWrapper {

    /**
     * value需要native内容前后包围符号
     */
    public static final String NATIVE_FIELD_OPEN_TOKEN = "{{";
    public static final String NATIVE_FIELD_CLOSE_TOKEN = "}}";

    private final boolean natives;
    private final String actualContent;

    public NativeContentWrapper(String content) {
        if (isNativeContent(content)) {
            this.natives = true;
            this.actualContent = getNativeContentActualValue(content);
        } else {
            this.natives = false;
            this.actualContent = content;
        }
    }


    private boolean isNativeContent(String content) {
        return StringUtils.startsWith(content, NATIVE_FIELD_OPEN_TOKEN) && StringUtils.endsWith(content, NATIVE_FIELD_CLOSE_TOKEN);
    }

    private String getNativeContentActualValue(String content) {
        return StringUtils.substring(content, NATIVE_FIELD_OPEN_TOKEN.length(), content.length() - NATIVE_FIELD_CLOSE_TOKEN.length());
    }
}
