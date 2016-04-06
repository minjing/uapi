package uapi.config;

/**
 * A parser used to parse configured input to output
 *
 */
public interface IConfigValueParser {

    /**
     * Test the specified type is supported or not
     *
     * @param   inType
     *          The specified input type string
     * @param   outType
     *          The output type string
     * @return  true mean supported, false mean does not
     */
    boolean isSupport(String inType, String outType);

    /**
     * Parse specified configured value to specific value type
     *
     * @param   value
     *          Configured value
     * @param   <T>
     *          The parser output type
     * @return  Output value
     */
    <T> T parse(Object value);
}
