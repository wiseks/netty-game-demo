package com.rpg.logic.test;

import com.rpg.framework.test.TestGame;

import xn.protobuf.map.MapMsg.PlayerMoveNewReqMsg_13118;
import xn.protobuf.player.PlayerMsg.PlayerCloseReqMsg_16015;
import xn.protobuf.player.PlayerMsg.PlayerCreateNewReqMsg_16011;
import xn.protobuf.player.PlayerMsg.PlayerLoginNewReqMsg_16013;

public class PlayerTest extends TestGame {

	public void testCreatePlayer(){
		PlayerCreateNewReqMsg_16011.Builder req = PlayerCreateNewReqMsg_16011.newBuilder();
		req.setUser("test1");
		this.sendMsg(req.build());
	}
	
	public void testLogin(){
		PlayerLoginNewReqMsg_16013.Builder req = PlayerLoginNewReqMsg_16013.newBuilder();
		req.setUser("test2");
		this.sendMsg(req.build());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<1000;i++){
			PlayerMoveNewReqMsg_13118.Builder msg1 = PlayerMoveNewReqMsg_13118.newBuilder();
			msg1.setX(i);
			msg1.setY(i);
//			channelContext.getChannel().write(msg1.build());
			this.sendMsg(msg1.build());
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		PlayerLoginNewReqMsg_16013.Builder req1 = PlayerLoginNewReqMsg_16013.newBuilder();
//		req1.setUser("test2");
//		this.sendMsg(req1.build());
		
		
	}
	
	public void testCloseMsg(){
		PlayerCloseReqMsg_16015.Builder req = PlayerCloseReqMsg_16015.newBuilder();
		req.setMsgName("PlayerCreateNewReqMsg_16011");
		this.sendMsg(req.build());
	}
}
