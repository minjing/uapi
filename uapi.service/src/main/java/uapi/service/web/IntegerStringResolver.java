package uapi.service.web;

import uapi.service.annotation.Service;

/**
 * A resolver used to encode integer to String and decode string to integer
 */
@Service(IStringResolver.class)
public class IntegerStringResolver implements IStringResolver<Integer> {

    @Override
    public String getId() {
        return Integer.class.getCanonicalName();
    }

    @Override
    public String encode(Integer value, String formatterName) {
        return String.valueOf(value);
    }

    @Override
    public Integer decode(String value, String formatterName) {
        return Integer.parseInt(value);
    }
}
