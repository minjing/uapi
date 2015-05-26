package uapi.config;

public interface IConfiguration {

    <T> T getConfig(String key);
}
