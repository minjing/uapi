/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config;

import rx.Observable;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;
import uapi.rx.Looper;
import uapi.service.IServiceReference;
import uapi.service.internal.QualifiedServiceId;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The Configuration hold at least one config value and store as a tree structure
 */
public class Configuration {

    public static final String ROOT_KEY                 = "/";
    private static final String PATH_SEPARATOR_PATTERN  = "\\.";
    private static final String PATH_SEPARATOR          = ".";

    public static Configuration createRoot() {
        return new Configuration();
    }

    private final Configuration _parent;
    private final String _key;
    private Object _value;
    private final Map<QualifiedServiceId, WeakReference<IServiceReference>> _configuableSvcs;
    private final Map<String, Configuration> _children;

    public Configuration(final Configuration parent, final String key) {
        this(parent, key, null, null);
    }

    Configuration(
            final Configuration parent,
            final String key,
            final Object value,
            final IServiceReference serviceReference
    ) throws InvalidArgumentException {
        ArgumentChecker.notNull(parent, "parent");
        ArgumentChecker.notEmpty(key, "key");
        ArgumentChecker.notContains(key, ROOT_KEY, "key");
        this._parent = parent;
        this._key = key;
        this._value = value;
        this._configuableSvcs = new HashMap<>();
        if (serviceReference != null) {
            this._configuableSvcs.put(serviceReference.getQualifiedId(), new WeakReference<>(serviceReference));
        }
        this._children = new HashMap<>();
    }

    /**
     * Only for creating root configuration
     */
    private Configuration() {
        this._parent = null;
        this._key = ROOT_KEY;
        // For root node, not configurable service can be bind on it.
        this._configuableSvcs = null;
        this._children = new HashMap<>();
    }

    public String getKey() {
        return this._key;
    }

    public boolean isRoot() {
        return this._parent == null && this._key.equals(ROOT_KEY);
    }

    public Object getValue() {
        if (this._value != null) {
            return this._value;
        }
        if (this._children.size() > 0) {
            return this._children;
        }
        return null;
    }

    public Class<?> getValueType() {
        if (this._value != null) {
            return this._value.getClass();
        }
        return null;
    }

    public Object getValue(final String path) {
        ArgumentChecker.notEmpty(path, "path");
        String[] steps = path.split(PATH_SEPARATOR_PATTERN);
        Configuration config = this;
        for (String step : steps) {
            if (config == null) {
                return null;
            }
            config = config.getChild(step);
        }
        return config.getValue();
    }

    @SuppressWarnings("unchecked")
    public void setValue(final Object value) {
        if (value instanceof Map) {
            setValue((Map<String, Object>) value);
        }
        this._value = value;

        Looper.from(this._configuableSvcs.values())
                .filter(ref -> ref.get() != null)
                .map(WeakReference::get)
                .next(svcRef -> ((IConfigurable) svcRef.getService()).config(getFullPath(), value))
                .foreach(IServiceReference::notifySatisfied);
        cleanNullReference();
    }

    public void setValue(final Map<String, Object> configMap) {
        ArgumentChecker.notNull(configMap, "configMap");

        Looper.from(configMap.entrySet()).foreach(entry -> {
            Configuration config = getOrCreateChild(entry.getKey());
            config.setValue(entry.getValue());
        });
    }

    public void setValue(final List<Object> configList) {
        ArgumentChecker.notNull(configList, "configList");
        // TODO:
    }

    public void setValue(final String path, final Object value) {
        ArgumentChecker.notEmpty(path, "path");
        ArgumentChecker.notNull(value, "value");

        Configuration config = getOrCreateChild(path);
        config.setValue(value);
    }

    public boolean bindConfigurable(final IServiceReference serviceRef) {
        ArgumentChecker.notNull(serviceRef, "serviceRef");

        String path = getFullPath();
        if (this._configuableSvcs.containsKey(serviceRef.getQualifiedId())) {
            if (this._value != null) {
                return true;
            }
            return ((IConfigurable) serviceRef.getService()).isOptionalConfig(path);
        }
        IConfigurable cfg = ((IConfigurable) serviceRef.getService());
        this._configuableSvcs.put(serviceRef.getQualifiedId(), new WeakReference<>(serviceRef));
        if (this._value != null) {
            cfg.config(path, this._value);
            return true;
        } else if (this._children.size() > 0) {
            cfg.config(path, this._children);
            return true;
        } else {
            return cfg.isOptionalConfig(path);
        }
    }

    public boolean bindConfigurable(final String path, final IServiceReference serviceRef) {
        ArgumentChecker.notEmpty(path, "path");
        ArgumentChecker.notNull(serviceRef, "serviceRef");

        Configuration config = getOrCreateChild(path);
        return config.bindConfigurable(serviceRef);
    }

    public Configuration getChild(String key) {
        ArgumentChecker.notEmpty(key, "key");
        if (this._children == null) {
            throw new KernelException("The configuration[{}] can't has child", this._key);
        }
        return this._children.get(key);
    }

    public Configuration setChild(String key, Object value) {
        ArgumentChecker.notEmpty(key, "key");
        ArgumentChecker.notNull(value, "value");
        if (this._children == null) {
            throw new KernelException("The configuration[{}] can't attach child", this._key);
        }
        Configuration child = new Configuration(this, key, value, null);
        this._children.put(key, child);
        return child;
    }

    public Configuration setChild(String key) {
        ArgumentChecker.notEmpty(key, "key");
        if (this._children == null) {
            throw new KernelException("The configuration[{}] can't attach child", this._key);
        }
        Configuration child = new Configuration(this, key);
        this._children.put(key, child);
        return child;
    }

    public String getFullPath() {
        StringBuilder buffer = new StringBuilder();
        boolean isRoot = isRoot();
        Configuration cfg = this;
        while (! isRoot) {
            buffer.insert(0, cfg._key).insert(0, PATH_SEPARATOR);
            cfg = cfg._parent;
            isRoot = cfg.isRoot();
        }
        if (buffer.length() > 0) {
            return buffer.deleteCharAt(0).toString();
        } else {
            return StringHelper.EMPTY;
        }
    }

    private Configuration getOrCreateChild(final String path) {
        ArgumentChecker.notEmpty(path, "path");
        String[] steps = path.split(PATH_SEPARATOR_PATTERN);
        Configuration config = this;
        for (String step : steps) {
            Configuration child = config.getChild(step);
            if (child == null) {
                config = config.setChild(step);
            } else {
                config = child;
            }
        }
        return config;
    }

    private void cleanNullReference() {
        Iterator<Map.Entry<QualifiedServiceId, WeakReference<IServiceReference>>> it = this._configuableSvcs.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().get() == null) {
                it.remove();
            }
        }
    }
}
