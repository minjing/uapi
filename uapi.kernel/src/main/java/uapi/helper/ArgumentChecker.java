package uapi.helper;

import com.google.common.base.Strings;
import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;

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
