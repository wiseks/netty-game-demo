package com.rpg.logic.card.domain;

public enum NameType {

	ONE(1,12),
	TWO(2,13),
	THREE(3,1),
	FOUR(4,2),
	FIVE(5,3),
	SIX(6,4),
	SEVEN(7,5),
	EIGHT(8,6),
	NINE(9,7),
	TEN(10,8),
	J(11,9),
	Q(12,10),
	K(13,11),
	BOSS(14,14);
	
	private int value;

	private int compare;
	
	
	private NameType(int value,int compare) {
		this.value = value;
		this.compare = compare;
	}

	public int getValue() {
		return value;
	}

	public int getCompare() {
		return compare;
	}
	
}
