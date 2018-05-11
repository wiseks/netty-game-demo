package com.rpg.logic.map.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rpg.logic.player.domain.Player;

public class PlayerMoveProcess implements Runnable {

	private Map<Integer,Player> playerList = new ConcurrentHashMap<Integer, Player>();
	
	public void run() {
		sendMoveData();
	}
	
	
	private void sendMoveData(){
		System.out.println("move");
	}
	

}
