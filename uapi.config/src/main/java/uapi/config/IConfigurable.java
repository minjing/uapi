/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config;

/**
 * Created by min on 16/3/6.
 */
public interface IConfigurable {

    String METHOD_GET_PATHS             = "getPaths";
    String METHOD_IS_OPTIONAL_CONFIG    = "isOptionalConfig";
    String METHOD_CONFIG                = "config";
    String PARAM_PATH                   = "path";
    String PARAM_CONFIG_OBJECT          = "configObject";

    String[] getPaths();

    boolean isOptionalConfig(String path);

    void config(String path, Object configObject);
}
