package uapi.config.internal;

import com.google.common.base.Strings;
import rx.Observable;
import uapi.helper.ArgumentChecker;
import uapi.helper.Pair;
import uapi.service.IServiceReference;
import uapi.service.IWatcher;
import uapi.service.annotation.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CliConfigProvider implements IWatcher {

    private static final String OPTION_PREFIX       = "-";
    private static final String OPTION_VALUE_TAG    = "=";

    public Map<String, String> parse(String[] args) {
        if (args == null || args.length == 0) {
            return new HashMap<>();
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
                }).subscribe(pair -> cliCfg.put(pair.getLeftValue(), pair.getRightValue()));
        return cliCfg;

//        Stream.of(args).parallel()
//                .filter((option) -> { return Strings.isNullOrEmpty(option) ? false : true; })
//                .map((option) -> {
//                    if (option.startsWith(OPTION_PREFIX)) {
//                        return option.substring(1);
//                    } else {
//                        return option;
//                    }})
//                .map((option) -> {
//                    String[] values = option.split(OPTION_VALUE_TAG);
//                    if (values.length == 1) {
//                        return new Pair<String, String>(values[0], Boolean.TRUE.toString());
//                    } else {
//                        return new Pair<String, String>(values[0], values[1]);
//                    }})
//                .forEach((pair) -> { cliCfg.put(pair.getLeftValue(), pair.getRightValue()); });
//        cliCfg.forEach((name, value) -> {
//            onChange(name, value);
//        });
    }

    @Override
    public void onRegister(IServiceReference serviceRef) {
        // Do nothing
    }

    @Override
    public void onResolved(IServiceReference serviceRef) {
        ArgumentChecker.notNull(serviceRef, "serviceRef");

    }
}
