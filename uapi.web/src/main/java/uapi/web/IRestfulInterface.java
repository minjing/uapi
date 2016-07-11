package uapi.web;

import uapi.service.ServiceMeta;
import uapi.service.web.HttpMethod;

import java.util.List;
import java.util.Map;

/**
 * A Restful interface combine multiple Restful service into a interface to exposed
 */
public interface IRestfulInterface {

    /**
     * Retrieve interface id
     *
     * @return  The interface id
     */
    String getInterfaceId();

    /**
     * Retrieve service and http method mapping info
     *
     * @return  mapping info
     */
    Map<ServiceMeta, List<HttpMethod>> getMethodHttpMethodInfos();
}
