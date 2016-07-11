/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.internal;

import uapi.config.IConfigValueParser;
import uapi.helper.CollectionHelper;
import uapi.service.annotation.Service;

import java.util.List;

/**
 * Config parse for consul configurations
 */
@Service(IConfigValueParser.class)
public class ConsulConfigParser implements IConfigValueParser {

    private static final String[] supportedTypesIn = new String[] {
            List.class.getCanonicalName()
    };
    private static final String[] supportedTypesOut = new String[] {
            ConsulInstance.class.getCanonicalName()
    };

    @Override
    public boolean isSupport(String inType, String outType) {
        return CollectionHelper.isContains(supportedTypesIn, inType) && CollectionHelper.isContains(supportedTypesOut, outType);
    }

    @Override
    public String getName() {
        return ConsulConfigParser.class.getCanonicalName();
    }

    @Override
    public ConsulInstance parse(Object value) {
        return null;
    }
}
