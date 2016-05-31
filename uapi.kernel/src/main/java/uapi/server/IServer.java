/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.server;

/**
 * A server interface
 */
public interface IServer {

    /**
     * Start the server
     *
     * @throws  ServerException
     *          Encounter an error when start server
     */
    void start() throws ServerException;

    /**
     * Stop the server
     *
     * @throws  ServerException
     *          Encounter an error when
     */
    void stop() throws ServerException;
}
