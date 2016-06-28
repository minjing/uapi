package uapi.service;

import uapi.helper.Pair;
import uapi.service.internal.QualifiedServiceId;

/**
 * Represent dependency information of service
 */
public class Dependency extends Pair<QualifiedServiceId, Class<?>> {

    public Dependency(String serviceIdString, Class<?> serviceType) {
        super(QualifiedServiceId.splitTo(serviceIdString), serviceType);
    }

    public Dependency(QualifiedServiceId qualifiedServiceId, Class<?> serviceType) {
        super(qualifiedServiceId, serviceType);
    }

    public QualifiedServiceId getServiceId() {
        return getLeftValue();
    }

    public Class<?> getServiceType() {
        return getRightValue();
    }

    @Override
    public int hashCode() {
        return getServiceId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (! (obj instanceof Dependency)) {
            return false;
        }
        Dependency other = (Dependency) obj;
        return getServiceId().equals(other.getServiceId());
    }
}
