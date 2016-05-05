package uapi.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate the value should be extracted from query string of the HTTP request
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface FromParam {

    /**
     * The query string key name
     *
     * @return  The query string key name
     */
    String value();
}
