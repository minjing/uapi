package uapi.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate the value should be extracted from query string of the HTTP request header
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface FromHeader {

    /**
     * The request header name
     *
     * @return  The request header name
     */
    String value();
}
