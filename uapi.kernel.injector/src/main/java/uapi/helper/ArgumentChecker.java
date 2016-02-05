package uapi.helper;

import com.google.common.base.Strings;
import uapi.InvalidArgumentException;

public final class ArgumentChecker {

    public static void required(Object argument, String argumentName) {
        if (argument instanceof String) {
            if (Strings.isNullOrEmpty((String) argument)) {
                throw new InvalidArgumentException(argumentName, InvalidArgumentException.InvalidArgumentType.EMPTY);
            }
        } else {
            if (argument == null) {
                throw new InvalidArgumentException(argumentName, InvalidArgumentException.InvalidArgumentType.EMPTY);
            }
        }
    }

    private ArgumentChecker() { }
}
