package com.rpg.framework.test;

import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;
import xn.protobuf.login.LoginMsg.LoginReqMsg_12001;

import org.junit.Before;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.protobuf.Message;
import com.rpg.framework.code.Decoder;
import com.rpg.framework.code.Encoder;
import com.rpg.framework.code.Response;
import com.rpg.framework.handler.dispatcher.ServerHandlerExecutorDispatcher;
import com.rpg.logic.server.ServerStart;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public abstract class TestGame extends TestCase {

	public final int port = 6000;
	public final String host = "127.0.0.1";
	// public final String host = "192.168.0.250";
	// public final String host = "120.24.16.23";
	
	private AtomicInteger index = new AtomicInteger();

	private Bootstrap bootstrap;

	public static ProtobufMapping protobufMapping;

	protected ChannelFuture futrue;

	public static volatile AtomicBoolean signal = new AtomicBoolean();
	public static volatile short cmd = 0;

	@Before
	public ChannelFuture startup() throws Exception {

		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

		Properties properties = new Properties();
		properties.load(new InputStreamReader(
				ServerStart.class.getResourceAsStream("/game_resources/system.properties"), "UTF-8"));
		System.setProperty("log4jdir", properties.getProperty("log4jdir"));
		//ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application-test.xml");
		//ServerHandlerExecutorDispatcher dispatcher = context.getBean(ServerHandlerExecutorDispatcher.class);
		// protobufMapping = new ProtobufMapping();
		// protobufMapping.initialize();
		// protobufMapping.init();
		NioEventLoopGroup worker = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(worker).channel(NioSocketChannel.class);

		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline()
				.addLast("decoder", new Decoder())
				.addLast("server-handler", new ClientHandler())
				.addLast("encoder", new Encoder())
				;
			}
		});

		ChannelFuture future = bootstrap.connect(host, port).sync();
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
				futrue.channel().writeAndFlush(res);
//				if(index.incrementAndGet()>=1000){
//					futrue.channel().close();
//				}
//				System.out.println(">>>>>>>>>>>>>>>"+res+","+index.incrementAndGet());
//				while (signal.get()) {
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
