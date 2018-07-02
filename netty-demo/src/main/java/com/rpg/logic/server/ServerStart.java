package com.rpg.logic.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.rpg.framework.event.EventBus;
import com.rpg.framework.server.ServerStopEvent;
import com.rpg.logic.utils.PlayerNameUtil;


@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.rpg.logic.player.dao")
//@ImportResource("classpath:application.xml")
public class ServerStart {
	
	private final Log log = LogFactory.getLog(this.getClass());

	public static volatile boolean SERVER_RUN = false;

	private ClassPathXmlApplicationContext context;

//	public static void main(String[] args) {
//		SpringApplication.run(ServerStart.class, args);
//	}
	
	public static void main(String[] args) {
		ServerStart server = new ServerStart();
		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void init() throws Exception {
		log.info("execute init method !");
	}

	public void init(String[] args) throws Exception {
		log.info("execute init(args) method");
	}

	public void start() throws Exception {
		// 设置为jdk1.6的排序
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");


		Properties properties = new Properties();
		properties.load(new InputStreamReader(ServerStart.class.getResourceAsStream("/game_resources/system.properties"), "UTF-8"));
		System.setProperty("log4jdir", properties.getProperty("log4jdir"));

		context = new ClassPathXmlApplicationContext("game.xml");

		PlayerNameUtil.load();
		
//		DataReaderModel.init(context);
//
//		PlayerService playerService = (PlayerService) context.getBean("playerService");
//		MapService mapService = (MapService) context.getBean("mapService");

		// 生成玩家线程
//		for (int i = 0; i < PLAYER_HANDLE_THREAD_NUM; i++) {
//			PlayerThread playerThread = new PlayerThread();
//			playerThread.setName("PlayerThread_" + i);
//			playerThread.start();
//			playerService.getActionThread().add(playerThread);
//		}


		SERVER_RUN = true;

		log.warn("server start");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("输入命令:");
			try {
				String str = br.readLine().trim();
				if (str.equals("1")) {
					log.info("配置重载成功");
				} else if (str.equals("stop")) {
					// 停服
					SERVER_RUN = false;
					log.info("begin close server...");
					EventBus eventBus = context.getBean(EventBus.class);
					eventBus.post(new ServerStopEvent());

					Thread.sleep(2000L);

					log.info("server is close...");

					break;
				} else if (str.equals("001")) {
					
				} else {
					log.error("不接受此命令: " + str);
				}
			} catch (Exception e) {
				log.info("执行命令失败:遇到致命错误", e);
			}
		}

		System.exit(0);

	}

	public void stop() throws Exception {
		EventBus eventBus = context.getBean(EventBus.class);
		eventBus.post(new ServerStopEvent());
		log.info("execute stop method!");
	}

	public void destroy() throws Exception {
		log.info("execute destroy method!");
	}
}
