package uapi.web;

import uapi.IIdentifiable;

import java.util.List;

/**
 * A interface for restful service
 */
public interface IRestfulService extends IIdentifiable<String> {



    /**
     * Get restful service method argument information which is related with specific http method.
     * One restful service method can be mapped to one or more http method that's said one http request
     * only can map to one restful service method.
     *
     * @param   method
     *          The http method
     * @return  The argument meta array
     */
    ArgumentMapping[] getMethodArgumentsInfo(HttpMethod method);

    /**
     * Invoke the web service by specific http method and parsed arguments
     *
     * @param   method
     *          The http method
     * @param   args
     *          The parsed arguments which is extracted from http header/query param/uri
     * @return  The web service execution result
     */
    Object invoke(HttpMethod method, List<Object> args);
}
