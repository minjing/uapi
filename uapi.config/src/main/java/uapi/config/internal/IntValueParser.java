package uapi.config.internal;

import uapi.config.IConfigValueParser;
import uapi.helper.CollectionHelper;
import uapi.service.annotation.Service;

/**
 * The parser used to parse config value which can be convert to Integer
 */
@Service({ IConfigValueParser.class })
public class IntValueParser implements IConfigValueParser {

    private static final String[] supportTypesIn  = new String[] {
            String.class.getCanonicalName(), Integer.class.getCanonicalName() };
    private static final String[] supportTypesOut = new String[] {
            "int", Integer.class.getCanonicalName() };

    @Override
    public boolean isSupport(String inType, String outType) {
        return CollectionHelper.isContains(supportTypesIn, inType) && CollectionHelper.isContains(supportTypesOut, outType);
    }

    @Override
    public String getName() {
        return IntValueParser.class.getCanonicalName();
    }

    @Override
    public Integer parse(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return Integer.parseInt(value.toString());
    }
}
