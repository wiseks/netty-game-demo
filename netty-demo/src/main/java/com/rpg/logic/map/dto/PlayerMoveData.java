package com.rpg.logic.map.dto;

public class PlayerMoveData {
	
	private int playerId;

	private int x;
	
	private int y;
	
	private int senceId;
	
	
	public PlayerMoveData(int playerId,int x, int y, int senceId) {
		this.playerId = playerId;
		this.x = x;
		this.y = y;
		this.senceId = senceId;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getSenceId() {
		return senceId;
	}

	public int getPlayerId() {
		return playerId;
	}

}
