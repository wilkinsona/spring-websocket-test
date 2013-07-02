package org.springframework.samples.websocket.echo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.annotation.MessageMapping;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.messaging.annotation.MessageExceptionHandler;
import org.springframework.web.messaging.annotation.SubscribeEvent;
import org.springframework.web.messaging.support.WebMessageHeaderAccesssor;


@Controller
public class StompController {

    private final MessageChannel brokerChannel;

    private final MessageChannel clientChannel;


    @Autowired
    public StompController(@Qualifier("messageBrokerChannel") MessageChannel brokerChannel,
            @Qualifier("clientOutputChannel") MessageChannel clientChannel) {

        this.brokerChannel = brokerChannel;
        this.clientChannel = clientChannel;
    }


    @SubscribeEvent(value="/init")
    public String init() {

        WebMessageHeaderAccesssor headers = WebMessageHeaderAccesssor.create();
        headers.setDestination("/topic/echo");

        Message<String> message = MessageBuilder.withPayload("Echo init").copyHeaders(headers.toMap()).build();
        this.brokerChannel.send(message);

        return "Init data";
    }


    @MessageMapping(value="/echo")
    public void echoMessage(String text) {

        if (text.equals("exception")) {
            throw new IllegalStateException();
        }

        text = "Echo message: " + text;

        WebMessageHeaderAccesssor headers = WebMessageHeaderAccesssor.create();
        headers.setDestination("/topic/echo");
        Message<String> message = MessageBuilder.withPayload(text).copyHeaders(headers.toMap()).build();

        this.brokerChannel.send(message);
    }


    @MessageExceptionHandler
    public void handleMessageException(IllegalStateException ex) {

        String text = "Exception: " + ex.getMessage();

        WebMessageHeaderAccesssor headers = WebMessageHeaderAccesssor.create();
        headers.setDestination("/error");
        Message<String> message = MessageBuilder.withPayload(text).copyHeaders(headers.toMap()).build();

        System.out.println("Handled exception. Sending message: " + message);

        this.clientChannel.send(message);
    }


    @RequestMapping(value="/echo", method=RequestMethod.POST)
    @ResponseBody
    public void echoRequest(String text) {

        if (text.equals("exception")) {
            throw new IllegalStateException();
        }

        text = "Echo HTTP POST: " + text;
        WebMessageHeaderAccesssor headers = WebMessageHeaderAccesssor.create();
        headers.setDestination("/topic/echo");
        Message<String> message = MessageBuilder.withPayload(text).copyHeaders(headers.toMap()).build();

        this.brokerChannel.send(message);
    }


    @ExceptionHandler
    public void handleException(IllegalStateException ex) {

        String text = "Exception: " + ex.getMessage();

        WebMessageHeaderAccesssor headers = WebMessageHeaderAccesssor.create();
        headers.setDestination("/error");
        Message<String> message = MessageBuilder.withPayload(text).copyHeaders(headers.toMap()).build();

        this.brokerChannel.send(message);
    }

}