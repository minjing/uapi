package uapi.internal;

import java.util.Map;

import uapi.config.Config;
import uapi.service.IService;

public class JsonConfigSource extends TraceableConfigSource implements IService {

    @Config(qualifier="")
    public void config(Map<String, String> config) {
        
    }
}
