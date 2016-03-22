package uapi.config;

/**
 * Created by min on 16/3/6.
 */
public interface IConfigurable {

    String[] getConfigKeys();

    void config(Object configObject);
}
