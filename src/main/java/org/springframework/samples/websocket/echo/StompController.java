package org.springframework.samples.websocket.echo;

import org.springframework.messaging.GenericMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.messaging.PubSubHeaders;
import org.springframework.web.messaging.annotation.SubscribeEvent;

@Controller
public class StompController {


	@SubscribeEvent(value="/init")
	public Message<?> handleSubscribe(MessageChannel channel) throws Exception {

		PubSubHeaders headers = new PubSubHeaders();
		headers.setDestination("/topic/echo");
		Message<String> message = new GenericMessage<String>("simulated echo", headers.getMessageHeaders());

		channel.send(message);

//		channel.send("/topic/echo", Collections.singletonMap("c", "d"), MediaType.APPLICATION_JSON);

//		subscription.reply("message2:some other kind of data");
//		subscription.reply(Collections.singletonMap("a", "b"), MediaType.APPLICATION_JSON);

		headers = new PubSubHeaders();
		headers.setDestination("/init");
		return new GenericMessage<String>("message 1: some data", headers.getMessageHeaders());
	}

	@MessageMapping(value="/echo")
	public void handleEcho(String text, MessageChannel channel) throws Exception {

		PubSubHeaders headers = new PubSubHeaders();
		headers.setDestination("/topic/echo");
		Message<String> message = new GenericMessage<String>("Echoing: " + text, headers.getMessageHeaders());

		channel.send(message);
	}

}
