package uapi.annotation;

/**
 * Created by min on 16/2/25.
 */
public final class Type {

    private Type () { }

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

    public static final String Q_OBJECT         = Object.class.getCanonicalName();
    public static final String Q_STRING         = String.class.getCanonicalName();
    public static final String Q_STRING_ARRAY   = String[].class.getCanonicalName();
}
