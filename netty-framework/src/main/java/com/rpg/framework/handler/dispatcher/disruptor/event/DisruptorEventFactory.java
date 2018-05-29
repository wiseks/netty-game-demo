package com.rpg.framework.handler.dispatcher.disruptor.event;

import com.lmax.disruptor.EventFactory;

/**
 * DisruptorEventFactory
 * @author wudeji
 *
 */
public class DisruptorEventFactory implements EventFactory<DisruptorEvent> {

	@Override
	public DisruptorEvent newInstance() {
		return new DisruptorEvent();
	}

}
