package uapi.log;

public interface ILogger {

    void trace(String message, Object... parameters);

    void debug(String message, Object... parameters);

    void info(String message, Object... parameters);

    void warn(String message, Object... parameters);

    void warn(Throwable t);

    void warn(Throwable t, String message, Object... parameters);

    void error(String message, Object... parameters);

    void error(Throwable t);

    void error(Throwable t, String message, Object... parameters);
}
