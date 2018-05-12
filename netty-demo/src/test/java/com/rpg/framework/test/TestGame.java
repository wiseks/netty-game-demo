package com.rpg.framework.test;

import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;
import xn.protobuf.login.LoginMsg.LoginReqMsg_12001;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.oio.OioClientSocketChannelFactory;
import org.junit.Before;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.protobuf.Message;
import com.rpg.framework.code.ProtobufDecoder;
import com.rpg.framework.code.ProtobufEncoder;
import com.rpg.framework.code.Response;
import com.rpg.framework.handler.ServerHandlerDispatcher;
import com.rpg.logic.server.ServerStart;

public abstract class TestGame extends TestCase {

	public final int port = 6000;
	public final String host = "127.0.0.1";
	// public final String host = "192.168.0.250";
	// public final String host = "120.24.16.23";
	private OioClientSocketChannelFactory clientSocketChannelFactory = new OioClientSocketChannelFactory(
			Executors.newCachedThreadPool());

	private ClientBootstrap clientBootstrap = new ClientBootstrap(clientSocketChannelFactory);

	public static ProtobufMapping protobufMapping;

	protected ChannelFuture futrue;

	public static volatile AtomicBoolean signal = new AtomicBoolean();
	public static volatile short cmd = 0;

	@Before
	public ChannelFuture startup() throws Exception {
		
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");


		Properties properties = new Properties();
		properties.load(new InputStreamReader(ServerStart.class.getResourceAsStream("/game_resources/system.properties"), "UTF-8"));
		System.setProperty("log4jdir", properties.getProperty("log4jdir"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application-test.xml");
		ServerHandlerDispatcher dispatcher = context.getBean(ServerHandlerDispatcher.class);
//		protobufMapping = new ProtobufMapping();
		//protobufMapping.initialize();
		//protobufMapping.init();
		clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("encoder", new ProtobufDecoder(dispatcher));
				pipeline.addLast("decoder", new ProtobufEncoder());
				pipeline.addLast("handler", new ClientHandler());
				return pipeline;
			}
		}); // 只能这样设置
		clientBootstrap.setOption("tcpNoDelay", true);
		clientBootstrap.setOption("keepAlive", true);
		ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(host, port));
		this.futrue = future;
		return future;
	}

	public void login(String user) {
		LoginReqMsg_12001.Builder login = LoginReqMsg_12001.newBuilder();
		login.setUser(user);
		login.setCode("1111");
		try {
			this.sendMsg(login.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void sendMsg(Message message) {
		try {
			if (futrue == null)
				futrue = startup();
			if (futrue != null) {
				signal.set(true);
				Response res = Response.createResponse(message);
				cmd = res.getCmd();
				futrue.getChannel().write(res);
//				while (signal.get()) {
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
