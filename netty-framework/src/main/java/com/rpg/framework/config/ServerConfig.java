package com.rpg.framework.config;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;

/**
 */
public class ServerConfig {
	
	public static final int HEAD_SZIE = 9;

	private int threadNum = 500;

	/**
	 * 是否调试
	 */
	private boolean debug;

	/**
	 * 服务发布的域名
	 */
	private String domain;

	/**
	 * 服务端口
	 */
	private Set<Integer> ports = null;

	/**
	 * 綁定ip
	 */
	private Set<IpAddress> addresseSet = null;

	public void setAddresseSet(Set<IpAddress> addresseSet) {
		this.addresseSet = addresseSet;
	}

	public Set<InetSocketAddress> getInetSocketAddressSet() {
		Set<InetSocketAddress> socketAddresseSet = new HashSet<InetSocketAddress>();
		if (this.addresseSet != null && !this.addresseSet.isEmpty()) {
			for (IpAddress ip : addresseSet)
				socketAddresseSet.addAll(ip.createInetSocketAddress());
		} else {
			for (Integer port : ports)
				socketAddresseSet.add(new InetSocketAddress(port));
		}
		return socketAddresseSet;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setPorts(String ports) {
		String[] portStrs = ports.split(",");
		this.ports = new HashSet<Integer>();
		for (String portStr : portStrs) {
			if (NumberUtils.isNumber(portStr))
				this.ports.add(NumberUtils.toInt(portStr));
		}
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public String getSid() {
		return getDomain().split("\\.")[0];
	}

}
