/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper;

import rx.Observable;

import java.util.Map;

/**
 * Created by xquan on 5/11/2016.
 */
public final class MapHelper {

    private MapHelper() { }

    /**
     * Find the specified map is contains anyone of specified keys
     *
     * @param   map
     *          The specified map
     * @param   keys
     *          The keys
     * @param   <T>
     *          The key type
     * @return  The first matched key will be returned otherwise return null
     */
    public static <T> T findKey(Map map, T... keys) {
        ArgumentChecker.required(map, "map");
        ArgumentChecker.required(keys, "keys");

        return Observable.from(keys)
                .filter(map::containsKey)
                .toBlocking().firstOrDefault(null);
    }
}
