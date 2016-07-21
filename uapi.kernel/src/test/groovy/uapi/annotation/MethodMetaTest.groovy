/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation

import spock.lang.Specification

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name
import javax.lang.model.type.TypeMirror

/**
 * Test for MethodMeta
 */
class MethodMetaTest extends Specification {

    def 'Test build'() {
        when:
        def mockAnnoBudr = new MockAnnoMetaBuilder()
        def paramBudr1 = new MockParamMeta('a')
        def paramBudr2 = new MockParamMeta('b')
        def codeBudr = new MockCodeMeta('tmp')

        MethodMeta methodMeta = MethodMeta.builder()
            .setName(name)
            .setReturnTypeName(rtnType)
            .setInvokeSuper(invokeSuper)
            .setIsSetter(isSetter)
            .addModifier(modifier1)
            .addModifier(modifier2)
            .addAnnotationBuilder(mockAnnoBudr)
            .addThrowTypeName(throwType1)
            .addThrowTypeName(throwType2)
            .addParameterBuilder(paramBudr1)
            .addParameterBuilder(paramBudr2)
            .addCodeBuilder(codeBudr)
            .build()

        then:
        methodMeta.getName() == name
        methodMeta.getReturnTypeName() == rtnType
        methodMeta.getInvokeSuperAfter() == invokeSuperAfter
        methodMeta.getInvokeSuperBefore() == invokeSuperBefore
        methodMeta.getIsSetter() == isSetter
        methodMeta.getModifiers() == modifiers
        methodMeta.getParameters().size() == 2
        methodMeta.getThrowTypeNames().size() == 2
        methodMeta.getCodes().size() == 1
        mockAnnoBudr._initInvokedCount == 1
        mockAnnoBudr._validationCount == 1
        mockAnnoBudr._createInstCount == 1
        paramBudr1._initInvokedCount == 1
        paramBudr1._validationCount == 1
        paramBudr1._createInstCount == 1
        paramBudr2._initInvokedCount == 1
        paramBudr2._validationCount == 1
        paramBudr2._createInstCount == 1
        codeBudr._initInvokedCount == 1
        codeBudr._validationCount == 1
        codeBudr._createInstCount == 1

        where:
        name    | rtnType   | invokeSuper                   | invokeSuperAfter  | invokeSuperBefore | isSetter  | modifier1       | modifier2      | modifiers      | throwType1    | throwType2
        'Name'  | 'String'  | MethodMeta.InvokeSuper.AFTER  | true              | false             | false     | Modifier.PUBLIC | Modifier.FINAL | 'public final' | 'AE'          | 'BE'
    }

    def 'Test build from Element'() {
        def mockName = Mock(Name) {
            toString() >> name
        }
        def mockRtnType = Mock(TypeMirror) {
            toString() >> rtnType
        }
        def mockThrownType = Mock(TypeMirror) {
            toString() >> thrownType
        }
        def mockElemt = Mock(ExecutableElement) {
            getKind() >> ElementKind.METHOD
            getSimpleName() >> mockName
            getReturnType() >> mockRtnType
            getThrownTypes() >> [ mockThrownType ]
            getModifiers() >> [ modifier ]
            getParameters() >> []
        }
        def mockBudrCtx = Mock(IBuilderContext)

        when:
        MethodMeta methodMeta = MethodMeta.builder(mockElemt, mockBudrCtx).build()

        then:
        methodMeta.getName() == name
        methodMeta.getReturnTypeName() == rtnType
        methodMeta.getThrowTypeNames()[0] == thrownType
        methodMeta.getModifiers() == modifiers

        where:
        name    | rtnType   | thrownType    | modifier          | modifiers
        'Name'  | 'String'  | 'Exception'   | Modifier.PUBLIC   | 'public'
    }

    def 'Test find'() {
        when:
        def mockAnnoBudr = new MockAnnoMetaBuilder()
        def paramBudr1 = new MockParamMeta('a')
        def paramBudr2 = new MockParamMeta('b')
        def codeBudr = new MockCodeMeta('tmp')

        MethodMeta.Builder methodBudr = MethodMeta.builder()
                .setName(name)
                .addAnnotationBuilder(mockAnnoBudr)
                .addThrowTypeName(throwType1)
                .addThrowTypeName(throwType2)
                .addParameterBuilder(paramBudr1)
                .addParameterBuilder(paramBudr2)
                .addCodeBuilder(codeBudr)

        then:
        methodBudr.getParameterCount() == 2
        methodBudr.findParameterBuilder('a') == paramBudr1
        methodBudr.findParameterBuilder('b') == paramBudr2
        methodBudr.findCodeBuilder('tmp') == codeBudr

        where:
        name    | rtnType   | invokeSuper                   | invokeSuperAfter  | invokeSuperBefore | isSetter  | modifier1       | modifier2      | modifiers      | throwType1    | throwType2
        'Name'  | 'String'  | MethodMeta.InvokeSuper.AFTER  | true              | false             | false     | Modifier.PUBLIC | Modifier.FINAL | 'public final' | 'AE'          | 'BE'
    }

