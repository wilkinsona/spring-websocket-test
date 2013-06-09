package org.springframework.samples.websocket.echo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.GenericMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.messaging.annotation.SubscribeEvent;

@Controller
public class StompController {


	@SubscribeEvent(value="/init")
	public Message<?> handleSubscribe(MessageChannel channel) throws Exception {

		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("destination", "/topic/echo");
		Message<String> message = new GenericMessage<String>("simulated echo", headers);

		channel.send(message);

//		channel.send("/topic/echo", Collections.singletonMap("c", "d"), MediaType.APPLICATION_JSON);

//		subscription.reply("message2:some other kind of data");
//		subscription.reply(Collections.singletonMap("a", "b"), MediaType.APPLICATION_JSON);

		headers = new HashMap<String, Object>();
		headers.put("destination", "/init");
		return new GenericMessage<String>("message 1: some data", headers);
	}

	@MessageMapping(value="/echo")
	public void handleEcho(String text, MessageChannel channel) throws Exception {

		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("destination", "/topic/echo");
		Message<String> message = new GenericMessage<String>("Echoing: " + text, headers);

		channel.send(message);
	}

}
