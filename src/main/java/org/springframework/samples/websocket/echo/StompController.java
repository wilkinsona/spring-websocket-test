package org.springframework.samples.websocket.echo;

import org.springframework.messaging.GenericMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.messaging.annotation.SubscribeEvent;

@Controller
public class StompController {

	@SubscribeEvent("/init")
	public Message<String> handleSubscribe() throws Exception {
		// Response is automatically sent to the destination that's being subscribed to

		// TODO Sending messages to the broker
//		broker.send("/topic/echo", "echoed data", MediaType.TEXT_PLAIN);
//		broker.send("/topic/echo", Collections.singletonMap("c", "d"), MediaType.APPLICATION_JSON);
		return new GenericMessage<String>("message1:some data");
	}

	@MessageMapping("/echo")
	public void handleEcho(String message) throws Exception {
		System.out.println("Handling " + message);
	}

}
