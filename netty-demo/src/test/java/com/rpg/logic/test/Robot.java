package com.rpg.logic.test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.oio.OioClientSocketChannelFactory;

import xn.protobuf.login.LoginMsg.LoginReqMsg_12001;
import xn.protobuf.map.MapMsg.MapPlayerMoveReqMsg_13105;
import xn.protobuf.map.MapMsg.PlayerEnterSenceReqMsg_13119;
import xn.protobuf.map.MapMsg.PlayerMoveNewReqMsg_13118;
import xn.protobuf.player.PlayerMsg.PlayerLoginNewReqMsg_16013;

import com.google.protobuf.Message;
import com.rpg.framework.code.ProtobufDecoder;
import com.rpg.framework.code.ProtobufEncoder;
import com.rpg.framework.code.Response;

public class Robot {

	public final int port = 6000;
	public final String host = "127.0.0.1";
	private OioClientSocketChannelFactory clientSocketChannelFactory = new OioClientSocketChannelFactory(
			Executors.newCachedThreadPool());

	private ClientBootstrap clientBootstrap = new ClientBootstrap(clientSocketChannelFactory);

	//public static ProtobufMapping protobufMapping;

	protected ChannelFuture futrue;

	private transient static boolean flag = true;
	
	public static Point[] points = new Point[] { new Point(1, 2),
			new Point(3, 4), new Point(5, 6),
			new Point(7, 8), new Point(9, 10) };

	public volatile AtomicBoolean login = new AtomicBoolean();

	public static List<Robot> robotList = new ArrayList<Robot>();

	public static void main(String[] args) throws Exception {
		for (int i = 1; i <= 4; i++) {
			Robot robot = new Robot();
			robot.login("test" + i);
			Thread.sleep(200);
			robotList.add(robot);
		}
		Thread.sleep(1000);
		for(int i=0;i<10;i++){
			for (Robot robot : robotList) {
				Random r = new Random();
				System.out.println("111111111");
				robot.move(points[r.nextInt(points.length)]);
				Thread.sleep(1000);
				robot.enterSence(1002+i);
			}
			Thread.sleep(3000);
			
		}
	
	}

//	public ChannelFuture startup() throws Exception {
//		protobufMapping = new ProtobufMapping();
//		clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
//
//			public ChannelPipeline getPipeline() throws Exception {
//				ChannelPipeline pipeline = Channels.pipeline();
//				pipeline.addLast("encoder", new ProtobufDecoder(protobufMapping));
//				pipeline.addLast("decoder", new ProtobufEncoder());
//				pipeline.addLast("handler", new RobotHandler());
//				return pipeline;
//			}
//		}); // 只能这样设置
//		clientBootstrap.setOption("tcpNoDelay", true);
//		clientBootstrap.setOption("keepAlive", true);
//		ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(host, port));
//
//		return future;
//	}

	public void move(Point point) {
		PlayerMoveNewReqMsg_13118.Builder req = PlayerMoveNewReqMsg_13118.newBuilder();
		req.setX(point.getX());
		req.setY(point.getY());
		try {
			this.sendMsg(req.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void enterSence(int senceId){
		PlayerEnterSenceReqMsg_13119.Builder req = PlayerEnterSenceReqMsg_13119.newBuilder();
		req.setSenceId(senceId);
		this.sendMsg(req.build());
	}

	public void login(String user) {
		PlayerLoginNewReqMsg_16013.Builder login = PlayerLoginNewReqMsg_16013.newBuilder();
		login.setUser(user);
//		login.setCode("1111");
		try {
			this.sendMsg(login.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void sendMsg(Message message) {
		try {
			if (futrue == null)
				//futrue = startup();
			if (futrue != null) {
				// signal.set(true);
				Response res = Response.createResponse(message);
				futrue.getChannel().write(res);
				// while (signal.get()) {
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
