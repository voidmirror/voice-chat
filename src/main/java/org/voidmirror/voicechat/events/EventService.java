package org.voidmirror.voicechat.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.model.ChatMessage;
import org.voidmirror.voicechat.misc.ComponentInitializer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class EventService {

    private static EventService self = null;

    private EventService() {}

    public static EventService getInstance() {
        if (self == null) {
            self = new EventService();
        }
        return self;
    }

    private final int port = 9035;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void sendEvent(BaseEvent event, String host) {
        try {
            BufferedWriter out;
            try (Socket socket = new Socket(host, port)) {
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                out.write(objectMapper.writeValueAsString(event));
                out.write("\n");
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void enableEventReceiver() {
        Thread eventReceiver = new Thread(() -> {
            ConcurrentLinkedQueue<BaseEvent> eventQueue = ComponentInitializer.getInstance().getEventQueue();
            try (ServerSocket eventSocket = new ServerSocket(port)) {
                while (Thread.currentThread().isAlive()) {
                    Socket socket = eventSocket.accept();
                    try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                    ) {
                        String res;
                        while (!in.ready()); // TODO: change if possible
                        while (in.ready()) {
                            res = in.readLine();
                            eventQueue.add(objectMapper.readValue(res, BaseEvent.class));
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Service layer Server error: {}", e.getMessage());
            }

        });
        eventReceiver.setDaemon(true);
        eventReceiver.start();
    }

}
