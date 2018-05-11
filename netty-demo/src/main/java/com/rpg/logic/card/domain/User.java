package com.rpg.logic.card.domain;

import java.util.ArrayList;
import java.util.List;

public class User {

	private int id;
	
	private List<Card> list = new ArrayList<Card>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Card> getList() {
		return list;
	}
	
	
}
