package uapi.config;

/**
 * A parser used to parse configured value to specified type
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
     * The parser name
     *
     * @return  Parser name
     */
    String getName();

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
