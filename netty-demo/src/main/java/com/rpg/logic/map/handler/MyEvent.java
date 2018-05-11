package com.rpg.logic.map.handler;

public class MyEvent{
	private int x;
	
	private int y;
	
	private int playerId;

	public MyEvent(int x, int y, int playerId) {
		this.x = x;
		this.y = y;
		this.playerId = playerId;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getPlayerId() {
		return playerId;
	}
	
}
