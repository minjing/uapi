package uapi.service;

/**
 * A helper for maintain service annotation at build-time
 */
public interface IServiceBuilderHelper {

    String key = "ServiceHelper";

    void addServiceId(String serviceId);
}
