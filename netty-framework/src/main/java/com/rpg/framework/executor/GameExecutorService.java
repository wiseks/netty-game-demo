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

	public Future<?> execute(Object id, Runnable runnable) {
		int hash = hash(id.hashCode());
		int threadIndex = hash % services.length;
		ExecutorService service = services[threadIndex];
		return service.submit(runnable);
	}

	private static int hash(int h) {
		h += (h << 15) ^ 0xffffcd7d;
		h ^= (h >>> 10);
		h += (h << 3);
		h ^= (h >>> 6);
		h += (h << 2) + (h << 14);
		return h ^ (h >>> 16);
	}

	@PostConstruct
	private void init() {
		services = new ExecutorService[serverConfig.getThreadNum()];
//		for(int i=0;i<services.length;i++){
//			services[i] = Executors.newSingleThreadExecutor();
//		}

		for (int i = 0; i < services.length; i++) {
			services[i] = Executors.newSingleThreadExecutor(new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					return new Thread("gameHandlerPool-thread-" + index.incrementAndGet());
				}
			});
		}
	}
}
