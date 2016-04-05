package uapi.config;

/**
 * Created by xquan on 4/5/2016.
 */
public class ConfigValueParser {

    public Object parse(String type, Object value) {
        if (type.equals("int") || type.equals("java.lang.Integer")) {
           return Integer.parseInt(value.toString());
        } else {
            throw new uapi.InvalidArgumentException(
                    "The config object {} can't be converted to {}", value, type);
        }
    }
}
