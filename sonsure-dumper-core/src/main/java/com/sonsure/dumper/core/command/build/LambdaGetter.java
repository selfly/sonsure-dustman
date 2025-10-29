package com.sonsure.dumper.core.command.build;

import com.sonsure.dumper.common.utils.NameUtils;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Getter;

import java.lang.invoke.SerializedLambda;

/**
 * @author selfly
 */
@Getter
public class LambdaGetter {
    
    private final String simpleClassName;
    private final String methodName;
    private final String fieldName;

    public LambdaGetter(SerializedLambda serializedLambda) {
        String[] info = serializedLambda.getInstantiatedMethodType().split(";");
        int index = info[0].lastIndexOf("/");
        this.simpleClassName = info[0].substring(index + 1);
        this.methodName = serializedLambda.getImplMethodName();
        int prefixLength = 0;
        if (this.methodName.startsWith("is")) {
            prefixLength = 2;
        } else if (this.methodName.startsWith("get")) {
            prefixLength = 3;
        }
        if (prefixLength == 0) {
            throw new SonsureJdbcException("方法名不符合规范:" + prefixLength);
        }
        this.fieldName = NameUtils.getFirstLowerName(this.methodName.substring(prefixLength));
    }
}
