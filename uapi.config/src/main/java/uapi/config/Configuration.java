package uapi.config;

import rx.Observable;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

import java.lang.ref.WeakReference;
import java.util.HashMap;
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
    private WeakReference<IConfigurable> _configuable;
    private final Map<String, Configuration> _children;

    public Configuration(final Configuration parent, final String key) {
        this(parent, key, null, null);
    }

    Configuration(
            final Configuration parent,
            final String key,
            final Object value,
            final IConfigurable configurable
    ) throws InvalidArgumentException {
        ArgumentChecker.notNull(parent, "parent");
        ArgumentChecker.notEmpty(key, "key");
        ArgumentChecker.notContains(key, ROOT_KEY, "key");
        this._parent = parent;
        this._key = key;
        this._value = value;
        if (configurable != null) {
            this._configuable = new WeakReference<>(configurable);
        }
        this._children = new HashMap<>();
    }

    /**
     * Only for creating root configuration
     */
    private Configuration() {
        this._parent = null;
        this._key = ROOT_KEY;
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
        if (this._configuable != null) {
            IConfigurable cfg = this._configuable.get();
            if (cfg != null) {
                cfg.config(getFullPath(), value);
            } else {
                this._configuable = null;
            }
        }
    }

    public void setValue(final Map<String, Object> configMap) {
        ArgumentChecker.notNull(configMap, "configMap");

        Observable.from(configMap.entrySet()).subscribe(entry -> {
            Configuration config = getOrCreateChild(entry.getKey());
            config.setValue(entry.getValue());
        });
    }

    public void setValue(final String path, final Object value) {
        ArgumentChecker.notEmpty(path, "path");
        ArgumentChecker.notNull(value, "value");

        Configuration config = getOrCreateChild(path);
        config.setValue(value);
    }

    public boolean bindConfigurable(final IConfigurable configurable) {
        this._configuable = new WeakReference<>(configurable);
        String path = getFullPath();
        if (this._value != null) {
            configurable.config(path, this._value);
            return true;
        } else if (this._children.size() > 0) {
            configurable.config(path, this._children);
            return true;
        } else {
            return configurable.isOptionalConfig(path);
        }
    }

    public boolean bindConfigurable(final String path, final IConfigurable configurable) {
        ArgumentChecker.notEmpty(path, "path");
        ArgumentChecker.notNull(configurable, "configurable");

        Configuration config = getOrCreateChild(path);
        return config.bindConfigurable(configurable);
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
}
