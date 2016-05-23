package uapi.service.remote.internal;

import uapi.helper.ArgumentChecker;
import uapi.service.remote.ServiceInterfaceMeta;

/**
 * The ServiceInspector inspect specific service interface by its id
 * and generate service interface meta class
 */
class ServiceInspector {

    ServiceInterfaceMeta inspect(final String serviceId) {
        ArgumentChecker.required(serviceId, "serviceId");
        return null;
    }
}
