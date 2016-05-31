/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config.internal

import spock.lang.Specification
import uapi.log.ILogger

/**
 * Test case for YamFileParser
 */
class YamlFileParserTest extends Specification {

    def 'Test supported file extensions'() {
        given:
        YamlFileParser parser = new YamlFileParser()

        expect:
        parser.isSupport(extension) == supported

        where:
        extension       | supported
        'yml'           | true
        'yaml'          | true
        'xml'           | false
        'json'          | false
        'properties'    | false
    }

    def 'Test parse yaml file'() {
        given:
        File yamlFile = new File('src/test/resources/config.yml')
        YamlFileParser parser = new YamlFileParser()
        parser._logger = Mock(ILogger)

        expect:
        Map config = parser.parse(yamlFile)
        config != null
        config.name == 'My Name'
        config.address[0].home == 'Home address'
        config.address[1].office == 'Office address'
    }
}
