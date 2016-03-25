package uapi.config.annotation;

import java.lang.annotation.*;

/**
 * Config annotation which is declared as
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Config {

    /**
     * The configuration path
     *
     * @return  Configuration path
     */
    String path();

    /**
     * The configuration parser service id
     *
     * @return  Parser service id
     */
    String parser() default "";

    /**
     * Indicate the configuration is optional or not
     *
     * @return  Indicate the configuration is optional or not
     */
    boolean optional() default false;
}
