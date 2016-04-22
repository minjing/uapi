package uapi.service;

/**
 * A service loader take a role to load external service
 */
public interface IServiceLoader {

    String getName();

    void prepareLoad(final String serviceId);
}
