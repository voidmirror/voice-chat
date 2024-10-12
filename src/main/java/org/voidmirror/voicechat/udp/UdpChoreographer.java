package org.voidmirror.voicechat.udp;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.frontend.FrontSwitcher;
import org.voidmirror.voicechat.model.ConnectionData;
import org.voidmirror.voicechat.model.ConnectionState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class UdpChoreographer {

    public void startUdpServer(ConnectionData connectionData) {

        try {
            DatagramSocket datagramSocketFirstReceive = new DatagramSocket(connectionData.getLocalPort());
            final byte[] udpInputBuffer = new byte[128];
            DatagramPacket dpRec = new DatagramPacket(udpInputBuffer, udpInputBuffer.length);

            DatagramSocket datagramSocketFirstSend = new DatagramSocket();
            
            log.info("Server socket created");

            CompletableFuture<SocketAddress> start = CompletableFuture.supplyAsync(() -> {
                try {
                    datagramSocketFirstReceive.receive(dpRec);
                    connectionData.setRemoteHost(String.valueOf(dpRec.getSocketAddress()));
                    System.out.println(connectionData);
                    return dpRec.getSocketAddress();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
            start.whenCompleteAsync((address, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                }
                datagramSocketFirstReceive.close();
                datagramSocketFirstSend.close();
                Thread receiver = new Thread(new UdpReceiver(connectionData.getLocalPort()));
                Thread sender = new Thread(new UdpSender(connectionData.getRemotePort(), connectionData.getRemoteHost()));
                receiver.setDaemon(true);
                sender.setDaemon(true);
                receiver.start();
                sender.start();
            });

            Platform.runLater(() -> {
                ImageView iv = FrontSwitcher.getInstance()
                        .getImageViewFromHolder("ivServerConnectionStatus");
                iv.setImage(new Image(getClass().getResourceAsStream("/assets/done.png")));
                iv.setDisable(true);
            });


        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }

    public void startUdpClient(ConnectionData connectionData) {
        ConnectionState state = new ConnectionState();
        try {

            DatagramSocket datagramSocketFirstSend = new DatagramSocket();

            final byte[] udpOutputBuffer = "client hello".getBytes();

            DatagramPacket dpSend = new DatagramPacket(
                    udpOutputBuffer, udpOutputBuffer.length,
                    InetAddress.getByName(connectionData.getRemoteHost()), connectionData.getRemotePort());

            System.out.println("Client socket created");
            CompletableFuture<String> pingServer = CompletableFuture.supplyAsync(() -> {
                try {
                    datagramSocketFirstSend.send(dpSend);
                    return "connection initialized";
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
            pingServer.whenComplete((str, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                }
                datagramSocketFirstSend.close();
                Thread receiver = new Thread(new UdpReceiver(connectionData.getLocalPort()));
                Thread sender = new Thread(new UdpSender(connectionData.getRemotePort(), connectionData.getRemoteHost()));
                receiver.setDaemon(true);
                sender.setDaemon(true);
                receiver.start();
                sender.start();
            });

        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
