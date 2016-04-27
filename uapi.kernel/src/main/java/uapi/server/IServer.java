package uapi.server;

/**
 * A server interface
 */
public interface IServer {

    /**
     * Start the server
     *
     * @throws  ServerException
     *          Encounter an error when start server
     */
    void start() throws ServerException;

    /**
     * Stop the server
     *
     * @throws  ServerException
     *          Encounter an error when
     */
    void stop() throws ServerException;
}
