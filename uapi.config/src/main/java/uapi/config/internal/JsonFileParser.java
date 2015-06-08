package uapi.config.internal;

import java.io.File;
import java.util.Map;

import uapi.internal.IConfigFileParser;
import uapi.service.IService;

public class JsonFileParser implements IService, IConfigFileParser {

    @Override
    public String[] supportedFileExtensions() {
        return new String[] { "json" };
    }

    @Override
    public Map<String, String> parse(File configFile) {
        return null;
    }
}
