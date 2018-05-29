package com.rpg.framework.session;

import java.util.Set;


import com.google.protobuf.Message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * 保持所有建立链接的会话信息
 * 
 */
public interface SessionHolder<K> {

	/**
	 * 添加一个新用户，账号重复时会踢掉对方，保证唯一
	 * 
	 * @param playerId
	 * @param userSession
	 * @return
	 */
	public AbstractUserSession<K> put(K id,AbstractUserSession<K> userSession);
	
	public AbstractUserSession<K> put(Channel channel,AbstractUserSession<K> userSession);

	/**
	 * 获取用户通道
	 * 
	 * @param playerId
	 * @return
	 */
	public AbstractUserSession<K> get(K id);
	
	public AbstractUserSession<K> get(Channel channel);
	
	public void removeChannel(AbstractUserSession<K> session);

	/**
	 * 移除一个用户
	 * 
	 * @param playerId
	 * @return
	 */
	public int remove(K id);

	/**
	 * 踢下所有用户
	 * 
	 * @return
	 */
	public int removeAll();

	/**
	 * 判断用户是否已经登录
	 * 
	 * @param playerId
	 * @return
	 */
	public boolean isOnline(K id);
	
	public boolean isOnline(Channel channel);

	/**
	 * 获取在线总数
	 * 
	 * @return
	 */
	public int getOnlineCount();

	public void sendMsg(K toId, Message message);

	public void sendMsg2All(Message message);

	public void onChannelClose(ChannelHandlerContext ctx);

	/**
	 * 获取在线PlayerId
	 */
	public Set<K> getAll();

}
