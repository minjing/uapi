package uapi.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.config.Config;

import com.google.common.base.Strings;

public final class Configurator {

    private final Map<String /* name space */, Map<String, ?>> _cgfs;
    private final Map<String /* name space */, List<ConfigurableServiceDescriptor>> _svcDescs;

    Configurator() {
        this._cgfs = new ConcurrentHashMap<>();
        this._svcDescs = new ConcurrentHashMap<>();
    }

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

    private final class AnnotationParser implements IAnnotationParser<Config> {

        @Override
        public void parse(Config annotation, ServiceReference serviceReference) {
            // TODO Auto-generated method stub
        }
    }
}
