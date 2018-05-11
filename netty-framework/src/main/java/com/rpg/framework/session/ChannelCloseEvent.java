package com.rpg.framework.session;

/**
 * 连接关闭事件
 * @author wudeji
 *
 * @param <K>
 */
public class ChannelCloseEvent<K> {

	private UserSession<K> session;

	public ChannelCloseEvent(UserSession<K> session) {
		this.session = session;
	}

	public UserSession<K> getSession() {
		return session;
	}

}
