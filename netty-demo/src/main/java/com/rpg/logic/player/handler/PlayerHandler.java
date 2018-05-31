package com.rpg.logic.player.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.rpg.framework.annotation.MessageController;
import com.rpg.framework.annotation.MessageRequest;
import com.rpg.framework.code.Response;
import com.rpg.framework.handler.dispatcher.IHandlerDispatcher;
import com.rpg.framework.session.AbstractUserSession;
import com.rpg.framework.session.DisruptorUserSession;
import com.rpg.logic.player.service.PlayerService;

import io.netty.channel.ChannelHandlerContext;
import xn.protobuf.login.LoginMsg.LoginReqMsg_12001;
import xn.protobuf.player.PlayerMsg.PlayerCloseReqMsg_16015;
import xn.protobuf.player.PlayerMsg.PlayerCreateNewReqMsg_16011;
import xn.protobuf.player.PlayerMsg.PlayerLoginNewReqMsg_16013;

@MessageController
public class PlayerHandler {

	@Autowired
	protected PlayerService playerService;
	
//	@Autowired
//	private IHandlerDispatcher commandDispatcher;
//	
	@MessageRequest(nocheck=true)
	public Response getUser(LoginReqMsg_12001 msg){
		playerService.getUser();
		return null;
	}
	
	@MessageRequest(nocheck=true)
	public Response createPlayer(PlayerCreateNewReqMsg_16011 msg){
		return playerService.createPlayer(msg.getUser());
	}
	
	@MessageRequest(nocheck=true)
	public Response login(ChannelHandlerContext channelContext,AbstractUserSession<Integer> session,PlayerLoginNewReqMsg_16013 msg){
		return playerService.login(channelContext, msg.getUser());
	}
	
	@MessageRequest(nocheck=true)
	public Response closeMsg(ChannelHandlerContext channelContext,AbstractUserSession<Integer> session,PlayerCloseReqMsg_16015 msg){
//		commandDispatcher.closeMsg(msg.getMsgName());
		PlayerCloseReqMsg_16015.Builder res = PlayerCloseReqMsg_16015.newBuilder();
		res.setMsgName(msg.getMsgName());
		return Response.createResponse(res.build());
	}
}
