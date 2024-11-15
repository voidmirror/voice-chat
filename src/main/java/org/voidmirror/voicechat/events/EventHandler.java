package org.voidmirror.voicechat.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.model.ChatMessage;

@Slf4j
public class EventHandler {

    private static EventHandler self = null;

    private EventHandler() {}

    public static EventHandler getInstance() {
        if (self == null) {
            self = new EventHandler();
        }
        return self;
    }

    ObjectMapper objectMapper = new ObjectMapper();

    public void handle(BaseEvent event) {
        switch (event.getType()) {
            case CHAT -> {
                ChatMessage chatMessage;
                try {
                    chatMessage = objectMapper.readValue(event.getPayload(), ChatMessage.class);
                    log.info("ChatMessage received: {}", chatMessage);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                log.info("CHAT Not implemented yet");
            }
            case TIME -> {
                log.info("TIME Not implemented yet");
            }
        }
    }
    
}
