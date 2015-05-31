package uapi.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;

import com.google.common.base.Strings;

public final class ClassHelper {

    private static final String FIELD_PREFIX    = "_";
    private static final String SETTER_PREFIX   = "set";

    public static String makeSetterName(String fieldName, boolean isCollection) {
        if (Strings.isNullOrEmpty(fieldName)) {
            throw new IllegalArgumentException("The field name can't be empty or null");
        }
        String propName;
        if (fieldName.startsWith(FIELD_PREFIX)) {
            propName = fieldName.substring(1);
        } else {
            propName = fieldName;
        }
        if (isCollection) {
            propName = WordHelper.singularize(propName);
        }
        String setterName = SETTER_PREFIX + propName.substring(0, 1).toUpperCase() + propName.substring(1, propName.length());
        return setterName;
    }

    public static Class<?>[] getInterfaceParameterizedClass(Class<?> type, Class<?> interfaceType) {
        if (type == null) {
            throw new InvalidArgumentException("type", InvalidArgumentType.EMPTY);
        }
        Class<?>[] paramClasses = null;
        List<Type> intfTypes = Arrays.asList(type.getGenericInterfaces());
        for (Type intfType : intfTypes) {
            if (! intfType.equals(interfaceType)) {
                continue;
            }
            Type[] paramTypes = ((ParameterizedType) intfType).getActualTypeArguments();
            paramClasses = new Class<?>[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                paramClasses[i] = (Class<?>) paramTypes[i];
            }
            break;
        }
        return paramClasses;
    }
}
