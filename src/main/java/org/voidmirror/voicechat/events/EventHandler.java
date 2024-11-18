package org.voidmirror.voicechat.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.dto.TimeSyncDto;
import org.voidmirror.voicechat.frontend.FrontSwitcher;
import org.voidmirror.voicechat.misc.RuntimeConfig;
import org.voidmirror.voicechat.model.ChatMessage;
import org.voidmirror.voicechat.model.ConnectionData;
import org.voidmirror.voicechat.service.ClockService;
import org.voidmirror.voicechat.service.ContactService;
import org.voidmirror.voicechat.udp.UdpChoreographer;

import java.time.Clock;
import java.time.Duration;

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
            case CONNECT -> {
                ClockService.getInstance().sendClockSync();
                log.info("Time Sync sent");
                BaseEvent receivedConnect = new BaseEvent();
                receivedConnect.setType(EventType.RECEIVED_CONNECT);

                ConnectionData connectionData = new ConnectionData();
                connectionData.setRemoteHost(ContactService.getInstance().getContactIp("current"));
                connectionData.setVoicePort(RuntimeConfig.getInstance().getVoicePort());

                EventService.getInstance().sendEvent(receivedConnect, connectionData.getRemoteHost());

                UdpChoreographer.getInstance().startVoice(connectionData);
            }
            case RECEIVED_CONNECT -> {
                ClockService.getInstance().sendClockSync();
                log.info("Time Sync sent");

                ConnectionData connectionData = new ConnectionData();
                connectionData.setRemoteHost(ContactService.getInstance().getContactIp("current"));
                UdpChoreographer.getInstance().startVoice(connectionData);
            }
            case CHAT -> {
                ChatMessage chatMessage;
                try {
                    chatMessage = objectMapper.readValue(event.getPayload(), ChatMessage.class);
                    log.info("ChatMessage received: {}", chatMessage);
                } catch (JsonProcessingException e) {
                    log.error("ChatMessage receive mapper error: {}", e.getMessage());
                }
                log.info("CHAT Not implemented yet");
            }
            case TIME -> {
                ClockService clockService = ClockService.getInstance();
                TimeSyncDto dto;
                try {
                    dto = objectMapper.readValue(event.getPayload(), TimeSyncDto.class);
                    clockService.initClock(Clock.systemUTC(),
                            Duration.ofMillis(System.currentTimeMillis() - dto.getTime())); // current time - send time
                } catch (JsonProcessingException e) {
                    log.error("TimeSyncDto receive mapper error: {}", e.getMessage());
                }
            }
        }
    }
    
}
