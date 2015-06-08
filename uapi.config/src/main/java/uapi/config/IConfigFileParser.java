package uapi.config;

import java.io.File;
import java.util.Map;

public interface IConfigFileParser {

    String[] supportedFileExtensions();

    Map<String, String> parse(File configFile);
}
