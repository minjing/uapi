/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.internal;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.helper.Pair;
import uapi.helper.StringHelper;

/**
 * The QualifiedServiceId indicate service id and where is the service from
 */
public class QualifiedServiceId extends Pair<String, String> {

    /**
     * The separator used to separate service id and service from
     * Like service@location
     */
    static final String LOCATION     = "@";

    /**
     * Indicate the service can be matched any location, normally
     * the {@code Inject} annotation can indicate where is the location of the request service
     */
    public static final String FROM_ANY     = "Any";

    /**
     * Indicate the service is from local
     */
    public static final String FROM_LOCAL   = "Local";

    public static QualifiedServiceId splitTo(String combined) {
        return splitTo(combined, LOCATION);
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

    public static String combine(String id, String from) {
        ArgumentChecker.required(id, "id");
        ArgumentChecker.required(from, "from");
        return StringHelper.makeString("{}{}{}", id, LOCATION, from);
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
        return qsId.getFrom().equals(FROM_ANY);
    }

    public boolean canFrom(final String from) {
        ArgumentChecker.notEmpty(from, "from");
        if (getFrom().equals(FROM_ANY)) {
            return true;
        } else if (getFrom().equals(FROM_ANY)) {
            return true;
        } else if (getFrom().equals(from)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("{}@{}", getId(), getFrom());
    }
}
