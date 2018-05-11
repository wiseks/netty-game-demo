package com.rpg.framework.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.rpg.framework.code.ProtobufDecoder;
import com.rpg.framework.code.ProtobufEncoder;
import com.rpg.framework.handler.ServerHandler;
import com.rpg.framework.protobuf.ProtobufMapping;

public abstract class AbstractServer {
	
	private final Logger log = Logger.getLogger(this.getClass());

	protected void bind(final InetSocketAddress address,final ProtobufMapping protobufMapping,final ServerHandler gameServerHandler) {
		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.setOption("reuseAddress", true);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();
				// pipeline.addLast("decoder", new MyDecoder());
				// pipeline.addLast("encoder", new MyEncoder());

				pipeline.addLast("decoder", new ProtobufDecoder(protobufMapping));
				pipeline.addLast("encoder", new ProtobufEncoder());

				pipeline.addLast("handler", gameServerHandler);
				return pipeline;
			}
		});

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
}
