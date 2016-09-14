/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config;

import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * The service hold one or more than one parsers
 */
@Service
@Tag("Config")
public class ConfigValueParsers {

    @Inject
    List<IConfigValueParser> _parsers = new ArrayList<>();

    public IConfigValueParser findParser(String inType, String outType) {
        ArgumentChecker.notEmpty(inType, "inType");
        ArgumentChecker.notEmpty(outType, "outType");
        List<IConfigValueParser> matcheds = Observable.from(this._parsers)
                .filter(parser -> parser.isSupport(inType, outType))
                .toList().toBlocking().single();
        if (matcheds == null || matcheds.size() == 0) {
            throw new KernelException("No parser for in type {} and out type {}",
                    inType, outType);
        }
        if (matcheds.size() > 1) {
            throw new KernelException("Found more than one parser for in type {} and out type: {}",
                    inType, outType, matcheds);
        }
        return matcheds.get(0);
    }

    public IConfigValueParser findParser(String name) {
        ArgumentChecker.notEmpty(name, "name");
        List<IConfigValueParser> matches = Observable.from(this._parsers)
                .filter(parser -> parser.getName().equals(name))
                .toList().toBlocking().single();
        if (matches == null || matches.size() == 0) {
            throw new KernelException("No parser with name {}", name);
        }
        if (matches.size() > 1) {
            throw new KernelException("Found more than one parser with name {} : {}",
                    name, matches);
        }
        return matches.get(0);
    }
}
