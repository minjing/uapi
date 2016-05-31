/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config.internal;

import java.io.File;
import java.util.Map;

import uapi.config.IConfigFileParser;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

//@Service({ IConfigFileParser.class })
@Deprecated
public class PropertiesFileParser
        implements IConfigFileParser {

    private static final String SUPPORTED_FILE_EXT  = "properties";

    @Inject
    ILogger _logger;

    @Override
    public boolean isSupport(String fileExtension) {
        return SUPPORTED_FILE_EXT.equalsIgnoreCase(fileExtension);
    }

    @Override
    public Map<String, Object> parse(File configFile) {
        this._logger.info("Start parse file {}", configFile);
        return null;
    }

}
