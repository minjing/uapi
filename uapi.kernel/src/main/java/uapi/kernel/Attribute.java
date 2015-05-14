package uapi.kernel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Attribute {

    /**
     * Custom service identify
     * 
     * @return Service identify
     */
    public String sid() default "";

    /**
     * Indicate the service should be initialized at launch time
     * 
     * @return
     */
    public boolean initAtLaunching() default false;
}
