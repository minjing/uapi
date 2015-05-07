package uapi.kernel.internal;

import uapi.kernel.IService;

final class ServiceResolver {

    private static final String FIELD_PREFIX    = "_";
    private static final String SETTER_PREFIX   = "set";

    ResolvedService resolve(Class<? extends IService> type) {
        if (type == null) {
            throw new IllegalArgumentException("The argument is required - type");
        }
        ResolvedService resolvedSvr = new ResolvedService(type);
        
        return null;
    }
}
