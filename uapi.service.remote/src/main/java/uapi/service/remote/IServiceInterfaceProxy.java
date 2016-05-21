package uapi.service.remote;

/**
 * The IServiceInterfaceProxy will act as specific service proxy to
 * interactive with remote real service.
 */
public interface IServiceInterfaceProxy {

    /**
     * Set a communicator which used for interaction
     *
     * @param   communicator
     *          The ICommunicator instance
     */
    void setCommunicator(ICommunicator communicator);

    /**
     * Set service interface meta class
     *
     * @param   meta
     *          Service interface meta
     */
    void setMeta(ServiceInterfaceMeta meta);
}
