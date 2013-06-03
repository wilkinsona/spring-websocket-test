package org.springframework.samples.websocket.echo;

import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.messaging.MessageBroker;
import org.springframework.web.messaging.MessageMapping;
import org.springframework.web.messaging.MessageType;
import org.springframework.web.messaging.Subscription;

@Controller
public class StompController {


	@MessageMapping(value="/init", messageType=MessageType.SUBSCRIBE)
	public void handleSubscribe(Subscription subscription, MessageBroker broker) throws Exception {

		subscription.reply("message1:some data");
		subscription.reply("message2:some other kind of data");
		subscription.reply(Collections.singletonMap("a", "b"), MediaType.APPLICATION_JSON);

		broker.send("/topic/echo", "echoed data", MediaType.TEXT_PLAIN);
		broker.send("/topic/echo", Collections.singletonMap("c", "d"), MediaType.APPLICATION_JSON);
	}

	@MessageMapping(value="/echo", messageType=MessageType.SEND)
	public void handleEcho(String message, MessageBroker broker) throws Exception {

		broker.send("/topic/echo", "Echoing: " + message, MediaType.TEXT_PLAIN);
	}

}
