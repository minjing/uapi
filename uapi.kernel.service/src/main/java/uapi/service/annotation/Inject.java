package uapi.service.annotation;

import uapi.helper.StringHelper;
import uapi.service.IRegistry;
import uapi.service.internal.QualifiedServiceId;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by min on 16/2/16.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Inject {

    String value() default StringHelper.EMPTY;

    /**
     * Indicate where is the injected service from
     *
     * @return  Injected service from
     */
    String from() default QualifiedServiceId.FROM_ANY;
}
