/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config.internal;

import com.esotericsoftware.yamlbeans.YamlReader;
import uapi.config.IConfigFileParser;
import uapi.helper.Functionals;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * The parser used to parse YAML format file
 */
@Service({ IConfigFileParser.class })
public class YamlFileParser implements IConfigFileParser {

    private static final String YML_FILE_EXT    = "yml";
    private static final String YAML_FILE_EXT   = "yaml";

    @Inject
    ILogger _logger;

    @Override
    public boolean isSupport(String fileExtension) {
        return YML_FILE_EXT.equalsIgnoreCase(fileExtension) || YAML_FILE_EXT.equalsIgnoreCase(fileExtension);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> parse(File configFile) {
        try {
            return extract(configFile, reader -> (Map) reader.read());
        } catch (IOException ex) {
            this._logger.error(ex, "Parse file {} failed", configFile.getName());
        }
        return null;
    }

    private Map extract(
            final File file,
            final Functionals.Extractor<YamlReader, Map, IOException> extractor
    ) throws IOException {
        YamlReader reader = null;
        try {
            reader = new YamlReader(new FileReader(file));
            return extractor.accept(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
