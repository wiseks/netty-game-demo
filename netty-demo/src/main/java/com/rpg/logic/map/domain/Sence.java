package com.rpg.logic.map.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.rpg.logic.map.dto.PlayerMoveData;
import com.rpg.logic.player.domain.Player;

import xn.protobuf.map.MapMsg.MapMoveDataNew;
import xn.protobuf.map.MapMsg.MapPlayerDataOutResMsg_13104;
import xn.protobuf.map.MapMsg.MoveDataNew;
import xn.protobuf.map.MapMsg.PlayerMoveNewResMsg_13117;
import xn.protobuf.monster.MonsterMsg.MonsterCreateNewResMsg_13207;
import xn.protobuf.monster.MonsterMsg.MonsterDataNew;

public class Sence implements Runnable {

	private int senceId;
	
	private Map<Integer,Player> playerMap = new ConcurrentHashMap<Integer,Player>();
	
	private Map<Integer,MonsterDataNew> monsters = new ConcurrentHashMap<Integer, MonsterDataNew>();
	
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private WriteLock writeLock = lock.writeLock();
	private ReadLock readLock = lock.readLock();
	
	
	public void run() {
		while (true) {
			try {
				sendMoveData();
//				for(Player player:playerMap.values()){
//					sendMonster(player);
//				}
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	public Sence(int senceId) {
		this.senceId = senceId;
		MonsterDataNew.Builder data = MonsterDataNew.newBuilder();
		data.setId(senceId+1000);
		data.setSenceId(senceId);
		data.setX(senceId);
		data.setY(senceId);
		monsters.put(data.getId(), data.build());
	}


	public int getSenceId() {
		return senceId;
	}

	public void addPlayer(Player player){
		playerMap.put(player.getPlayerId(), player);
		sendMonster(player);
	}
	
	public void removePlayer(int playerId){
		if(playerMap.containsKey(playerId)){
			playerMap.remove(playerId);
		}
		MapPlayerDataOutResMsg_13104.Builder res = MapPlayerDataOutResMsg_13104.newBuilder();
		res.setPlayerId(playerId);
		for(Player player : playerMap.values()){
			//player.sendMsg(res.build());
		}
	}
	
	public void move(PlayerMoveData data){
		try {
			writeLock.lock();
			for(Player player : playerMap.values()){
				if(player.getPlayerId()!=data.getPlayerId()){
					player.getMoveDate().add(data);
				}
			}
		} catch (Exception e) {
		}finally {
			writeLock.unlock();
		}
		
	}
	
	private void sendMoveData(){
		try {
			readLock.lock();
			for(Player player : playerMap.values()){
				if(player.getMoveDate().size()>0){
					PlayerMoveNewResMsg_13117.Builder res = PlayerMoveNewResMsg_13117.newBuilder();
					MapMoveDataNew.Builder bigData = MapMoveDataNew.newBuilder();
					bigData.setPlayerId(player.getPlayerId());
					for(PlayerMoveData d : player.getMoveDate()){
						MoveDataNew.Builder data = MoveDataNew.newBuilder();
						data.setSenceId(d.getSenceId());
						data.setX(d.getX());
						data.setY(d.getY());
						bigData.addDataList(data.build());
					}
					res.setData(bigData.build());
					//player.sendMsg(res.build());
					System.out.println(">>>>>>>>>>>>"+res.build());
					player.getMoveDate().clear();
				}
			}
		} catch (Exception e) {
			
		}finally {
			readLock.unlock();
		}
	}
	
	
	public boolean isInSence(int playerId){
		if(playerMap.containsKey(playerId)){
			return true;
		}
		return false;
	}
	
	public void sendMonster(Player player){
		MonsterCreateNewResMsg_13207.Builder res = MonsterCreateNewResMsg_13207.newBuilder();
		for(int key : monsters.keySet()){
			MonsterDataNew data = monsters.get(key);
					res.addMonster(data);
		}
		//player.sendMsg(res.build());
	}
	
}
