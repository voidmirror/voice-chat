package org.voidmirror.voicechat.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BaseEvent {

    private ObjectMapper objectMapper = new ObjectMapper();

    private EventType type = null;
    private String payload = null;

}
