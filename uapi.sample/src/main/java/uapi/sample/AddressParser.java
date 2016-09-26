/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.sample;

import uapi.config.IConfigValueParser;
import uapi.helper.CollectionHelper;
import uapi.service.annotation.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by xquan on 4/7/2016.
 */
@Service(IConfigValueParser.class)
public class AddressParser implements IConfigValueParser {

    private static final String[] supportedTypesIn = new String[] {
            List.class.getCanonicalName()
    };
    private static final String[] supportedTypesOut = new String[] {
            Address.class.getCanonicalName()
    };

    @Override
    public boolean isSupport(String inType, String outType) {
        return CollectionHelper.isContains(supportedTypesIn, inType) && CollectionHelper.isContains(supportedTypesOut, outType);
    }

    @Override
    public String getName() {
        return AddressParser.class.getCanonicalName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Address parse(Object value) {
        List<Map> addrList = (List<Map>) value;
        Address address = new Address();
        for (Map addr : addrList) {
            if (addr.get("home") != null) {
                address.home = addr.get("home").toString();
            }
            if (addr.get("office") != null) {
                address.office = addr.get("office").toString();
            }
        }
        return address;
    }
}
