/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.sample

import spock.lang.Specification

/**
 * Created by xquan on 9/23/2016.
 */
class AddressParserTest extends Specification {

    def 'Test parse'() {
        given:
        AddressParser parser = new AddressParser()

        when:
        def addr = parser.parse([['home': 'abc', 'office': 'cdf']])

        then:
        addr.home == 'abc'
        addr.office == 'cdf'
    }
}
