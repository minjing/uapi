package uapi.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import uapi.helper.Null;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Attribute {

    /**
     * Custom service name
     * 
     * @return Service name
     */
    String name() default "";

    Class<?> type() default Null.class;
}
