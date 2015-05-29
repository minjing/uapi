package uapi.config.internal;

import java.util.Arrays;
import java.util.List;

import uapi.kernel.InvalidArgumentException;
import uapi.kernel.InvalidArgumentException.InvalidArgumentType;
import uapi.kernel.helper.Pair;

import com.google.common.base.Strings;

public final class CliOptionsParser {

    private CliOptionsParser() { }

    public static CliOptions parse(String[] args) {
        if (args == null || args.length == 0) {
            throw new InvalidArgumentException("name", InvalidArgumentType.EMPTY);
        }
        List<String> arggs = Arrays.asList(args);
        CliOptions cliCfg = new CliOptions();
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
                .forEach((pair) -> { cliCfg.addOption(pair); });
        return cliCfg;
    }
}
