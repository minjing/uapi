/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi;

import java.util.ArrayList;

/**
 * A utility class for type definition
 */
public final class Type {

    public static final String VOID             = "void";
    public static final String BOOLEAN          = "boolean";
    public static final String INTEGER          = "int";
    public static final String LONG             = "long";
    public static final String SHORT            = "short";
    public static final String FLOAT            = "float";
    public static final String DOUBLE           = "double";

    public static final String Q_BOOLEAN        = Boolean.class.getCanonicalName();
    public static final String Q_INTEGER        = Integer.class.getCanonicalName();
    public static final String Q_LONG           = Long.class.getCanonicalName();
    public static final String Q_SHORT          = Short.class.getCanonicalName();
    public static final String Q_FLOAT          = Float.class.getCanonicalName();
    public static final String Q_DOUBLE         = Double.class.getCanonicalName();

    public static final String OBJECT           = "Object";
    public static final String STRING           = "String";
    public static final String STRING_ARRAY     = "String[]";
    public static final String STRING_LIST      = "java.util.List<java.lang.String>";

    public static final String Q_OBJECT         = Object.class.getCanonicalName();
    public static final String Q_STRING         = String.class.getCanonicalName();
    public static final String Q_STRING_ARRAY   = String[].class.getCanonicalName();
    public static final String Q_ARRAY_LIST     = ArrayList.class.getCanonicalName();

    public static final Class<Boolean>  T_BOOLEAN   = Boolean.class;
    public static final Class<Integer>  T_INTEGER   = Integer.class;
    public static final Class<Long>     T_LONG      = Long.class;
    public static final Class<Short>    T_SHORT     = Short.class;
    public static final Class<Float>    T_FLOAT     = Float.class;
    public static final Class<Double>   T_DOUBLE    = Double.class;

    public static final Class<String>   T_STRING    = String.class;

    private Type() { }
}
