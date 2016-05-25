package uapi.service.web;

import uapi.service.annotation.Service;

/**
 * A String to String resolver
 */
@Service(IStringResolver.class)
public class StringStringResolver implements IStringResolver<String> {

    @Override
    public String getId() {
        return String.class.getCanonicalName();
    }

    @Override
    public String encode(String value, String formatterName) {
        return value;
    }

    @Override
    public String decode(String value, String formatterName) {
        return value;
    }
}
