package uapi.config;

public interface IConfigTracer {

    void onChange(String path, Object config);
}
