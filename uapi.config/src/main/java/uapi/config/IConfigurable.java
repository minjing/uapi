package uapi.config;

/**
 * Created by min on 16/3/6.
 */
public interface IConfigurable {

    String[] getPaths();

    boolean configSatisfied();

    void config(String path, Object configObject);
}
