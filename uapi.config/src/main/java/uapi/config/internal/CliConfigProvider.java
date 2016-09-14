/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config.internal;

import com.google.common.base.Strings;
import rx.Observable;
import uapi.config.ICliConfigProvider;
import uapi.config.IConfigTracer;
import uapi.helper.ArgumentChecker;
import uapi.helper.Pair;
import uapi.helper.StringHelper;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

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
@Tag("Config")
public class CliConfigProvider implements ICliConfigProvider {

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
        Looper.from(args)
                .filter(option -> ! Strings.isNullOrEmpty(option))
                .filter(option -> option.startsWith(this._optionPrefix))
                .map(option -> option.substring(this._optionPrefix.length()))
                .map(option -> Pair.splitTo(option, this._optionValueSeparator))
                .flatmap(pair -> {
                    if (Strings.isNullOrEmpty(pair.getRightValue())) {
                        List<Pair<String, String>> pairs = new ArrayList<>();
                        for (char c : pair.getLeftValue().toCharArray()) {
                            pairs.add(new Pair<>(String.valueOf(c), Boolean.TRUE.toString()));
                        }
                        return Looper.from(pairs);
                    } else {
                        return Looper.from(pair);
                    }
                })
                .foreach(
                        pair -> this._configTracer.onChange(QUALIFY + pair.getLeftValue(), pair.getRightValue()));
    }
}
