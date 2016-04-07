package uapi.config.internal;

import uapi.config.IConfigValueParser;
import uapi.helper.CollectionHelper;
import uapi.service.annotation.Service;

/**
 * The parser used to parse config value which can be convert to Double
 */
@Service(IConfigValueParser.class)
public class LongValueParser implements IConfigValueParser {

    private static final String[] supportTypesIn = new String[] {
            Long.class.getCanonicalName(), String.class.getCanonicalName() };
    private static final String[] supportTypesOut = new String[] {
            "long", Long.class.getCanonicalName()
    };

    @Override
    public boolean isSupport(String inType, String outType) {
        return CollectionHelper.isContains(supportTypesIn, inType) && CollectionHelper.isContains(supportTypesOut, outType);
    }

    @Override
    public String getName() {
        return LongValueParser.class.getCanonicalName();
    }

    @Override
    public Long parse(Object value) {
        if (value instanceof Long) {
            return (Long) value;
        }
        return Long.parseLong(value.toString());
    }
}
