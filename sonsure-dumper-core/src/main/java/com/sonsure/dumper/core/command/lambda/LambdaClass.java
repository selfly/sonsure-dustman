package com.sonsure.dumper.core.command.lambda;

import com.sonsure.dumper.common.utils.NameUtils;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.SerializedLambda;

/**
 * @author selfly
 */
@Getter
@Setter
public class LambdaClass {


    private String simpleClassName;
    private String methodName;
    private String fieldName;

    public LambdaClass(SerializedLambda serializedLambda) {
        String[] info = StringUtils.split(serializedLambda.getInstantiatedMethodType(), ";");
        int index = StringUtils.lastIndexOf(info[0], "/");
        this.simpleClassName = StringUtils.substring(info[0], index + 1);
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
        this.fieldName = NameUtils.getFirstLowerName(StringUtils.substring(this.methodName, prefixLength));
    }
}
