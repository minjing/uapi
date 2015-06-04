package uapi.internal;

import java.util.Map;

import uapi.config.Config;
import uapi.config.Namespace;
import uapi.service.IService;

public final class PropertiesFileConfigSource extends TraceableConfigSource implements IService {

    @Config(namespace=Namespace.CLI)
    public void setTracer(Map<String, String> config) {
        // TODO Auto-generated method stub
    }
}
