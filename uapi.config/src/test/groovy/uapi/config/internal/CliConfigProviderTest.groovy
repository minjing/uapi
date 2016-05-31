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
import uapi.config.IConfigTracer
import uapi.log.ILogger

/**
 * Test case for CliConfigProvider
 */
class CliConfigProviderTest extends Specification {

    def "Test parse single option"() {
        def IConfigTracer mockCfgTracer = Mock(IConfigTracer)

        given:
        CliConfigProvider provider = new CliConfigProvider();
        provider._configTracer = mockCfgTracer

        when:
        provider.parse(args)

        then:
        1 * mockCfgTracer.onChange(CliConfigProvider.QUALIFY + cfgKey, cfgValue)

        where:
        args                        | cfgKey    | cfgValue
        ["-x"] as String[]          | "x"       | Boolean.TRUE.toString()
        ["-x=value"] as String[]    | "x"       | "value"
    }

    def "Test parse multiple options"() {
        def IConfigTracer mockCfgTracer = Mock(IConfigTracer)

        given:
        CliConfigProvider provider = new CliConfigProvider();
        provider._configTracer = mockCfgTracer

        when:
        provider.parse(args)

        then:
        1 * mockCfgTracer.onChange(CliConfigProvider.QUALIFY + cfgKey1, cfgValue)
        1 * mockCfgTracer.onChange(CliConfigProvider.QUALIFY + cfgKey2, cfgValue)
        1 * mockCfgTracer.onChange(CliConfigProvider.QUALIFY + cfgKey3, cfgValue)
        1 * mockCfgTracer.onChange(CliConfigProvider.QUALIFY + cfgKey4, cfgValue)


        where:
        args                        | cfgKey1   |cfgKey2    |cfgKey3    |cfgKey4    | cfgValue
        ["-xvcf"] as String[]       | "x"       | "v"       | "c"       | "f"       | Boolean.TRUE.toString()
    }
}
