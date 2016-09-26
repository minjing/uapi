/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;

import com.google.common.base.Strings;

public final class ClassHelper {

    private static final String FIELD_PREFIX    = "_";
    private static final String SETTER_PREFIX   = "set";
    private static final String ADD_PREFIX      = "add";
    private static final String PUT_PREFIX      = "put";

    public static String makeSetterName(String fieldName, boolean isCollection, boolean isMap) {
        if (Strings.isNullOrEmpty(fieldName)) {
            throw new IllegalArgumentException("The field name can't be empty or null");
        }
        String propName;
        if (fieldName.startsWith(FIELD_PREFIX)) {
            propName = fieldName.substring(1);
        } else {
            propName = fieldName;
        }
        String setterName;
        if (isCollection) {
            propName = WordHelper.singularize(propName);
            setterName = ADD_PREFIX + propName.substring(0, 1).toUpperCase() + propName.substring(1, propName.length());
        } else if (isMap) {
            propName = WordHelper.singularize(propName);
            setterName = PUT_PREFIX + propName.substring(0, 1).toUpperCase() + propName.substring(1, propName.length());
        } else {
            setterName = SETTER_PREFIX + propName.substring(0, 1).toUpperCase() + propName.substring(1, propName.length());
        }
        return setterName;
    }

    public static Class<?> getElementType(Class<?> collectionClass, Type collectionType, ChangeableBoolean isCollection) {
        if (! (collectionType instanceof ParameterizedType)) {
            return collectionClass;
        }
        if (Collection.class.isAssignableFrom(collectionClass)) {
            isCollection.set(true);
            Type elemType = ((ParameterizedType) collectionType).getActualTypeArguments()[0];
            if (elemType instanceof ParameterizedType) {
                return getElementType((Class<?>) ((ParameterizedType) elemType).getRawType(), elemType, isCollection);
            } else {
                return (Class<?>) elemType;
            }
        } else if (Map.class.isAssignableFrom(collectionClass)) {
            isCollection.set(true);
            Type elemType = ((ParameterizedType) collectionType).getActualTypeArguments()[1];
            if (elemType instanceof ParameterizedType) {
                return getElementType((Class<?>) ((ParameterizedType) elemType).getRawType(), elemType, isCollection);
            } else {
                return (Class<?>) elemType;
            }
        } else {
            return collectionClass;
        }
    }

    public static Class<?>[] getInterfaceParameterizedClasses(Class<?> type, Class<?> interfaceType) {
        if (type == null) {
            throw new InvalidArgumentException("type", InvalidArgumentType.EMPTY);
        }
        Class<?>[] paramClasses = null;
        List<Type> intfTypes = Arrays.asList(type.getGenericInterfaces());
        for (Type intfType : intfTypes) {
            if (! (intfType instanceof ParameterizedType)) {
                continue;
            }
            Type rowType = ((ParameterizedType) intfType).getRawType();
            if (! rowType.equals(interfaceType)) {
                continue;
            }
            Type[] paramTypes = ((ParameterizedType) intfType).getActualTypeArguments();
            paramClasses = new Class<?>[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                paramClasses[i] = (Class<?>) paramTypes[i];
            }
        }
        return paramClasses;
    }
}
