/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

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
