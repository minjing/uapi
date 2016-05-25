package uapi.service.web;

import uapi.IIdentifiable;

/**
 * The String resolve
 */
public interface IStringResolver<T> extends IIdentifiable<String> {

    /**
     * Encode value from type IT to OT
     *
     * @param   value
     *          The instance of IT
     * @return  The instance of OT
     */
    String encode(T value, String formatterName);

    /**
     * Decode value from type OT to IT
     *
     * @param   value
     *          The instance of OT
     * @return  The instance of IT
     */
    T decode(String value, String formatterName);
}
