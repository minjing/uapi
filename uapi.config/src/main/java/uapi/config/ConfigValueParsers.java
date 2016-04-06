package uapi.config;

import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * The service hold one or more than one parsers
 */
@Service
public class ConfigValueParsers {

    @Inject
    List<IConfigValueParser> _parsers = new ArrayList<>();

    public IConfigValueParser findParser(String inType, String outType) {
        ArgumentChecker.notEmpty(inType, "inType");
        ArgumentChecker.notEmpty(outType, "outType");
        List<IConfigValueParser> matcheds = Observable.from(this._parsers)
                .filter(parser -> parser.isSupport(inType, outType))
                .toList().toBlocking().single();
        if (matcheds == null || matcheds.size() == 0) {
            throw new KernelException("No parser for in type {} and out type {}", inType, outType);
        }
        if (matcheds.size() > 1) {
            throw new KernelException("Found more than one parser for in type {} and out type: {}",
                    inType, outType, matcheds);
        }
        return matcheds.get(0);
    }
}
