package uapi.log;

public interface ILogger {

    void log(int level, String message, String... paraemters);

    void log(int level, Throwable t, String message, String... parameters);

    void debug(String message, String... parameters);

    void info(String message, String... parameters);

    void warn(String message, String... parameters);

    void warn(Throwable t, String message, String parameters);

    void error(String message, String... parameters);

    void error(Throwable t, String message, String parameters);
}
