package com.rpg.framework.annotation;


/**
 * 监听用户断开socket
 */
@Listener
public interface ChannelCloseListener {

	/**
	 * 用户断开连接时触发的事件
	 * 
	 */
	public void onChannelClose(int playerId);
	
	public void onConn(long playerId);
}
