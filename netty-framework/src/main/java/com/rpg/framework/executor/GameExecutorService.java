package com.rpg.framework.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rpg.framework.config.ServerConfig;

@Service
public class GameExecutorService {
	
	@Autowired
	private ServerConfig serverConfig;

	private ExecutorService[] services;
	
	private AtomicInteger index = new AtomicInteger();
	
	public Future<?> execute(Object object, Runnable runnable) {
		int hash = object.hashCode();
		int threadIndex = hash % services.length;
		ExecutorService service = services[threadIndex];
		return service.submit(runnable);
	}
	
	@PostConstruct
	private void init() {
		services = new ExecutorService[serverConfig.getThreadNum()];
//		for(int i=0;i<services.length;i++){
//			services[i] = Executors.newSingleThreadExecutor();
//		}
		
		for(int i= 0;i < services.length; i++){
			services[i] = Executors.newSingleThreadExecutor(new ThreadFactory() {
				
				@Override
				public Thread newThread(Runnable r) {
					return new Thread("gameHandlerPool-thread-"+index.incrementAndGet());
				}
			});
		}
	}
}
