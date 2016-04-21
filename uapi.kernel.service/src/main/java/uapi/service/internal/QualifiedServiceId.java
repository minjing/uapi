package uapi.service.internal;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.helper.Pair;
import uapi.helper.StringHelper;

/**
 * The QualifiedServiceId indicate service id and where is the service from
 */
public class QualifiedServiceId extends Pair<String, String> {

    public static QualifiedServiceId splitTo(String combined, String separator) {
        ArgumentChecker.notEmpty(combined, "combined");
        ArgumentChecker.notEmpty(separator, "separator");
        String[] split = combined.split(separator);
        if (split.length == 2) {
            return new QualifiedServiceId(split[0], split[1]);
        } else {
            throw new InvalidArgumentException(
                    "The argument {} can not be separated to one or two value by separator {}",
                    combined, separator);
        }
    }

    public QualifiedServiceId(String leftValue, String rightValue) {
        super(leftValue, rightValue);
    }

    public String getId() {
        return getLeftValue();
    }

    public String getFrom() {
        return getRightValue();
    }

    @Override
    public String toString() {
        return StringHelper.makeString("{}@{}", getId(), getFrom());
    }
}
