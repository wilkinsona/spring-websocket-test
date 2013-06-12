package org.springframework.samples.websocket.echo;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.messaging.GenericMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.messaging.PubSubHeaders;
import org.springframework.web.messaging.annotation.SubscribeEvent;

@Controller
public class StompController {

	@SubscribeEvent("/init")
	public Message<String> handleSubscribe(MessageChannel messageChannel) throws Exception {
		// Messages sent to messageChannel are sent to the broker via the relay service
		PubSubHeaders textHeaders = new PubSubHeaders();
		textHeaders.setDestination("/topic/echo");
		textHeaders.setContentType(MediaType.TEXT_PLAIN);
		messageChannel.send(new GenericMessage<String>("echoed data", textHeaders.getMessageHeaders()));

		// You can send JSON too
		PubSubHeaders jsonHeaders = new PubSubHeaders();
		jsonHeaders.setDestination("/topic/echo");
		jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
		messageChannel.send(new GenericMessage<Map<String, String>>(Collections.singletonMap("c", "d"), jsonHeaders.getMessageHeaders()));

		// If no destination is specified, a returned message is automatically sent to the destination that's being subscribed to
		return new GenericMessage<String>("message1:some data");
	}

	@MessageMapping("/echo")
	public void handleEcho(String message, MessageChannel messageChannel) throws Exception {
		PubSubHeaders headers = new PubSubHeaders();
		headers.setDestination("/topic/echo");

		Message<String> echoed = new GenericMessage<String>("Echoing: " + message, headers.getMessageHeaders());

		messageChannel.send(echoed);
	}

}
