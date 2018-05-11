package com.rpg.logic.player.dao;

import com.rpg.logic.player.domain.Player;

public interface PlayerDao {

	public int createPlayer(Player player);
	
	public Player getByUser(String user);
	
	public Player getByPlayerId(int playerId);
}
