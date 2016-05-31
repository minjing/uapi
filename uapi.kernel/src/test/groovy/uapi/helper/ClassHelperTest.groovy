/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper

import spock.lang.Specification

import java.lang.annotation.Annotation
import java.lang.reflect.Field

/**
 * Unit test for ClassHelper
 */
class ClassHelperTest extends Specification {

    def 'test CollectionField'() {
        expect:
        ClassHelper.makeSetterName(prop, isCollection, isMap) == setterName

        where:
        prop        | isCollection  | isMap | setterName
        'messages'  | true          | false | 'addMessage'
        'children'  | true          | false | 'addChild'
    }

    def  'test GetInterfaceParameterizedClasses'() {
        given:
        Class<?>[] paramTypes = ClassHelper.getInterfaceParameterizedClasses(FakeService.class, IFakeInterface.class);

        expect:
        paramTypes.length == 1
        paramTypes[0] == TestAnno.class
    }

    def 'test GetElementType' () {
        given:
        def isCollection = new ChangeableBoolean()

        when:
        def field = CollectionService.class.getDeclaredField(fieldName);
        def fieldType = ClassHelper.getElementType(field.getType(), field.getGenericType(), isCollection)

        then:
        field != null
        fieldType == type
        isCollection.get() == collection

        where:
        fieldName   | type              | collection
        "listField" | String.class      | true
        "mapField"  | Integer.class     | true
        "mixField"  | Float.class       | true
        "mixField2" | Double.class      | true
        "test"      | FakeClass.class   | true
        "none"      | FakeClass.class   | false
    }

    private interface IFakeInterface<T extends Annotation> { }

    private final class FakeService implements IFakeInterface<TestAnno> { }

    @SuppressWarnings("unused")
    private final class CollectionService {

        private List<String> listField;
        private Map<String, Integer> mapField;
        private Map<String, List<Float>> mixField;
        private List<Map<String, Double>> mixField2;
        private Map<String, FakeClass<String>> test;
        private FakeClass<String> none;
    }

    private final class FakeClass<T> {}
}
