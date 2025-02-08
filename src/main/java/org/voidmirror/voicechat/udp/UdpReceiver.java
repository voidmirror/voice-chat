package org.voidmirror.voicechat.udp;

import javafx.application.Platform;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.frontend.FrontSwitcher;
import org.voidmirror.voicechat.misc.ByteUtils;
import org.voidmirror.voicechat.misc.ComponentInitializer;
import org.voidmirror.voicechat.service.ClockService;
import org.voidmirror.voicechat.voice.LineHolder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.Clock;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

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
            final byte[] udpInputBuffer = new byte[Long.BYTES + 13312];

            DatagramPacket dp = new DatagramPacket(udpInputBuffer, udpInputBuffer.length);

            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);

            DataLine.Info inInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(inInfo);
            speakers.open(format, 13312);
            speakers.start();

            LineHolder lineHolder = LineHolder.getInstance();
            lineHolder.addDataLine(speakers, "speakers");
            lineHolder.addFloatControl((FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN), "volumeSpeakers");

            // System sound config
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
                Clock clock = ClockService.getInstance().getClock();
                long start;
                long stop;
                byte[] toWrite;
                try {
                    while (Thread.currentThread().isAlive()) {
                        try {
                            start = System.currentTimeMillis();
                            datagramSocket.receive(dp);
                            toWrite = dp.getData();
                            stop = System.currentTimeMillis();
                            System.out.println(stop - start);
                        } catch (SocketTimeoutException e) {
                            toWrite = null;
                                speakers.flush();
                                pingQueue.clear();
                        }
                        if (toWrite != null) {
                            long time = Math.abs(clock.millis() - ByteUtils.bytesToLong(toWrite));
//                            if (time < 400) {
                                speakers.write(
                                        toWrite,
                                        Long.BYTES,
                                        13312
                                );
//                            }
                            pingQueue.add(time);
                        }
                    }
                } catch (IOException e) {
                    log.error("Receiver DataLine writer thread exception: {}", e.getMessage());
                }
            });

            Thread pingThread = new Thread(() -> {
                ConcurrentLinkedQueue<Long> pingQueue = ComponentInitializer.getInstance().getPingQueue();
                while (Thread.currentThread().isAlive()) {
                    if (pingQueue.size() > 50) {
                        AtomicLong sum = new AtomicLong();
                        AtomicLong count = new AtomicLong();
                        pingQueue.forEach(ping -> {
                            sum.addAndGet(ping);
                            count.addAndGet(1);
                        });
                        pingQueue.clear();
                        Platform.runLater(() -> lblPing.setText(
                                 sum.get() / count.get() + "ms"
                        ));
                    }
                }
            });

            speakerThread.setDaemon(true);
            speakerThread.setPriority(Thread.MAX_PRIORITY);
            speakerThread.start();
            pingThread.setDaemon(true);
            pingThread.start();

            log.info("UdpReceiver started");


        } catch (SocketException e) {
            log.error("UdpReceiver socket exception: {}", e.getMessage());
        } catch (LineUnavailableException e) {
            log.error("UdpReceiver DataLine is unavailable: {}", e.getMessage());
        }

    }

}
