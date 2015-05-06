package uapi.kernel.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.google.common.base.Strings;

import uapi.kernel.Attribute;
import uapi.kernel.Inject;
import uapi.kernel.IService;
import uapi.kernel.helper.StringHelper;

final class ServiceResolver {

    private static final String FIELD_PREFIX    = "_";
    private static final String SETTER_PREFIX   = "set";

    ResolvedService resolve(Class<? extends IService> type) {
        if (type == null) {
            throw new IllegalArgumentException("The argument is required - type");
        }
        Attribute attr = type.getAnnotation(Attribute.class);
        ResolvedService resolvedSvr;
        if (attr != null && ! Strings.isNullOrEmpty(attr.sid())) {
            resolvedSvr = new ResolvedService(type, attr.sid());
        } else {
            resolvedSvr = new ResolvedService(type);
        }
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            Inject inject = field.getAnnotation(Inject.class);
            if (inject == null) {
                continue;
            }
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            String setterName = makeSetterName(fieldName);
            Method setter;
            try {
                setter = type.getMethod(setterName, fieldType);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException(
                        StringHelper.makeString("Can't found setter for field {} in class {}", fieldName, type.getName()));
            }

            String dependSid = inject.sid();
            if (dependSid == "") {
                dependSid = field.getType().getName();
            }
            resolvedSvr.addDependency(dependSid, type, setter);
        }
        return null;
    }

    private String makeSetterName(String fieldName) {
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
