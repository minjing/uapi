package uapi.config.internal;

import java.util.HashMap;
import java.util.Map;

import uapi.kernel.InvalidArgumentException;
import uapi.kernel.KernelException;
import uapi.kernel.InvalidArgumentException.InvalidArgumentType;
import uapi.kernel.helper.Pair;

import com.google.common.base.Strings;

public final class CliOptions {

    private final Map<String, String> _options;

    CliOptions() {
        this._options = new HashMap<>();
    }

    public void addOption(String name) {
        this.addConfig(name, Boolean.TRUE.toString());
    }

    public void addOption(Pair<String, String> pair) {
        addConfig(pair.getLeftValue(), pair.getRightValue());
    }

    private void addConfig(String name, String value) {
        if (Strings.isNullOrEmpty(name)) {
            throw new InvalidArgumentException("name", InvalidArgumentType.EMPTY);
        }
        if (this._options.containsKey(name)) {
            throw new KernelException("The config {} has registered", name);
        }
        this._options.put(name, value);
    }
}
