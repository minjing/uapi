package uapi.service;

public interface IServiceContext {

    <T> T getService(Class<T> serviceType);

    <T> T[] getServices(Class<T> serviceType);

    <T> T getService(String serviceId);

    <T> T[] getServices(String serviceId);
}
