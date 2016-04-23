package uapi.service.internal;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.helper.Pair;
import uapi.helper.StringHelper;
import uapi.service.IRegistry;

/**
 * The QualifiedServiceId indicate service id and where is the service from
 */
public class QualifiedServiceId extends Pair<String, String> {

    public static QualifiedServiceId splitTo(String combined) {
        return splitTo(combined, IRegistry.LOCATION);
    }

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

    /**
     * Check this qualified service id can be assigned to specific qualified service id.
     * Assignment means for example Local can be assigned to Any
     *
     * @param   qsId
     *          The specific qualified service id
     */
    public boolean isAssignTo(QualifiedServiceId qsId) {
        ArgumentChecker.notNull(qsId, "qsId");
        if (! getId().equals(qsId.getId())) {
            return false;
        }
        if (getFrom().equals(qsId.getFrom())) {
            return true;
        }
        if (qsId.getFrom().equals(IRegistry.FROM_ANY)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("{}@{}", getId(), getFrom());
    }
}
