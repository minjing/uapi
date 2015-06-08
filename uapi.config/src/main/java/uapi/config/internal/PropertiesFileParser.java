package uapi.config.internal;

import java.io.File;
import java.util.Map;

import uapi.internal.IConfigFileParser;
import uapi.service.IService;

public final class PropertiesFileParser implements IService, IConfigFileParser {

    @Override
    public String[] supportedFileExtensions() {
        return new String[] { "properties" };
    }

    @Override
    public Map<String, String> parse(File configFile) {
        return null;
    }

}
