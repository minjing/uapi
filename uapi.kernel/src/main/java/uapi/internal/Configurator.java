package uapi.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.config.Config;
import uapi.config.IConfigSource;
import uapi.service.IService;
import uapi.service.IServiceRepository;
import uapi.service.Inject;
import uapi.service.OnInit;
import uapi.service.Registration;
import uapi.service.Type;

import com.google.common.base.Strings;

@Registration({
        @Type(Configurator.class),
        @Type(IAnnotationParser.class)
})
public final class Configurator implements IService, IAnnotationParser<Config> {

    private final Map<String /* name space */, Map<String, ?>> _cgfs;
    private final Map<String /* name space */, List<ConfigurableServiceDescriptor>> _svcDescs;

//    @Inject
//    private IServiceRepository _serviceRepository;

    @Inject
    private final List<IConfigSource> _configSources;

    public Configurator() {
        this._cgfs          = new ConcurrentHashMap<>();
        this._svcDescs      = new ConcurrentHashMap<>();
        this._configSources = new ArrayList<>();
    }

    public void addConfigSource(IConfigSource configSource) {
        if (configSource == null) {
            throw new InvalidArgumentException("configSource", InvalidArgumentType.EMPTY);
        }
        this._configSources.add(configSource);
    }

//    public void setServiceRepository(IServiceRepository serviceRepository) {
//        this._serviceRepository = serviceRepository;
//    }

//    @OnInit
//    public void init() {
//        this._serviceRepository.addAnnotationParser(this);
//    }

    void addConfig(String namespace, Map<String, ?> config) {
        if (Strings.isNullOrEmpty(namespace)) {
            throw new InvalidArgumentException("namespace", InvalidArgumentType.EMPTY);
        }
        if (config == null) {
            throw new InvalidArgumentException("config", InvalidArgumentType.EMPTY);
        }
        Map<String, ?> oldCfg = this._cgfs.put(namespace, config);
        doConfigChange(namespace, oldCfg, config);
    }

    void doConfigChange(String ns, Map<String, ?> oldCfg, Map<String, ?> newCfg) {
        List<ConfigurableServiceDescriptor> svcDescs = this._svcDescs.get(ns);
        svcDescs.forEach((svcDesc) -> { svcDesc.setConfig(oldCfg, newCfg); });
    }

    @Override
    public void parse(Config cfgAnno, ConfigurableServiceDescriptor svcDesc) {
        List<ConfigurableServiceDescriptor> svcDescs = this._svcDescs.get(cfgAnno.namespace());
        if (svcDescs == null) {
            svcDescs = new ArrayList<>();
            this._svcDescs.put(cfgAnno.namespace(), svcDescs);
        }
        svcDescs.add(svcDesc);
        Map<String, ?> newCfg = this._cgfs.get(cfgAnno.namespace());
        if (newCfg != null) {
            this.doConfigChange(cfgAnno.namespace(), null, newCfg);
        }
    }
}
