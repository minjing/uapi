/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config;

import java.util.Map;

/**
 * The IConfigTracer used to trace config changes event
 */
public interface IConfigTracer {

    /**
     * Invoked the config is changed on specified path
     *
     * @param   path
     *          The path which related with changed config
     * @param   config
     *          The changed config object
     */
    void onChange(String path, Object config);

    /**
     * Invoked the config is changed
     * The map's key is the path of the configuration
     *
     * @param   configMap
     *          The changed configuration map
     */
    void onChange(Map<String, Object> configMap);
}
