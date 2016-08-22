/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http.netty.internal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import uapi.config.annotation.Config;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.server.ServerException;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.web.http.IHttpConfigurableKey;
import uapi.web.http.IHttpHandler;
import uapi.web.http.IHttpServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The HTTP server implemented by Netty
 */
@Service(IHttpServer.class)
public class NettyHttpServer implements IHttpServer {

    @Config(path= IHttpConfigurableKey.SERVER_HTTP_HOST)
    String _host;

    @Config(path=IHttpConfigurableKey.SERVER_HTTP_PORT)
    int _port;

    @Inject
    ILogger _logger;

    @Inject
    Map<String, IHttpHandler> _handlers = new HashMap<>();

    private boolean _started = false;

    private EventLoopGroup _bossGroup;
    private EventLoopGroup _workerGroup;
    private ChannelFuture _channel;

    public void init() {
        // TODO: order handlers
    }

    boolean isStarted() {
        return this._started;
    }

    @Override
    public void start() throws ServerException {
        this._bossGroup = new NioEventLoopGroup(1);
        this._workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(this._bossGroup, this._workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());
            this._channel = bootstrap.bind(this._host, this._port).sync();
        } catch (InterruptedException ex) {
            stop();
            throw new ServerException(ex);
        }
        this._started = true;
        this._logger.info("Http server listener on {}:{}", this._host, this._port);
    }

    @Override
    public void stop() throws ServerException {
        if (this._bossGroup != null) {
            this._bossGroup.shutdownGracefully();
        }
        if (this._workerGroup != null) {
            this._workerGroup.shutdownGracefully();
        }

        try {
            if (this._channel != null) {
                this._channel.channel().closeFuture().sync();
            }
        } catch (InterruptedException ex) {
            // do nothing
        }
        this._started = false;
        this._logger.info("Http server is shutdown on {}:{}", this._host, this._port);
    }

    private class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

        /**
         * Invoked when channel is created
         *
         * @param channel
         * @throws Exception
         */
        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpRequestDispatcher(NettyHttpServer.this._logger, NettyHttpServer.this._handlers));
        }
    }
}
