package uapi.config.internal;

import com.google.common.base.Strings;
import rx.Observable;
import uapi.config.ICliConfigProvider;
import uapi.config.IConfigTracer;
import uapi.helper.ArgumentChecker;
import uapi.helper.Pair;
import uapi.helper.StringHelper;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The command line parser, it should support below pattern:
 * "-x" : x option with a boolean value which always set to true
 * "-x=???" : x option with value
 * "-xtrf" : x, t, r, f option which boolean value which always set to true
 */
@Service({ ICliConfigProvider.class })
public class CliConfigProvider implements ICliConfigProvider {

    public static final String DEFAULT_OPTION_PREFIX           = "-";
    public static final String DEFAULT_OPTION_VALUE_SEPARATOR  = "=";
    public static final String QUALIFY                         = "cli.";

    @Inject
    ILogger _logger;

    @Inject
    IConfigTracer _configTracer;

    private String _optionPrefix = DEFAULT_OPTION_PREFIX;
    private String _optionValueSeparator = DEFAULT_OPTION_VALUE_SEPARATOR;

    public void setOptionPrefix(String prefix) {
        ArgumentChecker.notEmpty(prefix, "prefix");
        this._optionPrefix = prefix;
    }

    public void setOptionValueSeparator(String separator) {
        this._optionValueSeparator = separator;
    }

    public void parse(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        Map<String, String> cliCfg = new HashMap<>();
        Observable.from(args)
                .filter(option -> ! Strings.isNullOrEmpty(option))
                .filter(option -> option.startsWith(this._optionPrefix))
                .map(option -> option.substring(this._optionPrefix.length()))
                .map(option -> Pair.splitTo(option, this._optionValueSeparator))
                .flatMap(pair -> {
                    if (Strings.isNullOrEmpty(pair.getRightValue())) {
                        List<Pair<String, String>> pairs = new ArrayList<>();
                        for (char c : pair.getLeftValue().toCharArray()) {
                            pairs.add(new Pair<>(String.valueOf(c), Boolean.TRUE.toString()));
                        }
                        return Observable.from(pairs);
                    } else {
                        return Observable.just(pair);
                    }
                })
                .subscribe(
                        pair -> this._configTracer.onChange(QUALIFY + pair.getLeftValue(), pair.getRightValue()),
                        throwable -> this._logger.error(throwable, "Unknown error"));
    }
}
