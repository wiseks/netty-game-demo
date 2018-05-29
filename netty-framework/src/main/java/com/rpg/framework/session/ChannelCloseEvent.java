package com.rpg.framework.session;

/**
 * 连接关闭事件
 * @author wudeji
 *
 * @param <K>
 */
public class ChannelCloseEvent<K> {

	private AbstractUserSession<K> session;

	public ChannelCloseEvent(AbstractUserSession<K> session) {
		this.session = session;
	}

	public AbstractUserSession<K> getSession() {
		return session;
	}

}
