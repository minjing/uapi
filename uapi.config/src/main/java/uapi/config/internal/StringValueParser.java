package uapi.config.internal;

import uapi.config.IConfigValueParser;
import uapi.helper.CollectionHelper;

/**
 * The parser used to parse config value which can be convert to String
 */
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
    public String parse(Object value) {
        return value.toString();
    }
}
