package org.voidmirror.voicechat.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ChatMessage {

    private String sender;
    private String receiver;
    private String message;

}
