package com.sonsure.dumper.core.command.lambda;

import com.sonsure.dumper.common.utils.NameUtils;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author selfly
 */
@Getter
@Setter
public class LambdaClass {

    private String className;
    private String methodName;

    private String simpleClassName;
    private String fieldName;

    public LambdaClass(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
        String[] clsInfo = StringUtils.split(className, "/");
        this.simpleClassName = clsInfo[clsInfo.length - 1];
        int prefixLength = 0;
        if (methodName.startsWith("is")) {
            prefixLength = 2;
        } else if (methodName.startsWith("get")) {
            prefixLength = 3;
        }
        if (prefixLength == 0) {
            throw new SonsureJdbcException("方法名不符合规范:" + prefixLength);
        }
        this.fieldName = NameUtils.getFirstLowerName(StringUtils.substring(methodName, prefixLength));
    }
}
