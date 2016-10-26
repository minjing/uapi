package uapi.helper;

/**
 * The interface indicate the class has one or more attributes and all
 * attribute is associated with a key.
 * Using key can receive the attribute.
 */
public interface IAttributed {

    /**
     * Get attribute by specific key
     *
     * @param   key
     *          The key which associated a attribute
     * @param   <T>
     *          The attribute type
     * @return  The attribute or null if no attribute is associated with the key
     */
    <T> T get(Object key);
}
