package uapi.config.internal;

import uapi.config.IConfigValueParser;
import uapi.helper.CollectionHelper;
import uapi.service.annotation.Service;

/**
 * The parser used to parse config value which can be convert to Double
 */
@Service(IConfigValueParser.class)
public class DoubleValueParser implements IConfigValueParser {

    private static final String[] supportTypesIn = new String[] {
            Double.class.getCanonicalName(), String.class.getCanonicalName() };
    private static final String[] supportTypesOut = new String[] {
            "double", Double.class.getCanonicalName()
    };

    @Override
    public boolean isSupport(String inType, String outType) {
        return CollectionHelper.isContains(supportTypesIn, inType) && CollectionHelper.isContains(supportTypesOut, outType);
    }

    @Override
    public String getName() {
        return DoubleValueParser.class.getCanonicalName();
    }

    @Override
    public Double parse(Object value) {
        if (value instanceof Integer) {
            return (Double) value;
        }
        return Double.parseDouble(value.toString());
    }
}
