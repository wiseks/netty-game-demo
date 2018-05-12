package com.rpg.logic.player.service.test;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.rpg.framework.annotation.MessageController;
import com.rpg.framework.annotation.MessageRequest;
import com.rpg.framework.code.Response;
import com.rpg.framework.handler.ServerHandlerDispatcher;
import com.rpg.logic.player.service.PlayerService;

import xn.protobuf.login.LoginMsg.LoginReqMsg_12001;
import xn.protobuf.map.MapMsg.PlayerMoveNewReqMsg_13118;
import xn.protobuf.player.PlayerMsg.PlayerCloseReqMsg_16015;
import xn.protobuf.player.PlayerMsg.PlayerCreateNewReqMsg_16011;
import xn.protobuf.player.PlayerMsg.PlayerLoginNewReqMsg_16013;
import xn.protobuf.player.PlayerMsg.PlayerLoginNewResMsg_16014;

@MessageController
public class PlayerTestHandler {

	@MessageRequest(nocheck=true)
	public void login(ChannelHandlerContext channelContext,PlayerLoginNewResMsg_16014 msg){
		for(int i=0;i<100;i++){
			PlayerMoveNewReqMsg_13118.Builder msg1 = PlayerMoveNewReqMsg_13118.newBuilder();
			msg1.setX(i);
			msg1.setY(i);
			channelContext.getChannel().write(msg1.build());
		}
	}
	
}
