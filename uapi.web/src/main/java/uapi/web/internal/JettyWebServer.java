package uapi.web.internal;

import org.eclipse.jetty.server.Server;
import uapi.config.annotation.Config;
import uapi.server.ServerException;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.web.IWebServer;

/**
 * A web server implemented by Jetty
 */
@Service
public class JettyWebServer implements IWebServer {

    @Config(path="server.web.host")
    String _host;

    @Config(path="server.web.port")
    int _port;

    @Inject
    IRegistry _registry;

    private Server _server;

    @Override
    public void start() throws ServerException {
        this._server = new Server();
        try {
            this._server.start();
        } catch (Exception ex) {
            throw new ServerException(ex);
        }
    }

    @Override
    public void stop() throws ServerException {
        if (this._server == null) {
            throw new ServerException("The jetty web server does not initialized.");
        }

        try {
            this._server.stop();
        } catch (Exception ex) {
            throw new ServerException(ex);
        }
    }
}
