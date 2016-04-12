package uapi.config.internal;

import java.io.*;
import java.util.Map;

import com.fasterxml.jackson.jr.ob.JSON;

import uapi.config.IConfigFileParser;
import uapi.helper.Functionals;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

/**
 * The parser used to parse JSON format file
 */
@Service({ IConfigFileParser.class })
public class JsonFileParser
    implements IConfigFileParser {

    private static final String JSON_FILE_EXT   = "json";

    @Inject
    ILogger _logger;

    @Override
    public boolean isSupport(String fileExtension) {
        return JSON_FILE_EXT.equalsIgnoreCase(fileExtension);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> parse(File configFile) {
        try {
            return extract(configFile, input -> {
                JSON.std.with(JSON.Feature.READ_ONLY);
                return JSON.std.mapFrom(input);
            });
        } catch (IOException ex) {
            this._logger.error(ex, "Parse file {} failed", configFile.getName());
        }
        return null;
    }

    private Map extract(
            final File file,
            final Functionals.Extractor<FileInputStream, Map, IOException> extractor
    ) throws IOException {
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            return extractor.accept(input);
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }
}
