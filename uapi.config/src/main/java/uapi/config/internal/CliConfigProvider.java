package uapi.config.internal;

import com.google.common.base.Strings;
import rx.Observable;
import uapi.config.ICliConfigProvider;
import uapi.config.IConfigTracer;
import uapi.helper.ArgumentChecker;
import uapi.helper.Pair;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import java.util.HashMap;
import java.util.Map;

@Service({ ICliConfigProvider.class })
public class CliConfigProvider implements ICliConfigProvider {

    private static final String DEFAULT_OPTION_PREFIX           = "-";
    private static final String DEFAULT_OPTION_VALUE_SEPARATOR  = "=";

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
                .map(option -> {
                    if (option.startsWith(this._optionPrefix)) {
                        return option.substring(1);
                    } else {
                        return option;
                    }
                }).map(option -> {
            String[] values = option.split(this._optionValueSeparator);
            if (values.length == 1) {
                return new Pair<>(values[0], Boolean.TRUE.toString());
            } else {
                return new Pair<>(values[0], values[1]);
            }
        }).subscribe(pair -> this._configTracer.onChange(pair.getLeftValue(), pair.getRightValue()), throwable -> this._logger.error(throwable, "Unknown error"));
    }
}
