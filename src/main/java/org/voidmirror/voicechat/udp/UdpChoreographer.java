package org.voidmirror.voicechat.udp;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.frontend.FrontSwitcher;
import org.voidmirror.voicechat.misc.ByteUtils;
import org.voidmirror.voicechat.model.ConnectionData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class UdpChoreographer {

    private static UdpChoreographer self = null;

    private UdpChoreographer() {}

    public static UdpChoreographer getInstance() {
        if (self == null) {
            self = new UdpChoreographer();
        }
        return self;
    }

    @Getter
    private static boolean started = false;

    public void startVoice(ConnectionData connectionData) {

        System.out.println(connectionData);
        Thread receiver = new Thread(new UdpReceiver(connectionData.getVoicePort()));
        Thread sender = new Thread(new UdpSender(connectionData.getRemoteHost(), connectionData.getVoicePort()));
        receiver.setDaemon(true);
        sender.setDaemon(true);
        receiver.start();
        sender.start();

        Platform.runLater(() -> {
            ImageView iv = FrontSwitcher.getInstance()
                    .getImageViewFromHolder("ivServerConnectionStatus");
            iv.setImage(new Image(getClass().getResourceAsStream("/assets/done.png")));
            iv.setDisable(true);
        });

        started = true;

    }

}
