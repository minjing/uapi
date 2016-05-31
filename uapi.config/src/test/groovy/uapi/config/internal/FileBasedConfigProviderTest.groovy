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
import uapi.config.IConfigFileParser
import uapi.config.IConfigTracer
import uapi.log.ILogger

/**
 * Test case for FileBasedConfigProvider
 */
class FileBasedConfigProviderTest extends Specification {

    def 'Test config'() {
        def cfgTracer = Mock(IConfigTracer)
        def yamlParser = Mock(IConfigFileParser) {
            isSupport('yml') >> true
            parse(_) >> ['key': 'value']
        }

        given:
        FileBasedConfigProvider provider = new FileBasedConfigProvider()
        provider._logger = Mock(ILogger)
        provider._cfgTracer = cfgTracer
        provider._parsers.add(yamlParser)

        when:
        provider.config(FileBasedConfigProvider.CFG_FILE_PATH, 'src/test/resources/config.yml')

        then:
        1 * cfgTracer.onChange(['key': 'value'])
    }
}
