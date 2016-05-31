/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

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
