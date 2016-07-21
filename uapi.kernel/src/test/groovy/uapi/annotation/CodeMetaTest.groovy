/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation

import freemarker.template.Template
import spock.lang.Specification

/**
 * Test for CodeMeta
 */
class CodeMetaTest extends Specification {

    def 'Test build with template'() {
        def tmp = Mock(Template) {
            process(_, _) >> { Object model, Writer writer -> writer.append(tmpStr) }
        }

        when:
        CodeMeta codeMeta = CodeMeta.builder()
            .setModel(model)
            .setTemplate(tmp)
            .build()

        then:
        codeMeta.getCode() == codes

        where:
        model           | tmpStr    | codes
        Mock(Object)    | 'aaa'     | 'aaa'
    }

    def 'Test build with raw code'() {
        when:
        CodeMeta codeMeta = CodeMeta.builder()
            .addRawCode(code1)
            .addRawCode(code2)
            .build()

        then:
        codeMeta.getCode() == codes

        where:
        code1   | code2 | codes
        'abc'   | 'def' | 'abcdef'
    }

    def 'Test equals'() {
        def tmp = Mock(Template) {
            getSourceName() >> "abc"
        }
        def model = Mock(Object) {
            equals(_) >> true
        }

        when:
        CodeMeta.Builder codeBuilder1 = CodeMeta.builder()
                .setModel(model)
                .setTemplate(tmp)
                .addRawCode(code1)
                .addRawCode(code2)
        CodeMeta.Builder codeBuilder2 = CodeMeta.builder()
                .setModel(model)
                .setTemplate(tmp)
                .addRawCode(code1)
                .addRawCode(code2)
        CodeMeta.Builder codeBuilder3 = CodeMeta.builder()
                .setModel(model)
                .addRawCode(code1)

        then:
        codeBuilder1 == codeBuilder2
        codeBuilder1 != codeBuilder3
        codeBuilder2 != codeBuilder3
        codeBuilder1.hashCode() == codeBuilder2.hashCode()
        codeBuilder1.hashCode() != codeBuilder3.hashCode()
        codeBuilder2.hashCode() != codeBuilder3.hashCode()

        where:
        code1 | code2
        'aaa' | 'def'
    }
}
