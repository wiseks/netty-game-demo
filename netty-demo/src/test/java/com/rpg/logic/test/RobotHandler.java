//package com.rpg.logic.test;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.jboss.netty.channel.ChannelHandlerContext;
//import org.jboss.netty.channel.ChannelStateEvent;
//import org.jboss.netty.channel.ExceptionEvent;
//import org.jboss.netty.channel.MessageEvent;
//import org.jboss.netty.channel.SimpleChannelHandler;
//
//
//import com.google.protobuf.TextFormat;
//import com.rpg.framework.code.Request;
//import com.rpg.logic.common.ErrorCode;
//
//public class RobotHandler extends SimpleChannelHandler {
//
//	private final Log log = LogFactory.getLog(this.getClass());
//
//	@Override
//	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//		final Request command = (Request) e.getMessage();
////		int cmd = Robot.protobufMapping.messageCmd(command.getMessage().getClass()).shortValue();
////		if (command.getError() == 0) {
////			log.warn("cmd:" + cmd + "->" + TextFormat.printToString(command.getMessage().toBuilder()));
////		} else {
////			ErrorCode error = ErrorCode.getErrorCode(command.getError());
////			log.warn("cmd:" + cmd + ",error:" + error.getCode() + "->" + error.getDesc());
////		}
//
//	}
//
//	@Override
//	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//	}
//
//	@Override
//	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
//	}
//}
