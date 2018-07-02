package com.rpg.logic.player.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.rpg.logic.player.domain.Player;

public interface PlayerDao {

	@Insert(" insert into Player(playerId,name,user,senceId) values (null,#{name},#{user},#{senceId}) ")
	public int createPlayer(Player player);
	
	@Select("select * from Player where user=#{user}")
	public Player getByUser(String user);
	
	@Select("select * from Player where playerId=#{playerId}")
	public Player getByPlayerId(int playerId);
}
