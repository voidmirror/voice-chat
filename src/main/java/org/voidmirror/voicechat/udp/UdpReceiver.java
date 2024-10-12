package org.voidmirror.voicechat.udp;

import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

@Slf4j
public class UdpReceiver implements Runnable{
    public UdpReceiver(int port) {
        this.port = port;
    }

    private final int port;
    private SourceDataLine speakers;

    @Override
    public void run() {

        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            final byte[] udpInputBuffer = new byte[1024];

            DatagramPacket dp = new DatagramPacket(udpInputBuffer, udpInputBuffer.length);

            AudioFormat format = new AudioFormat(16000, 16, 2, true, true);

            DataLine.Info inInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(inInfo);
            speakers.open(format);
            speakers.start();

            Thread speakerThread = new Thread(() -> {
                int bufferVarInput = udpInputBuffer.length;
                try {
                    while (datagramSocket.isBound()) {  // TODO: isBound() / isConnected() ?
                        datagramSocket.receive(dp);

                        // TODO: uncomment
                        speakers.write(
                                dp.getData(),
                                0,
                                bufferVarInput
                        );
                    }
                } catch (IOException e) {
                    log.error("### IO read exception");
                }
            });
            speakerThread.setDaemon(true);
            speakerThread.start();

            log.info("UdpReceiver started");


        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
