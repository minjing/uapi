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
import javax.lang.model.element.PackageElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements

/**
 * Test case for ClassMeta
 */
class ClassMetaTest extends Specification {

    def 'Test build'() {
        def mockAnnoBudr = new MockAnnotationBuilder()
        def mockFieldBudr = new MockFieldBuilder()
        def mockMethodBudr = new MockMethodBuilder()

        when:
        ClassMeta clsMeta = ClassMeta.builder()
            .setPackageName(pkgName)
            .setClassName(clsName)
            .setGeneratedClassName(genClsName)
            .addAnnotationBuilder(mockAnnoBudr)
            .addFieldBuilder(mockFieldBudr)
            .addImplement(impl)
            .addImport(impot)
            .addMethodBuilder(mockMethodBudr)
            .build()

        then:
        clsMeta.getPackageName() == pkgName
        clsMeta.getClassName() == clsName
        clsMeta.getGeneratedClassName() == genClsName
        clsMeta.getAnnotations().size() == 1
        clsMeta.getFields().size() == 1
        clsMeta.getImplements().size() == 1
        clsMeta.getImports().size() == 1
        clsMeta.getMethods().size() == 1

        where:
        pkgName     | clsName   | genClsName    | impl      | impot
        'pkgName'   | 'clsName' | 'clsName_gen' | 'Test'    | 'abc'
    }

    def 'Test build from Element'() {
        def mockElemt = Mock(Element) {
            getKind() >> ElementKind.CLASS
            getSimpleName() >> Mock(Name) {
                toString() >> clsName
            }
        }
        def mockBudrCtx = Mock(IBuilderContext) {
            getElementUtils() >> Mock(Elements) {
                getPackageOf(_) >> Mock(PackageElement) {
                    getQualifiedName() >> Mock(Name) {
                        toString() >> pkgName
                    }
                }
            }
        }

        when:
        ClassMeta.Builder clsBudr = ClassMeta.builder(mockElemt, mockBudrCtx)

        then:
        clsBudr.getPackageName() == pkgName
        clsBudr.getClassName() == clsName
        clsBudr.getGeneratedClassName() == genClsName

        where:
        pkgName     | clsName   | genClsName
        'pkgName'   | 'clsName' | 'clsName_Generated'
    }

    def mockAnnoMeta = Mock(AnnotationMeta)
    def mockFieldMeta = Mock(FieldMeta)
    def mockMethodMeta = Mock(MethodMeta)

    def 'Test find field builder'() {
        def mockFieldBudr = new MockFieldBuilder()

        when:
        ClassMeta.Builder clsBudr = ClassMeta.builder()
                .addFieldBuilder(mockFieldBudr)

        then:
        clsBudr.findFieldBuilder(mockFieldBudr) != null
    }

    def 'Test find field builder by name'() {
        def mockFieldBudr = new MockFieldBuilder()
        mockFieldBudr.setName(name)
        mockFieldBudr.setTypeName(type)

        when:
        ClassMeta.Builder clsBudr = ClassMeta.builder()
                .addFieldBuilder(mockFieldBudr)

        then:
        clsBudr.findFieldBuilder(mockFieldBudr) != null

        where:
        name    | type
        'Name'  | 'String'
    }

    def 'Test find method builder by element'() {
        def mockElemt = Mock(ExecutableElement) {
            getKind() >> ElementKind.METHOD
            getSimpleName() >> Mock(Name) {
                toString() >> methodName
            }
            getReturnType() >> Mock(TypeMirror) {
                toString() >> rtnType
            }
            getModifiers() >> [Modifier.PUBLIC]
            getThrownTypes() >> [ Mock(TypeMirror) {
                toString() >> thrownType
            } ]
            getParameters() >> []
        }
        def mockBudrCtx = Mock(IBuilderContext)

        def methodBudr = new MockMethodBuilder()

        when:
        ClassMeta.Builder clsBudr = ClassMeta.builder()
            .addMethodBuilder(methodBudr)

        then:
        clsBudr.findMethodBuilder(mockElemt, mockBudrCtx)  != null

        where:
        methodName  | rtnType   | thrownType
        'name'      | 'rtnType' | 'thrownType'
    }

    def 'Test equals'() {
        def mockAnnoBudr = new MockAnnotationBuilder()
        def mockFieldBudr = new MockFieldBuilder()
        def mockMethodBudr = new MockMethodBuilder()

        when:
        ClassMeta.Builder clsBudr1 = ClassMeta.builder()
                .setPackageName(pkgName)
                .setClassName(clsName)
                .setGeneratedClassName(genClsName)
                .addAnnotationBuilder(mockAnnoBudr)
                .addFieldBuilder(mockFieldBudr)
                .addImplement(impl)
                .addImport(impot)
                .addMethodBuilder(mockMethodBudr)
        ClassMeta.Builder clsBudr2 = ClassMeta.builder()
                .setPackageName(pkgName)
                .setClassName(clsName)
                .setGeneratedClassName(genClsName)
                .addAnnotationBuilder(mockAnnoBudr)
                .addFieldBuilder(mockFieldBudr)
                .addImplement(impl)
                .addImport(impot)
                .addMethodBuilder(mockMethodBudr)
        ClassMeta.Builder clsBudr3 = ClassMeta.builder()
                .setPackageName(pkgName)
                .setClassName('sss')
                .setGeneratedClassName(genClsName)
                .addAnnotationBuilder(mockAnnoBudr)
                .addFieldBuilder(mockFieldBudr)
                .addImport(impot)
                .addMethodBuilder(mockMethodBudr)

        then:
        clsBudr1 == clsBudr2
        clsBudr1 != clsBudr3
        clsBudr2 != clsBudr3

        where:
        pkgName     | clsName   | genClsName    | impl      | impot
        'pkgName'   | 'clsName' | 'clsName_gen' | 'Test'    | 'abc'
    }

    class MockAnnotationBuilder extends AnnotationMeta.Builder {
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

    class MockFieldBuilder extends FieldMeta.Builder {
        private int _initInvokedCount = 0
        private int _validationCount = 0;
        private int _createInstCount = 0

        public void initProperties() {
            this._initInvokedCount++
        }

        public void validation() {
            this._validationCount++
        }

        public FieldMeta createInstance() {
            this._createInstCount++
            return mockFieldMeta
        }

        public boolean equals(Object obj) {
            return true
        }
    }

    class MockMethodBuilder extends MethodMeta.Builder {
        private int _initInvokedCount = 0
        private int _validationCount = 0;
        private int _createInstCount = 0

        public void initProperties() {
            this._initInvokedCount++
        }

        public void validation() {
            this._validationCount++
        }

        public MethodMeta createInstance() {
            this._createInstCount++
            return mockMethodMeta
        }
    }
}
