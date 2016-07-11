/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web;

import uapi.IIdentifiable;

/**
 * Used to register service to remote service registration center
 */
public interface IServiceRegister extends IIdentifiable<String> {

    /**
     * Register restful service
     *
     * @param   restfulService
     *          The restful service which will be registered
     */
    void register(IRestfulService restfulService);

    /**
     * Register restful interface
     *
     * @param   restfulInterface
     *          The restful interface which will be registered
     */
    void register(IRestfulInterface restfulInterface);
}
