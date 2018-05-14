package com.rpg.logic.player.service.test;

import com.rpg.framework.annotation.MessageController;
import com.rpg.framework.annotation.MessageRequest;

import io.netty.channel.ChannelHandlerContext;
import xn.protobuf.map.MapMsg.PlayerMoveNewReqMsg_13118;
import xn.protobuf.player.PlayerMsg.PlayerLoginNewResMsg_16014;

@MessageController
public class PlayerTestHandler {

	@MessageRequest(nocheck=true)
	public void login(ChannelHandlerContext channelContext,PlayerLoginNewResMsg_16014 msg){
		for(int i=0;i<100;i++){
			PlayerMoveNewReqMsg_13118.Builder msg1 = PlayerMoveNewReqMsg_13118.newBuilder();
			msg1.setX(i);
			msg1.setY(i);
			channelContext.channel().write(msg1.build());
		}
	}
	
}
