package uapi.config;

import java.util.Map;

public interface IConfigTracer {

    void onChange(String path, Object config);

    void onChange(Map<String, Object> configMap);
}
