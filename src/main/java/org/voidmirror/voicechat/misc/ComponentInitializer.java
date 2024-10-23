package org.voidmirror.voicechat.misc;

import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import lombok.Setter;
import org.voidmirror.voicechat.frontend.FrontSwitcher;
import org.voidmirror.voicechat.voice.LineHolder;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;

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

    /**
     * Slider range 0-200
     */
    public void volumeSliderInit() {
        FloatControl speakersVolumeFloatControl = LineHolder.getInstance().getFloatControl("volumeSpeakers");

        Platform.runLater(() -> {
            volumeSlider.setOnMousePressed(pressEvent -> {
                volumeSlider.setOnMouseDragged(dragEvent -> {
                    float volume = 20f * (float) Math.log10((float) (volumeSlider.getValue() / 100));

                    speakersVolumeFloatControl.setValue(
                            volume < -35 ? -80 : volume
                    );
                    System.out.println(speakersVolumeFloatControl.getValue());
                });
            });
        });
    }

    public void microMuteInit() {
        BooleanControl muteMicroControl = (BooleanControl) LineHolder.getInstance().getDataLine("microphone").getControl(BooleanControl.Type.MUTE);
        ToggleButton btnMuteMicro = FrontSwitcher.getInstance().getToggleButtonFromHolder("btnMuteMicro");
        btnMuteMicro.setOnAction(actionEvent -> {
            if (btnMuteMicro.isSelected()) {
                muteMicroControl.setValue(true);
                System.out.println(muteMicroControl.getValue());
            } else {
                muteMicroControl.setValue(false);
                System.out.println(muteMicroControl.getValue());
            }
        });
    }

}
