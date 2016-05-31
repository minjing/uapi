/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config

import spock.lang.Specification

/**
 * Test case for ConfigValueParsers
 */
class ConfigValueParsersTest extends Specification {

    def 'Test find parser by type'() {
        def IConfigValueParser mockParser = Mock(IConfigValueParser)
        mockParser.isSupport(inType, outType) >> {
            return true;
        }

        given:
        ConfigValueParsers parsers = new ConfigValueParsers()
        parsers._parsers.add(mockParser)

        expect:
        parsers.findParser(inType, outType) == mockParser

        where:
        inType                  | outType
        String.canonicalName    | Integer.canonicalName
        'int'                   | Long.canonicalName
    }

    def 'Test find parser by name'() {
        def IConfigValueParser mockParser = Mock(IConfigValueParser)
        mockParser.getName() >> {
            return parserName;
        }

        given:
        ConfigValueParsers parsers = new ConfigValueParsers()
        parsers._parsers.add(mockParser)

        expect:
        parsers.findParser(parserName) == mockParser

        where:
        parserName  | node
        'IntParser' | ''
    }
}
