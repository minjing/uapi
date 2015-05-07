package uapi.kernel.helper;

import com.google.common.base.Strings;

public final class ClassHelper {

    private static final String FIELD_PREFIX    = "_";
    private static final String SETTER_PREFIX   = "set";

    public static String makeSetterName(String fieldName) {
        if (Strings.isNullOrEmpty(fieldName)) {
            throw new IllegalArgumentException("The field name can't be empty or null");
        }
        String propName;
        if (fieldName.startsWith(FIELD_PREFIX)) {
            propName = fieldName.substring(1);
        } else {
            propName = fieldName;
        }
        String setterName = SETTER_PREFIX + propName.substring(0, 1).toUpperCase() + propName.substring(1, propName.length());
        return setterName;
    }
}
