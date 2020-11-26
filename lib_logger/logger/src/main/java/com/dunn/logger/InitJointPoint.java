package com.dunn.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dunn
 * 标记切点 注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InitJointPoint {
    String mFilePath() default "";
    String mFileName() default "";
    boolean isDebug() default false;
}
