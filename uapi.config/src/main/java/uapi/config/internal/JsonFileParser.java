package uapi.config.internal;

import java.io.File;
import java.util.Map;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.config.IConfigFileParser;
import uapi.internal.TraceableConfigSource;
import uapi.service.IService;

public class JsonFileParser
    extends TraceableConfigSource
    implements IService, IConfigFileParser {

    @Override
    public String[] supportedFileExtensions() {
        return new String[] { "json" };
    }

    @Override
    public Map<String, String> parse(File configFile) {
        if (configFile == null) {
            throw new InvalidArgumentException("configFile", InvalidArgumentType.EMPTY);
        }
        return null;
    }
}
