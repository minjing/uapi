/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.log.internal;

import org.slf4j.LoggerFactory;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.log.ILogger;
import uapi.service.*;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

@Service
@Tag("Log")
public class LoggerManager implements IServiceFactory<ILogger> {

    @Override
    public ILogger createService(Object serveFor) {
        if (serveFor == null) {
            throw new InvalidArgumentException("servFor", InvalidArgumentType.EMPTY);
        }
        return new Logger(LoggerFactory.getLogger(serveFor.getClass()));
    }
}
