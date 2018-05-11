package com.rpg.framework.config;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;

public class IpAddress {

	private String ip;
	private Set<Integer> ports;

	public IpAddress() {
	}

	public IpAddress(String ip, String ports) {
		super();
		this.ip = ip;
		String[] portStrs = ports.split(",");
		this.ports = new HashSet<Integer>();
		for (String portStr : portStrs) {
			if (NumberUtils.isNumber(portStr))
				this.ports.add(NumberUtils.toInt(portStr));
		}
	}

	public Set<InetSocketAddress> createInetSocketAddress() {
		Set<InetSocketAddress> ipSet = new HashSet<InetSocketAddress>();
		for (int port : ports)
			ipSet.add(new InetSocketAddress(ip, port));
		return ipSet;
	}

}
