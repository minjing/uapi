package uapi.service;

import uapi.annotation.Type;

/**
 * Implement this interface will indicate the object is managed by framework
 */
public interface IService {

    String METHOD_GETIDS                        = "getIds";
    String METHOD_GET_DEPENDENT_ID              = "getDependentIds";
    String METHOD_GETIDS_RETURN_TYPE            = Type.STRING_ARRAY;
    String METHOD_GET_DEPENDENT_ID_RETURN_TYPE  = Type.STRING_ARRAY;

    /**
     * Return the service identifications
     *
     * @return  The service identifications
     */
    String[] getIds();
}
