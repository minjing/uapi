/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config.internal;

import uapi.Type;
import uapi.config.IConfigValueParser;
import uapi.helper.CollectionHelper;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A configuration value parser for string list
 */
@Service({ IConfigValueParser.class })
@Tag("Config")
public class StringListValueParser implements IConfigValueParser {

    private static final String[] supportedTypesIn = new String[] {
            List.class.getCanonicalName(),
            ArrayList.class.getCanonicalName(),
            LinkedList.class.getCanonicalName()
    };
    private static final String[] supportedTypesOut = new String[] {
            Type.STRING_LIST
    };

    @Override
    public boolean isSupport(String inType, String outType) {
        return CollectionHelper.isContains(supportedTypesIn, inType) && CollectionHelper.isContains(supportedTypesOut, outType);
    }

    @Override
    public String getName() {
        return StringListValueParser.class.getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> parse(Object value) {
        return (List<String>) value;
    }
}
