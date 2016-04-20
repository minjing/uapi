package uapi.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The method with this annotation must be invoked
 * after the service instance created and all dependent
 * services has been injected..
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Init {

    /**
     * Indicate the init method will be invoked when the service is satisfied or invoked by first used
     *
     * @return  true means invoked by first used
     *          false means invoked by service is satisfied
     */
    boolean lazy() default true;
}
