package com.rpg.framework.handler.dispatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.rpg.framework.annotation.MessageRequest;

public class ProxyFactory implements MethodInterceptor {

	// 1.工具类
	Enhancer en = new Enhancer();

	// 给目标对象创建一个代理对象
	public Object getProxyInstance(Class<?> clazz) {

		// 2.设置父类
		en.setSuperclass(clazz);
		// 3.设置回调函数
		en.setCallback(this);
		// 4.创建子类(代理对象)
		return en.create();

	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		System.out.println("开始事务...");

		MessageRequest req = method.getAnnotation(MessageRequest.class);
		System.out.println(req);
		// 执行目标对象的方法
		Object returnValue = proxy.invokeSuper(obj, args);

		System.out.println("提交事务...");

		return returnValue;
	}

}
