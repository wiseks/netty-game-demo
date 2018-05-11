package com.rpg.logic.card.domain;

public enum CompareType {

	ONE(1,"方"),
	TWO(2,"花"),
	THREE(3,"红"),
	FOUR(4,"黑"),
	
	FIVE(5,"小"),
	SIX(6,"大");
	
	private int value;

	private String name;
	
	private CompareType(int value,String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}
	
	
}
