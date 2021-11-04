package com.hith.hithium.upload.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DocumentOperationRecord {
    String value() default "";
}
