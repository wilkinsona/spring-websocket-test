package org.springframework.samples.websocket.echo;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.annotation.MessageMapping;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.messaging.PubSubHeaders;
import org.springframework.web.messaging.annotation.SubscribeEvent;

@Controller
public class StompController {


	@SubscribeEvent(value="/init")
	public Message<?> handleSubscribe(MessageChannel<Message<?>> channel) throws Exception {

		PubSubHeaders headers = PubSubHeaders.create();
		headers.setDestination("/topic/echo");
		Message<String> message = MessageBuilder.fromPayloadAndHeaders("simulated echo", headers.toMessageHeaders()).build();

		channel.send(message);

//		channel.send("/topic/echo", Collections.singletonMap("c", "d"), MediaType.APPLICATION_JSON);

//		subscription.reply("message2:some other kind of data");
//		subscription.reply(Collections.singletonMap("a", "b"), MediaType.APPLICATION_JSON);

		headers = PubSubHeaders.create();
		headers.setDestination("/init");
		message = MessageBuilder.fromPayloadAndHeaders("message 1: some data", headers.toMessageHeaders()).build();
		return message;
	}

	@MessageMapping(value="/echo")
	public void handleEcho(String text, MessageChannel<Message<?>> channel) throws Exception {

		PubSubHeaders headers = PubSubHeaders.create();
		headers.setDestination("/topic/echo");
		Message<String> message = MessageBuilder.fromPayloadAndHeaders(
				"Echoing: " + text, headers.toMessageHeaders()).build();

		channel.send(message);
	}

}
