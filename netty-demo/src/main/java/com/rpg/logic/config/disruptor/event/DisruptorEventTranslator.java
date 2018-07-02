package com.rpg.logic.config.disruptor.event;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.rpg.logic.player.repository.MythInvocation;

public class DisruptorEventTranslator implements EventTranslatorOneArg<DisruptorEvent,MythInvocation> {

	@Override
	public void translateTo(DisruptorEvent event, long sequence, MythInvocation dto) {
		event.setMythInvocation(dto);
	}

}
