package com.rpg.logic.map.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.rpg.framework.annotation.EventClass;
import com.rpg.framework.annotation.EventMethod;
import com.rpg.framework.annotation.MessageController;
import com.rpg.framework.annotation.MessageRequest;
import com.rpg.framework.session.AbstractUserSession;
import com.rpg.logic.map.service.SenceService;
import com.rpg.logic.player.domain.Player;
import com.rpg.logic.player.service.PlayerService;

import xn.protobuf.map.MapMsg.PlayerEnterSenceReqMsg_13119;
import xn.protobuf.map.MapMsg.PlayerMoveNewReqMsg_13118;

@MessageController
@EventClass
public class SenceHandler {

	@Autowired
	private SenceService senceService;
	
	@Autowired
	private PlayerService playerService;
	
	@MessageRequest
	public void move(AbstractUserSession<Integer> session,PlayerMoveNewReqMsg_13118 msg){
		Player player = playerService.getPlayer(session.getId());
		senceService.move(player, msg.getX(), msg.getY());
	}
	
	@MessageRequest
	public void enterSence(AbstractUserSession<Integer> session,PlayerEnterSenceReqMsg_13119 msg){
		Player player = playerService.getPlayer(session.getId());
		senceService.enterMap(player, msg.getSenceId());
	}
	
	@EventMethod
	private void processEvent(MyEvent e){
		System.out.println("event:"+e.getPlayerId()+","+e.getX()+","+e.getY());
	}
	
	
}
