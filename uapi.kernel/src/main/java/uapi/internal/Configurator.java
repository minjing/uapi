package uapi.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.config.Config;
import uapi.config.IConfigTracer;
import uapi.service.AnnotatedMethod;
import uapi.service.IAnnotationMethodHandler;
import uapi.service.IService1;
import uapi.service.Registration;
import uapi.service.Type;

import com.google.common.base.Strings;

@Registration({
        @Type(Configurator.class),
        @Type(IAnnotationMethodHandler.class),
        @Type(IConfigTracer.class)
})
public final class Configurator
    implements IService1, IAnnotationMethodHandler<Config>, IConfigTracer {

    private final Map<String /* qualifier */, Object> _cgfs;
    private final Map<String /* qualifier */, List<ConfigurableServiceMethod>> _svcDescs;

    public Configurator() {
        this._cgfs      = new ConcurrentHashMap<>();
        this._svcDescs  = new ConcurrentHashMap<>();
    }

    void addConfig(String qualifier, Object config) {
        if (Strings.isNullOrEmpty(qualifier)) {
            throw new InvalidArgumentException("qualifier", InvalidArgumentType.EMPTY);
        }
        if (config == null) {
            throw new InvalidArgumentException("config", InvalidArgumentType.EMPTY);
        }
        Object oldCfg = this._cgfs.put(qualifier, config);
        doConfigChange(qualifier, oldCfg, config);
    }

    void doConfigChange(String qualifier, Object oldCfg, Object newCfg) {
        List<ConfigurableServiceMethod> svcDescs = this._svcDescs.get(qualifier);
        if (svcDescs != null) {
            svcDescs.forEach((svcDesc) -> { svcDesc.updateConfig(oldCfg, newCfg); });
        }
    }

    @Override
    public void parse(AnnotatedMethod serviceMethod) {
        Config cfgAnno = serviceMethod.getAnnotation();
        List<ConfigurableServiceMethod> svcDescs = this._svcDescs.get(cfgAnno.value());
        if (svcDescs == null) {
            svcDescs = new ArrayList<>();
            this._svcDescs.put(cfgAnno.value(), svcDescs);
        }
        ConfigurableServiceMethod cfgSvcMethod = new ConfigurableServiceMethod(serviceMethod);
        svcDescs.add(cfgSvcMethod);
        Object newCfg = this._cgfs.get(cfgAnno.value());
        if (newCfg != null) {
            cfgSvcMethod.updateConfig(null, newCfg);
        }
    }

    @Override
    public void onChange(String qulifier, Object config) {
        addConfig(qulifier, config);
    }
}
