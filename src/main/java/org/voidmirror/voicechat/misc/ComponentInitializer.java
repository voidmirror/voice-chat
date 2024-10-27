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

//    public void microMuteInit() {
//        ThreadHolder holder = ThreadHolder.getInstance();
//        ToggleButton btnMuteMicro = FrontSwitcher.getInstance().getToggleButtonFromHolder("btnMuteMicro");
//        btnMuteMicro.setOnAction(actionEvent -> {
//            if (btnMuteMicro.isSelected()) {
////                System.out.println("selected");
//                System.out.println(holder.getThread("microphone").getId());
//                System.out.println(holder.getThread("microphoneCopy").getId());
//                holder.getThread("microphone").interrupt();
//                holder.addThread(holder.getThread("microphoneCopy"), "microphone");
//            } else {
////                System.out.println("unselected");
//                System.out.println(holder.getThread("microphone").getId());
//                holder.getThread("microphone").start();
//            }
//        });
//    }

}
