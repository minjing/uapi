/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.spring.internal;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uapi.config.annotation.Config;
import uapi.helper.ArgumentChecker;
import uapi.service.IRegistry;
import uapi.service.IServiceLoader;
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * The service used to load Spring bean into the framework
 */
@Service
public class SpringServiceLoader implements IServiceLoader {

    public static final String NAME = "Spring";

    private Map<String, Object> _beanCache;

    @Inject
    IRegistry _registry;

    @Config(path="spring.config")
    String _cfgFile;

    private ApplicationContext _ctx;

    @Init
    public void init() {
        this._beanCache = new HashMap<>();
        this._ctx = new ClassPathXmlApplicationContext(new String[] { this._cfgFile });
        this._registry.registerServiceLoader(this);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T load(
            final String serviceId) {
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        T bean = (T) this._beanCache.get(serviceId);
        if (bean != null) {
            return bean;
        }
        bean = (T) this._ctx.getBean(serviceId);
        this._beanCache.put(serviceId, bean);
        return bean;
    }
}
