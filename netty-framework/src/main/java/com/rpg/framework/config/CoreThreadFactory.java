package com.rpg.framework.config;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CoreThreadFactory implements ThreadFactory {

	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;
	private final boolean daemon;

	public CoreThreadFactory(String namePrefix) {
		this(namePrefix, false);
	}
	
	public CoreThreadFactory(String namePrefix, boolean daemon) {
		SecurityManager localSecurityManager = System.getSecurityManager();
		this.group = ((localSecurityManager != null) ? localSecurityManager.getThreadGroup() : Thread.currentThread().getThreadGroup());
		this.namePrefix = namePrefix + "-" + "-thread-";
		this.daemon = daemon;
	}

	public Thread newThread(Runnable paramRunnable) {
		Thread localThread = new Thread(this.group, paramRunnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
		new Thread(paramRunnable, this.namePrefix + this.threadNumber.getAndIncrement());
		if (localThread.isDaemon())
			localThread.setDaemon(daemon);
		if (localThread.getPriority() != 5)
			localThread.setPriority(5);
		return localThread;
	}

}
