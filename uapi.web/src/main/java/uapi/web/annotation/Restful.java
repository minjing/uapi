package uapi.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate the specific method will be exposed as web service
 * The method must no be private.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Restful {

    /**
     * Indicate which http methods are supported by this Restful interface
     * See HttpMethod
     *
     * @return  Supported http methods
     */
    int value();
}
