package uapi.internal;

import java.io.File;
import java.util.Map;

import uapi.service.IService;

public class JsonFileConfigSource implements IService, IConfigFileParser {

    @Override
    public String[] supportedFileExtensions() {
        return new String[] { "json" };
    }

    @Override
    public Map<String, String> parse(File configFile) {
        return null;
    }
}
