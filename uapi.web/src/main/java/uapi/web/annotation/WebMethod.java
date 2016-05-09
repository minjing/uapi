package uapi.web.annotation;

import uapi.web.WebMethodType;

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
public @interface WebMethod {

    /**
     * Indicate what type is used for the web method
     *
     * @return  web method type
     */
    WebMethodType type();

    int httpMethods();
}
