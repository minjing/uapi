package uapi.app.internal;

import com.google.common.base.Strings;
import rx.Observable;
import uapi.config.IConfigTracer;
import uapi.helper.Pair;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CliConfigProvider {

    private static final String OPTION_PREFIX       = "-";
    private static final String OPTION_VALUE_TAG    = "=";

    @Inject
    IConfigTracer _configTracer;

    public void parse(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        Map<String, String> cliCfg = new HashMap<>();
        Observable.from(args)
                .filter(option -> ! Strings.isNullOrEmpty(option))
                .map(option -> {
                    if (option.startsWith(OPTION_PREFIX)) {
                        return option.substring(1);
                    } else {
                        return option;
                    }
                }).map(option -> {
                    String[] values = option.split(OPTION_VALUE_TAG);
                    if (values.length == 1) {
                        return new Pair<>(values[0], Boolean.TRUE.toString());
                    } else {
                        return new Pair<>(values[0], values[1]);
                    }
                }).subscribe(pair -> this._configTracer.onChange(pair.getLeftValue(), pair.getRightValue()));
//                }).subscribe(pair -> cliCfg.put(pair.getLeftValue(), pair.getRightValue()));
    }
}
