package uapi.config.internal;

import uapi.config.IConfigValueParser;
import uapi.helper.CollectionHelper;
import uapi.service.annotation.Service;

/**
 * The parser used to parse config value which can be convert to Integer
 */
@Service({ IConfigValueParser.class })
public class IntValueParser implements IConfigValueParser {

    private static final String[] supportedInTypes  = new String[] {
            String.class.getCanonicalName(), Integer.class.getCanonicalName() };
    private static final String[] supportedOutTypes = new String[] {
            "int", Integer.class.getCanonicalName() };

    @Override
    public boolean isSupport(String inType, String outType) {
        return CollectionHelper.isContains(supportedInTypes, inType) && CollectionHelper.isContains(supportedOutTypes, outType);
    }

    @Override
    public Integer parse(Object value) {
        return Integer.parseInt(value.toString());
    }
}
