package uapi.config;

public interface IConfigSource {

    <T> T getConfig(String key);
}
