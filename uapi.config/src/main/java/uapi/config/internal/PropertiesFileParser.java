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
