package com.rpg.logic.player.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Message;
import com.rpg.framework.session.SessionHolder;
import com.rpg.logic.map.dto.PlayerMoveData;

public class Player {

	private int playerId;
	
	private String name;
	
	private String user;
	
	private int senceId;
	
	private SessionHolder sessionHolder;
	
	private List<PlayerMoveData> moveDate = new ArrayList<PlayerMoveData>();

	public void sendMsg(Message message){
		sessionHolder.sendMsg(playerId, message);
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setSessionHolder(SessionHolder sessionHolder) {
		this.sessionHolder = sessionHolder;
	}

	public int getSenceId() {
		return senceId;
	}

	public void setSenceId(int senceId) {
		this.senceId = senceId;
	}

	public List<PlayerMoveData> getMoveDate() {
		return moveDate;
	}
	
}
