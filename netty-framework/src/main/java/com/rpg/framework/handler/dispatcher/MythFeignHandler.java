package com.rpg.framework.handler.dispatcher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MythFeignHandler implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println(">>>>>>>>>>>>>>>>");
		Object obj = method.invoke(this, args);
		System.out.println("??????????????????");
		return obj;
	}

}
