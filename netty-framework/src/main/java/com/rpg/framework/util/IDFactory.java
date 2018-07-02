package com.rpg.framework.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IDFactory {

	private static final AtomicInteger id = new AtomicInteger();
	
	public static int genId(){
		return id.incrementAndGet();
	}
}
