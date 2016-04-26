package uapi.service;

/**
 * A service loader used to load external service
 */
public interface IServiceLoader {

    String getName();

    <T> T load(final String serviceId);
}
