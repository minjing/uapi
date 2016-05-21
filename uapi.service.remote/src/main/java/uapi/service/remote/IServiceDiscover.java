package uapi.service.remote;

/**
 * The service discover is used to discover service from somewhere.
 */
public interface IServiceDiscover {

    Object discover(ServiceInterfaceMeta serviceInterfaceMeta);
}
