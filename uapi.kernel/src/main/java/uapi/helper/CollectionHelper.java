package uapi.helper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public final class CollectionHelper {

    public static final Object[] emptyArray = new Object[0];

    private static final String DEFAULT_SEPARATOR   = ",";

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

    public static String asString(Object[] array) {
        return asString(array, DEFAULT_SEPARATOR);
    }

    public static String asString(Object[] array, String separator) {
        if (array == null || array.length == 0) {
            return StringHelper.EMPTY;
        }
        int sepLen = separator == null ? 0 : separator.length();
        StringBuilder sb = new StringBuilder();
        Stream.of(array).forEach(item -> sb.append(item).append(separator));
        return sb.deleteCharAt(sb.length() - sepLen).toString();
    }

    public static String asString(Collection collection, String separator) {
        if (collection.size() == 0) {
            return StringHelper.EMPTY;
        }
        int sepLen = separator == null ? 0 : separator.length();
        StringBuilder sb = new StringBuilder();
        collection.forEach(item ->
            sb.append(item).append(separator));
        return sb.deleteCharAt(sb.length() - sepLen).toString();
    }

    public static boolean equals(List l1, List l2) {
        if (l1 == l2) {
            return true;
        }
        if (l1 == null || l2 == null) {
            return false;
        }
        if (l1.size() != l2.size()) {
            return false;
        }
        for (int i = 0; i < l1.size(); i++) {
            Object o1 = l1.get(i);
            Object o2 = l2.get(i);
            if (o1 == null || o2 == null) {
                continue;
            }
            if (o1 == o2) {
                continue;
            }
            if (o1.equals(o2)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static boolean equals(Set s1, Set s2) {
        if (s1 == s2) {
            return true;
        }
        if (s1 == null || s2 == null) {
            return false;
        }
        if (s1.size() != s2.size()) {
            return false;
        }
        for (Object obj : s1) {
            if (! s2.contains(obj)) {
                return false;
            }
        }
        for (Object obj : s2) {
            if (! s1.contains(obj)) {
                return false;
            }
        }
        return true;
    }
}
