/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.annotation;

/**
 * Indicate the value should be a part of uri
 * For example the request uri is: GET /ws/user/1, the ws is web service prefix.
 * The user is web service name, the "1" is user id, so if you want to fetch the
 * user id from uri, you need tell the index of uri, the web service name index
 * is 0, so the user id is index 1.
 */
public @interface FromUri {

    /**
     * The index of uri part
     *
     * @return  The index of uri
     */
    int value();
}
