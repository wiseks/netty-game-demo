package com.rpg.framework.session;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.Message;
import com.rpg.framework.code.Response;
import com.rpg.framework.config.CoreThreadFactory;
import com.rpg.framework.event.EventBus;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * 用户管理器
 */
@Service
public class SessionHolderImpl<K> implements SessionHolder<K> {

	private final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private EventBus eventBus;

	private static final long SHOW_TIME = 30; // s

	protected final ConcurrentMap<K, UserSession<K>> sessions = new ConcurrentHashMap<K, UserSession<K>>();

	protected final ConcurrentHashMap<Channel, UserSession<K>> c2s = new ConcurrentHashMap<Channel, UserSession<K>>();

	protected final ConcurrentSkipListSet<Long> loginIdSet = new ConcurrentSkipListSet<Long>();

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10,
			new CoreThreadFactory("LockMonitorPool", true));

	private final Runnable monitorRunnable = new Runnable() {
		public void run() {
			scheduler.schedule(this, SHOW_TIME, TimeUnit.SECONDS);
		}
	};

	private long getNextClearTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTimeInMillis();
	}

	private final Runnable clearLoginIdsRunnable = new Runnable() {
		public void run() {
			loginIdSet.clear();
			long time = getNextClearTime() - System.currentTimeMillis();
			scheduler.schedule(this, time, TimeUnit.MILLISECONDS);
		}
	};

	public SessionHolderImpl() {
		scheduler.schedule(monitorRunnable, 30, TimeUnit.SECONDS);
		long time = getNextClearTime() - System.currentTimeMillis();
		scheduler.schedule(clearLoginIdsRunnable, time, TimeUnit.MILLISECONDS);

		scheduler.scheduleWithFixedDelay(new Runnable() {

			public void run() {
				log.info("player online size:" + sessions.size());
			}
		}, 120, 120, TimeUnit.SECONDS);
	}

	public UserSession<K> put(K id, UserSession<K> userSession) {
		sessions.putIfAbsent(userSession.getId(), userSession);
		c2s.putIfAbsent(userSession.getChannel(), userSession);
		return userSession;
	}

	public UserSession<K> put(Channel channel, UserSession<K> userSession) {
		c2s.putIfAbsent(userSession.getChannel(), userSession);
		return userSession;
	}

	public UserSession<K> get(Channel channel) {
		return c2s.get(channel);
	}

	public UserSession<K> get(K id) {
		return sessions.get(id);
	}

	public int remove(K id) {
		UserSession<K> session = sessions.remove(id);
		if (session != null)
			c2s.remove(session.getChannel());
		return remove(session);
	}

	public synchronized int removeAll() {
		int result = 0;
		for (K key : sessions.keySet()) {
			UserSession<K> session = sessions.get(key);
			c2s.remove(session.getChannel());
			sessions.remove(key);
			result += remove(session);
		}
		return result;
	}

	private int remove(UserSession<K> session) {
		try {
			if (session != null) {
				if (session.getChannel().isActive())
					session.getChannel().disconnect();
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean isOnline(K id) {
		return sessions.containsKey(id);
	}

	public int getOnlineCount() {
		return sessions.size();
	}

	public void sendMsg(K toId, Message message) {
		try {
			this.send(toId, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMsg2All(Message message) {
		try {
			for (K id : sessions.keySet())
				this.send(id, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void send(K id, Message message) {
		UserSession<K> session = this.get(id);
		if (session == null)
			return;
		try {
			Channel c = session.getChannel();
			if (null != c && c.isActive()) {
				Response response = Response.createResponse(message);
				c.write(response);
			} else {
				sessions.remove(session.getId(), session);
			}
		} catch (Exception e) {
			sessions.remove(session.getId(), session);
			e.printStackTrace();
		}
	}

	public Set<K> getAll() {
		return sessions.keySet();
	}

	public boolean isOnline(Channel channel) {
		return c2s.containsKey(channel);
	}

	public void onChannelClose(ChannelHandlerContext ctx) {
		UserSession<K> session = this.get(ctx.channel());
		if (session != null) {
			K id = session.getId();
			if(id!=null){
				this.remove(id);
				eventBus.post(new ChannelCloseEvent<K>(session));
				System.out.println(this.getClass().getName()+"| closeService onChannelClose...id="+id);
			}
			this.removeChannel(session);
		}
	}

	@Override
	public void removeChannel(UserSession<K> session) {
		this.c2s.remove(session.getChannel());
	}

}
