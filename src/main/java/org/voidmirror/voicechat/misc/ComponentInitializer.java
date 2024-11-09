package org.voidmirror.voicechat.misc;

import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.frontend.FrontSwitcher;
import org.voidmirror.voicechat.voice.LineHolder;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class ComponentInitializer {

    private ComponentInitializer() {}

    private static ComponentInitializer self;

    public static ComponentInitializer getInstance() {
        if (self == null) {
            self = new ComponentInitializer();
        }
        return self;
    }

    // Components

    @Setter
    private Slider volumeSlider;

    @Getter
    private final ConcurrentLinkedQueue<byte[]> speakerConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();

    /**
     * Slider range 0-200
     */
    public void volumeSliderInit() {
        FloatControl speakersVolumeFloatControl = LineHolder.getInstance().getFloatControl("volumeSpeakers");
        System.out.println("Init volume value: " + speakersVolumeFloatControl.getValue());

        Platform.runLater(() -> {
            volumeSlider.setOnMousePressed(pressEvent -> {
                volumeSlider.setOnMouseDragged(releaseEvent -> {
                    float volume = 20f * (float) Math.log10((float) (volumeSlider.getValue() / 100));
                    speakersVolumeFloatControl.setValue(
                            volume < -35 ? -80 : volume
                    );
                });
            });
            volumeSlider.setOnMouseClicked(clickEvent -> {
                float volume = 20f * (float) Math.log10((float) (volumeSlider.getValue() / 100));
                speakersVolumeFloatControl.setValue(
                        volume < -35 ? -80 : volume
                );
            });
        });
    }

}
