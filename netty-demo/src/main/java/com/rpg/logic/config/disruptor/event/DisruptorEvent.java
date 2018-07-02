package com.rpg.logic.config.disruptor.event;

import com.rpg.logic.player.repository.MythInvocation;

public class DisruptorEvent {

	private MythInvocation mythInvocation;

	public MythInvocation getMythInvocation() {
		return mythInvocation;
	}

	public void setMythInvocation(MythInvocation mythInvocation) {
		this.mythInvocation = mythInvocation;
	}
	
	
	
}
