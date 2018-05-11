package com.rpg.logic.player.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rpg.framework.code.Response;
import com.rpg.framework.session.SessionHolder;
import com.rpg.framework.session.UserSession;
import com.rpg.logic.common.ErrorCode;
import com.rpg.logic.map.service.SenceService;
import com.rpg.logic.player.dao.PlayerDao;
import com.rpg.logic.player.dao.UserDao;
import com.rpg.logic.player.domain.Player;
import com.rpg.logic.player.domain.User;
import com.rpg.logic.utils.PlayerNameUtil;

import xn.protobuf.player.PlayerMsg.PlayerCreateNewResMsg_16012;
import xn.protobuf.player.PlayerMsg.PlayerDataNew;
import xn.protobuf.player.PlayerMsg.PlayerLoginNewResMsg_16014;

@Service
public class PlayerService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PlayerDao playerDao;
	
	@Autowired
	private SessionHolder<Integer> sessionHolder;
	
	@Autowired
	private SenceService senceService;
	
	private Map<Integer,Player> players = new ConcurrentHashMap<Integer, Player>();
	
	
	public Player getPlayer(int playerId){
		Player player = players.get(playerId);
		if(player==null){
			playerDao.getByPlayerId(playerId);
		}
		return player;
	}
	
	public Response createPlayer(String user){
		Player player = new Player();
		String name = PlayerNameUtil.randomName();
		player.setName(name);
		player.setUser(user);
		playerDao.createPlayer(player);
		System.out.println(player.getPlayerId());
		PlayerCreateNewResMsg_16012.Builder res = PlayerCreateNewResMsg_16012.newBuilder();
		res.setPlayerData(this.createPlayerData(player));
		return Response.createResponse(res.build());
	}
	
	private PlayerDataNew createPlayerData(Player player){
		PlayerDataNew.Builder data = PlayerDataNew.newBuilder();
		data.setPlayerId(player.getPlayerId());
		data.setName(player.getName());
		data.setUser(player.getUser());
		return data.build();
	}
	
	public Response login(ChannelHandlerContext channelContext,String user){
		PlayerLoginNewResMsg_16014.Builder res = PlayerLoginNewResMsg_16014.newBuilder();
		Player player = playerDao.getByUser(user);
		if(player==null){
			return Response.createErrResponse(res.build());
		}
		players.put(player.getPlayerId(), player);
//		Attachment attachment = (Attachment)channelContext.getAttachment();
//		attachment.setPlayerId(player.getPlayerId());
		UserSession<Integer> userSession = sessionHolder.get(channelContext.getChannel());
		userSession.setId(player.getPlayerId());
		sessionHolder.put(userSession.getId(),userSession);
		player.setSessionHolder(sessionHolder);
		senceService.enterMap(player, 1001);
		res.setPlayerData(this.createPlayerData(player));
		System.out.println("player login,id="+player.getPlayerId()+",name="+player.getName());
		return Response.createResponse(res.build());
	}
	
	public void getUser(){
		User user = userDao.getById(1);
		System.out.println(user.getName());
		
	}
}
