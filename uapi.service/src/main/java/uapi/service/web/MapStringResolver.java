package uapi.service.web;

import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * A Map to String resolver
 */
@Deprecated
@Service(IStringResolver.class)
public class MapStringResolver implements IStringResolver<Map> {

    @Inject
    Map<String, IStringCodec> _formatters = new HashMap<>();

    @Override
    public String getId() {
        return Map.class.getCanonicalName();
    }

    @Override
    public String decode(Map value, String formatterName) {
        ArgumentChecker.required(value, "value");
        ArgumentChecker.required(formatterName, "formatterName");
        IStringCodec formatter = this._formatters.get(formatterName);
        return formatter.decode(value, Map.class);
    }

    @Override
    public Map encode(String value, String formatterName) {
        ArgumentChecker.required(value, "value");
        ArgumentChecker.required(formatterName, "formatterName");
        IStringCodec formatter = this._formatters.get(formatterName);
        return (Map) formatter.encode(value, Map.class);
    }


}
