package uapi.web;

import uapi.IIdentifiable;
import uapi.service.web.ArgumentMapping;
import uapi.web.internal.MethodInfo;

import java.util.Map;

/**
 * A Restful interface combine multiple Restful service into a interface to exposed
 */
public interface IRestfulInterface extends IIdentifiable<String> {

    Map<MethodInfo, ArgumentMapping[]> getMethodArgumentsInfos();
}
