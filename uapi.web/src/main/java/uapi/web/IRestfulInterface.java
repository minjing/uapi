package uapi.web;

import uapi.service.MethodMeta;
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

    Map<MethodMeta, List<HttpMethod>> getMethodHttpMethodInfos();
}
