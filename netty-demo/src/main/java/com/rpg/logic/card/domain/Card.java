package com.rpg.logic.card.domain;

public class Card {

	private String name;
	
	private NameType nameType;
	
	private CompareType compareType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NameType getNameType() {
		return nameType;
	}

	public void setNameType(NameType nameType) {
		this.nameType = nameType;
	}

	public CompareType getCompareType() {
		return compareType;
	}

	public void setCompareType(CompareType compareType) {
		this.compareType = compareType;
	}
	
	@Override
	public String toString() {
		return compareType.getName()+"-"+nameType;
	}
}
