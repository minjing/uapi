package uapi.config.annotation;

import java.lang.annotation.*;

/**
 * Config annotation
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Config {

    String[] value();
}
