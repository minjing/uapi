package uapi.behavior.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The class with this annotation will implement IEventDrivenBehavior interface
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBehavior {

    /**
     * The behavior's name
     *
     * @return  Behavior name
     */
    String name();

    /**
     * The topic of event which will be handled by this behavior
     *
     * @return  Event topic
     */
    String topic();

    /**
     * The type of event which will be handled by this behavior
     *
     * @return  Event class type
     */
    Class event();
}
