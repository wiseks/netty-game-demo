package com.rpg.framework.server;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import com.rpg.framework.code.Decoder;
import com.rpg.framework.code.Encoder;
import com.rpg.framework.handler.ServerHandler;
import com.rpg.framework.handler.dispatcher.IHandlerDispatcher;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class AbstractServer {

	private final Logger log = Logger.getLogger(this.getClass());

	private NioEventLoopGroup workerGroup;
	private NioEventLoopGroup bossGroup;

	protected void bind(final InetSocketAddress address, final IHandlerDispatcher<?> dispatcher,
			final ServerHandler<?> gameServerHandler) {

		ServerBootstrap bootstrap = new ServerBootstrap();
		workerGroup = new NioEventLoopGroup();
		bossGroup = new NioEventLoopGroup();

		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 5).option(ChannelOption.TCP_NODELAY, true);

		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline()
				.addLast("decoder", new Decoder())
				.addLast("server-handler", gameServerHandler)
				.addLast("encoder", new Encoder());
			}
		});
		try {
			bootstrap.bind(address).sync();
		} catch (InterruptedException e) {
			log.error("bind " + address.getHostName() + ":" + address.getPort() + " failed", e);
			shutdown();
		}

		bootstrap.bind(address);

		// // 注删jvm关闭钩子,发出关闭事件
		// Runtime.getRuntime().addShutdownHook(new Thread() {
		// @Override
		// public void run() {
		// // closeService.onServerClose(address);
		// log.info("START|ShutdownHook Registry.");
		// }
		// });

		log.info("START|LISION PORT:" + address.getAddress().getHostName() + ":" + address.getPort());
	}

	public void shutdown() {
		try {
			bossGroup.shutdownGracefully().sync();
		} catch (InterruptedException e) {
			log.error("shutdown boss group failed", e);
		}
		try {
			workerGroup.shutdownGracefully().sync();
		} catch (InterruptedException e) {
			log.error("shutdown worker group failed", e);
		}
	}
}
