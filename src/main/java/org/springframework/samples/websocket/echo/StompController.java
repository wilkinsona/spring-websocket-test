package org.springframework.samples.websocket.echo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.messaging.PubSubChannelRegistry;
import org.springframework.web.messaging.annotation.MessageExceptionHandler;
import org.springframework.web.messaging.annotation.SubscribeEvent;
import org.springframework.web.messaging.support.PubSubMessageBuilder;


@Controller
public class StompController {

	private final MessageChannel<Message<?>> brokerChannel;

	private final MessageChannel<Message<?>> clientChannel;


	@Autowired
	public StompController(PubSubChannelRegistry<Message<?>, MessageHandler<Message<?>>> channelRegistry) {
		this.brokerChannel = channelRegistry.getMessageBrokerChannel();
		this.clientChannel = channelRegistry.getClientOutputChannel();
	}


	@SubscribeEvent(value="/init")
	public Message<?> handleInit() {

		Message<String> message = PubSubMessageBuilder.withPayload("fake echo").destination("/topic/echo").build();
		this.brokerChannel.send(message);

		return PubSubMessageBuilder.withPayload("init data").build();
	}

	@MessageMapping(value="/echo")
	public void handleEchoMessage(String text) {

		if (text.equals("exception")) {
			throw new IllegalStateException();
		}

		Message<String> message = PubSubMessageBuilder.withPayload("Echo: " + text)
				.destination("/topic/echo").build();

		this.brokerChannel.send(message);
	}

	@MessageExceptionHandler
	public void handle(IllegalStateException ex) {

		Message<String> message = PubSubMessageBuilder.withPayload("Exception: " + ex.toString())
				.destination("/error").build();

		this.clientChannel.send(message);
	}

	@RequestMapping(value="/echo", method=RequestMethod.POST)
	@ResponseBody
	public void handleEcho(String text) {

		Message<String> message = PubSubMessageBuilder.withPayload("Echo (HTTP POST): " + text)
				.destination("/topic/echo").build();

		this.brokerChannel.send(message);
	}

}
