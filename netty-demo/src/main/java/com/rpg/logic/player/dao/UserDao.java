package com.rpg.logic.player.dao;

import org.apache.ibatis.annotations.Select;

import com.rpg.logic.player.domain.User;

public interface UserDao {

	@Select("select * from user where id=#{id}")
	public User getById(int id);
}
