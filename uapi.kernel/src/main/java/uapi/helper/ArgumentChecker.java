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

    public static void checkInt(int arg, String argName, int minValue, int maxValue) {
        if (arg < minValue || arg > maxValue) {
            throw new InvalidArgumentException(argName,
                    "The argument must be more than {} and less than {}",
                    minValue, maxValue);
        }
    }

    public static void notNull(
            final Object argument,
            final String argumentName
    ) throws InvalidArgumentException {
        if (argument == null) {
            throw new InvalidArgumentException(argumentName, InvalidArgumentType.EMPTY);
        }
    }

    public static void notEmpty(
            final String argument,
            final String argumentName
    ) throws InvalidArgumentException {
        if (Strings.isNullOrEmpty(argument)) {
            throw new InvalidArgumentException(argumentName, InvalidArgumentType.EMPTY);
        }
    }

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