    def 'Test find Parameter by element'() {
        def paramBudr = new MockParamMeta(paramName)
        paramBudr._type = paramType
        def mockName = Mock(Name) {
            toString() >> paramName
        }
        def mockType = Mock(TypeMirror) {
            toString() >> paramType
        }
        def mockElemt = Mock(Element) {
            getKind() >> ElementKind.PARAMETER
            getSimpleName() >> mockName
            asType() >> mockType
        }
        def mockBudrCtx = Mock(IBuilderContext)

        when:
        MethodMeta.Builder methodBudr = MethodMeta.builder()
                .setName(name)
                .setReturnTypeName(rtnType)
                .addModifier(modifier)
                .addThrowTypeName(thrownType)
                .addParameterBuilder(paramBudr)


        then:
        methodBudr.findParameterBuilder(mockElemt, mockBudrCtx) != null

        where:
        name    | rtnType   | thrownType    | modifier          | modifiers | paramName | paramType
        'Name'  | 'String'  | 'Exception'   | Modifier.PUBLIC   | 'public'  | 'Param'   | 'String'
    }

    def 'Test equals'() {
        when:
        def mockAnnoBudr = new MockAnnoMetaBuilder()
        def paramBudr1 = new MockParamMeta('a')
        def paramBudr2 = new MockParamMeta('b')
        def codeBudr = new MockCodeMeta('tmp')

        MethodMeta.Builder methodBudr1 = MethodMeta.builder()
                .setName(name)
                .setReturnTypeName(rtnType)
                .setInvokeSuper(invokeSuper)
                .setIsSetter(isSetter)
                .addModifier(modifier1)
                .addModifier(modifier2)
                .addAnnotationBuilder(mockAnnoBudr)
                .addThrowTypeName(throwType1)
                .addThrowTypeName(throwType2)
                .addParameterBuilder(paramBudr1)
                .addParameterBuilder(paramBudr2)
                .addCodeBuilder(codeBudr)
        MethodMeta.Builder methodBudr2 = MethodMeta.builder()
                .setName(name)
                .setReturnTypeName(rtnType)
                .setInvokeSuper(invokeSuper)
                .setIsSetter(isSetter)
                .addModifier(modifier1)
                .addModifier(modifier2)
                .addAnnotationBuilder(mockAnnoBudr)
                .addThrowTypeName(throwType1)
                .addThrowTypeName(throwType2)
                .addParameterBuilder(paramBudr1)
                .addParameterBuilder(paramBudr2)
                .addCodeBuilder(codeBudr)
        MethodMeta.Builder methodBudr3 = MethodMeta.builder()
                .setName(name)
                .setReturnTypeName(rtnType)
                .setInvokeSuper(invokeSuper)
                .setIsSetter(isSetter)
                .addModifier(modifier1)
                .addModifier(modifier2)
                .addAnnotationBuilder(mockAnnoBudr)
                .addThrowTypeName(throwType1)
                .addThrowTypeName(throwType2)
                .addParameterBuilder(paramBudr1)
                .addCodeBuilder(codeBudr)

        then:
        methodBudr1 == methodBudr2
        methodBudr1 != methodBudr3
        methodBudr2 != methodBudr3
        methodBudr1.hashCode() == methodBudr2.hashCode()
        methodBudr1.hashCode() != methodBudr3.hashCode()
        methodBudr2.hashCode() != methodBudr3.hashCode()

        where:
        name    | rtnType   | invokeSuper                   | invokeSuperAfter  | invokeSuperBefore | isSetter  | modifier1       | modifier2      | modifiers      | throwType1    | throwType2
        'Name'  | 'String'  | MethodMeta.InvokeSuper.AFTER  | true              | false             | false     | Modifier.PUBLIC | Modifier.FINAL | 'public final' | 'AE'          | 'BE'
    }

    def mockAnnoMeta = Mock(AnnotationMeta)
    def mockParamMeta = Mock(ParameterMeta)
    def mockCodeMeta = Mock(CodeMeta)

    private class MockAnnoMetaBuilder extends AnnotationMeta.Builder {

        private int _initInvokedCount = 0
        private int _validationCount = 0;
        private int _createInstCount = 0

        public void initProperties() {
            this._initInvokedCount++
        }

        public void validation() {
            this._validationCount++
        }

        public AnnotationMeta createInstance() {
            this._createInstCount++
            return mockAnnoMeta
        }
    }

    private class MockParamMeta extends ParameterMeta.Builder {

        private String _name;
        private String _type;
        private int _initInvokedCount = 0
        private int _validationCount = 0;
        private int _createInstCount = 0

        private MockParamMeta(String name) {
            this._name = name;
        }

        public String getType() {
            return this._type
        }

        public String getName() {
            return this._name
        }

        public void initProperties() {
            this._initInvokedCount++
        }

        public void validation() {
            this._validationCount++
        }

        public ParameterMeta createInstance() {
            this._createInstCount++
            return mockParamMeta
        }

        public boolean equals(Object other) {
            return true
        }
    }

    private class MockCodeMeta extends CodeMeta.Builder {

        private String _srcTemp
        private int _initInvokedCount = 0
        private int _validationCount = 0;
        private int _createInstCount = 0

        MockCodeMeta(String srcTemp) {
            this._srcTemp = srcTemp
        }

        public String getTemplateSourceName() {
            return this._srcTemp
        }

        public void initProperties() {
            this._initInvokedCount++
        }

        public void validation() {
            this._validationCount++
        }

        public CodeMeta createInstance() {
            this._createInstCount++
            return mockCodeMeta
        }
    }
}