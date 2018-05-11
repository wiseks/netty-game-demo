package com.rpg.logic.map.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rpg.framework.annotation.EventClass;
import com.rpg.framework.annotation.EventMethod;
import com.rpg.framework.event.EventBus;
import com.rpg.framework.session.ChannelCloseEvent;
import com.rpg.logic.map.domain.Sence;
import com.rpg.logic.map.dto.PlayerMoveData;
import com.rpg.logic.map.handler.MyEvent;
import com.rpg.logic.player.domain.Player;
import com.rpg.logic.player.service.PlayerService;

@Service
@EventClass
public class SenceService {

	@Autowired
	private PlayerService playerService;
	
	@Autowired
	private EventBus eventBus;
	
	private int a = 0;
	
	private ExecutorService service = Executors.newCachedThreadPool();
	private Map<Integer,Sence> senceMap = new ConcurrentHashMap<Integer, Sence>();
	
	
	
	@PostConstruct
	public void initSence(){
		for(int i=1001;i<=1010;i++){
			Sence sence = new Sence(i);
			service.execute(sence);
			senceMap.put(sence.getSenceId(), sence);
		}
	}
	
	public void enterMap(Player player,int senceId){
		int oldSenceId = player.getSenceId();
		Sence oldSence = senceMap.get(oldSenceId);
		if(oldSence!=null){
			if(senceId==oldSenceId){
				if(!oldSence.isInSence(player.getPlayerId())){
					oldSence.addPlayer(player);
					return;
				}else{
					return;
				}
			}
		}else{
			return;
		}
		Sence sence = senceMap.get(senceId);
		if(sence==null){
			return;
		}
		System.out.println("playerId:"+player.getPlayerId()+"切换到场景:"+senceId);
		oldSence.removePlayer(player.getPlayerId());
		sence.addPlayer(player);
	}
	
	public void move(Player player,int x,int y){
		a ++;
		System.out.println("playerId="+player.getPlayerId()+",move to,x="+x+",y="+y+",index="+a);
		eventBus.post(new MyEvent(x, y, player.getPlayerId()));
		Sence sence = senceMap.get(player.getSenceId());
		if(sence!=null){
			PlayerMoveData data = new PlayerMoveData(player.getPlayerId(),x,y,sence.getSenceId());
			sence.move(data);
		}
	}

	@EventMethod
	public void onChannelClose(ChannelCloseEvent<Integer> event) {
		int playerId = event.getSession().getId();
		Player player = playerService.getPlayer(playerId);
		if(player!=null){
			Sence sence = senceMap.get(player.getSenceId());
			if(sence!=null)
				sence.removePlayer(playerId);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>removePlayerId:"+playerId);
		}
	}

	public void onConn(long playerId) {
		// TODO Auto-generated method stub
		
	}
	
}