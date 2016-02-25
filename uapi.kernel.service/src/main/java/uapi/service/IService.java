package uapi.service;

import uapi.annotation.Type;

/**
 * Created by min on 16/2/22.
 */
public interface IService {

    String METHOD_GETIDS                = "getIds";
    String METHOD_GETIDS_RETURN_TYPE    = Type.STRING_ARRAY;

    String[] getIds();
}
