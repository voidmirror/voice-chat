package org.voidmirror.voicechat.service;

import org.voidmirror.voicechat.events.ChatEvent;
import org.voidmirror.voicechat.events.EventService;
import org.voidmirror.voicechat.events.EventType;
import org.voidmirror.voicechat.model.ChatMessage;

public class MessageService {

    private static MessageService self = null;

    private MessageService() {
        eventService = EventService.getInstance();
        contactService = ContactService.getInstance();
    }

    public static MessageService getInstance() {
        if (self == null) {
            self = new MessageService();
        }
        return self;
    }

    private final EventService eventService;
    private final ContactService contactService;

    /**
     *
     * @param receiver value has to be "current" while connection is p2p
     * @param text message
     */
    public void sendMessage(String receiver, String text) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(text);
//        chatMessage.setSender();
//        chatMessage.setReceiver();
        ChatEvent chatEvent = new ChatEvent();
        chatEvent.setType(EventType.CHAT);
        chatEvent.setChatMessage(chatMessage);
        eventService.sendEvent(chatEvent, contactService.getContactIp(receiver));
    }

}
