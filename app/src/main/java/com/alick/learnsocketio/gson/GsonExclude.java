package com.alick.learnsocketio.gson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
//被此注解的属性,不会参与gson的序列化与反序列化
public @interface GsonExclude {
}