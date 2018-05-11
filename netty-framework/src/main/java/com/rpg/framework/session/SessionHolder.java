package com.rpg.framework.session;

import java.util.Set;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.google.protobuf.Message;

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
	public UserSession<K> put(K id,UserSession<K> userSession);
	
	public UserSession<K> put(Channel channel,UserSession<K> userSession);

	/**
	 * 获取用户通道
	 * 
	 * @param playerId
	 * @return
	 */
	public UserSession<K> get(K id);
	
	public UserSession<K> get(Channel channel);

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
