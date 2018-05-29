package com.rpg.logic.map.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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
	
	private AtomicInteger a = new AtomicInteger(0);
	private AtomicInteger b = new AtomicInteger(0);
	
	private ExecutorService service = Executors.newCachedThreadPool();
	private Map<Integer,Sence> senceMap = new ConcurrentHashMap<Integer, Sence>();
	
	long startTime = 0;
	
	@PostConstruct
	public void initSence(){
		for(int i=1001;i<=1010;i++){
			Sence sence = new Sence(i);
			service.execute(sence);
			senceMap.put(sence.getSenceId(), sence);
		}
	}
	
	public void enterMap(Player player,int senceId){
		int tmp = b.getAndIncrement();
		if(tmp==0){
			startTime = System.currentTimeMillis();
		}
//		int oldSenceId = player.getSenceId();
//		Sence oldSence = senceMap.get(oldSenceId);
//		if(oldSence!=null){
//			if(senceId==oldSenceId){
//				if(!oldSence.isInSence(player.getPlayerId())){
//					oldSence.addPlayer(player);
//					return;
//				}else{
//					return;
//				}
//			}
//		}else{
//			return;
//		}
//		Sence sence = senceMap.get(senceId);
//		if(sence==null){
//			return;
//		}
//		System.out.println("playerId:"+player.getPlayerId()+"切换到场景:"+senceId);
//		oldSence.removePlayer(player.getPlayerId());
//		sence.addPlayer(player);
	}
	
	public void move(Player player,int x,int y){
		int tmpA = a.getAndIncrement();
		if(tmpA==0){
			startTime = System.currentTimeMillis();
		}
//		eventBus.post(new MyEvent(x, y, player.getPlayerId()));
//		Sence sence = senceMap.get(player.getSenceId());
//		if(sence!=null){
//			PlayerMoveData data = new PlayerMoveData(player.getPlayerId(),x,y,sence.getSenceId());
//			sence.move(data);
//		}
//		System.out.println("》》》》》》》》》》》》》》》》》》》》》a="+a);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if(a.get()>=3000000){
			System.out.println(System.currentTimeMillis()-startTime+",>>>>>>>>>>>>>a="+a);
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
		event.getSession().closeSession();
	}

	@EventMethod
	private void processEvent1(MyEvent e){
		System.out.println("event11111:"+e.getPlayerId()+","+e.getX()+","+e.getY());
	}
	
}
