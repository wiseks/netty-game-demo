package com.rpg.framework.handler.dispatcher.disruptor.event;

import com.lmax.disruptor.EventFactory;

/**
 * DisruptorEventFactory
 * @author wudeji
 *
 */
public class DisruptorEventFactory implements EventFactory<DispatcherEvent> {

	@Override
	public DispatcherEvent newInstance() {
		return new DispatcherEvent();
	}

}
