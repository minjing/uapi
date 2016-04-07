package uapi.config.internal;

import uapi.config.IConfigValueParser;
import uapi.helper.CollectionHelper;
import uapi.service.annotation.Service;

/**
 * The parser used to parse config value which can be convert to Float
 */
@Service(IConfigValueParser.class)
public class FloatValueParser implements IConfigValueParser {

    private static final String[] supportTypesIn = new String[] {
            Float.class.getCanonicalName(), String.class.getCanonicalName() };
    private static final String[] supportTypesOut = new String[] {
            "float", Float.class.getCanonicalName()
    };

    @Override
    public boolean isSupport(String inType, String outType) {
        return CollectionHelper.isContains(supportTypesIn, inType) && CollectionHelper.isContains(supportTypesOut, outType);
    }

    @Override
    public String getName() {
        return FloatValueParser.class.getCanonicalName();
    }

    @Override
    public Float parse(Object value) {
        if (value instanceof Float) {
            return (Float) value;
        }
        return Float.parseFloat(value.toString());
    }
}
