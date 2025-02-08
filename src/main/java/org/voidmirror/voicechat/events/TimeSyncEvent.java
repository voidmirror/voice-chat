package org.voidmirror.voicechat.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.dto.TimeSyncDto;

@Slf4j
public class TimeSyncEvent extends BaseEvent{

    public void setTime(TimeSyncDto dto) {
        setType(EventType.TIME);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            setPayload(objectMapper.writeValueAsString(dto)); // TODO: change "current" to user name
        } catch (JsonProcessingException e) {
            log.error("TimeSyncDto send mapper error: {}", e.getMessage());
        }
    }

}
