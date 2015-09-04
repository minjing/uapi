package uapi.helper;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;

/**
 * A utility for argument checker
 * 
 * @author min
 */
public class ArgumentChecker {

    private ArgumentChecker() { }

    /**
     * Check the argument is empty or not.
     * For generic object the method only check it is null
     * For string object the method will check it is empty string or not
     * 
     * @param   arg
     *          The argument object which will be checked
     * @param   argName
     *          The argument name will be used in exception message if check failed
     */
    public static void isEmpty(Object arg, String argName) {
        if (arg == null) {
            throw new InvalidArgumentException(argName, InvalidArgumentType.EMPTY);
        }
        if (arg instanceof String) {
            if (((String) arg).trim().length() == 0) {
                throw new InvalidArgumentException(argName, InvalidArgumentType.EMPTY);
            }
        }
    }

    public static void checkInt(int arg, String argName, int minValue, int maxValue) {
        if (arg < minValue || arg > maxValue) {
            throw new InvalidArgumentException(argName,
                    "The argument must be more than {} and less than {}",
                    minValue, maxValue);
        }
    }
}
