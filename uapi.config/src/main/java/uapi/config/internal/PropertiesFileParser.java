package uapi.config.internal;

import java.io.File;
import java.util.Map;

import uapi.config.IConfigFileParser;
import uapi.internal.TraceableConfigSource;
import uapi.service.IService;

public final class PropertiesFileParser
    extends TraceableConfigSource
    implements IService, IConfigFileParser {

    @Override
    public String[] supportedFileExtensions() {
        return new String[] { "properties" };
    }

    @Override
    public Map<String, String> parse(File configFile) {
        return null;
    }

}
