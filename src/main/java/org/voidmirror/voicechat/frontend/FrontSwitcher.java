package org.voidmirror.voicechat.frontend;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * It is necessary to use class inside Platform.runLater()
 */
public class FrontSwitcher {

    private static FrontSwitcher self;

    private FrontSwitcher() {
    }

    public static FrontSwitcher getInstance() {
        if (self == null) {
            self = new FrontSwitcher();
        }
        return self;
    }

    private FxHolder fxHolder = new FxHolder();

    public FrontSwitcher addButtonToHolder(Button button, String fxId) {
        fxHolder.getFxButtons().put(fxId, button);
        return this;
    }

    public Button getButtonFromHolder(String fxId) {
        return fxHolder.getFxButtons().get(fxId);
    }

    public FrontSwitcher addToggleButtonToHolder(ToggleButton button, String fxId) {
        fxHolder.getFxToggleButtons().put(fxId, button);
        return this;
    }

    public ToggleButton getToggleButtonFromHolder(String fxId) {
        return fxHolder.getFxToggleButtons().get(fxId);
    }

    public FrontSwitcher addTextFieldToHolder(TextField textField, String fxId) {
        fxHolder.getFxTextFields().put(fxId, textField);
        return this;
    }

    public TextField getTextFieldFromHolder(String fxId) {
        return fxHolder.getFxTextFields().get(fxId);
    }

    public FrontSwitcher addImageViewToHolder(ImageView imageView, String fxId) {
        fxHolder.getFxImageViews().put(fxId, imageView);
        return this;
    }

    public ImageView getImageViewFromHolder(String fxId) {
        return fxHolder.getFxImageViews().get(fxId);
    }

    public FrontSwitcher addSliderToHolder(Slider slider, String fxId) {
        fxHolder.getFxSliders().put(fxId, slider);
        return this;
    }

    public Slider getSliderFromHolder(String fxId) {
        return fxHolder.getFxSliders().get(fxId);
    }

    public FrontSwitcher addLabelToHolder(Label label, String fxId) {
        fxHolder.getFxLabels().put(fxId, label);
        return this;
    }

    public Label getLabelFromHolder(String fxId) {
        return fxHolder.getFxLabels().get(fxId);
    }

}

@Getter
@NoArgsConstructor
class FxHolder {
    private final HashMap<String, Button> fxButtons = new HashMap<>();
    private final HashMap<String, ToggleButton> fxToggleButtons = new HashMap<>();
    private final HashMap<String, TextField> fxTextFields = new HashMap<>();
    private final HashMap<String, ImageView> fxImageViews = new HashMap<>();
    private final HashMap<String, Slider> fxSliders = new HashMap<>();
    private final HashMap<String, Label> fxLabels = new HashMap<>();
}
