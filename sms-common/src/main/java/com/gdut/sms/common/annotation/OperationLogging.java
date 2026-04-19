package com.gdut.sms.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogging {
    //操作模块
    String module();
    //操作类型
    String type();
    //描述
    String desc();
}
