package uapi.config.internal;

import java.io.File;
import java.util.Map;

import uapi.config.IConfigFileParser;
import uapi.service.IService;
import uapi.service.Registration;
import uapi.service.Type;

@Registration({
    @Type(IConfigFileParser.class)
})
public final class PropertiesFileParser
    implements IService, IConfigFileParser {

    @Override
    public String[] supportedFileExtensions() {
        return new String[] { "properties" };
    }

    @Override
    public Map<String, Object> parse(File configFile) {
        return null;
    }

}
