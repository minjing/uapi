package uapi.kernel.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import uapi.kernel.InvalidArgumentException;
import uapi.kernel.InvalidArgumentException.InvalidArgumentType;
import uapi.kernel.KernelException;

import com.google.common.base.Strings;

public final class CliOptionsParser {

    private CliOptionsParser() { }

    public static CliConfig parse(String[] args) {
        if (args == null || args.length == 0) {
            throw new InvalidArgumentException("name", InvalidArgumentType.EMPTY);
        }
        List<String> arggs = Arrays.asList(args);
        CliConfig cliCfg = new CliConfig();
        arggs.parallelStream()
                .filter((option) -> { return Strings.isNullOrEmpty(option) ? false : true; })
                .map((option) -> {
                    if (option.startsWith("-")) {
                        return option.substring(1);
                    } else {
                        return option;
                    }})
                .map((option) -> {
                    String[] values = option.split("=");
                    if (values.length == 1) {
                        return new Pair<String, String>(values[0], Boolean.TRUE.toString());
                    } else {
                        return new Pair<String, String>(values[0], values[1]);
                    }})
                .forEach((pair) -> { cliCfg.addConfig(pair); });
        return cliCfg;
    }

    private static final class CliConfig {

        private final Map<String, String> _options;

        CliConfig() {
            this._options = new HashMap<>();
        }

        private void addConfig(String name) {
            this.addConfig(name, Boolean.TRUE.toString());
        }

        private void addConfig(Pair<String, String> pair) {
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
}
