package uapi.kernel.helper;

public final class ArrayHelper {

    public static final Object[] emptyArray = new Object[0];

    @SuppressWarnings("unchecked")
    public static <T> T[] empty() {
        return (T[]) emptyArray;
    }
}
