package org.voidmirror.voicechat.udp;

import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.frontend.FrontSwitcher;
import org.voidmirror.voicechat.misc.ComponentInitializer;
import org.voidmirror.voicechat.voice.LineHolder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
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

    public UdpSender(int port, String host) {
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
            final byte[] udpOutputBuffer = new byte[1024];

            DatagramPacket dp = new DatagramPacket(udpOutputBuffer, udpOutputBuffer.length, host, port);

            AudioFormat format = new AudioFormat(16000, 16, 2, true, true);

            DataLine.Info outInfo = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(outInfo);
            microphone.open(format);
            microphone.start();
//            System.out.println("Controls: " + Arrays.toString(microphone.getControls()));

            LineHolder lineHolder = LineHolder.getInstance();
            lineHolder.addDataLine(microphone, "microphone");
//            lineHolder.addFloatControl((FloatControl) microphone.getControl(BooleanControl.Type.MUTE), "muteMicro");

//            FrontSwitcher.getInstance().getToggleButtonFromHolder("btnMuteMicro").setDisable(false);

//            FloatControl booleanControl = (FloatControl) microphone.getControl(FloatControl.Type.MASTER_GAIN);

//            ComponentInitializer.getInstance().microMuteInit();



            Thread microphoneThread = new Thread(new Runnable() {
                final byte[] outputBuffer = new byte[1024];

                @Override
                public void run() {
                    while (true) {
                        microphone.read(outputBuffer, 0, 1024);
                        dp.setData(outputBuffer, 0, 1024);
//                        System.out.println(Arrays.toString(outputBuffer));
                        try {
                            datagramSocket.send(dp);
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }

                }
            });
            microphoneThread.setDaemon(true);
            microphoneThread.start();

            log.info("UdpSender started");


        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
