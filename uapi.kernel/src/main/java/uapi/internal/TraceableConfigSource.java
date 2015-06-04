package uapi.internal;

import java.util.Map;

import uapi.config.IConfigSource;
import uapi.config.IConfigTracer;
import uapi.service.Inject;

public abstract class TraceableConfigSource implements IConfigSource {

    @Inject
    private IConfigTracer _tracer;

    @Override
    public void setTracer(IConfigTracer tracer) {
        this._tracer = tracer;
    }

    protected void onChanged(String namespace, Map<String, ?> config) {
        this._tracer.onChanged(namespace, config);
    }
}
