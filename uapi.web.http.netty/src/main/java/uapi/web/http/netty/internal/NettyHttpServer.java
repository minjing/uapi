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
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import uapi.config.annotation.Config;
import uapi.log.ILogger;
import uapi.server.ServerException;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.web.http.IHttpConfigurableKey;
import uapi.web.http.IHttpServer;

/**
 * Created by xquan on 8/5/2016.
 */
@Service(IHttpServer.class)
public class NettyHttpServer implements IHttpServer {

    @Config(path= IHttpConfigurableKey.SERVER_HTTP_HOST)
    String _host;

    @Config(path=IHttpConfigurableKey.SERVER_HTTP_PORT)
    int _port;

    @Inject
    ILogger _logger;

    @Override
    public void start() throws ServerException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());
            Channel channel = bootstrap.bind(this._host, this._port).sync().channel();
            this._logger.info("Http server listener on {}:{}", this._host, this._port);
            channel.closeFuture().sync();
        } catch (InterruptedException ex) {
            throw new ServerException(ex);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() throws ServerException {

    }

    private class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(new HttpRequestDecoder());
            pipeline.addLast(new HttpResponseEncoder());
            pipeline.addLast(new HttpRequestDispatcher(NettyHttpServer.this._logger));
        }
    }
}
