package com.rpg.logic.server;

import java.net.InetSocketAddress;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.handler.ServerHandler;
import com.rpg.framework.handler.dispatcher.IHandlerDispatcher;
import com.rpg.framework.handler.dispatcher.ServerHandlerExecutorDispatcher;
import com.rpg.framework.server.AbstractServer;


/**
 */
@Service
public class GameServer extends AbstractServer {
	
	private final Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private ServerConfig serverConfig;

	@Autowired
	private ServerHandler<Integer> gameServerHandler;



	@Autowired
	private IHandlerDispatcher dispatcher;

	@PostConstruct
	public void init() {
		Set<InetSocketAddress> addressSet = serverConfig.getInetSocketAddressSet();
		for (InetSocketAddress address : addressSet) {
			this.bind(address,dispatcher,gameServerHandler);
		}

	}


}
