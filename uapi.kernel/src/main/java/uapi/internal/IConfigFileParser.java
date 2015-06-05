package uapi.internal;

import java.io.File;
import java.util.Map;

public interface IConfigFileParser {

    String[] supportedFileExtensions();

    Map<String, String> parse(File configFile);
}
