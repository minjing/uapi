package uapi.config;

/**
 * The interface used for parse command line arguments and provide configurations
 */
public interface ICliConfigProvider {

    void setOptionPrefix(String prefix);

    void setOptionValueSeparator(String separator);

    void parse(String[] args);
}
