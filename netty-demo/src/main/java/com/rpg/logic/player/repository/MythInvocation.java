package com.rpg.logic.player.repository;


import java.io.Serializable;


public class MythInvocation implements Serializable {

    private static final long serialVersionUID = -5108578223428529356L;

    private Class<?> targetClass;


    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] args;
    
    

	public MythInvocation() {
	}

	public MythInvocation(Class<?> clazz, String name, Class<?>[] args2, Object[] arguments) {
		this.targetClass = clazz;
		this.methodName = name;
		this.parameterTypes = args2;
		this.args = arguments;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}



}
