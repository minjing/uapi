package uapi.config;

import java.io.File;
import java.util.Map;

/**
 * For local configuration file parser which must be implement this interface
 */
public interface IConfigFileParser {

    /**
     * Check the specific file extension can be supported by this parser
     *
     * @param   fileExtension
     *          The file extension which will be checked
     * @return  true means the parse can support the file extension otherwise return false
     */
    boolean isSupport(String fileExtension);

    /**
     * Parse specific configuration file
     *
     * @param   configFile
     *          The specific configuration file which will be parsed
     * @return  The configuration map
     */
    Map<String, Object> parse(File configFile);
}
