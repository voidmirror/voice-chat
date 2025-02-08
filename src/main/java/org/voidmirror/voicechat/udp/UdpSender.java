package org.voidmirror.voicechat.udp;

import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.frontend.FrontSwitcher;
import org.voidmirror.voicechat.misc.ByteUtils;
import org.voidmirror.voicechat.misc.RuntimeConfig;
import org.voidmirror.voicechat.voice.LineHolder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

@Slf4j
public class UdpSender implements Runnable{

    public UdpSender(String host, int port) {
        this.port = port;
        try {
            this.host = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private final int port;
    private final InetAddress host;
    private TargetDataLine microphone = null;

    @Override
    public void run() {

        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            final byte[] udpOutputBuffer = new byte[13312];

            DatagramPacket dp = new DatagramPacket(udpOutputBuffer, udpOutputBuffer.length, host, port);

            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);

            DataLine.Info outInfo = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(outInfo);
            microphone.open(format);
            microphone.start();

            System.out.println("### Microphone controls: " + Arrays.toString(microphone.getControls()));

            LineHolder lineHolder = LineHolder.getInstance();
            lineHolder.addDataLine(microphone, "microphone");

            Thread microphoneThread = new Thread(new Runnable() {
                final byte[] outputBuffer = new byte[Long.BYTES + 13312];

                @Override
                public void run() {
                    RuntimeConfig runtimeConfig = RuntimeConfig.getInstance();
                    while (Thread.currentThread().isAlive()) {
                        if (runtimeConfig.isMicroActive()) {
                            microphone.read(outputBuffer, Long.BYTES, 13312);
                            byte[] time = ByteUtils.longToBytes(System.currentTimeMillis());
                            System.arraycopy(time, 0, outputBuffer, 0, 8);
                            dp.setData(outputBuffer, 0, outputBuffer.length); // offset of time length
                            try {
                                datagramSocket.send(dp);
                            } catch (IOException e) {
                                log.error("UdpSender datagramSocket send exception: {}", e.getMessage());
                            }
                        } else {
                            microphone.flush();
                        }
                    }

                }
            });
            microphoneThread.setDaemon(true);
            microphoneThread.setPriority(Thread.MAX_PRIORITY);
            microphoneThread.start();
            FrontSwitcher.getInstance().getToggleButtonFromHolder("btnMuteMicro").setDisable(false);

            log.info("UdpSender started");


        } catch (SocketException e) {
            log.error("UdpSender socket exception: {}", e.getMessage());
        } catch (LineUnavailableException e) {
            log.error("UdpSender DataLine is unavailable: {}", e.getMessage());
        }
    }
}
