package org.springframework.samples.websocket.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.stomp.GenericToStompTransformer;
import org.springframework.integration.stomp.StompConnectHandlingChannelInterceptor;
import org.springframework.integration.stomp.StompToWebSocketTransformer;
import org.springframework.integration.stomp.WebSocketToStompTransformer;
import org.springframework.integration.stomp.service.AbstractMessageService;
import org.springframework.integration.stomp.service.AnnotationMessageService;
import org.springframework.integration.stomp.service.MessageServiceMessageHandler;
import org.springframework.integration.stomp.support.RelayStompService;
import org.springframework.integration.transformer.MessageTransformingHandler;
import org.springframework.integration.websocket.SessionManager;
import org.springframework.integration.websocket.StandardSessionManager;
import org.springframework.integration.websocket.WebSocketMessageDrivenEndpoint;
import org.springframework.integration.websocket.WebSocketOutboundHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsService;
import org.springframework.web.socket.sockjs.support.DefaultSockJsService;
import org.springframework.web.socket.sockjs.support.SockJsHttpRequestHandler;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages="org.springframework.samples")
public class WebConfig extends WebMvcConfigurerAdapter {

	private static final String[] rabbitDestinations = new String[] {
		"/exchange/**", "/queue/*", "/amq/queue/*", "/topic/*" };

	@Autowired
	private RootConfig rootConfig;


	@Bean
	public SimpleUrlHandlerMapping handlerMapping() {

		SockJsService sockJsService = new DefaultSockJsService(sockJsTaskScheduler());

		Map<String, Object> urlMap = new HashMap<String, Object>();
		urlMap.put("/stomp/echo/**", new SockJsHttpRequestHandler(sockJsService, stompWebSocketHandler()));

		SimpleUrlHandlerMapping hm = new SimpleUrlHandlerMapping();
		hm.setOrder(-1);
		hm.setUrlMap(urlMap);

		return hm;
	}

	@Bean
	public SessionManager sessionManager() {
		return new StandardSessionManager();
	}

	@Bean
	public WebSocketHandler stompWebSocketHandler() {
		return new WebSocketMessageDrivenEndpoint(webSocketInputChannel(), sessionManager());
	}

	@Bean
	public DirectChannel webSocketInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public PublishSubscribeChannel stompInputChannel() {
		PublishSubscribeChannel channel = new PublishSubscribeChannel();
		// TODO This isn't required when CONNECT is being relayed to Rabbit. Configuration needs to be better.
		// channel.addInterceptor(new StompConnectHandlingChannelInterceptor(stompOutputChannel()));
		return channel;
	}

	@Bean
	public DirectChannel genericOutputChannel() {
		return new DirectChannel();
	}

	@Bean
	public DirectChannel genericRelayChannel() {
		return new DirectChannel();
	}

	@Bean
	public DirectChannel stompOutputChannel() {
		return new DirectChannel();
	}

	@Bean
	public DirectChannel webSocketOutputChannel() {
		return new DirectChannel();
	}

	@Bean
	public AbstractMessageService messageService() {
		return new AnnotationMessageService(genericOutputChannel(), genericRelayChannel());
	}

	@Bean
	public RelayStompService relayStompService() {
		RelayStompService relayStompService = new RelayStompService(stompRelayTaskScheduler(), stompOutputChannel());
		relayStompService.setAllowedDestinations(rabbitDestinations);
		return relayStompService;
	}

	@Bean
	public MessageHandler inputMessageHandler() {
		MessageServiceMessageHandler handler = new MessageServiceMessageHandler(messageService());
		stompInputChannel().subscribe(handler);

		return handler;
	}

	@Bean MessageHandler relayMessageHandler() {
		MessageServiceMessageHandler handler = new MessageServiceMessageHandler(relayStompService());
		stompInputChannel().subscribe(handler);

		return handler;
	}

	@Bean
	public MessageTransformingHandler webSocketToStompTransformer() {
		MessageTransformingHandler handler = new MessageTransformingHandler(new WebSocketToStompTransformer());
		// When a heartbeat frame is received, no message is produced
		handler.setRequiresReply(false);
		handler.setOutputChannel(stompInputChannel());

		webSocketInputChannel().subscribe(handler);

		return handler;
	}

	@Bean
	public MessageTransformingHandler genericToStompTransformer() {
		MessageTransformingHandler handler = new MessageTransformingHandler(new GenericToStompTransformer());
		handler.setOutputChannel(stompOutputChannel());
		genericOutputChannel().subscribe(handler);
		return handler;
	}

	@Bean
	public MessageTransformingHandler stompToWebSocketTransformer() {
		MessageTransformingHandler handler = new MessageTransformingHandler(new StompToWebSocketTransformer());
		handler.setOutputChannel(webSocketOutputChannel());
		stompOutputChannel().subscribe(handler);
		return handler;
	}

	@Bean
	public MessageHandler outputMessageHandler() {
		WebSocketOutboundHandler handler = new WebSocketOutboundHandler(sessionManager());
		webSocketOutputChannel().subscribe(handler);
		return handler;
	}

//	@Bean
//	public RelayStompService rabbitStompService() {
//		RelayStompService service = new RelayStompService(reactor(), stompRelayTaskScheduler());
//		service.setAllowedDestinations(rabbitDestinations);
//		return service;
//	}

//	@Bean
//	public SimpleStompService simpleStompService() {
//		return new SimpleStompService(reactor());
//	}

	@Bean
	public ThreadPoolTaskScheduler sockJsTaskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setThreadNamePrefix("SockJS-");
		return taskScheduler;
	}

	@Bean
	public ThreadPoolTaskScheduler stompRelayTaskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setThreadNamePrefix("StompRelay-");
		return taskScheduler;
	}

	// Allow serving HTML files through the default Servlet

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

}
