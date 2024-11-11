package org.voidmirror.voicechat.udp;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.frontend.FrontSwitcher;
import org.voidmirror.voicechat.misc.ByteUtils;
import org.voidmirror.voicechat.misc.ComponentInitializer;
import org.voidmirror.voicechat.voice.LineHolder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
public class UdpReceiver implements Runnable{
    public UdpReceiver(int port) {
        this.port = port;
    }

    private final int port;
    private SourceDataLine speakers;

    private final Label lblPing = FrontSwitcher.getInstance().getLabelFromHolder("lblPing");

    @Override
    public void run() {

        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            datagramSocket.setSoTimeout(200);
            final byte[] udpInputBuffer = new byte[Long.BYTES + 1024];

            DatagramPacket dp = new DatagramPacket(udpInputBuffer, udpInputBuffer.length);

            AudioFormat format = new AudioFormat(16000, 16, 2, true, true);

            DataLine.Info inInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(inInfo);
            speakers.open(format, 1024);
            speakers.start();

            LineHolder lineHolder = LineHolder.getInstance();
            lineHolder.addDataLine(speakers, "speakers");
            lineHolder.addFloatControl((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN), "volumeSpeakers");
            System.out.println(((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN)).getPrecision());
            System.out.println(((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN)).getMaximum());
            System.out.println(((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN)).getMinimum());
            System.out.println(((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN)).getMaximum() / ((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN)).getMinimum());
            System.out.println(((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN)).getMaxLabel());
            System.out.println(((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN)).getMidLabel());
            System.out.println(((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN)).getMinLabel());
            System.out.println(((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN)).getUnits());
            System.out.println(((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN)).getValue());

            ComponentInitializer.getInstance().volumeSliderInit();

            FrontSwitcher.getInstance().getSliderFromHolder("volumeSpeakers").setDisable(false);
            System.out.println("Controls: " + Arrays.toString(speakers.getControls()));

            Thread speakerThread = new Thread(() -> {
                ConcurrentLinkedQueue<Long> pingQueue = ComponentInitializer.getInstance().getPingQueue();
                byte[] toWrite;
                try {
                    while (Thread.currentThread().isAlive()) {
                        try {
                            datagramSocket.receive(dp);
                            toWrite = dp.getData();
                        } catch (SocketTimeoutException e) {
                            toWrite = null;
                            speakers.flush();
                        }
                        if (toWrite != null) {
                            long time = System.currentTimeMillis() - ByteUtils.bytesToLong(toWrite);
                            if (time < 400) {
                                speakers.write(
                                        toWrite,
                                        Long.BYTES,
                                        1024
                                );
                            }
                            pingQueue.add(time);
                        }
                    }
                } catch (IOException e) {
                    log.error("### IO read exception");
                }
            });

            Thread pingThread = new Thread(() -> {
                ConcurrentLinkedQueue<Long> pingQueue = ComponentInitializer.getInstance().getPingQueue();
                while (Thread.currentThread().isAlive()) {
                    if (pingQueue.size() > 50) {
                        long[] ls = pingQueue.stream().mapToLong(Long::longValue).toArray();
                        pingQueue.clear();
                        Platform.runLater(() -> lblPing.setText(
                                 Arrays.stream(ls).sum() / Arrays.stream(ls).count() + "ms"
                        ));
                    }
                }
            });

            speakerThread.setDaemon(true);
            speakerThread.start();
            pingThread.setDaemon(true);
            pingThread.start();

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
