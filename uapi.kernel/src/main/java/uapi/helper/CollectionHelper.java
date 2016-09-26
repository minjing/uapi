/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper;

import java.util.*;
import java.util.stream.Stream;

/**
 * Some useful function for collection
 */
public final class CollectionHelper {

    public static final Object[] emptyArray = new Object[0];

    private static final String DEFAULT_SEPARATOR   = ",";

    @SuppressWarnings("unchecked")
    public static <T> T[] empty() {
        return (T[]) emptyArray;
    }

    /**
     * Check whether the array contains null element or not
     *
     * @param   elements
     *          The array which will be checked
     * @param   <T>
     *          The element type
     * @return  If found null element in the array then return true otherwise return false
     */
    public static <T> boolean hasNull(final T... elements) {
        ArgumentChecker.required(elements, "elements");
        for (T element : elements) {
            if (element == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the collection contains null element or not
     *
     * @param   collection
     *          The collection which will be checked
     * @param   <T>
     *          The element type
     * @return  If found null element in the collection then return true otherwise return false
     */
    public static <T> boolean hasNull(final Collection<T> collection) {
        ArgumentChecker.required(collection, "collection");
        for (T element : collection) {
            if (element == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the collection contains one of specified elements and
     * return matched element or null if nothing is matched
     *
     * @param   collection
     *          The collection which will be checked
     * @param   elements
     *          The elements
     * @param   <T>
     *          The element type
     * @return  Matched element or null if nothing is matched
     */
    public static <T> T contains(
            final Collection<T> collection,
            final T... elements) {
        ArgumentChecker.notNull(collection, "collection");
        for (T element : elements) {
            if (collection.contains(element)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Check whether the array contains on of specified elements and return matched
     * element or null if nothing is matched
     *
     * @param   array
     *          The array which will be checked
     * @param   elements
     *          The elements
     * @param   <T>
     *          The element type
     * @return  Matched element of null if nothing is matched
     */
    public static <T> T contains(
            final T[] array,
            final T... elements) {
        ArgumentChecker.notNull(array, "array");
        for (T element : elements) {
            for (T item : array) {
                if (item.equals(element)) {
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * Check whether the collection contains all of specified elements and
     * return true if all element is matching or false if it is not all elements are matched
     *
     * @param   collection
     *          The collection which will be checked
     * @param   elements
     *          The elements
     * @param   <T>
     *          The element type
     * @return  True means all elements are matched or otherwise false
     */
    public static <T> boolean isContainsAll(final Collection<T> collection, T... elements) {
        ArgumentChecker.notNull(collection, "collection");
        for (T element : elements) {
            if (! collection.contains(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether the array contains contains all of specified elements and
     * return true if all element is matching or false if it is not all elements are matched
     *
     * @param   array
     *          The array which will be checked
     * @param   elements
     *          The elements
     * @param   <T>
     *          The element type
     * @return  Matched element of null if nothing is matched
     */
    public static <T> boolean isContainsAll(final T[] array, final T... elements) {
        ArgumentChecker.notNull(array, "array");
        for (T element : elements) {
            boolean found = false;
            for (T item : array) {
                if (item.equals(element)) {
                    found = true;
                    break;
                }
            }
            if (! found) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether the collection contains one of specified elements and
     * return matched element or null if nothing is matched
     * The method using strict matching which mean using compared object must
     * is same object (same object address)
     *
     * @param   collection
     *          The collection which will be checked
     * @param   elements
     *          The elements
     * @param   <T>
     *          The element type
     * @return  Matched element or null if nothing is matched
     */
    public static <T> T strictContains(
            final Collection<T> collection,
            final T... elements) {
        ArgumentChecker.notNull(collection, "collection");
        for (T element : elements) {
            for (T item : collection) {
                if (element == item) {
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * Check whether the array contains on of specified elements and return matched
     * element or null if nothing is matched
     * The method using strict matching which mean using compared object must
     * is same object (same object address)
     *
     * @param   array
     *          The array which will be checked
     * @param   elements
     *          The elements
     * @param   <T>
     *          The element type
     * @return  Matched element of null if nothing is matched
     */
    public static <T> T strictContains(
            final T[] array,
            final T... elements) {
        ArgumentChecker.notNull(array, "array");
        for (T element : elements) {
            for (T item : array) {
                if (element == item) {
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * Check specified collection contains one specified elements
     *
     * @param   collection
     *          The collection which will be checked
     * @param   elements
     *          The elements which used for element matching
     * @param   <T>
     *          The element type
     * @return  True the collection contains one otherwise return false
     */
    public static <T> boolean isContains(
            final Collection<T> collection,
            final T... elements) {
        return contains(collection, elements) != null;
    }

    /**
     * Check specified array contains one specified elements
     *
     * @param   array
     *          The array which will be checked
     * @param   elements
     *          The elements which used for element matching
     * @param   <T>
     *          The element type
     * @return  True means the array contains one otherwise return false
     */
    public static <T> boolean isContains(
            final T[] array,
            final T... elements) {
        return contains(array, elements) != null;
    }

    /**
     * Check specified collection contains one specified elements
     * The method using strict matching which mean using compared object must
     * is same object (same object address)
     *
     * @param   collection
     *          The collection which will be checked
     * @param   elements
     *          The elements which used for element matching
     * @param   <T>
     *          The element type
     * @return  True the collection contains one otherwise return false
     */
    public static <T> boolean isStrictContains(
            final Collection<T> collection,
            final T... elements) {
        return strictContains(collection, elements) != null;
    }

    /**
     * Check specified array contains one specified elements
     * The method using strict matching which mean using compared object must
     * is same object (same object address)
     *
     * @param   array
     *          The array which will be checked
     * @param   elements
     *          The elements which used for element matching
     * @param   <T>
     *          The element type
     * @return  True means the array contains one otherwise return false
     */
    public static <T> boolean isStrictContains(
            final T[] array,
            final T... elements) {
        return strictContains(array, elements) != null;
    }

    /**
     * Convert an array to a string, each array element will be construct with
     * {@code DEFAULT_SEPARATOR}
     *
     * @param   array
     *          The array
     * @return  The string
     */
    public static String asString(
            final Object[] array) {
        return asString(array, DEFAULT_SEPARATOR);
    }

    /**
     * Convert an array to a string, each array element wil be construct with specified
     * separator
     *
     * @param   array
     *          The array
     * @param   separator
     *          The separator
     * @return  The string
     */
    public static String asString(
            final Object[] array,
            final String separator) {
        if (array == null || array.length == 0) {
            return StringHelper.EMPTY;
        }
        int sepLen = separator == null ? 0 : separator.length();
        StringBuilder sb = new StringBuilder();
        Stream.of(array).forEach(item -> sb.append(item).append(separator));
        return sb.deleteCharAt(sb.length() - sepLen).toString();
    }

    /**
     * Convert an collection to a string, each collection element will be construct with
     * {@code DEFAULT_SEPARATOR}
     * separator
     *
     * @param   collection
     *          The collection
     * @return  The string
     */
    public static String asString(final Collection collection) {
        return asString(collection, DEFAULT_SEPARATOR);
    }

    /**
     * Convert an collection to a string, each collection element wil be construct with specified
     * separator
     *
     * @param   collection
     *          The collection
     * @param   separator
     *          The separator
     * @return  The string
     */
    public static String asString(
            final Collection collection,
            final String separator) {
        if (collection.size() == 0) {
            return StringHelper.EMPTY;
        }
        int sepLen = separator == null ? 0 : separator.length();
        StringBuilder sb = new StringBuilder();
        collection.forEach(item ->
            sb.append(item).append(separator));
        return sb.deleteCharAt(sb.length() - sepLen).toString();
    }

    /**
     * Compare two Set, make sure two set has same item
     *
     * @param   s1
     *          First set which will be compared
     * @param   s2
     *          Second set which will be compared
     * @return  true means two set is equals otherwise return false
     */
    public static boolean equals(Set s1, Set s2) {
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
