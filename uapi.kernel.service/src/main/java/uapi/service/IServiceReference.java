package uapi.service;

import uapi.service.internal.QualifiedServiceId;

/**
 * A service reference hold a service and provide only some limited functionality
 */
public interface IServiceReference {

    /**
     * Retrieve the service id
     *
     * @return  Service id
     */
    String getId();

    /**
     * Retrieve the service qualified id
     * The qualified id is composed with service id and service form
     *
     * @return  The service qualified id
     */
    QualifiedServiceId getQualifiedId();

    /**
     * Retrieve the service instance
     *
     * @return  The service instance
     */
    Object getService();

    /**
     * When the service is satisfied, the method wil be invoked
     */
    void notifySatisfied();
}
