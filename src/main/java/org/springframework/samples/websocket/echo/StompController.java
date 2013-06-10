package org.springframework.samples.websocket.echo;

import org.springframework.messaging.GenericMessage;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.messaging.annotation.SubscribeEvent;

@Controller
public class StompController {

	@SubscribeEvent("/init")
	public Message<String> handleSubscribe() throws Exception {
		System.out.println("Handling subscription to /init");
		return new GenericMessage<String>("message1:some data");

//		broker.send("/topic/echo", "echoed data", MediaType.TEXT_PLAIN);
//		broker.send("/topic/echo", Collections.singletonMap("c", "d"), MediaType.APPLICATION_JSON);
	}

//	@MessageMapping(value="/echo", messageType=MessageType.SEND)
//	public void handleEcho(String message, MessageBroker broker) throws Exception {
//
//		broker.send("/topic/echo", "Echoing: " + message, MediaType.TEXT_PLAIN);
//	}

}
