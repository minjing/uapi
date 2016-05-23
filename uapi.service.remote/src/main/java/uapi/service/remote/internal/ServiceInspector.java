package uapi.service.remote.internal;

import uapi.helper.ArgumentChecker;

/**
 * The ServiceInspector inspect specific service interface by its id
 * and generate service interface meta class
 */
class ServiceInspector {

    void inspect(final String serviceId) {
        ArgumentChecker.required(serviceId, "serviceId");
        return;
    }
}
