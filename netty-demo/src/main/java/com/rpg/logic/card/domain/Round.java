package com.rpg.logic.card.domain;

import java.util.ArrayList;
import java.util.List;

public class Round implements Runnable {

	private List<User> list = new ArrayList<User>();
	
	public void run() {
		User user = list.get(0);
		List<Card> u1List = user.getList();
		for(int i=0;i<u1List.size();i++){
			Card card = u1List.get(i);
			NameType u1NameType = card.getNameType();
			CompareType u1CompareType = card.getCompareType();
			for(int index=1;index<list.size();i++){
				User u = list.get(index);
				List<Card> uList = u.getList();
				for(int j=0;i<uList.size();j++){
					Card c = uList.get(j);
					NameType uNameType = c.getNameType();
					CompareType uCompareType = c.getCompareType();
					if(u1NameType.getValue()>uNameType.getValue()){
						continue;
					}else
					if(u1NameType.getValue()==uNameType.getValue()){
						if(u1CompareType.getValue()>uCompareType.getValue()){
							continue;
						}
					}else{
						uList.remove(j);
						break;
					}
				}
			}
			u1List.remove(i);
		}
	}
	
	public void addUser(User user){
		list.add(user);
	}

}
