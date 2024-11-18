package org.voidmirror.voicechat.service;

import lombok.Getter;
import org.voidmirror.voicechat.dto.TimeSyncDto;
import org.voidmirror.voicechat.events.BaseEvent;
import org.voidmirror.voicechat.events.EventService;
import org.voidmirror.voicechat.events.TimeSyncEvent;

import java.time.Clock;
import java.time.Duration;

public class ClockService {

    private static ClockService self = null;

    private ClockService() {
        eventService = EventService.getInstance();
        contactService = ContactService.getInstance();
        clock = Clock.systemUTC();
    }

    public static ClockService getInstance() {
        if (self == null) {
            self = new ClockService();
        }
        return self;
    }

    private final EventService eventService;
    private final ContactService contactService;

    @Getter
    private Clock clock;

    public void initClock(Clock baseClock, Duration millis) {
        clock = Clock.offset(baseClock, millis);
    }

    public void sendClockSync() {
        TimeSyncEvent event = new TimeSyncEvent();
        TimeSyncDto dto = new TimeSyncDto("current", System.currentTimeMillis());
        event.setTime(dto);
        eventService.sendEvent(event, contactService.getContactIp("current"));
    }

}
