package uapi.config;

/**
 * The interface used for parse command line arguments and provide configurations
 */
public interface ICliConfigProvider {

    String DEFAULT_OPTION_PREFIX           = "-";
    String DEFAULT_OPTION_VALUE_SEPARATOR  = "=";

    /**
     * Set command line option prefix, for example the option "-x" is "-"
     * The default prefix is {@code DEFAULT_OPTION_PREFIX}
     *
     * @param   prefix
     *          The command line option prefix
     */
    void setOptionPrefix(String prefix);

    /**
     * Set command line option value separator, for example some command line option is
     * a key-value format like "-x=y", the separator is "="
     * The default prefix is {@code DEFAULT_OPTION_VALUE_SEPARATOR}
     *
     * @param   separator
     *          The command line option value separator
     */
    void setOptionValueSeparator(String separator);

    /**
     * Parse command line option array
     *
     * @param   args
     *          The command line option array which will be parsed
     */
    void parse(String[] args);
}
