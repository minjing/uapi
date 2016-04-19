package uapi.service;

/**
 * A lifecycle management for service
 */
public interface IServiceLifecycle {

    /**
     * Invoked when a injectable property is set
     *
     * @param   propertyName
     *          The injected property name
     * @param   propertyValue
     *          The injected property value
     */
    void onPropertySet(String propertyName, Object propertyValue);

    /**
     * Invoked when the service is resolved and all other conditions is satisfied.
     * Resolved means all dependencies has been set
     * Satisfied means that like configurations has been set
     */
    void onInit();
}
