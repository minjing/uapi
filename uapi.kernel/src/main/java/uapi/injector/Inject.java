package uapi.injector;

import uapi.helper.StringHelper;

/**
 * Created by min on 16/2/9.
 */
public @interface Inject {

    public String value() default StringHelper.EMPTY;
}
