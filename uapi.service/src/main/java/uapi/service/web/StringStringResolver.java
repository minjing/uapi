package uapi.service.web;

/**
 * A String to String resolver
 */
public class StringStringResolver implements IStringResolver<String> {

    @Override
    public String getId() {
        return String.class.getCanonicalName();
    }

    @Override
    public String encode(String value) {
        return value;
    }

    @Override
    public String decode(String value) {
        return value;
    }
}
