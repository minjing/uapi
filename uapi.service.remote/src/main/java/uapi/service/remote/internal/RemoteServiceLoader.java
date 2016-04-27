package uapi.service.remote.internal;

import uapi.service.IServiceLoader;

/**
 * ServiceLoader used to load service remotely
 * Discover driver: Direct, Consul
 * Invocation Method: HTTP, TCP, UDP
 * Message Format: JSON, XML, GPB
 */
public class RemoteServiceLoader implements IServiceLoader {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public <T> T load(String serviceId) {
        return null;
    }
}
