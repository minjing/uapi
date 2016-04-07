package uapi.config.internal;

import uapi.config.IConfigValueParser;
import uapi.helper.CollectionHelper;
import uapi.service.annotation.Service;

/**
 * The parser used to parse config value which can be convert to String
 */
@Service({ IConfigValueParser.class })
public class StringValueParser implements IConfigValueParser {

    private static final String[] supportedInTypes  = new String[] {
            String.class.getCanonicalName() };
    private static final String[] supportedOutTypes = new String[] {
            String.class.getCanonicalName() };

    @Override
    public boolean isSupport(String inType, String outType) {
        return CollectionHelper.isContains(supportedInTypes, inType) && CollectionHelper.isContains(supportedOutTypes, outType);
    }

    @Override
    public String getName() {
        return StringValueParser.class.getCanonicalName();
    }

    @Override
    public String parse(Object value) {
        return value.toString();
    }
}
