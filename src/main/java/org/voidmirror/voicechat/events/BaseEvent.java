package org.voidmirror.voicechat.events;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BaseEvent {

    private EventType type = null;
    private String payload = null;

}
