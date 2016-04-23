package uapi.config;

import java.util.Map;

/**
 * The IConfigTracer used to trace config changes event
 */
public interface IConfigTracer {

    /**
     * Invoked the config is changed on specified path
     *
     * @param   path
     *          The path which related with changed config
     * @param   config
     *          The changed config object
     */
    void onChange(String path, Object config);

    /**
     * Invoked the config is changed
     * The map's key is the path of the configuration
     *
     * @param   configMap
     *          The changed configuration map
     */
    void onChange(Map<String, Object> configMap);
}
