package uapi.service.web;

import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * A Map to String resolver
 */
@Service(IStringResolver.class)
public class MapStringResolver implements IStringResolver<Map> {

    @Inject
    Map<String, IStringFormatter> _formatters = new HashMap<>();

    @Override
    public String getId() {
        return Map.class.getCanonicalName();
    }

    @Override
    public String encode(Map value, String formatterName) {
        ArgumentChecker.required(value, "value");
        ArgumentChecker.required(formatterName, "formatterName");
        IStringFormatter formatter = this._formatters.get(formatterName);
        return formatter.format(value, Map.class);
    }

    @Override
    public Map decode(String value, String formatterName) {
        ArgumentChecker.required(value, "value");
        ArgumentChecker.required(formatterName, "formatterName");
        IStringFormatter formatter = this._formatters.get(formatterName);
        return (Map) formatter.unformat(value, Map.class);
    }


}
