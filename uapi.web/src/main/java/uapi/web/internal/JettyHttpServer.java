package uapi.web.internal;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import rx.Observable;
import uapi.config.annotation.Config;
import uapi.log.ILogger;
import uapi.server.ServerException;
import uapi.service.IRegistry;
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Optional;
import uapi.service.annotation.Service;
import uapi.web.IHttpServer;
import uapi.web.IWebConfigurableKey;
import uapi.web.MappableHttpServlet;

import javax.servlet.http.HttpServlet;
import java.util.LinkedList;
import java.util.List;

/**
 * A web server implemented by Jetty
 */
@Service(IHttpServer.class)
public class JettyHttpServer implements IHttpServer {

    @Config(path=IWebConfigurableKey.SERVER_HTTP_HOST)
    String _host;

    @Config(path=IWebConfigurableKey.SERVER_HTTP_PORT)
    int _port;

    @Config(path=IWebConfigurableKey.SERVER_HTTP_IDLE_TIMEOUT, optional=true)
    long _idleTimeout = 3000L;

    @Inject
    ILogger _logger;

    @Inject
    @Optional
    List<MappableHttpServlet> _servlets = new LinkedList<>();

    private Server _server;
    private ServletHandler _servletHandler;

    @Init
    public void init() {
        this._server = new Server();

        ServerConnector serverConnector = new ServerConnector(this._server);
        serverConnector.setHost(this._host);
        serverConnector.setPort(this._port);
        serverConnector.setIdleTimeout(this._idleTimeout);
        this._server.addConnector(serverConnector);

        this._servletHandler = new ServletHandler();
        this._server.setHandler(this._servletHandler);

        Observable.from(this._servlets)
                .subscribe(servlet -> {
                    ServletHolder holder = new ServletHolder(servlet);
                    this._servletHandler.addServletWithMapping(holder, servlet.getPathPattern());
                });
    }

    @Override
    public void start() throws ServerException {
        try {
            this._server.start();
            this._server.join();
        } catch (Exception ex) {
            throw new ServerException(ex);
        }
        this._logger.info("Http server is started at host {} on port {}", this._host, this._port);
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
        this._logger.info("Http server is stopped at host {} on port {}", this._host, this._port);
    }
}
