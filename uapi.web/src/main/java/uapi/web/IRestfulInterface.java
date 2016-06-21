package uapi.web;

import uapi.IIdentifiable;
import uapi.service.MethodMeta;
import uapi.service.web.ArgumentMapping;
import uapi.service.web.HttpMethod;

import java.util.Map;

/**
 * A Restful interface combine multiple Restful service into a interface to exposed
 */
public interface IRestfulInterface extends IIdentifiable<String> {

    Map<MethodMeta, ArgumentMapping[]> getMethodArgumentsInfos();

    Map<MethodMeta, HttpMethod[]> getMethodHttpMethodInfos();
}
