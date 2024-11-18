package org.voidmirror.voicechat.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.model.ChatMessage;

@Slf4j
public class ChatEvent extends BaseEvent {

    public void setChatMessage(ChatMessage chatMessage) {
        try {
            this.setPayload(getObjectMapper().writeValueAsString(chatMessage));
        } catch (JsonProcessingException e) {
            log.error("ChatMessage send mapper error: {}", e.getMessage());
        }
        this.setType(EventType.CHAT);
    }

}
