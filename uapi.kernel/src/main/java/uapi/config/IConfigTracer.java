package uapi.config;

import java.util.Map;

public interface IConfigTracer {

    void onChanged(String namespace, Map<String, ?> config);
}
