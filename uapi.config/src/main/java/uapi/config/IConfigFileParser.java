package uapi.config;

import java.io.File;
import java.util.Map;

public interface IConfigFileParser {

    boolean isSupport(String fileExtension);

    Map<String, Object> parse(File configFile);
}
