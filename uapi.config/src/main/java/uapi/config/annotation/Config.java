package uapi.config.annotation;

import uapi.config.IConfigValueParser;

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
    Class<? extends IConfigValueParser> parser() default IConfigValueParser.class;

    /**
     * Indicate the configuration is optional or not
     *
     * @return  Indicate the configuration is optional or not
     */
    boolean optional() default false;
}
