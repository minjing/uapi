package uapi.service.remote;

/**
 * The service discover is used to discover service from somewhere.
 */
public interface IServiceDiscover {

//    /**
//     * Retrieve the specific service invocation driver
//     *
//     * @param   serviceId
//     *          The service id
//     * @return  The related invocation driver or null if no driver was found.
//     */
//    ICommunicationDriver getInvocationDriver(String serviceId);

    Object discover(String serviceId);
}
