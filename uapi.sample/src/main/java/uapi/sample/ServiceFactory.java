/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.sample;

import uapi.service.IServiceFactory;
import uapi.service.annotation.Service;

/**
 * Created by xquan on 3/9/2016.
 */
@Service
public class ServiceFactory implements IServiceFactory<String> {

    @Override
    public String createService(Object serveFor) {
        return null;
    }
}
