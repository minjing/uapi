package uapi.config;

/**
 * Created by min on 16/3/6.
 */
public interface IConfigurable {

    String METHOD_GET_PATHS             = "getPaths";
    String METHOD_IS_OPTIONAL_CONFIG    = "isOptionalConfig";
    String METHOD_CONFIG                = "config";
    String PARAM_PATH                   = "path";
    String PARAM_CONFIG_OBJECT          = "configObject";

    String[] getPaths();

    boolean isOptionalConfig(String path);

    void config(String path, Object configObject);
}
