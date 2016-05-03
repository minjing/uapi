package uapi.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate where are the web service parameters value from
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface FromQueryString {

    String value();
}
