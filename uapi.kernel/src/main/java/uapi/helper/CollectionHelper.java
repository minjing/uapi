package uapi.helper;

import java.util.Collection;
import java.util.stream.Stream;

public final class CollectionHelper {

    public static final Object[] emptyArray = new Object[0];

    @SuppressWarnings("unchecked")
    public static <T> T[] empty() {
        return (T[]) emptyArray;
    }

    /**
     * Check whether the collection contains one of specified elements.
     *
     * @param collection
     * @param elements
     * @param <T>
     * @return
     */
    public static <T> boolean contains(Collection collection, T... elements) {
        for (T element : elements) {
            if (collection.contains(element)) {
                return true;
            }
        }
        return false;
    }

    public static String asString(Collection collection, String separator) {
        if (collection.size() == 0) {
            return StringHelper.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        collection.forEach(item ->
            sb.append(item).append(separator));
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
