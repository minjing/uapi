/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config.annotation;

import uapi.config.IConfigValueParser;

import java.lang.annotation.*;

/**
 * Config annotation which is declared as
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Config {

    /**
     * The configuration path
     *
     * @return  Configuration path
     */
    String path();

    /**
     * The configuration parser service id
     *
     * @return  Parser service id
     */
    Class<? extends IConfigValueParser> parser() default IConfigValueParser.class;

    /**
     * Indicate the configuration is optional or not
     *
     * @return  Indicate the configuration is optional or not
     */
    boolean optional() default false;
}
