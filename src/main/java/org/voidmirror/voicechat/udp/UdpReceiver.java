package org.voidmirror.voicechat.udp;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class UdpReceiver implements Runnable{
    public UdpReceiver(int port) {
        this.port = port;
    }

    private final int port;
    private SourceDataLine speakers;
    private TargetDataLine microphone = null;

    @Override
    public void run() {

        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            final byte[] udpInputBuffer = new byte[8192];

            DatagramPacket dp = new DatagramPacket(udpInputBuffer, udpInputBuffer.length);

            AudioFormat format = new AudioFormat(16000, 16, 2, true, true);

            DataLine.Info inInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(inInfo);
            speakers.open(format);
            speakers.start();

            System.out.println(datagramSocket.isBound());

            Thread speakerThread = new Thread(() -> {
//                byte[] inputBuffer = new byte[8192];
                int bufferVarInput = udpInputBuffer.length;
                try {
                    while (datagramSocket.isBound()) {  // TODO: isBound() / isConnected() ?
                        datagramSocket.receive(dp);
//                        System.out.println(Arrays.toString(dp.getData()));

                        // TODO: uncomment
                        speakers.write(
                                dp.getData(),
                                0,
                                bufferVarInput
                        );
                    }
                } catch (IOException e) {
                    System.out.println("### IO read exception");
                }
            });
            speakerThread.setDaemon(true);
            speakerThread.start();

            System.out.println("### UdpReceiver started");


        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

    }
}
