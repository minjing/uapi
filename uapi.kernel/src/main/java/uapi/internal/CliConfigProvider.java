package uapi.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import uapi.config.Qualifier;
import uapi.helper.Pair;
import uapi.service.IService;

import com.google.common.base.Strings;

public final class CliConfigProvider
    extends TraceableConfigProvider
    implements IService {

    private static final String OPTION_PREFIX       = "-";
    private static final String OPTION_VALUE_TAG    = "=";

    public void parse(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        Map<String, String> cliCfg = new HashMap<>();
        Stream.of(args).parallel()
                .filter((option) -> { return Strings.isNullOrEmpty(option) ? false : true; })
                .map((option) -> {
                    if (option.startsWith(OPTION_PREFIX)) {
                        return option.substring(1);
                    } else {
                        return option;
                    }})
                .map((option) -> {
                    String[] values = option.split(OPTION_VALUE_TAG);
                    if (values.length == 1) {
                        return new Pair<String, String>(values[0], Boolean.TRUE.toString());
                    } else {
                        return new Pair<String, String>(values[0], values[1]);
                    }})
                .forEach((pair) -> { cliCfg.put(pair.getLeftValue(), pair.getRightValue()); });
        onChange(Qualifier.CLI, cliCfg);
    }
}