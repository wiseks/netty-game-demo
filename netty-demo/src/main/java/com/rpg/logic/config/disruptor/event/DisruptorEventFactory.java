package com.rpg.logic.config.disruptor.event;

import com.lmax.disruptor.EventFactory;

public class DisruptorEventFactory implements EventFactory<DisruptorEvent> {

	@Override
	public DisruptorEvent newInstance() {
		return new DisruptorEvent();
	}

}
