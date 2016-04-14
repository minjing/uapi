package uapi.helper;

import com.google.common.base.Strings;
import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.KernelException;

import java.util.Collection;

/**
 * A utility for argument checker
 * 
 * @author min
 */
public class ArgumentChecker {

    private ArgumentChecker() { }

    /**
     * Check the integer argument value should between min value and max value
     * The min value and max value should be included.
     * An {@code InvalidArgumentException} will be thrown when the argument value
     * is not between min value and max value
     *
     * @param   arg
     *          The integer argument which will be checked
     * @param   argName
     *          The checked argument name
     * @param   minValue
     *          The min value
     * @param   maxValue
     *          The max value
     * @throws  InvalidArgumentException
     *          The checked argument value is not between min value and max value
     */
    public static void checkInt(int arg, String argName, int minValue, int maxValue) {
        if (arg < minValue || arg > maxValue) {
            throw new InvalidArgumentException(argName,
                    "The argument must be more than {} and less than {}",
                    minValue, maxValue);
        }
    }

    /**
     * Ensure the argument is not null, if it is null the exception will be thrown
     *
     * @param   argument
     *          The argument which will be checked
     * @param   argumentName
     *          The argument name
     * @throws  InvalidArgumentException
     *          The argument is null
     */
    public static void notNull(
            final Object argument,
            final String argumentName
    ) throws InvalidArgumentException {
        if (argument == null) {
            throw new InvalidArgumentException(argumentName, InvalidArgumentType.EMPTY);
        }
    }

    /**
     * Ensure the argument is not empty, if it is empty then the exception will be thrown
     *
     * @param   argument
     *          The argument which will be checked
     * @param   argumentName
     *          The argument name
     * @throws  InvalidArgumentException
     *          The argument is null or empty string or only contains blank string
     */
    public static void notEmpty(
            final String argument,
            final String argumentName
    ) throws InvalidArgumentException {
        if (isEmpty(argument)) {
            throw new InvalidArgumentException(argumentName, InvalidArgumentType.EMPTY);
        }
    }

    /**
     * Test the argument is empty or not.
     * Empty string means null, empty string or only contains blank string
     *
     * @param   argument
     *          The argument which will be tested
     * @return  true if the argument is empty otherwise return false
     */
    public static boolean isEmpty(
            final String argument
    ) {
        return Strings.isNullOrEmpty(argument) || argument.trim().length() == 0;
    }

    /**
     * Ensure the array argument is not empty.
     * A empty array means null or the array length is 0
     *
     * @param   argument
     *          The argument which will be checked
     * @param   argumentName
     *          The argument name
     * @param   <T>
     *          The argument type
     * @throws  InvalidArgumentException
     *          If the array argument is empty
     */
    public static <T> void notEmpty(
            final T[] argument,
            final String argumentName
    ) throws InvalidArgumentException {
        notNull(argument, "argument");
        if (argument.length == 0) {
            throw new InvalidArgumentException(argumentName, InvalidArgumentType.EMPTY);
        }
    }

    public static void equals(Object argument, Object expect, String argumentName) {
        if (argument == null && expect == null) {
            return;
        }
        if (argument != null && argument.equals(expect)) {
            return;
        }
        throw new InvalidArgumentException("The arguments {} is not equals expected value {} - {}",
                argumentName, argument, expect);
    }

    public static void notEquals(Object argument, Object unexpected, String argumentName) {
        if (argument != null && ! argument.equals(unexpected)) {
            return;
        }
        throw new InvalidArgumentException("The arguments {} is equals unexpected value {} - {}",
                argumentName, argument, unexpected);
    }

    public static void contains(String argument, String unexpected, String argumentName) {
        if (argument != null && ! argument.contains(unexpected)) {
            return;
        }
        throw new InvalidArgumentException("The argument {} contains unexpected value {} - {}",
                argumentName, argument, unexpected);
    }

    public static <T> void notContains(Collection<T> argument, String argumentName, String otherInfo, T... unexpects) {
        notNull(argument, argumentName);
        T unexpected = CollectionHelper.contains(argument, unexpects);
        if (unexpected != null) {
            throw new InvalidArgumentException(
                    "The argument {} with {} contains an unexpected item: {}",
                    argumentName, otherInfo, unexpected);
        }
    }

    /**
     * Ensure the argument is presented, if the argument is null or is empty string
     * then the exception will be thrown.
     *
     * @param   argument
     *          The argument object which will be checked
     * @param   argumentName
     *          The argument name will be used in exception message if check failed
     */
    public static void required(
            final Object argument,
            final String argumentName
    ) throws InvalidArgumentException {
        if (argument instanceof String) {
            notEmpty((String) argument, argumentName);
        } else {
            notNull(argument, argumentName);
        }
    }

    public static void notZero(
            final Collection collection,
            final String argumentName
    ) throws InvalidArgumentException {
        notNull(collection, "collection");
        if (collection.size() == 0) {
            throw new InvalidArgumentException(
                    "The size of argument[{}] must be more then 0", argumentName);
        }
    }
}
