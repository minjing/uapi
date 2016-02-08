package uapi.injector.helper;

/**
 * Created by min on 16/2/8.
 */
public final class ServiceHelper {

    private ServiceHelper() { }

    public static String generateServiceName(String serviceName) {
        return serviceName + "_Generated";
    }
}
