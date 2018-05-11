package com.rpg.logic.card.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


public class Test {

	public static void main(String[] args) {
		List<Card> cardList = new ArrayList<Card>();
		for(CompareType compareType:CompareType.values()){
			for(NameType nameType : NameType.values()){
				if(nameType.getValue()==NameType.BOSS.getValue()&&compareType.getValue()>CompareType.FOUR.getValue()){
					Card card = new Card();
					card.setCompareType(compareType);
					card.setNameType(nameType);
					card.setName(compareType.getName()+"-"+nameType);
					cardList.add(card);
				}else if(nameType.getValue()<NameType.BOSS.getValue()&&compareType.getValue()<=CompareType.FOUR.getValue()){
					Card card = new Card();
					card.setCompareType(compareType);
					card.setNameType(nameType);
					card.setName(compareType.getName()+"-"+nameType);
					cardList.add(card);
				}
			}
		}
//		for(int i=0;i<cardList.size();i++){
//			Card card = cardList.get(i);
//			System.out.println(i+1+"="+card.getName());
//		}
		List<User> list = new ArrayList<User>();
		for(int i=0;i<4;i++){
			User cards = new User();
			cards.setId(i+1);
			list.add(cards);
		}
		add(cardList, list);
		List<User> tmpList = new ArrayList<User>();
		int index = 0;
		System.out.println(list.get(0).getList().size()+"="+list.get(0).getId());
		System.out.println(list.get(1).getList().size()+"="+list.get(1).getId());
		System.out.println(list.get(2).getList().size()+"="+list.get(2).getId());
		System.out.println(list.get(3).getList().size()+"="+list.get(3).getId());
		for(int i=0;i<list.size();i++){
			User cards = list.get(i);
			for(Card card : cards.getList()){
				if(card.getNameType().getValue()==NameType.THREE.getValue()&&card.getCompareType().getValue()==CompareType.ONE.getValue()){
					index = i;
					break;
				}
			}
		}
		if(index>0){
			for(int i=index;i<list.size();i++){
				tmpList.add(list.get(i));
			}
			for(int i=0;i<index;i++){
				tmpList.add(list.get(i));
			}
		}else{
			for(int i=index;i<list.size();i++){
				tmpList.add(list.get(i));
			}
		}
		System.out.println(tmpList.get(0).getList().size()+"="+tmpList.get(0).getId());
		System.out.println(tmpList.get(1).getList().size()+"="+tmpList.get(1).getId());
		System.out.println(tmpList.get(2).getList().size()+"="+tmpList.get(2).getId());
		System.out.println(tmpList.get(3).getList().size()+"="+tmpList.get(3).getId());
		Comparator<Card> c = new Comparator<Card>() {

			public int compare(Card o1, Card o2) {
				if(o1.getNameType().getValue()==o2.getNameType().getValue()){
					return o1.getCompareType().getValue()-o2.getCompareType().getValue();
				}
				return o1.getNameType().getValue()-o2.getNameType().getValue();
			}
		};
		for(User user : tmpList){
			Collections.sort(user.getList(), c);
		}
		
		
	}
	
	static void add(List<Card> cardList,List<User> list){
		for(int i=0;i<list.size();i++){
			User cards = list.get(i);
			Random random = new Random();
			if(cardList.size()>0){
				int index = random.nextInt(cardList.size());
				Card card = cardList.get(index);
				if(card!=null){
					if(cards.getList().size()<13){
						cards.getList().add(card);
						cardList.remove(index);
						add(cardList, list);
					}else if(cards.getList().size()==13&&cardList.size()<=2){
						cards.getList().add(card);
						cardList.remove(index);
						add(cardList, list);
					}
				}
			}
		}
	}
}
